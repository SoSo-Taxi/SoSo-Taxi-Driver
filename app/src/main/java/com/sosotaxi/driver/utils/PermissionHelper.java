package com.sosotaxi.driver.utils;

import android.content.Context;
import android.content.pm.PackageManager;

/**
 * @Author 范承祥
 * @CreateTime 2020/7/17
 * @UpdateTime 2020/7/17
 */
public class PermissionHelper {
    public static boolean hasBaseAuth(Context context, String[] authArray) {
        PackageManager packageManager = context.getPackageManager();
        for (String auth : authArray) {
            if (packageManager.checkPermission(auth, context.getPackageName()) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static boolean hasBaseAuth(Context context, String auth) {
        PackageManager packageManager = context.getPackageManager();
        if (packageManager.checkPermission(auth, context.getPackageName()) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }
}
