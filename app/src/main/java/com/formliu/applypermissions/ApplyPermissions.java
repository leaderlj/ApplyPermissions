package com.formliu.applypermissions;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.Fragment;

import com.formliu.applypermissions.annotation.PermissionFail;
import com.formliu.applypermissions.annotation.PermissionSuccess;
import com.formliu.applypermissions.utils.PremissionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.formliu.applypermissions.utils.PremissionUtils.getActivity;


public class ApplyPermissions {
  private String[] mPermissions;
  private int mRequestCode;
  private Object object;

  private ApplyPermissions(Object object) {
    this.object = object;
  }

  public static ApplyPermissions with(Context context){
    return new ApplyPermissions(context);
  }

  public static ApplyPermissions with(Fragment fragment){
    return new ApplyPermissions(fragment);
  }
  public ApplyPermissions permissions(String... permissions){
    this.mPermissions = permissions;
    return this;
  }

  public ApplyPermissions addRequestCode(int requestCode){
    this.mRequestCode = requestCode;
    return this;
  }

  @TargetApi(value = Build.VERSION_CODES.M)
  public void request(){
    requestPermissions(object, mRequestCode, mPermissions);
  }


  /**
   *请求权限
   * @param object
   * @param requestCode
   * @param permissions
   */
  @TargetApi(value = Build.VERSION_CODES.M)
  private static void requestPermissions(Object object, int requestCode, String[] permissions){
    //小于23
    if(!PremissionUtils.isOverMarshmallow()) {
      doExecuteSuccess(object, requestCode);
      return;
    }

    List<String> deniedPermissions = PremissionUtils.onDeniedPermissions(getActivity(object), permissions);

    if(deniedPermissions.size() > 0){
      if(object instanceof Activity){
        ((Activity)object).requestPermissions(deniedPermissions.toArray(new String[deniedPermissions.size()]), requestCode);
      } else if(object instanceof Fragment){
        ((Fragment)object).requestPermissions(deniedPermissions.toArray(new String[deniedPermissions.size()]), requestCode);
      }

    } else {
      doExecuteSuccess(object, requestCode);
    }
  }

  /**
   * 执行成功
   * @param activity 上下文
   * @param requestCode 请求码
   */
  private static void doExecuteSuccess(Object activity, int requestCode) {
    Method executeMethod = PremissionUtils.findMethodWithRequestCode(activity.getClass(),
        PermissionSuccess.class, requestCode);

    executeMethod(activity, executeMethod);
  }

  /**
   * 执行失败
   * @param activity 上下文
   * @param requestCode 请求码
   */
  private static void doExecuteFail(Object activity, int requestCode) {
    Method executeMethod = PremissionUtils.findMethodWithRequestCode(activity.getClass(),
        PermissionFail.class, requestCode);

    executeMethod(activity, executeMethod);
  }

  /**
   *  授权成功（失败）都将执行此方法
   * @param activity
   * @param executeMethod
   */
  private static void executeMethod(Object activity, Method executeMethod) {
    if(executeMethod != null){
      try {
        if(!executeMethod.isAccessible()) executeMethod.setAccessible(true);
        executeMethod.invoke(activity, null);
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      } catch (InvocationTargetException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * TODO activity 申请结果
   * @param activity 上下文
   * @param requestCode 请求码
   * @param permissions 申请需要的权限
   * @param grantResults 授权结果
   */
  public static void onRequestPermissionsResult(Activity activity, int requestCode, String[] permissions,
                                                int[] grantResults) {
    onRequestResult(activity, requestCode, permissions, grantResults);
  }

  /**
   *TODO fragment页面
   * @param fragment
   * @param requestCode
   * @param permissions
   * @param grantResults
   */
  public static void onRequestPermissionsResult(Fragment fragment, int requestCode, String[] permissions,
                                                int[] grantResults) {
    onRequestResult(fragment, requestCode, permissions, grantResults);
  }

  /**
   *请求结果
   * @param obj
   * @param requestCode
   * @param permissions
   * @param grantResults
   */
  private static void onRequestResult(Object obj, int requestCode, String[] permissions,
                                    int[] grantResults){
    List<String> deniedPermissions = new ArrayList<>();
    for(int i=0; i<grantResults.length; i++){
      if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
        //添加拒绝授权集合中
        deniedPermissions.add(permissions[i]);
      }
    }

    if(deniedPermissions.size() > 0){
      doExecuteFail(obj, requestCode);
    } else {
      doExecuteSuccess(obj, requestCode);
    }
  }


  /**
   * 需要权限
   * @param activity
   * @param requestCode
   * @param permissions
   */
  public static void needPermission(Activity activity, int requestCode, String[] permissions){
    requestPermissions(activity, requestCode, permissions);
  }

  public static void needPermission(Fragment fragment, int requestCode, String[] permissions){
    requestPermissions(fragment, requestCode, permissions);
  }

  public static void needPermission(Activity activity, int requestCode, String permission){
    needPermission(activity, requestCode, new String[] { permission });
  }

  public static void needPermission(Fragment fragment, int requestCode, String permission){
    needPermission(fragment, requestCode, new String[] { permission });
  }

}
