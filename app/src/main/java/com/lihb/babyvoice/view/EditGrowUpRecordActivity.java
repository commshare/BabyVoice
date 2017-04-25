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

import com.lihb.babyvoice.BabyVoiceApp;
import com.lihb.babyvoice.R;
import com.lihb.babyvoice.action.ApiManager;
import com.lihb.babyvoice.action.ServiceGenerator;
import com.lihb.babyvoice.customview.TitleBar;
import com.lihb.babyvoice.customview.base.BaseFragmentActivity;
import com.lihb.babyvoice.db.impl.GrowUpImpl;
import com.lihb.babyvoice.model.Article;
import com.lihb.babyvoice.model.GrowUpRecord;
import com.lihb.babyvoice.model.HttpResponse;
import com.lihb.babyvoice.utils.NotificationCenter;
import com.lihb.babyvoice.utils.StringUtils;
import com.lihb.babyvoice.utils.UserProfileChangedNotification;
import com.lihb.babyvoice.utils.camera.PhotoHelper;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by lihb on 2017/3/5.
 */

public class EditGrowUpRecordActivity extends BaseFragmentActivity {


    private static final int AVATAR_WIDTH_HEIGHT = 480;
    private TitleBar mTitleBar;
    private EditText mEditRecordTxt;
    private EditText mEditDateTxt;
    private ImageView mAddPicImg1, mAddPicImg2, mDelImg1, mDelImg2;
    private RelativeLayout mAddPicLayout1, mAddPicLayout2;
    private String mPic1, mPic2;

    public enum From {
        PREGNANT_ZONE_FRAGMENT, GROWUP_FRAGMENT
    }

    From from;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_grow_up_record);
        from = (From) getIntent().getSerializableExtra("from");
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
                if (from == From.GROWUP_FRAGMENT) {
                    GrowUpRecord growUpRecord = new GrowUpRecord();
                    growUpRecord.date = mEditDateTxt.getText().toString();
                    growUpRecord.content = mEditRecordTxt.getText().toString();
                    growUpRecord.picList = new ArrayList<String>();
                    growUpRecord.picList.add(mPic1);
                    growUpRecord.picList.add(mPic2);
                    // 保存到数据库
                    insertGrowupRecordItem(growUpRecord);
                    // 保存到服务器
                    uploadGrowUpRecordToServer(growUpRecord);
                    finish();
                } else if (from == From.PREGNANT_ZONE_FRAGMENT) {
                    Article article = new Article();
                    article.realname = BabyVoiceApp.currUserName;
                    article.type = 10000;
                    article.time = System.currentTimeMillis();
                    article.title = mEditDateTxt.getText().toString();
                    article.content = mEditRecordTxt.getText().toString();
                    if (!StringUtils.isEmpty(mPic1)) {
                        String fileName1 = mPic1.substring(mPic1.lastIndexOf("/") + 1);
                        article.attachment = fileName1 +",";
                        if (!StringUtils.isEmpty(mPic2)) {
                            String fileName2 = mPic2.substring(mPic2.lastIndexOf("/") + 1);
                            article.attachment += fileName2;
                        }
                    }
                    // 保存文章到服务器
                    uploadArticleToServer(article);
                    // 保存图片到服务器
                    uploadPicToServer();
                    finish();
                }
            }
        });

        if (from == From.GROWUP_FRAGMENT) {
            mTitleBar.setLeftText(getString(R.string.txt_growup_record));
            mTitleBar.setTitle("");
        }else if (from == From.PREGNANT_ZONE_FRAGMENT) {
            mTitleBar.setLeftText("");
            mTitleBar.setTitle(getString(R.string.i_want_share));
        }

        mEditRecordTxt = (EditText) findViewById(R.id.edit_grow_up_content_txt);
        mEditDateTxt = (EditText) findViewById(R.id.edit_grow_up_title_txt);

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

    /**
     * 保存成长记录数据到服务器
     *
     * @param growUpRecord
     */
    private void uploadGrowUpRecordToServer(final GrowUpRecord growUpRecord) {
        ServiceGenerator.createService(ApiManager.class)
                .createGrowupRecord(growUpRecord.date, growUpRecord.content, BabyVoiceApp.currUserName, growUpRecord.picList.get(0), growUpRecord.picList.get(1))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<HttpResponse<GrowUpRecord>>() {
                    @Override
                    public void call(HttpResponse<GrowUpRecord> growUpRecordHttpResponse) {
                        Logger.i(growUpRecordHttpResponse.toString());
                        if (growUpRecordHttpResponse.code == 0) {
                            Logger.i("add GrowUpRecord success");
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Logger.e("error-->" + throwable.toString());
                    }
                });
    }

    /**
     * 保存孕婴圈文章数据到服务器
     *
     * @param article
     */
    private void uploadArticleToServer(final Article article) {
        ServiceGenerator.createService(ApiManager.class)
                .addPregnantArticle(article.title, article.content, article.realname, article.type, article.attachment)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<HttpResponse<Void>>() {
                    @Override
                    public void call(HttpResponse<Void> voidHttpResponse) {
                        Logger.i(voidHttpResponse.toString());
                        if (voidHttpResponse.code == 0) {
                            Logger.i("add article success");
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Logger.e("error-->" + throwable.toString());
                    }
                });
    }

    private int index = 1;
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
        Logger.e("图片地址是：%s", picturePath);
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

            Bitmap bitmap = BitmapFactory.decodeStream(fis);
            if (index == 1) {
                mAddPicImg1.setImageBitmap(bitmap);
                mPic1 = picturePath;
            } else {
                mAddPicImg2.setImageBitmap(bitmap);
                mPic2 = picturePath;
            }
        }
    }

    private void insertGrowupRecordItem(final GrowUpRecord growUpRecord) {
        GrowUpImpl.getInstance()
                .insertData(growUpRecord)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if (aBoolean) {
                            Logger.i("insert growuprecord success," + growUpRecord.toString());
                        } else {
                            Logger.i("insert growuprecord failed," + growUpRecord.toString());
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Logger.e(throwable.getMessage());
                    }
                });
    }

    /**
     * 上传图片到服务器
     */
    private void uploadPicToServer() {
        List<File> files = new ArrayList<>();
        if (!StringUtils.isBlank(mPic1)) {
            File file1 = new File(mPic1);
            files.add(file1);
        }
        if (!StringUtils.isBlank(mPic2)) {
            File file2 = new File(mPic2);
            files.add(file2);
        }
        if (files.isEmpty()) {
            Logger.i("no pic to upload!!");
            return;
        }
        MultipartBody body = filesToMultipartBody(files);
        ServiceGenerator.createService(ApiManager.class)
                .uploadPicFiles(BabyVoiceApp.currUserName, body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<HttpResponse<Void>>() {
                    @Override
                    public void call(HttpResponse<Void> stringBaseResponse) {
                        Logger.i(stringBaseResponse.msg);
                        if (stringBaseResponse.code == 0) {
                            Logger.i("upload pic success.");
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Logger.e(throwable.getMessage());
                    }
                });
    }


    public static MultipartBody filesToMultipartBody(List<File> files) {
        MultipartBody.Builder builder = new MultipartBody.Builder();

        for (File file : files) {
            RequestBody requestBody = RequestBody.create(MediaType.parse(""), file);
            builder.addFormDataPart("picfile", file.getName(), requestBody);
//            builder.addFormDataPart("fileName", file.getName());
        }
        builder.setType(MultipartBody.FORM);
        MultipartBody multipartBody = builder.build();
        return multipartBody;
    }


}
