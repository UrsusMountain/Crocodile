package cn.com.ursus.crocodile.dynamicpermissiondemo;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int CALL_PHONE_REQUEST_CODE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_call).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        boolean b = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE);
        Toast.makeText(this, String.valueOf(b) , Toast.LENGTH_SHORT).show();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            //没有 CALL_PHONE 权限
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.CALL_PHONE)){
                new AlertDialog.Builder(this)
                        .setTitle("申请权限")
                        .setMessage("我需要拥有xxx权限，去完成...")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CALL_PHONE},CALL_PHONE_REQUEST_CODE);
                            }
                        })
                        .show();
            }else{
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CALL_PHONE},CALL_PHONE_REQUEST_CODE);
            }

        } else {
            //拥有 CALL_PHONE 权限
            callPhone();
        }
    }

    public void callPhone() {
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + "000000");
        intent.setData(data);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CALL_PHONE_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 申请同意
                    callPhone();
                } else {
                    Toast.makeText(this, "您已拒绝了拨打电话的权限，我们无法为您执行该功能", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }
}
