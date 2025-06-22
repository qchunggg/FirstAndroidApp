package com.example.firstandroidapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.firstandroidapp.R;

public class DialogHandle extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Đảm bảo gọi đúng layout

        // Nếu bạn muốn thêm sự kiện click cho nút trong Java
        Button btnExplore = findViewById(R.id.btnExplore);
        btnExplore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Thực thi hành động khi nhấn nút (nếu bạn muốn làm thêm ngoài phương thức onExploreClick)
                showExploreDialog();
            }
        });
    }

    // Phương thức được gọi khi nhấn nút từ XML (android:onClick)
    public void onExploreClick(View view) {
        // Xử lý khi nhấn nút "Khám Phá"
        Toast.makeText(this, "Nút Khám Phá đã được nhấn", Toast.LENGTH_SHORT).show();
    }

    // Ví dụ về một phương thức khác để thực thi hành động, chẳng hạn là hiển thị Dialog
    private void showExploreDialog() {
        // Thực thi hành động khác nếu cần
    }
}
