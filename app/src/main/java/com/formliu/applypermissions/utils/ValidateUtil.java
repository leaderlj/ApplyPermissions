package com.formliu.applypermissions.utils;

import android.content.pm.PackageManager;

public class ValidateUtil {
    /**
     * 验证成功授权的
     * @param grantResults
     * @return true 授权成功
     */
    public static boolean verifySuccess(int... grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}
