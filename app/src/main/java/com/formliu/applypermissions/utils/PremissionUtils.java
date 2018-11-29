package com.formliu.applypermissions.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.Fragment;

import com.formliu.applypermissions.annotation.PermissionFail;
import com.formliu.applypermissions.annotation.PermissionSuccess;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

final public class PremissionUtils {
    private PremissionUtils(){}

    public static boolean isOverMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     *将拒绝授权添加集合中
     * @param activity
     * @param permission
     * @return
     */
    @TargetApi(value = Build.VERSION_CODES.M)
    public static List<String> onDeniedPermissions(Activity activity, String... permission){
        List<String> denyPermissions = new ArrayList<>();
        for(String value : permission){
            //PackageManager.PERMISSION_DENIED--->表示权限被否认了
            if(activity.checkSelfPermission(value) != PackageManager.PERMISSION_GRANTED){
                denyPermissions.add(value);
            }
        }
        return denyPermissions;
    }

    public static List<Method> findAnnotationMethods(Class clazz, Class<? extends Annotation> clazz1){
        List<Method> methods = new ArrayList<>();
        for(Method method : clazz.getDeclaredMethods()){
            if(method.isAnnotationPresent(clazz1)){
                methods.add(method);
            }
        }
        return methods;
    }

    public static <T extends Annotation> Method findMethodPermissionFailWithRequestCode(Class clazz,
                                                                                        Class<T> permissionFailClass, int requestCode) {
        for(Method method : clazz.getDeclaredMethods()){
            if(method.isAnnotationPresent(permissionFailClass)){
                if(requestCode == method.getAnnotation(PermissionFail.class).requestCode()){
                    return method;
                }
            }
        }
        return null;
    }

    public static boolean isEqualRequestCodeFromAnntation(Method m, Class clazz, int requestCode){
        if(clazz.equals(PermissionFail.class)){
            return requestCode == m.getAnnotation(PermissionFail.class).requestCode();
        } else if(clazz.equals(PermissionSuccess.class)){
            return requestCode == m.getAnnotation(PermissionSuccess.class).requestCode();
        } else {
            return false;
        }
    }

    public static <T extends Annotation> Method findMethodWithRequestCode(Class clazz,
                                                                          Class<T> annotation, int requestCode) {
        for(Method method : clazz.getDeclaredMethods()){
            if(method.isAnnotationPresent(annotation)){
                if(isEqualRequestCodeFromAnntation(method, annotation, requestCode)){
                    return method;
                }
            }
        }
        return null;
    }

    /**
     *用请求代码查找方法许可成功
     * @param clazz
     * @param permissionFailClass
     * @param requestCode
     * @param <T>
     * @return
     */
    public static <T extends Annotation> Method findMethodPermissionSuccessWithRequestCode(Class clazz,
                                                                                           Class<T> permissionFailClass, int requestCode) {
        for(Method method : clazz.getDeclaredMethods()){
            if(method.isAnnotationPresent(permissionFailClass)){
                if(requestCode == method.getAnnotation(PermissionSuccess.class).requestCode()){
                    return method;
                }
            }
        }
        return null;
    }

    public static Activity getActivity(Object object){
        if(object instanceof Fragment){
            return ((Fragment)object).getActivity();
        } else if(object instanceof Activity){
            return (Activity) object;
        }
        return null;
    }
}
