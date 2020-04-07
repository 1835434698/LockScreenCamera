package com.tangzy.screenlockcamera;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.FrameLayout;


import java.io.File;
import java.io.FileOutputStream;

/**
 * 设置定时拍照功能
 *
 * @author <p>
 *         创建定时拍照任务
 *         cameraType  摄像头
 *         resolutionString  分辨率
 *         tvSaveLocation 保存地址
 *         etExtension  拓展名
 *         cameraStart, 开始拍摄时间
 *         cameraNumber, 拍摄次数
 *         cameraStop  拍摄张数
 */
public class InitTimetoTakePic {

    private static final String TAG = "InitTimetoTakePic";
    private static InitTimetoTakePic mInstance;
    private static int cameraType = 1;
    Context mContext;
    static FrameLayout mSurfaceViewFrame;
    private static Camera mCamera;
    private static CameraPreview mPreview;
    private static String resolutionString = "1920x1080";
    private static String saveLocation = getSDCardPath();
    private static String extension = "JPG";
    private static String cameraStart = "1";
    private static String cameraNumber = "1";
    private static String cameraStop = "10";
    private static int number = 0;
    private static boolean clearVoice = false;
    private Intent intent;

    private InitTimetoTakePic(Context context) {
        this.mContext = context;
    }

    public synchronized static InitTimetoTakePic getInstance(Context context) {
        mInstance = null;
        mInstance = new InitTimetoTakePic(context);

        return mInstance;
    }

    public void initView(FrameLayout surfaceViewFrame) {
        mSurfaceViewFrame = surfaceViewFrame;
    }

    /**
     * 启动定时拍照并上传功能
     */
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Log.v(TAG,"开始拍照");
                    initCarema();
                    break;
                case 2:
                    if (mCamera == null) {
                        releaseCarema();
                        number = 0;
                        mHandler.removeCallbacksAndMessages(null);
                    } else {
                        if (number < Integer.valueOf(cameraStop)) {
                            mCamera.autoFocus(new AutoFocusCallback() {
                                @Override
                                public void onAutoFocus(boolean success, Camera camera) {
                                    // 从Camera捕获图片
                                    Log.v(TAG,"自动聚焦111" + success);
                                    try {
                                        mCamera.takePicture(null, null, mPicture);
                                        mHandler.sendEmptyMessageDelayed(1, Integer.valueOf(cameraNumber) * 1000);
                                    } catch (Exception e) {
                                        releaseCarema();
                                        mHandler.removeCallbacksAndMessages(null);
                                    }
                                }
                            });
                        } else {
                            releaseCarema();
                            number = 0;
                            mHandler.removeCallbacksAndMessages(null);
                        }
                    }
                    break;
            }
        }
    };

    public void start() {
        mHandler.sendEmptyMessageDelayed(1, 1 * 1000); //7s 后开始启动相机
    }

    private void initCarema() {
        Log.v(TAG,"initCarema");
        if (mCamera == null) {
            Log.v(TAG,"camera=null");
            mCamera = getCameraInstance();
            mPreview = new CameraPreview(mContext, mCamera);
            mSurfaceViewFrame.removeAllViews();
            mSurfaceViewFrame.addView(mPreview);
        }
        Log.v(TAG,mCamera == null ? "mCamera is null" : "mCamera is not null");
        mCamera.startPreview();
        mHandler.sendEmptyMessageDelayed(2, Integer.valueOf(cameraStart) * 1000); //3s后拍照
    }

    /**
     * 检测设备是否存在Camera硬件
     */
    @SuppressLint("UnsupportedChromeOsCameraSystemFeature")
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // 存在
            return true;
        } else {
            // 不存在
            return false;
        }
    }

    /**
     * 打开一个Camera
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(cameraType);
            c.setDisplayOrientation(90);
            Camera.Parameters mParameters = c.getParameters();
            //快门声音
            c.enableShutterSound(clearVoice);
            //可以用得到当前所支持的照片大小，然后
            //List<Size> ms = mParameters.getSupportedPictureSizes();
            //mParameters.setPictureSize(ms.get(0).width, ms.get(0).height);  //默认最大拍照取最大清晰度的照片
            String[] xes = resolutionString.split("x");
            // LogUtils.i("ms.get(0).width==>"+ms.get(0).width);
            // LogUtils.i("ms.get(0).height==>"+ms.get(0).height);
            // LogUtils.i("Integer.valueOf(xes[0])==>"+Integer.valueOf(xes[0]));
            // LogUtils.i("Integer.valueOf(xes[1])==>"+Integer.valueOf(xes[1]));
            mParameters.setPictureSize(Integer.valueOf(xes[0]), Integer.valueOf(xes[1]));  //默认最大拍照取最大清晰度的照片
            c.setParameters(mParameters);
        } catch (Exception e) {
            Log.v(TAG,"打开Camera失败失败");
        }
        return c;
    }

    private PictureCallback mPicture = new PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            // 获取Jpeg图片，并保存在sd卡上
            String path = saveLocation;
            File dirF = new File(path);
            if (!dirF.exists()) {
                dirF.mkdirs();
            }
            File pictureFile = new File(path + "/" + System.currentTimeMillis() + "." + extension);//扩展名
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();

                Log.v(TAG,"保存图成功 pictureFile = "+pictureFile.getAbsolutePath());
                number++;
                intent = new Intent();
                intent.setAction("CameraFragment.start");
                intent.putExtra("number", number);
                mContext.sendBroadcast(intent);
            } catch (Exception e) {
                Log.v(TAG,"保存图片失败");
                e.printStackTrace();
            }
            releaseCarema();
        }
    };

    public void releaseCarema() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }
    /**
     * 得到sdcard的路径
     * @return 返回一个字符串数组  下标0:内置sdcard  下标1:外置sdcard
     */
    public static String getSDCardPath(){
        return Environment.getExternalStorageDirectory()+"/DCIM";
//        String[] sdCardPath=new String[2];
//        File sdFile= Environment.getExternalStorageDirectory();
//        File[] files=sdFile.getParentFile().listFiles();
//        for(File file:files){
//            if(file.getAbsolutePath().equals(sdFile.getAbsolutePath())){//外置
//                sdCardPath[1]=sdFile.getAbsolutePath();
//            }else if(file.getAbsolutePath().contains("sdcard")){//得到内置sdcard
//                sdCardPath[0]=file.getAbsolutePath();
//            }
//        }
//        return sdCardPath[1];
    }

}
