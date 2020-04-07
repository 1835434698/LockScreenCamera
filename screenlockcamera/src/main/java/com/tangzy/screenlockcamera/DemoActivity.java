package com.tangzy.screenlockcamera;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

public class DemoActivity extends AppCompatActivity {
    private static final String TAG = "DemoActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(!Settings.canDrawOverlays(getApplicationContext())) {
                //启动Activity让用户授权
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent,100);
            }
        }


    }
    private boolean isVedio = true;

    public void bindService_(View view) {
        Log.e(TAG,"bindService_");

        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED)){
            Log.e(TAG,"hhhhhhh");
            MyServiceConn conn = new MyServiceConn();
            Intent intent = new Intent(this, PhotoWindowService.class);
            bindService(intent, conn, Context.BIND_AUTO_CREATE);
        }else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    101);
        }


    }
    PhotoWindowService.myServiceBinder binder;
    private class MyServiceConn implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // TODO Auto-generated method stub
            binder = (PhotoWindowService.myServiceBinder) service;
            Log.e(TAG,"onServiceConnected  isVedio = "+isVedio);

            if (isVedio) {
                binder.startCarema();
            } else {
                binder.stopCarema();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
        }

    }

}
