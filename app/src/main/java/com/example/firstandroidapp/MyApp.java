package com.example.firstandroidapp;

import android.app.Application;
import com.google.firebase.FirebaseApp;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Khởi tạo Firebase chỉ một lần duy nhất
        FirebaseApp.initializeApp(this);
    }
}
