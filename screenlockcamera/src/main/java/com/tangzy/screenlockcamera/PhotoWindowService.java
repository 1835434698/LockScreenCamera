package com.tangzy.screenlockcamera;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class PhotoWindowService extends Service {
    private static final String TAG = "DemoActivity";
   private MyPhotoWindowManager myWindowManager;
    myServiceBinder binder = new myServiceBinder();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG,"onBind");
        init();
        return binder;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG,"onStartCommand");
        init();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG,"onCreate");
    }

    private void init() {
        myWindowManager = new MyPhotoWindowManager();
        createWindow();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    private void createWindow() {
        // 当前界面是桌面，且没有悬浮窗显示，则创建悬浮窗。
        myWindowManager.removeSmallWindow(getApplicationContext());
        myWindowManager.createSmallWindow(getApplicationContext());

    }

    public class myServiceBinder extends Binder {
        public void startCarema() {
            myWindowManager.startCarema();
        }

        public void stopCarema() {
            myWindowManager.stopCarema();
        }
    }

}
