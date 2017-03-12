package com.lihb.babyvoice.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.lihb.babyvoice.R;
import com.lihb.babyvoice.customview.TitleBar;
import com.lihb.babyvoice.customview.base.BaseFragmentActivity;
import com.lihb.babyvoice.utils.CommonToast;
import com.lihb.babyvoice.utils.NotificationCenter;
import com.lihb.babyvoice.utils.UserProfileChangedNotification;
import com.lihb.babyvoice.utils.camera.PhotoHelper;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by lihb on 2017/3/5.
 */

public class EditGrowUpRecordActivity extends BaseFragmentActivity {


    private static final int AVATAR_WIDTH_HEIGHT = 480;
    private TitleBar mTitleBar;
    private EditText mEditRecordTxt;
    private ImageView mAddPicImg1, mAddPicImg2, mDelImg1, mDelImg2;
    private RelativeLayout mAddPicLayout1,mAddPicLayout2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_grow_up_record);
        initView();
    }


    private void initView() {

        mTitleBar = (TitleBar) findViewById(R.id.title_bar);
        mTitleBar.setLeftOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mTitleBar.setRightOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonToast.showShortToast("save button was clicked.");
            }
        });

        mEditRecordTxt = (EditText) findViewById(R.id.edit_grow_up_content_txt);

        mAddPicImg1 = (ImageView) findViewById(R.id.edit_grow_up_content_img1);
        mAddPicImg2 = (ImageView) findViewById(R.id.edit_grow_up_content_img2);
        mDelImg1 = (ImageView) findViewById(R.id.eliminate_img1);
        mDelImg2 = (ImageView) findViewById(R.id.eliminate_img2);
        mAddPicLayout1 = (RelativeLayout) findViewById(R.id.edit_grow_up_content_rl1);
        mAddPicLayout2 = (RelativeLayout) findViewById(R.id.edit_grow_up_content_rl2);
        mAddPicLayout2.setVisibility(View.GONE);

        mAddPicImg1.setOnClickListener(mOnClickListener);
        mAddPicImg2.setOnClickListener(mOnClickListener);
        mDelImg1.setOnClickListener(mOnClickListener);
        mDelImg2.setOnClickListener(mOnClickListener);

    }

    private int index=1;
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == mAddPicImg1) {
                index = 1;
                selectImageFromAlbum();
                mAddPicLayout2.setVisibility(View.VISIBLE);
            } else if (v == mAddPicImg2) {
                index = 2;
                selectImageFromAlbum();
            } else if (v == mDelImg1) {
                mAddPicImg1.setImageResource(R.mipmap.add_photos);
            } else if (v == mDelImg2) {
                mAddPicImg2.setImageResource(R.mipmap.add_photos);
            }
        }
    };

    private void selectImageFromAlbum() {
        PhotoHelper.create(this)
                .setSourceGallery()
                .setPreferredSize(AVATAR_WIDTH_HEIGHT, AVATAR_WIDTH_HEIGHT)
                .start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PhotoHelper.REQUEST_TAKE_PICTURE && resultCode == Activity.RESULT_OK) {
            String picturePath = data.getStringExtra(PhotoHelper.OUTPUT_PATH);
            updatePhoto(picturePath);
        }
    }

    private void updatePhoto(String picturePath) {
        Logger.e("图片地址是：%s",picturePath);
        File file = new File(picturePath);
        if (file.exists()) {
            NotificationCenter.INSTANCE
                    .getObserver(UserProfileChangedNotification.class).onProfileChanged();
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(picturePath);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            Bitmap bitmap  = BitmapFactory.decodeStream(fis);
            if (index == 1) {
                mAddPicImg1.setImageBitmap(bitmap);
            }else {
                mAddPicImg2.setImageBitmap(bitmap);
            }
        }
    }


}
