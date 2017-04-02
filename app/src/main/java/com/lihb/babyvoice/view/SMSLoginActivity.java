package com.lihb.babyvoice.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;

import com.lihb.babyvoice.R;
import com.lihb.babyvoice.customview.TitleBar;
import com.lihb.babyvoice.customview.base.BaseFragmentActivity;
import com.lihb.babyvoice.utils.CommonToast;

/**
 * Created by Administrator on 2017/4/3.
 */
public class SMSLoginActivity  extends BaseFragmentActivity {

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
//                login(mLoginAccount, mPassword);
                if (TextUtils.equals(mLoginAccount, "admin") && TextUtils.equals(mPassword, "123456")) {
                    CommonToast.showShortToast("登录成功");
                    Intent intent = new Intent(SMSLoginActivity.this, NewMainActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    CommonToast.showShortToast("登录失败，账户：admin，密码：123456,请重新登录");
                }
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
                if (s.length() > 0) {
                    mPwdClearInputImg.setVisibility(View.VISIBLE);
                } else {
                    mPwdClearInputImg.setVisibility(View.GONE);
                }
                if (s.length() > 5) {
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
                if (s.length() > 0) {
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
                mSendCodeBtn.setText(millisUntilFinished /1000 + "秒后可重发");
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
                CommonToast.showLongToast("验证码正火速发往您的手机中，请及时查收！");
                // FIXME: 2017/4/3 发送短信

//                修改ui,倒计时1分钟
                if (null != mCountDownTimer) {
                    mCountDownTimer.start();
                }

            }
        });

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
}
