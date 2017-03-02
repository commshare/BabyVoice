package com.lihb.babyvoice.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.lihb.babyvoice.R;
import com.lihb.babyvoice.action.ApiManager;
import com.lihb.babyvoice.action.ServiceGenerator;
import com.lihb.babyvoice.customview.base.BaseFragment;
import com.lihb.babyvoice.model.HttpResponse;
import com.lihb.babyvoice.utils.CommonToast;
import com.lihb.babyvoice.utils.FileUtils;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


/**
 * Created by lhb on 2017/2/16.
 */

public class VoiceSaveFragment extends BaseFragment {

    private EditText mEditText;
    private Spinner mSpinner;
    private TextView mCancelTxt;
    private TextView mSaveTxt;
    private String mFileName;

    public static VoiceSaveFragment create() {
        return new VoiceSaveFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            mFileName = bundle.getString("fileName");
        }
        return inflater.inflate(R.layout.fragment_voice_save, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        hideBottomTab();
        initView();
    }

    private void initView() {
        mEditText = (EditText) getView().findViewById(R.id.voice_save_title);
        mSpinner = (Spinner) getView().findViewById(R.id.spinner1);
        mCancelTxt = (TextView) getView().findViewById(R.id.voice_save_cancel);
        mSaveTxt = (TextView) getView().findViewById(R.id.voice_save_done);

        String[] items = getResources().getStringArray(R.array.voice_type);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);

        mCancelTxt.setOnClickListener(mOnClickListener);
        mSaveTxt.setOnClickListener(mOnClickListener);
        mSpinner.setOnItemSelectedListener(mOnItemSelectedListener);

    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden == false) {
            hideBottomTab();
        }
    }

    private void hideBottomTab() {
        if (getActivity() == null) {
            return;
        }
        // 隐藏底部的导航栏和分割线
        ((LinearLayout) getActivity().findViewById(R.id.linearLayout1)).setVisibility(View.GONE);
        ((View) getActivity().findViewById(R.id.divider_line2)).setVisibility(View.GONE);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getActivity().onBackPressed();
            Logger.i(FileUtils.getAMRFilePath(mFileName));
            Logger.i(FileUtils.getAMRFilePath(mEditText.getText().toString().trim() + ".amr"));
            if (v == mCancelTxt) {
                FileUtils.deleteFile(FileUtils.getAMRFilePath(mFileName));
            } else if (v == mSaveTxt) {
                FileUtils.renameFile(FileUtils.getAMRFilePath(mFileName), FileUtils.getAMRFilePath(mEditText.getText().toString().trim() + ".amr"));
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        HttpUploadUtil.uploadFile(FileUtils.getAMRFilePath(mEditText.getText().toString().trim() + ".amr"));
//                    }
//                }).start();
                List<File> files = new ArrayList<>();
                File file = new File(FileUtils.getAMRFilePath(mEditText.getText().toString().trim() + ".amr"));
                files.add(file);
                MultipartBody body = filesToMultipartBody(files);
                ServiceGenerator.createService(ApiManager.class)
                        .uploadFiles(body)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<HttpResponse<String>>() {
                            @Override
                            public void call(HttpResponse<String> stringBaseResponse) {
                                Logger.i(stringBaseResponse.msg);
                                if (stringBaseResponse.code == 200) {
                                    CommonToast.showShortToast("上传成功！！");
                                }
                                FileUtils.deleteFile(FileUtils.getAMRFilePath(mEditText.getText().toString().trim() + ".amr"));
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                Logger.e(throwable.getMessage());
                                CommonToast.showShortToast("error : " + throwable.getMessage());
                                FileUtils.deleteFile(FileUtils.getAMRFilePath(mEditText.getText().toString().trim() + ".amr"));
                            }
                        });

            }
        }
    };

    private AdapterView.OnItemSelectedListener mOnItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String[] items = getResources().getStringArray(R.array.voice_type);
            CommonToast.showShortToast("选择了" + items[position]);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }

    };

    public static MultipartBody filesToMultipartBody(List<File> files) {
        MultipartBody.Builder builder = new MultipartBody.Builder();

        for (File file : files) {
            RequestBody requestBody = RequestBody.create(MediaType.parse(""), file);
            builder.addFormDataPart("file", file.getName(), requestBody);
            builder.addFormDataPart("fileName", file.getName());
        }
        builder.setType(MultipartBody.FORM);
        MultipartBody multipartBody = builder.build();
        return multipartBody;
    }


}
