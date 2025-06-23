package com.example.firstandroidapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class SettingManageActivity extends AppCompatActivity {

    private android.app.ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);

        LinearLayout itemLogout = findViewById(R.id.logout);

        progressDialog = new android.app.ProgressDialog(this);
        progressDialog.setMessage("Đang đăng xuất...");
        progressDialog.setCancelable(false);

        itemLogout.setOnClickListener(v -> {
            progressDialog.show();

            itemLogout.postDelayed(() -> {
                FirebaseAuth.getInstance().signOut();

                Intent intent = new Intent(SettingManageActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                progressDialog.dismiss();
                startActivity(intent);
            }, 1300); // 1.5 giây loading
        });
    }
}
