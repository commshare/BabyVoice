package com.lihb.babyvoice.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.lihb.babyvoice.BabyVoiceApp;
import com.lihb.babyvoice.R;
import com.lihb.babyvoice.customview.base.BaseFragmentActivity;
import com.lihb.babyvoice.utils.SharedPreferencesUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by lhb on 2017/4/1.
 */

public class StartupActivity extends BaseFragmentActivity {

    private static final String TAG = "StartupActivity";
    private TextView mRegisterBtn = null;

    private TextView mLoginBtn = null;
    private TextView mSmsLoginTxt;

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

        //加密上传到友盟的数据
        MobclickAgent.enableEncrypt(true);
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
        mSmsLoginTxt = (TextView) findViewById(R.id.sms_login_txt);
        mSmsLoginTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartupActivity.this, SMSLoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        if (!SharedPreferencesUtil.isFirstLaunch(StartupActivity.this)) {
            SharedPreferences sharedPreferences = getSharedPreferences("userinfo", MODE_PRIVATE);

            BabyVoiceApp.currUserName = sharedPreferences.getString("username", "");
            Intent intent = new Intent(StartupActivity.this, NewMainActivity.class);
            startActivity(intent);
            finish();
        }

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

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG);
        MobclickAgent.onResume(this);
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG);
        MobclickAgent.onPause(this);
    }
}
