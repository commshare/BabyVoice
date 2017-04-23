package com.lihb.babyvoice.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.lihb.babyvoice.BabyVoiceApp;
import com.lihb.babyvoice.R;
import com.lihb.babyvoice.action.ApiManager;
import com.lihb.babyvoice.action.ServiceGenerator;
import com.lihb.babyvoice.customview.TitleBar;
import com.lihb.babyvoice.customview.base.BaseFragment;
import com.lihb.babyvoice.db.impl.BabyVoiceDataImpl;
import com.lihb.babyvoice.model.BabyVoice;
import com.lihb.babyvoice.model.HttpResponse;
import com.lihb.babyvoice.utils.CommonToast;
import com.lihb.babyvoice.utils.FileUtils;
import com.lihb.babyvoice.utils.SoftInputUtil;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
    private String mFileName;
    private TitleBar mTitleBar;
    private int mRecordType;

    private static final String SUFFIX = ".wav";
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
    private String[] items;


    public static VoiceSaveFragment create() {
        return new VoiceSaveFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            mFileName = bundle.getString("fileName");
            mRecordType = bundle.getInt("type");
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

        items = getActivity().getResources().getStringArray(R.array.voice_type);

        mTitleBar = (TitleBar) getView().findViewById(R.id.title_bar);
        mTitleBar.setLeftOnClickListener(mOnClickListener);
        mTitleBar.setRightOnClickListener(mOnClickListener);

        mEditText = (EditText) getView().findViewById(R.id.voice_save_title);
        mEditText.setText(mFileName);
        mEditText.requestFocus();
        mEditText.setSelection(mFileName.length());
        SoftInputUtil.showSoftInput(mEditText, getActivity());
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        mEditText.setText("");
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden == false) {
            hideBottomTab();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mEditText.setText(mFileName);
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
            mEditText.requestFocus();
            SoftInputUtil.hideSoftInput(getActivity());
            getActivity().onBackPressed();
            Logger.i(FileUtils.getVoiceFilePath(mFileName));
            Logger.i(FileUtils.getVoiceFilePath(mEditText.getText().toString().trim() + SUFFIX));
            if (v == mTitleBar.getLeftText()) {
                FileUtils.deleteFile(FileUtils.getVoiceFilePath(mFileName));
            } else if (v == mTitleBar.getRightText()) {
                FileUtils.renameFile(FileUtils.getVoiceFilePath(mFileName)+ SUFFIX, FileUtils.getVoiceFilePath(mEditText.getText().toString().trim() + SUFFIX));
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        HttpUploadUtil.uploadFile(FileUtils.getVoiceFilePath(mEditText.getText().toString().trim() + ".amr"));
//                    }
//                }).start();
                // 上传到服务器
                uploadToServer();
                // 保存到本地数据库
                saveToDatabase();

            }
        }


    };

    private void saveToDatabase() {
        final BabyVoice babyVoice = new BabyVoice();
        babyVoice.name = mEditText.getText().toString().trim() + SUFFIX;
        babyVoice.date = sdf.format(new Date());
        babyVoice.category = items[mRecordType];
        babyVoice.url = FileUtils.getVoiceFilePath(mEditText.getText().toString().trim() + SUFFIX);
        babyVoice.duration = FileUtils.getVoiceDuration(babyVoice.url);
        BabyVoiceDataImpl.getInstance().insertData(babyVoice)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if (aBoolean) {
                            com.orhanobut.logger.Logger.i("insert babyvoice record success,"+ babyVoice.toString());
                        }else {
                            com.orhanobut.logger.Logger.i("insert babyvoice record failed,"+ babyVoice.toString());
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        com.orhanobut.logger.Logger.e(throwable.getMessage());
                    }
                });

    }
    private void uploadToServer() {
        List<File> files = new ArrayList<>();
        File file = new File(FileUtils.getVoiceFilePath(mEditText.getText().toString().trim() + SUFFIX));
        files.add(file);
        MultipartBody body = filesToMultipartBody(files);
        ServiceGenerator.createService(ApiManager.class)
                .uploadFiles(BabyVoiceApp.currUserName,body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<HttpResponse<String>>() {
                    @Override
                    public void call(HttpResponse<String> stringBaseResponse) {
                        Logger.i(stringBaseResponse.msg);
                        if (stringBaseResponse.code == 0) {
                            CommonToast.showShortToast("上传成功！！");
                        }
//                                FileUtils.deleteFile(FileUtils.getVoiceFilePath(mEditText.getText().toString().trim() + SUFFIX));
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Logger.e(throwable.getMessage());
                        CommonToast.showShortToast("error : " + throwable.getMessage());
//                                FileUtils.deleteFile(FileUtils.getVoiceFilePath(mEditText.getText().toString().trim() + SUFFIX));
                    }
                });
    }


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
