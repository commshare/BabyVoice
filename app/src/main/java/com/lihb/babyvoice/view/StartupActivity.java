package com.lihb.babyvoice.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.lihb.babyvoice.R;
import com.lihb.babyvoice.customview.base.BaseFragmentActivity;

/**
 * Created by lhb on 2017/4/1.
 */

public class StartupActivity extends BaseFragmentActivity {

    private TextView mRegisterBtn = null;

    private TextView mLoginBtn = null;

//    private TextView mCopyRight = null;
//
//    private TextView mVersionTextView = null;
//
//    private int mClickCount = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
        initView();
    }

    private void initView() {
        mRegisterBtn = (TextView) findViewById(R.id.register);
        mLoginBtn = (TextView) findViewById(R.id.login);
//        mCopyRight = (TextView) findViewById(R.id.copyright);
//        mVersionTextView = (TextView) findViewById(R.id.version);
//        setVersion();

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StartupActivity.this,
                        RegisterActivity.class));
                finish();
            }
        });

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StartupActivity.this,
                        LoginActivity.class));
                finish();
            }
        });

//        mCopyRight.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mClickCount < 9) {
//                    mClickCount++;
//                    return;
//                }
//                mClickCount = 0;
//                DemoApplication.getInstance().switchApplicationToDebugMode();
//            }
//        });
    }

//    private void setVersion() {
//        PackageManager pm = this.getPackageManager();
//        try {
//            PackageInfo pkgInfo = pm.getPackageInfo(this.getPackageName(), 0);
//            if (pkgInfo != null) {
//                if (BabyVoiceApp.getInstance().mIsDebug) {
//                    mVersionTextView.setText(pkgInfo.versionName + "_debug");
//                } else {
//                    mVersionTextView.setText(pkgInfo.versionName);
//                }
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }
//
//    }
}
