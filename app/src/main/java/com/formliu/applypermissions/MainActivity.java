package com.formliu.applypermissions;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.formliu.applypermissions.annotation.PermissionFail;
import com.formliu.applypermissions.annotation.PermissionSuccess;
import com.formliu.applypermissions.other.PermissionListener;

import java.util.List;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
      findViewById(R.id.call).setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
                // initRequesPermission();
              initApplyPermissions();

          }
      });
    }

/**
 * 方式一
 */
private  void initRequesPermission(){
    requestRunTimePermission(new String[]{Manifest.permission.WRITE_CALENDAR,
                       Manifest.permission.CAMERA,
                       Manifest.permission.GET_ACCOUNTS,
                       Manifest.permission.ACCESS_FINE_LOCATION,
                       Manifest.permission.RECORD_AUDIO,
                       Manifest.permission.CALL_PHONE,
                       Manifest.permission.BODY_SENSORS,
                       Manifest.permission.SEND_SMS,
                       Manifest.permission.WRITE_EXTERNAL_STORAGE}, new PermissionListener(){
                   @Override
                   public void onGranted() {
                       onSuccess();
                   }

                   @Override
                   public void onGranted(List<String> grantedPermission) {
                       Toast.makeText(MainActivity.this,"部分授权成功",Toast.LENGTH_SHORT).show();
                   }

                   @Override
                   public void onDenied(List<String> deniedPermission) {
                       onFail();
                   }
               });
}

    /**
     * 方式二
     */
    private void initApplyPermissions(){
    ApplyPermissions.with(MainActivity.this).addRequestCode(10).permissions(Manifest.permission.CALL_PHONE,Manifest.permission.ACCESS_FINE_LOCATION).request();
   }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        ApplyPermissions.onRequestPermissionsResult(this,requestCode,permissions,grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
     @PermissionSuccess(requestCode=10)
     private void onSuccess(){
         Toast.makeText(MainActivity.this,"授权成功",Toast.LENGTH_SHORT).show();
         Intent intent = new Intent(Intent.ACTION_DIAL);

                       Uri data = Uri.parse("tel:" + "159******72");

                       intent.setData(data);

                       startActivity(intent);
    }
    @PermissionFail(requestCode = 10)
    private  void onFail(){
        Toast.makeText(MainActivity.this,"授权失败",Toast.LENGTH_SHORT).show();
        new AlertDialog.Builder(this)
                .setTitle("")
                .setMessage("获取定位")
                .setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(intent);
                            }
                        })

                .setCancelable(false)
                .show();
    }
}
