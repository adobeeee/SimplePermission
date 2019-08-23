package cn.ld.permissionlib.listener;

import android.support.annotation.Nullable;

/**
 * Created by ld on 2019/8/22.
 */
public interface RequestPermission<T> {
    //请求权限组
    void requestPermission(T target,String[] permissions);

    //授权结果返回
    void onRequestPermissionResult(T target, int requestCode, @Nullable int[] grantResults);

}
