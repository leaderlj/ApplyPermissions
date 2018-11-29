package com.formliu.applypermissions.other;

import java.util.List;

/**
 * 此方法为获取权限 方式1 调用
 */
public  interface PermissionListener {
    //全部授权成功
    void onGranted();
    /**授权部分*/
    void  onGranted(List<String> grantedPermission);
    /**拒绝授权*/
    void  onDenied(List<String> deniedPermission);


}
