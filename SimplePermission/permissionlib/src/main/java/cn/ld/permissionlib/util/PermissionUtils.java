package cn.ld.permissionlib.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by ld on 2019/8/22.
 * 工具类不能被继承，也不能被new
 */

public final class PermissionUtils {

    private PermissionUtils(){}

    /**
     * @author ld
     * created at 2019/8/22:15:17
     * 检查所有权限是否已允许
     */
    public static boolean verifyPermissions(int... grantResult){
        if (grantResult.length==0){
            return false;
        }
        for (int result : grantResult) {
            if (result!= PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }

    /**
     * @author ld
     * created at 2019/8/22:15:20
     * 检查用户是否拥有此权限
     */
    public static boolean hasSelfPermissions(Context context,String... permissions){
        for (String permission : permissions) {
            if (!hasSelfPermission(context,permission)){
                return false;
            }
        }
        return true;
    }


    /**
     * @author ld
     * created at 2019/8/22:15:23
     * 用户申请权限
     */
    private static boolean hasSelfPermission(Context context,String permission){
        if (Build.VERSION.SDK_INT< Build.VERSION_CODES.M){
            return true;
        }
        return ContextCompat.checkSelfPermission(context,permission)==PackageManager.PERMISSION_GRANTED;
    }



    /**
     * @author ld
     * created at 2019/8/22:15:29
     * 检查被拒绝的权限组中，是否有点击了“不再询问”权限
     * 第一次打开App是返回false
     * 上次弹出权限点击了拒绝，并未点击不再询问  true
     * 上次弹出权限点击了拒绝，并点击了不再询问  false
     * 点击拒绝，未勾选不再询问 true
     * 点击拒绝，勾选了不再询问 false
     *
     * @Param permissions 被拒绝的权限组
     * @return 如果全部都是false，则false；有任意一个“不再询问”的权限返回true ，那么返回true
     */
    public static boolean shouldShowRequestPermissionRationale(Activity activity,String... permissions){
        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,permission)){
                return true;
            }
        }
        return false;
    }
}
