package com.lihb.babyvoice.utils.camera;

import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;

import com.lihb.babyvoice.utils.StringUtils;

import java.util.List;

/**
 * Created by huqiuyun on 16/9/2.
 */

@SuppressWarnings("deprecation")
public class CameraHelper {
    private static final String TAG = "TApp:CameraHelper";

    private Camera camera;

    private FlashMode flashMode = FlashMode.FLASH_OFF;
    private boolean frontCamera = false;

    public enum FlashMode {
        FLASH_ON, FLASH_TORCH, FLASH_OFF, FLASH_AUTO
    }

    public boolean isFrontCamera() {
        return frontCamera;
    }

    /**
     * 设置是否使用前置摄像头
     *
     * @param frontCamera 是否使用前置摄像头
     */
    public void setFrontCamera(boolean frontCamera) {
        this.frontCamera = frontCamera;
    }

    public Camera getCamera() {
        return camera;
    }

    public int cameraId() {
        int id = -1;
        int numberOfCameras = Camera.getNumberOfCameras();
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (isFrontCamera()) {
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    Log.i(TAG, "Use front camera id = " + i);
                    id = i;
                }
            } else if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                id = i;
                Log.i(TAG, "Use back camera id = " + i);
            }
        }

        if (id == -1 && numberOfCameras > 0) {
            id = 0;
            Log.i(TAG, "Use default camera id = 0");
        }
        return id;
    }

    public final static int kOk = 0;
    public final static int kCameraIdInvalid = 1;
    public final static int kCameraException = 2;
    public final static int kCameraNull = 3;

    /**
     *
     * @param holder
     * @return @see kCameraIdInvalid
     */
    public int init(SurfaceHolder holder) {

        // 获得后置摄像头ID
        int id = cameraId();
        if (id >= 0) {
            try {
                camera = Camera.open(id);
                camera.setPreviewDisplay(holder);
            } catch (Exception e) {
                Log.e(TAG, "Exception on open camera", e);
                camera = null;
                return kCameraException;
            }
        } else {
            return kCameraIdInvalid;
        }
        return camera != null ? kOk : kCameraNull;
    }

    public void release() {
        try {
            if (camera != null) {
                stop();
                camera.release();
                camera = null;
            }
        } catch (Exception e) {
            Log.e(TAG,"release exception: ", e);
        }
    }

    public void start() {
        if (camera != null) {
            camera.startPreview();
        }
    }

    public void stop() {
        if (camera != null) {
            camera.stopPreview();
        }
    }

    public boolean setFlashMode(FlashMode mode) {

        if (camera != null) {

            String modeName = "";
            switch (mode) {
                case FLASH_ON:
                    modeName = "on";
                    break;
                case FLASH_TORCH:
                    modeName = "torch";
                    break;
                case FLASH_OFF:
                    modeName = "off";
                    break;
                case FLASH_AUTO:
                    modeName = "auto";
                    break;
                default:
                    break;
            }

            try {

                final Camera.Parameters parameters = camera.getParameters();
                if (!isSupportFlashMode(parameters,modeName)) {
                    Log.e(TAG, "not support flash mode: " + modeName);
                    return false;
                }

                switch (mode) {
                    case FLASH_TORCH:
                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);    // 常亮
                        break;
                    case FLASH_OFF:
                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                        break;
                    case FLASH_AUTO:
                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                        break;
                    default:
                        break;
                }
                Log.i(TAG, "set flash mode to " + parameters.getFlashMode());

                camera.setParameters(parameters);
                flashMode = mode;
                return true;

            } catch (Exception e) {
                Log.e(TAG, "Set flash mode failed", e);
                return false;
            }
        }
        return false;
    }

    public FlashMode getFlashMode() {
        return flashMode;
    }

    private boolean isSupportFlashMode(final Camera.Parameters parameters,final String mode) {
        final List<String> supportedList = parameters.getSupportedFlashModes();
        if (supportedList == null) {
            return false;
        }
        for (String m : supportedList) {
            if (StringUtils.areEqual(m, mode)) {
                return true;
            }
        }
        return false;
    }

    public void setDisplayOrientation(int degrees) {
        try {
            if (camera != null) {
                camera.setDisplayOrientation(degrees);
            }
        } catch (Exception e) {
            Log.e(TAG, "setDisplayOrientation exception: ", e);
        }
    }

    public boolean isCamera() {
        return (camera != null);
    }
}
