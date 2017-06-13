package cn.com.ursus.crocodile.dynamicpermissiondemo;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;
import com.yanzhenjie.permission.SettingService;

import java.util.List;

public class AddPermissionActivity extends AppCompatActivity implements View.OnClickListener {

    Activity activity;
    List<String> permissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_permission);
        activity = this;
        findViewById(R.id.btn_call).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        AndPermission.with(this)
                .requestCode(100)
                .permission(
                        Manifest.permission.CALL_PHONE,
                        Manifest.permission.SEND_SMS
                )
                .rationale(new RationaleListener() {
                    @Override
                    public void showRequestPermissionRationale(int i, final Rationale rationale) {
                        new AlertDialog.Builder(AddPermissionActivity.this)
                                .setTitle("申请权限")
                                .setMessage("我需要拥有xxx权限，去完成...")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        rationale.resume();
                                    }
                                })
                                .show();
                    }
                })
                .callback(new PermissionListener() {
                    @Override
                    public void onSucceed(int i, @NonNull List<String> list) {
                        callPhone();
                    }

                    @Override
                    public void onFailed(int i, @NonNull List<String> list) {
                        Toast.makeText(AddPermissionActivity.this, "您已拒绝了拨打电话的权限，我们无法为您执行该功能", Toast.LENGTH_SHORT).show();
                        permissions = list;
                        if (AndPermission.hasAlwaysDeniedPermission(activity, permissions)) {
                            final SettingService settingService = AndPermission.defineSettingDialog(activity, 400);
                            new AlertDialog.Builder(activity)
                                    .setTitle("申请被拒绝")
                                    .setMessage("我们需要......")
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            settingService.execute();
                                        }
                                    })
                                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            settingService.cancel();
                                        }
                                    })
                                    .show();
                        }
                    }
                })
                .start();
    }

    public void callPhone() {
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + "000000");
        intent.setData(data);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 400: {
                if (permissions != null && AndPermission.hasPermission(activity, permissions)) {
                    callPhone();
                }
                break;
            }
        }
    }
}
