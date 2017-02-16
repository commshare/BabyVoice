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
import com.lihb.babyvoice.customview.base.BaseFragment;
import com.lihb.babyvoice.utils.CommonToast;
import com.lihb.babyvoice.utils.FileUtils;
import com.orhanobut.logger.Logger;


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


}
