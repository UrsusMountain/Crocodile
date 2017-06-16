package cn.com.ursus.crocodile.dynamicpermissiondemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class WelcomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        findViewById(R.id.btn_default).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.launch(WelcomeActivity.this);
            }
        });

        findViewById(R.id.btn_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddPermissionActivity.launch(WelcomeActivity.this);
            }
        });

        findViewById(R.id.btn_dispatch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionDispatchActivity.launch(WelcomeActivity.this);
            }
        });
    }
}
