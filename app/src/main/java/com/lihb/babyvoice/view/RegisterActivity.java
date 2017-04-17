package com.lihb.babyvoice.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lihb.babyvoice.R;
import com.lihb.babyvoice.action.ApiManager;
import com.lihb.babyvoice.action.ServiceGenerator;
import com.lihb.babyvoice.customview.TitleBar;
import com.lihb.babyvoice.customview.base.BaseFragmentActivity;
import com.lihb.babyvoice.model.BabyVoice;
import com.lihb.babyvoice.model.HttpResList;
import com.lihb.babyvoice.model.HttpResponse;
import com.lihb.babyvoice.utils.CommonToast;
import com.orhanobut.logger.Logger;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by lhb on 2017/4/1.
 */
public class RegisterActivity extends BaseFragmentActivity {

    private Button mRegisterBtn = null;

    private EditText mAccountEditText = null;

    private TextView mTipsTextView = null;

    private ImageView mAccountClearInputImg = null;

    private ImageView mPwdClearInputImg = null;

    private EditText mPwdEditText = null;

    private ImageView mPwdShowImg = null;

    private boolean mIsPwdVisiable = false;

    private ProgressDialog mProgressDialog = null;

    private TitleBar mTitleBar;

    private TextView mSmsLoginTxt = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();

    }

    /**
     * Called when the activity has detected the user's press of the back
     * key.  The default implementation simply finishes the current activity,
     * but you can override this to do whatever you want.
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(RegisterActivity.this, StartupActivity.class);
        startActivity(intent);
        finish();
    }

    private void initView() {

        mTitleBar = (TitleBar) findViewById(R.id.title_bar);
        mTitleBar.setLeftOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, StartupActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mRegisterBtn = (Button) findViewById(R.id.register_btn);
        mRegisterBtn.setEnabled(false);
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });

        mAccountClearInputImg = (ImageView) findViewById(R.id.account_clear_input);
        mAccountClearInputImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAccountEditText.setText("");
            }
        });

        mPwdClearInputImg = (ImageView) findViewById(R.id.pwd_clear_input);
        mPwdClearInputImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPwdEditText.setText("");
            }
        });

        mPwdShowImg = (ImageView) findViewById(R.id.pwd_show);
        mPwdShowImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mIsPwdVisiable) {
                    mPwdEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    mPwdShowImg.setImageResource(R.mipmap.zy);
                    mIsPwdVisiable = true;
                } else {
                    mPwdEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    mPwdShowImg.setImageResource(R.mipmap.by);
                    mIsPwdVisiable = false;
                }
                // 光标移到最后
                mPwdEditText.setSelection(mPwdEditText.getText().length());
            }
        });

        mAccountEditText = (EditText) findViewById(R.id.account);
        InputFilter[] filters = {new InputFilter.LengthFilter(255)};
        mAccountEditText.setFilters(filters);
        mAccountEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO Auto-generated method stub
                mPwdEditText.setText("");
                mRegisterBtn.setEnabled(true);
                mRegisterBtn.setClickable(true);

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

        mPwdEditText = (EditText) findViewById(R.id.password);
        mPwdEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    check(mAccountEditText.getText().toString());
                }
            }
        });
        mPwdEditText.addTextChangedListener(new TextWatcher() {

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
                    mRegisterBtn.setBackgroundResource(R.drawable.register_login_pressed_shape);
                } else {
                    mRegisterBtn.setBackgroundResource(R.drawable.register_login_normal_shape);

                }

            }
        });


        mTipsTextView = (TextView) findViewById(R.id.tips);
        mTipsTextView.setVisibility(View.GONE);

    }

    private void check(String passport) {
        if (TextUtils.isEmpty(passport) /* || TextUtils.isEmpty(password) */) {
            showDialog("帐号不能为空！");
            return;
        }

    }

    private void register() {
        final String userAccount = mAccountEditText.getText().toString();
        final String password = mPwdEditText.getText().toString();
        if (TextUtils.isEmpty(userAccount) || TextUtils.isEmpty(password)) {
            showDialog("帐号和密码不能为空！");
            return;
        }
        showProgressDialog("正在注册...");

        ServiceGenerator.createService(ApiManager.class)
            .register(userAccount, password,userAccount)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<HttpResponse<Void>>() {
                    @Override
                    public void call(HttpResponse<Void> voidHttpResponse) {
                        if (voidHttpResponse.code == 200) {
                            dismissLoginDialog();
                            CommonToast.showShortToast("注册成功");
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        dismissLoginDialog();
                        CommonToast.showShortToast("注册失败");
                        Log.e("error:", throwable.toString());
                    }
                });

    }

    private void showProgressDialog(String msg) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(msg);
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
}