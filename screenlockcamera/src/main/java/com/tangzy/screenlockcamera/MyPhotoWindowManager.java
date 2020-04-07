package com.tangzy.screenlockcamera;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.FrameLayout;

public class MyPhotoWindowManager {

    private Context mContext;
    private PhotoWindowSmallView smallWindow;
    private WindowManager.LayoutParams smallWindowParams;
    private FrameLayout mSurfaceview;

    /**
     * 创建一个小悬浮窗。初始位置为屏幕的右部中间位置。
     *
     * @param context 必须为应用程序的Context.
     */
    public void createSmallWindow(Context context) {
        mContext = context;
        WindowManager windowManager = getWindowManager(context);
        int screenWidth = windowManager.getDefaultDisplay().getWidth();
        int screenHeight = windowManager.getDefaultDisplay().getHeight();
        if (smallWindow == null) {
            smallWindow = new PhotoWindowSmallView(context);
            if (smallWindowParams == null) {
                smallWindowParams = new WindowManager.LayoutParams();
                smallWindowParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
                smallWindowParams.format = PixelFormat.RGBA_8888;
                smallWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                smallWindowParams.gravity = Gravity.LEFT | Gravity.TOP;
                smallWindowParams.width = PhotoWindowSmallView.viewWidth;
                smallWindowParams.height = PhotoWindowSmallView.viewHeight;
                smallWindowParams.x = screenWidth;
                smallWindowParams.y = screenHeight / 2;
            }
            smallWindow.setParams(smallWindowParams);
            windowManager.addView(smallWindow, smallWindowParams);

            mSurfaceview = (FrameLayout) smallWindow.findViewById(R.id.percent);

        }
    }

    private WindowManager getWindowManager(Context context) {
        return (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
    }

    /**
     * 将小悬浮窗从屏幕上移除。
     *
     * @param context 必须为应用程序的Context.
     */
    public void removeSmallWindow(Context context) {
        if (smallWindow != null) {
            WindowManager windowManager = getWindowManager(context);
            windowManager.removeView(smallWindow);
            smallWindow = null;
        }
    }
    private InitTimetoTakePic itt;
    public void startCarema() {
        itt = InitTimetoTakePic.getInstance(mContext);
        itt.initView(mSurfaceview);
        itt.start();
    }

    public void stopCarema() {
        if (itt != null)
            itt.releaseCarema();
    }

}
