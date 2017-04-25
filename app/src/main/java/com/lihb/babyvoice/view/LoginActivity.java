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

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lihb.babyvoice.BabyVoiceApp;
import com.lihb.babyvoice.R;
import com.lihb.babyvoice.action.ApiManager;
import com.lihb.babyvoice.action.ServiceGenerator;
import com.lihb.babyvoice.customview.TitleBar;
import com.lihb.babyvoice.customview.base.BaseFragmentActivity;
import com.lihb.babyvoice.model.HttpResponse;
import com.lihb.babyvoice.utils.CommonToast;
import com.lihb.babyvoice.utils.FileUtils;
import com.lihb.babyvoice.utils.SharedPreferencesUtil;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by lhb on 2017/4/1.
 */
public class LoginActivity extends BaseFragmentActivity {


    private EditText mUserAccountEditText;

    private EditText mUserPasswordEditText;

    private Button mLoginBtn;


    private String mPassword;

    private String mLoginAccount = null;

    private ProgressDialog mProgressDialog = null;

    private ImageView mAccountClearInputImg = null;
    private ImageView mPwdClearInputImg = null;

    // 密码可见
    private ImageView mPwdShowImg = null;
    private boolean mIsPwdVisiable = false;
    private TitleBar mTitleBar;
    private TextView mSmsLoginTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
        getWindow().setBackgroundDrawable(null);

    }

    private void initViews() {
        mTitleBar = (TitleBar) findViewById(R.id.title_bar);
        mAccountClearInputImg = (ImageView) findViewById(R.id.account_clear_input);
        mUserAccountEditText = (EditText) findViewById(R.id.account);
        mUserPasswordEditText = (EditText) findViewById(R.id.password);
        mSmsLoginTxt = (TextView) findViewById(R.id.sms_login_txt);
        mLoginBtn = (Button) findViewById(R.id.login_btn);
        mPwdClearInputImg = (ImageView) findViewById(R.id.pwd_clear_input);
        mPwdShowImg = (ImageView) findViewById(R.id.pwd_show);

        mAccountClearInputImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mUserAccountEditText.setText("");
            }
        });
        mPwdClearInputImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mUserPasswordEditText.setText("");
            }
        });


        SharedPreferences sharedPreferences = getSharedPreferences("userinfo", MODE_PRIVATE);

        mLoginAccount = sharedPreferences.getString("username", "");
        mPassword = sharedPreferences.getString("password", "");
        if (mLoginAccount.length() > 0) {
            mAccountClearInputImg.setVisibility(View.VISIBLE);
        } else {
            mAccountClearInputImg.setVisibility(View.GONE);
        }
        if (mPassword.length() > 0) {
            mPwdClearInputImg.setVisibility(View.VISIBLE);
        } else {
            mPwdClearInputImg.setVisibility(View.GONE);
        }

        if (mPassword.length() > 3) {
            mLoginBtn.setBackgroundResource(R.drawable.register_login_pressed_shape);
        } else {
            mLoginBtn.setBackgroundResource(R.drawable.register_login_normal_shape);
        }

        mUserAccountEditText.setText(mLoginAccount);
        mUserPasswordEditText.setText(mPassword);


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

        mTitleBar.setLeftOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, StartupActivity.class);
                startActivity(intent);
                finish();
            }
        });


        mPwdShowImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mIsPwdVisiable) {
                    mUserPasswordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    mPwdShowImg.setImageResource(R.mipmap.zy);
                    mIsPwdVisiable = true;
                } else {
                    mUserPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    mPwdShowImg.setImageResource(R.mipmap.by);
                    mIsPwdVisiable = false;
                }
                // 光标移到最后
                mUserPasswordEditText.setSelection(mUserPasswordEditText.getText().length());
            }
        });



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
                if (s.length() > 0) {
                    mPwdClearInputImg.setVisibility(View.VISIBLE);
                } else {
                    mPwdClearInputImg.setVisibility(View.GONE);
                }

                if (s.length() > 3) {
                    mLoginBtn.setBackgroundResource(R.drawable.register_login_pressed_shape);
                } else {
                    mLoginBtn.setBackgroundResource(R.drawable.register_login_normal_shape);
                }

            }
        });
        mUserPasswordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
            }
        });


        InputFilter[] filters = {new InputFilter.LengthFilter(255)};
        mUserAccountEditText.setFilters(filters);
        mUserAccountEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO Auto-generated method stub
                mUserPasswordEditText.setText("");
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
                    mAccountClearInputImg.setVisibility(View.VISIBLE);
                } else {
                    mAccountClearInputImg.setVisibility(View.GONE);
                }

            }
        });


        mSmsLoginTxt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SMSLoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

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

        ServiceGenerator.createService(ApiManager.class)
                .loginByPassword(userAccount, password)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<HttpResponse<Void>>() {
                    @Override
                    public void call(HttpResponse<Void> httpResponse) {
                        Log.i("lihb", httpResponse.toString());
                        if (httpResponse.code == 0) {
                            // 成功
                            CommonToast.showShortToast("登录成功");

                            // 插入产检、疫苗数据到数据库，只插入一次
                            if (SharedPreferencesUtil.isFirstLaunch(LoginActivity.this)) {
                                FileUtils.insertPregnantData(FileUtils.getPregnantData(LoginActivity.this));
                                FileUtils.insertVaccineData(FileUtils.getVaccineData(LoginActivity.this));
                            }

                            SharedPreferencesUtil.setFirstLaunch(LoginActivity.this, false);
                            SharedPreferencesUtil.saveToPreferences(LoginActivity.this,userAccount, password);
                            BabyVoiceApp.getInstance().setLogin(true);
                            BabyVoiceApp.currUserName = userAccount;
                            Intent intent = new Intent(LoginActivity.this, NewMainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        CommonToast.showShortToast("登录失败，请重新登录!");
                        Log.e("lihb", throwable.toString());
                    }
                });

    }




}
