package cn.com.ursus.crocodile.dynamicpermissiondemo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;


@RuntimePermissions
public class PermissionDispatchActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission_dispatch);
        findViewById(R.id.btn_call).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        PermissionDispatchActivityPermissionsDispatcher.callPhoneWithCheck(this);
    }

    @NeedsPermission(Manifest.permission.CALL_PHONE)
    public void callPhone() {
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + "000000");
        intent.setData(data);
        startActivity(intent);
    }

    @OnShowRationale(Manifest.permission.CALL_PHONE)
    public void showRationaleCallPhone(final PermissionRequest request){
        new AlertDialog.Builder(this)
                .setTitle("申请权限")
                .setMessage("我需要拥有xxx权限，去完成...")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.proceed();
                    }
                })
                .show();

    }

    @OnPermissionDenied(Manifest.permission.CALL_PHONE)
    public void deniedCallPhone(){
        Toast.makeText(this, "您已拒绝了拨打电话的权限，我们无法为您执行该功能", Toast.LENGTH_SHORT).show();
    }

    @OnNeverAskAgain(Manifest.permission.CALL_PHONE)
    public void notAskAgainCallPhone(){
        Toast.makeText(this, "不再询问", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionDispatchActivityPermissionsDispatcher.onRequestPermissionsResult(this,requestCode,grantResults);
    }

    public static void launch(Context context) {
        Intent intent = new Intent(context, PermissionDispatchActivity.class);
        if (!(context instanceof Activity)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }
}
