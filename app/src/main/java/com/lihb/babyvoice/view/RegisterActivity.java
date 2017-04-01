package com.lihb.babyvoice.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lihb.babyvoice.R;

/**
 * Created by lhb on 2017/4/1.
 */
public class RegisterActivity extends Activity {

    private ImageView mBackBtn = null;

    private Button mRegisterBtn = null;

    private EditText mAccountEditText = null;

    private TextView mTipsTextView = null;

    private ImageView mClearInputImg = null;

    private EditText mPwdEditText = null;

    private ImageView mPwdShowImg = null;

    private boolean mIsPwdVisiable = false;

    private ProgressDialog mProgressDialog = null;

    private String mUdbCallback = "";

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
        mBackBtn = (ImageView) findViewById(R.id.iv_back);
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

        mClearInputImg = (ImageView) findViewById(R.id.clear_input);
        mClearInputImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAccountEditText.setText("");
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
                    ;
                    mPwdShowImg.setImageResource(R.mipmap.by);
                    mIsPwdVisiable = false;
                }
                // 光标移到最后
                mPwdEditText.setSelection(mPwdEditText.getText().length());
            }
        });

        mAccountEditText = (EditText) findViewById(R.id.account);

        mPwdEditText = (EditText) findViewById(R.id.pwd);
        mPwdEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    check(mAccountEditText.getText().toString());
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
        final String udbCallback = mUdbCallback;
        if (TextUtils.isEmpty(userAccount) || TextUtils.isEmpty(password)) {
            showDialog("帐号和密码不能为空！");
            return;
        }
        showProgressDialog("正在注册...");

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
}