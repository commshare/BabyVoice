package com.lihb.babyvoice.view;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lihb.babyvoice.R;
import com.lihb.babyvoice.command.BaseAndroidCommand;
import com.lihb.babyvoice.command.NetStateChangedCommand;
import com.lihb.babyvoice.command.PickedCategoryCommand;
import com.lihb.babyvoice.customview.base.BaseFragmentActivity;
import com.lihb.babyvoice.utils.NetworkHelper;
import com.lihb.babyvoice.utils.RecorderHelper;
import com.lihb.babyvoice.utils.RxBus;
import com.orhanobut.logger.Logger;

import cn.sharesdk.framework.ShareSDK;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class NewMainActivity extends BaseFragmentActivity {

    private static final String TAG = "NewMainActivity";

    private static final int HEART_TAB = 0;
    private static final int WATCH_TAB = 1;
    private static final int ASSIST_TAB = 2;
    private static final int ME_TAB = 3;

    private Fragment[] mFragmentList;

    private MeFragment mMeFragment;
    private PregnantZoneFragment mPregnantZoneFragment;
    private HeartFragment mHeartFragment;
    private AssistFragment mAssistFragment;

    //ui
    private TextView mHeartPageTab;
    private TextView mWatchTab;
    private TextView mAssistTab;
    private TextView mMeTab;
    private RelativeLayout mNetErrorNoticeBar;

    private int mCurrTab = HEART_TAB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_new);
        ShareSDK.initSDK(this);
        initViews();
        checkNetStatus();

    }

    private void initViews() {
        mHeartPageTab = (TextView) findViewById(R.id.heart_textview);
        mWatchTab = (TextView) findViewById(R.id.watch_textview);
        mAssistTab = (TextView) findViewById(R.id.assist_textview);
        mMeTab = (TextView) findViewById(R.id.me_textview);

        mHeartPageTab.setOnClickListener(mTabOnClickListener);
        mWatchTab.setOnClickListener(mTabOnClickListener);
        mAssistTab.setOnClickListener(mTabOnClickListener);
        mMeTab.setOnClickListener(mTabOnClickListener);

        mNetErrorNoticeBar = (RelativeLayout) findViewById(R.id.net_error_notice_bar);

        mHeartFragment = HeartFragment.create();
        mPregnantZoneFragment = PregnantZoneFragment.create();
        mAssistFragment = AssistFragment.create();
        mMeFragment = MeFragment.create();

        mFragmentList = new Fragment[]{mHeartFragment, mPregnantZoneFragment, mAssistFragment, mMeFragment};

        // 加入fragment,显示爱听贝tab
        getSupportFragmentManager().beginTransaction()
                .add(R.id.main_layout, mHeartFragment)
                .add(R.id.main_layout, mMeFragment)
                .add(R.id.main_layout, mPregnantZoneFragment)
                .add(R.id.main_layout, mAssistFragment)
                .show(mHeartFragment)
                .hide(mMeFragment)
                .hide(mPregnantZoneFragment)
                .hide(mAssistFragment)
                .commit();

        switchToFragment(HEART_TAB);

    }

    private View.OnClickListener mTabOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            int index;
            if (view == mHeartPageTab) {
                index = HEART_TAB;
            } else if (view == mWatchTab) {
                index = WATCH_TAB;
            } else if (view == mAssistTab) {
                index = ASSIST_TAB;
            } else {
                index = ME_TAB;
            }
            if (index != mCurrTab) {
                switchToFragment(index);
            }

        }
    };

    /**
     * 跳转到指定的fragment
     *
     * @param index 需要跳转fragment的index
     */
    private void switchToFragment(int index) {
        Logger.i("switchToView() index = %d", index);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count > 0) {
            getSupportFragmentManager().popBackStackImmediate();
        }
        transaction.hide(mFragmentList[mCurrTab])
                .show(mFragmentList[index])
                .commit();
        changeIndicatorByIndex(index);
    }

    /**
     * 改变底部tab图片和文字颜色
     *
     * @param index 选中的tab的index
     */
    private void changeIndicatorByIndex(int index) {
        switch (index) {
            case HEART_TAB:
                mHeartPageTab.setSelected(true);
                mMeTab.setSelected(false);
                mAssistTab.setSelected(false);
                mWatchTab.setSelected(false);
                mCurrTab = HEART_TAB;
                break;
            case WATCH_TAB:
                mHeartPageTab.setSelected(false);
                mMeTab.setSelected(false);
                mAssistTab.setSelected(false);
                mWatchTab.setSelected(true);
                mCurrTab = WATCH_TAB;
                break;
            case ASSIST_TAB:
                mHeartPageTab.setSelected(false);
                mMeTab.setSelected(false);
                mAssistTab.setSelected(true);
                mWatchTab.setSelected(false);
                mCurrTab = ASSIST_TAB;
                break;
            case ME_TAB:
                mHeartPageTab.setSelected(false);
                mMeTab.setSelected(true);
                mAssistTab.setSelected(false);
                mWatchTab.setSelected(false);
                mCurrTab = ME_TAB;
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        RecorderHelper.getInstance().cancel();
    }

    private void checkNetStatus() {
        updateNetErrorNoticeBar();
        RxBus.getDefault().registerOnActivity(BaseAndroidCommand.class, this)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<BaseAndroidCommand>() {
                    @Override
                    public void call(BaseAndroidCommand baseAndroidCommand) {
                        if (baseAndroidCommand instanceof NetStateChangedCommand) {
                            updateNetErrorNoticeBar();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Logger.d("BaseAndroidCommand failed.", throwable);
                    }
                });
        RxBus.getDefault().registerOnActivity(PickedCategoryCommand.class, this)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<PickedCategoryCommand>() {
                    @Override
                    public void call(PickedCategoryCommand pickedCategoryCommand) {
                        int type = pickedCategoryCommand.getAction();
                        Logger.i("type is %d", type);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Logger.e(throwable, "error");
                    }
                });
    }

    private void updateNetErrorNoticeBar() {
        mNetErrorNoticeBar.setVisibility(NetworkHelper.isDisconnected(NewMainActivity.this) ? View.VISIBLE : View.GONE);
        findViewById(R.id.divider_line).setVisibility(NetworkHelper.isDisconnected(NewMainActivity.this) ? View.VISIBLE : View.GONE);
    }

    private void addStatusBarView() {
        View view = new View(this);
        view.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                getStatusBarHeight(this));
        ViewGroup decorView = (ViewGroup) findViewById(android.R.id.content);
        decorView.addView(view, params);
    }

    public int getStatusBarHeight(Context context) {
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }


}
