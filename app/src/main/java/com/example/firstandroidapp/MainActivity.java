package com.example.firstandroidapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private FirebaseAuth mAuth;
    private LinearLayout bottomNav;
    private PopupWindow popupWindow;  // Biến để giữ PopupWindow

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navbar); // Gán layout chứa ic_menu

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

        // Ánh xạ ic_menu và thiết lập sự kiện click
        ImageView icMenu = findViewById(R.id.ic_menu);
        icMenu.setOnClickListener(v -> {
            // Kiểm tra xem menu đã được mở chưa, nếu có thì đóng, nếu không thì mở
            if (popupWindow != null && popupWindow.isShowing()) {
                popupWindow.dismiss();  // Đóng menu nếu đang mở
            } else {
                // Hiển thị menu khi nhấn vào ic_menu
                showMenu();
            }
        });
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


    }

    // Phương thức hiển thị menu khi nhấn vào ic_menu
    private void showMenu() {
        // Inflate layout menu từ XML
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View menuView = inflater.inflate(R.layout.nav_menu, null);  // menu_layout là layout bạn đã cung cấp

        // Tạo PopupWindow với layout đã tạo
        popupWindow = new PopupWindow(menuView, 550, LinearLayout.LayoutParams.MATCH_PARENT, true);  // Đã thay đổi chiều rộng thành 350dp
        popupWindow.showAsDropDown(findViewById(R.id.ic_menu));  // Hiển thị PopupWindow ở dưới ic_menu

        // Ánh xạ các mục trong menu và thiết lập sự kiện click
        menuView.findViewById(R.id.menu_edit_profile).setOnClickListener(v -> {
            // Chuyển ngay đến EditProfileActivity mà không cần kiểm tra đăng nhập
            Intent intent = new Intent(MainActivity.this, EditProfileActivity.class);  // Chuyển tới EditProfileActivity
            startActivity(intent);  // Mở EditProfileActivity
            popupWindow.dismiss();  // Đóng menu sau khi click
        });


        menuView.findViewById(R.id.menu_change_password).setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "Đổi mật khẩu", Toast.LENGTH_SHORT).show();
            popupWindow.dismiss();
        });

        menuView.findViewById(R.id.menu_notification_settings).setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "Cài đặt thông báo", Toast.LENGTH_SHORT).show();
            popupWindow.dismiss();
        });

        // **Đăng xuất**: Khi người dùng nhấn vào "Đăng xuất"
        menuView.findViewById(R.id.logout).setOnClickListener(v -> {
            // Đăng xuất người dùng
            mAuth.signOut();

            // Chuyển hướng về màn hình đăng nhập
            Toast.makeText(MainActivity.this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));  // Chuyển đến LoginActivity
            finish();  // Đóng MainActivity sau khi đăng xuất
            popupWindow.dismiss();  // Đóng menu
        });
    }
}
