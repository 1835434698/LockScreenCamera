# LockScreenCamera
锁屏下静默拍照

锁屏情况下仍然可以拍照。
可以通过注册锁屏广播开启拍照服务。
一定要授权
                //启动Activity让用户授权
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
还有相机权限、sd卡权限等动态权限。