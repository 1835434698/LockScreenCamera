package com.tangzy.screenlockcamera;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

public class PhotoWindowSmallView extends LinearLayout {
    private WindowManager windowManager;
    public static int viewWidth;
    public static int viewHeight;
    private WindowManager.LayoutParams mParams;

    public PhotoWindowSmallView(Context context) {
        super(context);
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater.from(context).inflate(R.layout.float_window_small, this);
        View view = findViewById(R.id.small_window_layout);
        viewWidth = view.getLayoutParams().width;
        viewHeight = view.getLayoutParams().height;
//        SurfaceView percentView = (SurfaceView) findViewById(R.id.percent);
//        percentView.setText(MyWindowManager.getUsedPercentValue(context));
    }



    /**
     * 将小悬浮窗的参数传入，用于更新小悬浮窗的位置。
     *
     * @param params 小悬浮窗的参数
     */
    public void setParams(WindowManager.LayoutParams params) {
        mParams = params;
    }


}
