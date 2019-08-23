package cn.ld.permissionlib.listener;

/**
 * Created by ld on 2019/8/22.
 */
public interface PermissionRequest {
    //拒绝之后是否还需要去执行
    void proceed();
}
