package com.lihb.babyvoice.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lihb.babyvoice.R;
import com.lihb.babyvoice.customview.base.BaseFragment;

/**
 * Created by lhb on 2017/2/8.
 */

public class HeartFragment extends BaseFragment {

    public static HeartFragment create() {
        return new HeartFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_heart, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
    }

}
