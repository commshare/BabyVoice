/*
 * 文 件 名:  LoginActivity.java
 * 描    述:  <描述>
 * 修 改 人:  liuxinyang
 * 修改时间:  2015年4月1日
 * 跟踪单号:  <跟踪单号>
 * 修改单号:  <修改单号>
 * 修改内容:  <修改内容>
 */
package com.lihb.babyvoice.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.lihb.babyvoice.BabyVoiceApp;
import com.lihb.babyvoice.R;
import com.orhanobut.logger.Logger;

/**
 * Created by lhb on 2017/4/1.
 */
public class LoginActivity extends Activity {

    private ImageView mBackBtn;

    private EditText mUserAccountEditText;

    private EditText mUserPasswordEditText;

    private Button mLoginBtn;

    private String mUserAccount;

    private String mPassword;

    private String mLoginAccount = null;

    private ProgressDialog mProgressDialog = null;

    private ImageView mClearInputImg = null;

    // 密码可见
    private ImageView mPwdShowImg = null;
    private boolean mIsPwdVisiable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
        getWindow().setBackgroundDrawable(null);

    }

    private void initViews() {
        mClearInputImg = (ImageView) findViewById(R.id.clear_input);
        mClearInputImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mUserAccountEditText.setText("");
            }
        });
        mLoginBtn = (Button) findViewById(R.id.login_btn);
        mLoginBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                mLoginAccount = mUserAccountEditText.getText().toString();
                mPassword = mUserPasswordEditText.getText().toString();
                login(mLoginAccount, mPassword);
//				loginWithPassword(mLoginAccount, mPassword);
            }
        });

        mBackBtn = (ImageView) findViewById(R.id.iv_back);
        mBackBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, StartupActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mPwdShowImg = (ImageView) findViewById(R.id.pwd_show);
        mPwdShowImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mIsPwdVisiable) {
                    mUserPasswordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    mPwdShowImg.setImageResource(R.mipmap.zy);
                    mIsPwdVisiable = true;
                } else {
                    mUserPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    ;
                    mPwdShowImg.setImageResource(R.mipmap.by);
                    mIsPwdVisiable = false;
                }
                // 光标移到最后
                mUserPasswordEditText.setSelection(mUserPasswordEditText.getText().length());
            }
        });


        mUserPasswordEditText = (EditText) findViewById(R.id.password);
        mUserPasswordEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });
        mUserPasswordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
            }
        });

        mUserAccountEditText = (EditText) findViewById(R.id.account);
        InputFilter[] filters = {new InputFilter.LengthFilter(255)};
        mUserAccountEditText.setFilters(filters);
        mUserAccountEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO Auto-generated method stub
                mUserPasswordEditText.setText(null);
                mLoginBtn.setClickable(true);

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                if (s.length() > 0) {
                    mClearInputImg.setVisibility(View.VISIBLE);
                } else {
                    mClearInputImg.setVisibility(View.GONE);
                }
            }
        });

    }

    private void handleLoginSuccess() {
        Logger.i("handleLoginSuccess");
        BabyVoiceApp.getInstance().setFirstLaunch(false);
        // 账号的保存，用于自动登录
//		BabyVoiceApp.getInstance().getUserInfo().getAccountManager()
//				.saveLoginAccount(mLoginAccount);
//		BabyVoiceApp.getInstance().getUserInfo().getAccountManager()
//				.saveAccount(mLoginAccount);
//		BabyVoiceApp.getInstance().getUserInfo().getAccountManager()
//				.savePassword(mPassword);
        // 跳转到主界面
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(LoginActivity.this, StartupActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        dismissLoginDialog();

        mProgressDialog = null;
    }


    private void showProgressDialog(String msg) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Login...");
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCanceledOnTouchOutside(false);
        }
        mProgressDialog.show();
    }

    private void dismissLoginDialog() {
        if (mProgressDialog == null) {
            return;
        }
        mProgressDialog.dismiss();
    }


    private void showDialog(String tips) {
        Toast toast = Toast.makeText(getApplicationContext(),
                tips, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    private void login(final String userAccount, final String password) {
        if (TextUtils.isEmpty(userAccount) /* || TextUtils.isEmpty(password) */) {

            showDialog("帐号不能为空！");
            return;
        }

    }


}
