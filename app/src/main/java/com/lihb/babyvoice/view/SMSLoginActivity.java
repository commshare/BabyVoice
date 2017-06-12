package com.lihb.babyvoice.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;

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
import com.orhanobut.logger.Logger;
import com.umeng.analytics.MobclickAgent;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/4/3.
 */
public class SMSLoginActivity  extends BaseFragmentActivity {

    private static final String TAG = "SMSLoginActivity";
    private EditText mUserAccountEditText;

    private EditText mUserPasswordEditText;

    private Button mLoginBtn;

    private ImageView mAccountClearInputImg = null;
    private ImageView mPwdClearInputImg = null;

    // 密码可见
    private ImageView mPwdShowImg = null;
    private boolean mIsPwdVisiable = false;
    private TitleBar mTitleBar;

    private Button mSendCodeBtn = null;
    private CheckBox mCheckBox = null;
    private CountDownTimer mCountDownTimer;
    private String mLoginAccount;
    private String mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_login);
        initViews();
        getWindow().setBackgroundDrawable(null);

    }

    private void initViews() {
        mAccountClearInputImg = (ImageView) findViewById(R.id.account_clear_input);
        mAccountClearInputImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUserAccountEditText.setText("");
            }
        });
        mPwdClearInputImg = (ImageView) findViewById(R.id.pwd_clear_input);
        mPwdClearInputImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUserPasswordEditText.setText("");
            }
        });
        mLoginBtn = (Button) findViewById(R.id.login_btn);
        mLoginBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                mLoginAccount = mUserAccountEditText.getText().toString();
                mPassword = mUserPasswordEditText.getText().toString();
                if (!mCheckBox.isChecked()) {
                    CommonToast.showShortToast("请勾选同意协议");
                    return;
                }
                login(mLoginAccount, mPassword);
            }
        });
        mTitleBar = (TitleBar) findViewById(R.id.title_bar);
        mTitleBar.setLeftOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SMSLoginActivity.this, StartupActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mPwdShowImg = (ImageView) findViewById(R.id.pwd_show);
        mPwdShowImg.setOnClickListener(new View.OnClickListener() {
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

        mUserAccountEditText = (EditText) findViewById(R.id.account);
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
                if (s.length() > 10) {
                    mAccountClearInputImg.setVisibility(View.VISIBLE);
                    mSendCodeBtn.setEnabled(true);
                    mSendCodeBtn.setBackgroundResource(R.drawable.register_login_pressed_shape);
                } else {
                    mAccountClearInputImg.setVisibility(View.GONE);
                    mSendCodeBtn.setEnabled(false);
                    mSendCodeBtn.setBackgroundResource(R.drawable.register_login_normal_shape);
                }


            }
        });

        mCheckBox = (CheckBox) findViewById(R.id.sms_check_box);
        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    CommonToast.showShortToast("选中");
                }else {
                    CommonToast.showShortToast("not选中");
                }
            }
        });
        mCountDownTimer = new CountDownTimer(1000 * 60, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mSendCodeBtn.setEnabled(false);
                mSendCodeBtn.setText(getString(R.string.resend_sms_code, millisUntilFinished /1000));
            }

            @Override
            public void onFinish() {
                mSendCodeBtn.setEnabled(true);
                mSendCodeBtn.setText("重新发送");
            }
        };

        mSendCodeBtn = (Button) findViewById(R.id.send_code_btn);
        mSendCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonToast.showLongToast(R.string.sms_to_your_phone);
                // 成功,修改ui,倒计时1分钟
                if (null != mCountDownTimer) {
                    mCountDownTimer.start();
                }
                mLoginAccount = mUserAccountEditText.getText().toString();
                Log.e("lihb", "onClick: account = " + mLoginAccount);
                ServiceGenerator.createService(ApiManager.class)
                        .getSmsCode(mLoginAccount)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new Action1<HttpResponse<Void>>() {
                            @Override
                            public void call(HttpResponse<Void> httpResponse) {
                                Log.i("lihb", httpResponse.toString());
                                if (httpResponse.code == 0) {

                                }
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                Logger.e("error-->" + throwable.toString());
                                CommonToast.showShortToast("发送失败。");
                            }
                        });

            }
        });

    }

    private void login(final String loginAccount, final String password) {
        ServiceGenerator.createService(ApiManager.class)
                .loginBySmsCode(loginAccount, password)
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
                            if (SharedPreferencesUtil.isFirstLaunch(SMSLoginActivity.this)) {
                                FileUtils.insertPregnantData(FileUtils.getPregnantData(SMSLoginActivity.this));
                                FileUtils.insertVaccineData(FileUtils.getVaccineData(SMSLoginActivity.this));
                            }

                            SharedPreferencesUtil.setFirstLaunch(SMSLoginActivity.this, false);
                            SharedPreferencesUtil.saveToPreferences(SMSLoginActivity.this, loginAccount, password);
                            BabyVoiceApp.getInstance().setLogin(true);
                            BabyVoiceApp.currUserName = loginAccount;
                            Intent intent = new Intent(SMSLoginActivity.this, NewMainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        CommonToast.showShortToast("登录失败，请重新登录!");
                    }
                });
    }

    /**
     * 简单保存到SharedPreferences中
     * @param username
     * @param password
     */
    private void saveToPreferences(String username, String password) {
        //创建sharedPreference对象，info表示文件名，MODE_PRIVATE表示访问权限为私有的
        SharedPreferences sp = getSharedPreferences("userinfo", MODE_PRIVATE);

        //获得sp的编辑器
        SharedPreferences.Editor ed = sp.edit();

        //以键值对的显示将用户名和密码保存到sp中
        ed.putString("username", username);
        ed.putString("password", password);

        //提交用户名和密码
        ed.apply();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SMSLoginActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mCountDownTimer) {
            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }
    }

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
