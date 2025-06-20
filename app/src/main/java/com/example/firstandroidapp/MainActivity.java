package com.example.firstandroidapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Lấy instance Auth (FirebaseApp đã initialize trong MyApp)
        mAuth = FirebaseAuth.getInstance();

        // Nếu chưa login, chuyển luôn sang LoginActivity
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Nếu đã login, mới show UI chính
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        // … các thiết lập UI khác của MainActivity
    }
}