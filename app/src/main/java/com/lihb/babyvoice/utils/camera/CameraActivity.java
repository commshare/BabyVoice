package com.lihb.babyvoice.utils.camera;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import com.lihb.babyvoice.R;
import com.lihb.babyvoice.customview.FixedRatioLayout;
import com.lihb.babyvoice.customview.ImageButtonEx;
import com.lihb.babyvoice.customview.base.BaseActivity;
import com.lihb.babyvoice.utils.CommonToast;
import com.lihb.babyvoice.utils.DimensionUtil;
import com.lihb.babyvoice.utils.FileUtils;
import com.lihb.babyvoice.view.cropper.CropImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 提供拍照、闪关灯、相册选择、二次确认、旋转、裁剪功能
 * 支持启动时通过Intent指定横竖屏模式、输入路径、输出路径、照片比例、导出质量
 * 通过 onActivityResult 的 Intent.getData 获得输出图片路径，getIntArrayExtra(OUTPUT_SIZE) 可获得图片尺寸
 * <p/>
 * 请使用 PhotoHelper 调用此 Activity
 * <p/>
 * Created by caijw on 2015/9/12.
 */
public class CameraActivity extends BaseActivity {
    private static final String TAG = "TCamera:CameraActivity";

    private static final int REQUEST_IMAGE_GALLERY = 1;
    private static final String SCHEME_FILE = "file";
    private static final String SCHEME_CONTENT = "content";

    private CameraSurfaceView mCameraSurfaceView;
    private CropImageView mCropImageView;
    private TextView mHorizontalHintText;
    private FixedRatioLayout mPreviewClipLayout;
    private ImageButtonEx cameraFlashBtn;
    private ImageButtonEx mTakePhotoBtn;
    private ImageButtonEx mGalleryBtn;
    private ImageButtonEx mCancelCropBtn;
    private ImageButtonEx mConfirmCropBtn;
    private ImageButtonEx mRotateBtn;
    private SourceMode mSourceMode = SourceMode.CameraGallery;
    private int mQuality = 70;
    private boolean mPortrait = true;
    private boolean mFrontCamera = false;
    private int mWidthRatio = 1;
    private int mHeightRatio = 1;
    private int mPreferableWidth = 9999;
    private int mPreferableHeight = 9999;
    private File mSourceFile = null;
    private File mOutputFile = null;

    private static File getFromMediaUri(ContentResolver resolver, Uri uri) {
        if (uri == null) return null;

        if (SCHEME_FILE.equals(uri.getScheme())) {
            return new File(uri.getPath());
        } else if (SCHEME_CONTENT.equals(uri.getScheme())) {
            final String[] filePathColumn = {MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DISPLAY_NAME};
            Cursor cursor = null;
            try {
                cursor = resolver.query(uri, filePathColumn, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    final int columnIndex = (uri.toString().startsWith("content://com.google.android.gallery3d")) ?
                            cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME) :
                            cursor.getColumnIndex(MediaStore.MediaColumns.DATA);
                    // Picasa image on newer devices with Honeycomb and up
                    if (columnIndex != -1) {
                        String filePath = cursor.getString(columnIndex);
                        if (!TextUtils.isEmpty(filePath)) {
                            return new File(filePath);
                        }
                    }
                }
            } catch (SecurityException ignored) {
                // Nothing we can do
            } finally {
                if (cursor != null) cursor.close();
            }
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "Activity onCreate : " + this);

        super.onCreate(savedInstanceState);
        // 初始化界面
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 按要求设置屏幕方向
        mPortrait = getIntent().getBooleanExtra(PhotoHelper.PORTRAIT, true);
        if (mPortrait) {
            Log.i(TAG, "Set screen orientation to portrait");
            if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_PORTRAIT) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        } else {
            Log.i(TAG, "Set screen orientation to landscape");
            if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        }

        mFrontCamera = getIntent().getBooleanExtra(PhotoHelper.FRONT_CAMERA, false);
        Log.i(TAG, String.format("Set use front camera: %b", mFrontCamera));

        // 获得图片采集比例
        int[] ratio = getIntent().getIntArrayExtra(PhotoHelper.OUTPUT_RATIO);
        if (ratio != null && ratio.length > 1) {
            mWidthRatio = ratio[0];
            mHeightRatio = ratio[1];
        }
        Log.i(TAG, String.format("Set picture ratio to width = %d, height = %d", mWidthRatio, mHeightRatio));

        // 获得最佳图片尺寸
        int[] preferredSize = getIntent().getIntArrayExtra(PhotoHelper.PREFERRED_SIZE);
        if (preferredSize != null && preferredSize.length > 1) {
            mPreferableWidth = preferredSize[0];
            mPreferableHeight = preferredSize[1];
        }
        Log.i(TAG, String.format("Set preferred size to width = %d, height = %d", mPreferableWidth, mPreferableHeight));

        // 导出图像质量
        mQuality = getIntent().getIntExtra(PhotoHelper.OUTPUT_QUALITY, 70);
        if (mQuality <= 0 || mQuality >= 100) {
            mQuality = 70;
        }
        Log.i(TAG, String.format("Set picture quality to %d", mQuality));

        // 获得原始图片来源
        String sourcePath = getIntent().getStringExtra(PhotoHelper.SOURCE_PATH);
        if (!TextUtils.isEmpty(sourcePath)) {
            if (sourcePath.equals(PhotoHelper.SOURCE_OF_GALLERY)) {
                // 从系统相册选择
                mSourceMode = SourceMode.Gallery;
                Log.i(TAG, "Set source from gallery");
            } else {
                // 指定文件
                mSourceMode = SourceMode.CropOnly;
                mSourceFile = new File(sourcePath);
                Log.i(TAG, String.format("Set source file=%s", sourcePath));
            }
        }

        // 获得图像输出路径
        String outputPath = getIntent().getStringExtra(PhotoHelper.OUTPUT_PATH);
        if (!TextUtils.isEmpty(outputPath)) {
            mOutputFile = new File(outputPath);
            Log.i(TAG, String.format("Set output file=%s", outputPath));
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        float screenRatio = (float) Math.round(DimensionUtil.getScreenRatio(this) * 100) / 100;
        float clipRatio = (float) (Math.round((float) mWidthRatio * 100 / mHeightRatio)) / 100;
        setContentView(mPortrait ? R.layout.camera_activity_phone_portrait : R.layout.camera_activity_phone_landscape);
//        if (screenRatio > clipRatio) {
//            setContentView(mPortrait ? R.layout.camera_activity_phone_portrait : R.layout.camera_activity_phone_landscape);
//        } else {
//            setContentView(mPortrait ? R.layout.camera_activity_pad_portrait : R.layout.camera_activity_pad_landscape);
//        }

        initViews();

        if (mSourceMode == SourceMode.CropOnly) {
            switchToCropMode();
        } else if (mSourceMode == SourceMode.Gallery) {
            switchToCropMode();
        } else if (mSourceMode == SourceMode.CameraGallery) {
            switchToCameraMode();
        }
        openDelayed();
    }

    private void openDelayed() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isUiActive()) {
                    return;
                }
                if (mSourceMode == SourceMode.Gallery) {
                    openGallery();
                } else if (mSourceMode == SourceMode.CropOnly) {
                    setCropImage(mSourceFile.getAbsolutePath());
                }
            }
        },500);
    }

    public boolean isPortraitMode() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    private void initViews() {
        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "on back button clicked");
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        mPreviewClipLayout = (FixedRatioLayout) findViewById(R.id.previewClipLayout);
        mPreviewClipLayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                mCameraSurfaceView.setClipRect(new Rect(left, top, right, bottom));
            }
        });

        mCameraSurfaceView = (CameraSurfaceView) findViewById(R.id.cameraSurfaceView);
        mCameraSurfaceView.setFrontCamera(mFrontCamera);
        mCameraSurfaceView.setOnRotationListener(new CameraSurfaceView.OnRotationListener() {
            @Override
            public void onRotate(int newRotation, int oldRotation) {
                toggleOrientationHint(
                        (isPortraitMode() && newRotation != Surface.ROTATION_0)
                                || (!isPortraitMode() && newRotation != Surface.ROTATION_270),
                        isPortraitMode());
            }
        });

        mHorizontalHintText = (TextView) findViewById(R.id.orientationHintText);

        mCropImageView = (CropImageView) findViewById(R.id.cropImageView);
        mCropImageView.setGuidelines(2);    // no guide lines
        mCropImageView.setFixedAspectRatio(true);
        if (mWidthRatio > 0 && mHeightRatio > 0) {
            mPreviewClipLayout.setRatio(mWidthRatio, mHeightRatio);
        } else {
            mWidthRatio = mPreviewClipLayout.getWidthRatio();
            mHeightRatio = mPreviewClipLayout.getHeightRatio();
        }
        mCropImageView.setAspectRatio(mWidthRatio, mHeightRatio);
        mCropImageView.setPreferredSize(mPreferableWidth, mPreferableHeight);
        Log.i(TAG, String.format("Set aspect ratio widthRatio = %d, heightRatio = %d", mWidthRatio, mHeightRatio));

        cameraFlashBtn = (ImageButtonEx) findViewById(R.id.cameraFlashBtn);
        cameraFlashBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "on flash button clicked");
                if ((cameraFlashBtn.isChecked() && mCameraSurfaceView.setFlashMode(CameraHelper.FlashMode.FLASH_OFF))
                        || (!cameraFlashBtn.isChecked() && mCameraSurfaceView.setFlashMode(CameraHelper.FlashMode.FLASH_TORCH))) {
                    cameraFlashBtn.setChecked(!cameraFlashBtn.isChecked());
                } else {
                    CommonToast.showShortToast(R.string.flash_unsupported);
                }
            }
        });

        mTakePhotoBtn = (ImageButtonEx) findViewById(R.id.shutterBtn);
        mTakePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "on shutter button clicked");
                setCameraButtonsEnabled(false); // 禁用拍照控制按钮，避免连击
                mCameraSurfaceView.takePicture(new CameraSurfaceView.TakePictureCallback() {
                    @Override
                    public void onSuccess(Bitmap bitmap) {
                        Log.v(TAG, "take picture step 4: on onSuccess");
                        switchToCropMode();
                        setCropImage(bitmap);
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e(TAG, "onError", e);
                        CommonToast.showExtendLongToast(R.string.take_photo_failed);
                        setCameraButtonsEnabled(true);
                    }
                });
            }
        });

        mGalleryBtn = (ImageButtonEx) findViewById(R.id.galleryBtn);
        mGalleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "on gallery button clicked");
                setCameraButtonsEnabled(false);
                openGallery();
            }
        });

        mRotateBtn = (ImageButtonEx) findViewById(R.id.rotateBtn);
        mRotateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "on rotate button clicked");
                mCropImageView.rotateImage(90);
            }
        });

        mCancelCropBtn = (ImageButtonEx) findViewById(R.id.cancelPictureBtn);
        mCancelCropBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "on cancel crop button clicked");
                cancel();
            }
        });

        mConfirmCropBtn = (ImageButtonEx) findViewById(R.id.confirmPictureBtn);
        mConfirmCropBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "on confirm crop button clicked");
                confirm();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 从相册取得照片
        if (REQUEST_IMAGE_GALLERY != requestCode) {
            return;
        }
        Log.i(TAG, "Selected from gallery, resultCode: " + resultCode);

        if (RESULT_OK != resultCode) {
            if (mSourceMode == SourceMode.Gallery) {
                cancel();
            } else if (mSourceMode == SourceMode.CameraGallery) {
                switchToCameraMode();
            }
            return;
        }
        switchToCropMode();
        Uri uri = data.getData();
        try {
            final File file = getFromMediaUri(getContentResolver(), uri);
            if (file != null) {
                setCropImage(file.getAbsolutePath());
                Log.i(TAG, "Selected picture file: " + file.getAbsoluteFile());
            } else {
                Log.e(TAG, "Selected failure.");
            }
        } catch (Throwable e) {
            Log.e(TAG, "Selected exception ", e);
        }
    }

    /**
     * 禁用控制按钮
     *
     * @param enabled
     */
    private void setCameraButtonsEnabled(boolean enabled) {
        cameraFlashBtn.setEnabled(enabled);
        mTakePhotoBtn.setEnabled(enabled);
        mGalleryBtn.setEnabled(enabled);
    }

    /**
     * 将裁剪后的Bitmap保存为jpg图片
     *
     * @param bitmap
     * @param file
     * @return
     */
    private boolean saveBitmap2File(Bitmap bitmap, File file) {
        if (file == null) {
            return false;
        }

        try {
            FileOutputStream fos = new FileOutputStream(file);
            boolean saveSuccess = bitmap.compress(Bitmap.CompressFormat.JPEG, mQuality, fos);
            fos.flush();
            fos.close();
            return saveSuccess;
        } catch (IOException e) {
            Log.e(TAG, "save picture error", e);
            return false;
        }
    }

    /**
     * 切换到拍照视图
     */
    private void switchToCameraMode() {
        if (mSourceMode == SourceMode.CameraGallery) {
            Log.i(TAG, "Switch to camera mode");
            toggleViews(true);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 拍照模式时屏幕常亮
        }
    }

    /**
     * 切换到裁剪视图
     */
    private void switchToCropMode() {
        Log.i(TAG, "Switch to crop mode");
        toggleViews(false);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mCropImageView.setCropEnabled(true);
    }

    private void toggleViews(boolean camera) {
        mCropImageView.setVisibility(camera ? View.INVISIBLE : View.VISIBLE);
        mCameraSurfaceView.setVisibility(camera ? View.VISIBLE : View.INVISIBLE);
        mTakePhotoBtn.setVisibility(camera ? View.VISIBLE : View.INVISIBLE);
        cameraFlashBtn.setVisibility(camera ? View.VISIBLE : View.GONE);
        mGalleryBtn.setVisibility(camera ? View.VISIBLE : View.GONE);
        mCancelCropBtn.setVisibility(camera ? View.GONE : View.VISIBLE);
        mConfirmCropBtn.setVisibility(camera ? View.GONE : View.VISIBLE);
        mRotateBtn.setVisibility(camera ? View.GONE : View.VISIBLE);
        setCameraButtonsEnabled(camera);
    }

    private void toggleOrientationHint(boolean visible, boolean portrait) {
        if (visible != mHorizontalHintText.isShown()) {
            if (visible) {
                mHorizontalHintText.clearAnimation();
                mHorizontalHintText.setVisibility(View.VISIBLE);
                mHorizontalHintText.setText(portrait ? R.string.please_keep_portrait : R.string.please_keep_landscape);
            } else {
                // 消失动画
                AlphaAnimation hideAnimation = new AlphaAnimation(1.0f, 0.0f);
                hideAnimation.setDuration(600);
                hideAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        mHorizontalHintText.setVisibility(View.INVISIBLE);
                        mHorizontalHintText.clearAnimation();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
                mHorizontalHintText.startAnimation(hideAnimation);
            }
        }
    }

    @Override
    public void onBackPressed() {
        Log.i(TAG, "onBackPressed");
        cancel();
    }

    private void openGallery() {
        Log.i(TAG, "openGallery");
//        if (ExternalStorageHelper.isWritable()) {
        if (FileUtils.isSdcardExit()) {
            try {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_IMAGE_GALLERY);
            } catch (Throwable e) {
                CommonToast.showShortToast(R.string.no_gallery);
            }
        } else {
//            EnvTestHelper.permissionWarning(this,EnvTestHelper.kNoPicture);
        }
    }

    private void setCropImage(String file) {
        onLoadImage(mCropImageView.setImageFile(file));
    }

    private void setCropImage(Bitmap bitmap) {
        onLoadImage(mCropImageView.setImageBitmap(bitmap));
    }

    private void onLoadImage(boolean success) {
        if (success) {
            mConfirmCropBtn.setEnabled(true);
            mRotateBtn.setEnabled(true);
        } else {
            mConfirmCropBtn.setEnabled(false);
            mRotateBtn.setEnabled(false);
            CommonToast.showShortToast(R.string.invalid_image);
        }
    }

    private void cancel() {
        if (mCropImageView.isShown() && mSourceMode == SourceMode.CameraGallery) {
            // Crop mode
            switchToCameraMode();
        } else {
            Log.i(TAG, "Cancel and finish");
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    private void confirm() {
        Bitmap croppedBitmap = mCropImageView.getCroppedImage();
        if (croppedBitmap == null) {
            CommonToast.showShortToast(R.string.invalid_image);
            Log.e(TAG, "Crop image failed");
            return;
        }

        boolean autoCreateOutput = false; // 标记图片文件是否自动创建的，可能需要删除
        if (mOutputFile == null) {
            // 若无外部传入，则自动创建图片
            mOutputFile = PhotoHelper.newPictureFile(null);
            if (mOutputFile != null) {
                autoCreateOutput = true;
                Log.i(TAG, String.format("Create output file=%s", mOutputFile.getAbsoluteFile()));
            } else {
                CommonToast.showLongToast(R.string.photo_save_failed);
                Log.e(TAG, "Create output file failed");
                return;
            }
        }

        if (saveBitmap2File(croppedBitmap, mOutputFile)) {
            Log.i(TAG, String.format("Save picture to [%s] success, size width = %d, height = %d",
                    mOutputFile.getAbsoluteFile(), croppedBitmap.getWidth(), croppedBitmap.getHeight()));

            // 通过Intent返回数据
            Intent resultIntent = new Intent();
            resultIntent.setData(Uri.fromFile(mOutputFile));
            resultIntent.putExtra(PhotoHelper.OUTPUT_PATH, mOutputFile.getAbsolutePath());
            resultIntent.putExtra(PhotoHelper.OUTPUT_SIZE, new int[]{croppedBitmap.getWidth(), croppedBitmap.getHeight()});
            setResult(RESULT_OK, resultIntent);
            finish();
        } else {
            Log.e(TAG, String.format("Save picture to %s failed", mOutputFile.getAbsoluteFile()));
            if (autoCreateOutput) {
                mOutputFile.delete();
            }
        }
    }


    enum SourceMode {
        CropOnly,
        Gallery,
        CameraGallery
    }
}
