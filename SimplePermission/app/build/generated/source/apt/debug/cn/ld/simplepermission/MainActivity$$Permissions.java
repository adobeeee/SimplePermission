package cn.ld.simplepermission;

import cn.ld.permissionlib.listener.RequestPermission;
import cn.ld.permissionlib.listener.PermissionRequest;
import cn.ld.permissionlib.listener.PermissionSetting;
import cn.ld.permissionlib.util.PermissionUtils;

import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.ActivityCompat;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;
import android.support.annotation.NonNull;

import java.lang.ref.WeakReference;

public class MainActivity$$Permissions implements RequestPermission<cn.ld.simplepermission.MainActivity> {
    private static final int REQUEST_PERMISSION_CODE = 666;
    private static String[] NEEDS_PERMISSION;

    public void requestPermission(cn.ld.simplepermission.MainActivity target, String[] permissions) {
        NEEDS_PERMISSION = permissions;
        if (PermissionUtils.hasSelfPermissions(target, NEEDS_PERMISSION)) {
            target.showCamera();
            target.showStorage();
        } else if (PermissionUtils.shouldShowRequestPermissionRationale(target, NEEDS_PERMISSION)) {
            target.showRationaleForCamera(new PermissionRequestImpl(target));
        } else {
            ActivityCompat.requestPermissions(target, NEEDS_PERMISSION, REQUEST_PERMISSION_CODE);
        }
    }

    public void onRequestPermissionResult(cn.ld.simplepermission.MainActivity target, int requestCode, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_CODE:
                if (PermissionUtils.verifyPermissions(grantResults)) {
                    target.showCamera();
                    target.showStorage();
                } else if (!PermissionUtils.shouldShowRequestPermissionRationale(target, NEEDS_PERMISSION)) {
                    target.showNeverAskForCamera(new PermissionSettingImpl(target));
                } else {
                    target.showDeniedForCamera();
                }
                break;
            default:
                break;
        }
    }

    private static final class PermissionRequestImpl implements PermissionRequest {
        private final WeakReference<cn.ld.simplepermission.MainActivity> weakTarget;

        private PermissionRequestImpl(cn.ld.simplepermission.MainActivity target) {
            this.weakTarget = new WeakReference(target);
        }

        public void proceed() {
            cn.ld.simplepermission.MainActivity target = (cn.ld.simplepermission.MainActivity) this.weakTarget.get();
            if (target != null) {
                ActivityCompat.requestPermissions(target, NEEDS_PERMISSION, REQUEST_PERMISSION_CODE);
            }
        }
    }

    private static final class PermissionSettingImpl implements PermissionSetting {
        private final WeakReference<cn.ld.simplepermission.MainActivity> weakTarget;

        private PermissionSettingImpl(cn.ld.simplepermission.MainActivity target) {
            this.weakTarget = new WeakReference(target);
        }

        public void setting(int settingCode) {
            cn.ld.simplepermission.MainActivity target = (cn.ld.simplepermission.MainActivity) this.weakTarget.get();
            if (target != null) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", target.getPackageName(), null);
                intent.setData(uri);
                target.startActivityForResult(intent, settingCode);
            }
        }
    }

}