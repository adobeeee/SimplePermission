package cn.ld.permissionlib;

import android.app.Activity;
import android.support.annotation.NonNull;

import cn.ld.permissionlib.listener.RequestPermission;

/**
 * Created by ld on 2019/8/22.
 * 此为管理类请求权限的管理类
 */

public class PermissionManager {

    /**
     * @author ld
     * created at 2019/8/23:15:50
     * 在需要请求权限的类中进行调用，会在生成此类的文件中进行回调处理，换句话说，就是传值（需要的权限）到生成的类中进行判断处理
     */
    public static void request(Activity activity,String[] permissions){
        String className=activity.getClass().getName()+"$$Permissions";
        try {
            Class<?> clazz = Class.forName(className);
            RequestPermission rPermission= (RequestPermission) clazz.newInstance();
            rPermission.requestPermission(activity,permissions);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @author ld
     * created at 2019/8/23:15:54
     * 当请求权限之后，会再activity中进行回调，此方法写入回调方法中，在生成的类中进行执行
     */
    public static void onRequestPermissionResult(Activity activity, int requestCode, @NonNull int[] grantResults){
        String className=activity.getClass().getName()+"$$Permissions";
        try {
            Class<?> clazz=Class.forName(className);
            RequestPermission rPermission= (RequestPermission) clazz.newInstance();
            rPermission.onRequestPermissionResult(activity,requestCode,grantResults);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}
