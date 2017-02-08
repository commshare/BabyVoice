package com.lihb.babyvoice.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.lihb.babyvoice.R;
import com.lihb.babyvoice.customview.base.BaseFragmentActivity;
import com.lihb.babyvoice.customview.base.SwipeControllableViewPager;
import com.orhanobut.logger.Logger;

public class NewMainActivity extends BaseFragmentActivity {

    private static final String TAG = "NewMainActivity";

    private static final int HEART_TAB = 0;
    private static final int WATCH_TAB = 1;
    private static final int ASSIST_TAB = 2;
    private static final int ME_TAB = 3;
    private static final int TABS_COUNT = 4;

    private Fragment[] mFragmentList = new Fragment[TABS_COUNT];

//    private MeFragment mMeFragment;
//    private WatchFragment mWatchFragment;
//    private HeartFragment mHeartFragment;
//    private AssistFragment mAssistFragment;

    //ui
    private TextView mHeartPageTab;
    private TextView mWatchTab;
    private TextView mAssistTab;
    private TextView mMeTab;
    private SwipeControllableViewPager mViewPager;

    private FragmentPagerAdapter mFragmentPagerAdapter;
    private FragmentManager mFragmentManager;

    private int mCurrTab = HEART_TAB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_new);
        initViews();

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

        mViewPager = (SwipeControllableViewPager) findViewById(R.id.baby_view_pager);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setSwipeEnabled(true);
        mFragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int idx) {
                if (idx < 0 || idx >= TABS_COUNT) {
                    return null;
                }

                Logger.d("mFragViewPager.getItem, idx: %d", idx);

                if (mFragmentList[idx] != null) {
                    return mFragmentList[idx];
                }

                Fragment fragment = null;
                switch (idx) {
                    case HEART_TAB:
                        fragment = HeartFragment.create();
                        break;
                    case WATCH_TAB:
                        fragment = WatchFragment.create();
                        break;
                    case ASSIST_TAB:
                        fragment = AssistFragment.create();
                        break;
                    case ME_TAB:
                        fragment = MeFragment.create();
                        break;
                    default:
                        break;
                }
                mFragmentList[idx] = fragment;
                return fragment;
            }

            @Override
            public int getCount() {
                return TABS_COUNT;
            }

        };
        mViewPager.setAdapter(mFragmentPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                changeIndicatorByIndex(position);
            }
        });
        changeIndicatorByIndex(0);
        mFragmentManager = getSupportFragmentManager();
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
            switchToView(index);

        }
    };

    private void switchToView(int index) {
        Logger.i("switchToView() index = %d", index);
        mViewPager.setCurrentItem(index, false);
        changeIndicatorByIndex(index);
    }

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


}
