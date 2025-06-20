package com.example.firstandroidapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private FirebaseAuth mAuth;
    private LinearLayout bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navbar);

        // Khởi tạo Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Kiểm tra đăng nhập
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return; // Dừng việc thực thi tiếp
        }

        // Ánh xạ LinearLayout bottom navigation
        bottomNav = findViewById(R.id.bottomNavBar);
        if (bottomNav == null) {
            Log.e(TAG, "bottomNavBar not found");
            finish();
            return;
        }

        // Ánh xạ các item trong bottom navigation
        LinearLayout homeLayout = findViewById(R.id.iconHome).getParent() instanceof LinearLayout ?
                (LinearLayout) findViewById(R.id.iconHome).getParent() : null;
        LinearLayout activitesLayout = findViewById(R.id.iconActivities).getParent() instanceof LinearLayout ?
                (LinearLayout) findViewById(R.id.iconActivities).getParent() : null;
        LinearLayout historiesLayout = findViewById(R.id.iconHistories).getParent() instanceof LinearLayout ?
                (LinearLayout) findViewById(R.id.iconHistories).getParent() : null;
        LinearLayout accountsLayout = findViewById(R.id.iconAccounts).getParent() instanceof LinearLayout ?
                (LinearLayout) findViewById(R.id.iconAccounts).getParent() : null;

        // Đặt fragment mặc định là Trang chủ
        loadFragment(new HomeFragment());
        updateSelectedItem(homeLayout);

        // Xử lý sự kiện click cho từng item
        setupClickListener(homeLayout, new HomeFragment());
        setupClickListener(activitesLayout, new ActivityFragment());
        setupClickListener(historiesLayout, new HistoryFragment());
        setupClickListener(accountsLayout, new AccountFragment());
    }

    private void setupClickListener(LinearLayout layout, Fragment fragment) {
        if (layout != null) {
            layout.setOnClickListener(v -> {
                Log.d(TAG, "Item clicked: " + fragment.getClass().getSimpleName());
                loadFragment(fragment);
                updateSelectedItem(layout);
            });
        } else {
            Log.e(TAG, "Layout is null for fragment: " + fragment.getClass().getSimpleName());
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Áp dụng hiệu ứng chuyển tiếp
        transaction.setCustomAnimations(
                android.R.anim.fade_in,   // Hiệu ứng khi fragment mới xuất hiện
                android.R.anim.fade_out   // Hiệu ứng khi fragment cũ biến mất
        );

        View fragmentContainer = findViewById(R.id.fragmentContainer);
        if (fragmentContainer != null) {
            transaction.replace(R.id.fragmentContainer, fragment);
            transaction.addToBackStack(null); // Nếu không muốn backstack, có thể bỏ dòng này
            transaction.commit();
            Log.d(TAG, "Loaded fragment: " + fragment.getClass().getSimpleName());
        } else {
            Log.e(TAG, "fragmentContainer not found");
            finish();
        }
    }

    private void updateSelectedItem(LinearLayout selectedLayout) {
        if (bottomNav == null) return;

        // Đặt tất cả các mục về màu không được chọn
        for (int i = 0; i < bottomNav.getChildCount(); i++) {
            LinearLayout item = (LinearLayout) bottomNav.getChildAt(i);
            TextView textView = (TextView) item.getChildAt(1); // TextView nằm ở vị trí thứ 2 trong mỗi LinearLayout
            if (textView != null) {
                textView.setTextColor(getResources().getColor(R.color.navbar_unselected));
            }
        }

        // Đổi màu cho mục đã được chọn
        if (selectedLayout != null) {
            TextView selectedText = (TextView) selectedLayout.getChildAt(1); // Lấy TextView từ layout đã chọn
            if (selectedText != null) {
                selectedText.setTextColor(getResources().getColor(R.color.navbar_selected));
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Clear session khi app bị tắt từ đa nhiệm
        mAuth.signOut(); // Đăng xuất người dùng
    }
}
