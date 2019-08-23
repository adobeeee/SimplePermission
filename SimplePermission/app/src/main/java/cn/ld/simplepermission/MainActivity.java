package cn.ld.simplepermission;

import android.Manifest;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import cn.ld.annotations.NeedsPermission;
import cn.ld.annotations.OnNeverAskAgain;
import cn.ld.annotations.OnPermissionDenied;
import cn.ld.annotations.OnShowRationale;
import cn.ld.permissionlib.PermissionManager;
import cn.ld.permissionlib.listener.PermissionRequest;
import cn.ld.permissionlib.listener.PermissionSetting;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }



    //在需要获取权限的地方注释
    @NeedsPermission()
    void showCamera(){
        Log.e("dada","showCamera()");
    }
    @NeedsPermission()
    void showStorage(){
        Log.e("dada","showStorage()");
    }



    //提示用户为何要开启权限
    @OnShowRationale()
    void showRationaleForCamera(final PermissionRequest request){
        Log.e("dada","showRationaleForCamera()");
        request.proceed();
    }
    //用户选择不再询问后的提示
    @OnNeverAskAgain()
    void showNeverAskForCamera(final PermissionSetting setting){
        Log.e("dada" ,"showNeverAskForCamera()");
        setting.setting(888);
    }

    //用户选择拒绝是的提示
    @OnPermissionDenied()
    void showDeniedForCamera(){
        Log.e("dada","showDeniedForCamera()");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.onRequestPermissionResult(this,requestCode,grantResults);
    }

    /**
     * @author ld
     * Create at 2019/8/23 22:29
     * 请求照相权限
     */
    public void requestCamera(View view) {
        PermissionManager.request(this, new String[]{Manifest.permission.CAMERA});
    }

    /**
     * @author ld
     * Create at 2019/8/23 22:29
     * 请求存储权限
     */
    public void requestStorage(View view) {
        PermissionManager.request(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE});
    }

    /**
     * @author ld
     * Create at 2019/8/23 22:29
     * 请求全部权限
     */
    public void requestAll(View view) {
        PermissionManager.request(this, new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE});
    }
}
