package com.example.firstandroidapp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

public class HomeFragment extends Fragment {

    private static final int EDIT_PROFILE_REQUEST_CODE = 1; // Mã yêu cầu để nhận kết quả từ EditProfileActivity

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main, container, false);

        ImageView ivMenu = view.findViewById(R.id.ivMenu);

        ivMenu.setOnClickListener(v -> showPopupMenu(v));

        return view;
    }

    private void showPopupMenu(View anchor) {
        View popupView = LayoutInflater.from(getContext()).inflate(R.layout.nav_menu, null);

        int popupWidth = (int) (getResources().getDisplayMetrics().widthPixels * 0.50);

        PopupWindow popupWindow = new PopupWindow(
                popupView,
                popupWidth,
                ViewGroup.LayoutParams.MATCH_PARENT,
                true
        );

        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);

        // Gán background có hiệu ứng mờ nhẹ
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#80000000")));

        // Animation
        popupWindow.setAnimationStyle(R.style.PopupAnimation);

        // Hiển thị tại cạnh trái
        popupWindow.showAtLocation(anchor.getRootView(), Gravity.START, 0, 0);

        // Sự kiện logout
        LinearLayout logoutLayout = popupView.findViewById(R.id.logout);
        if (logoutLayout != null) {
            logoutLayout.setOnClickListener(v -> {
                // Đăng xuất khỏi Firebase
                FirebaseAuth.getInstance().signOut();

                // Quay về màn đăng nhập, xóa backstack
                Intent intent = new Intent(getContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            });
        }

        // Sự kiện chỉnh sửa hồ sơ
        LinearLayout editProfileLayout = popupView.findViewById(R.id.menu_edit_profile);
        if (editProfileLayout != null) {
            editProfileLayout.setOnClickListener(v -> {
                // Chuyển sang màn hình chỉnh sửa hồ sơ
                Intent intent = new Intent(getContext(), EditProfileActivity.class);
                startActivityForResult(intent, EDIT_PROFILE_REQUEST_CODE); // Mở EditProfileActivity để sửa thông tin
                popupWindow.dismiss();  // Đóng menu sau khi chọn
            });
        }
    }

    // Nhận kết quả trả về từ EditProfileActivity sau khi người dùng lưu thông tin
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EDIT_PROFILE_REQUEST_CODE && resultCode == getActivity().RESULT_OK) {
            // Lấy dữ liệu sửa đổi từ EditProfileActivity
            String updatedClass = data.getStringExtra("updatedClass");
            String updatedDepartment = data.getStringExtra("updatedDepartment");
            String updatedPhoneNumber = data.getStringExtra("updatedPhoneNumber");


            // Cập nhật thông tin người dùng trong HomeFragment (ví dụ, cập nhật TextViews)
            updateUserInfo(updatedClass, updatedDepartment, updatedPhoneNumber);
        }
    }

    // Cập nhật thông tin người dùng trong HomeFragment
    private void updateUserInfo(String updatedClass, String updatedDepartment, String updatedPhoneNumber
                               ) {
        // Ánh xạ TextViews và cập nhật thông tin
        TextView tvClass = getView().findViewById(R.id.tvClass);
        TextView tvDepartment = getView().findViewById(R.id.tvDepartment);
        TextView tvPhoneNumber = getView().findViewById(R.id.tvPhone);


        tvClass.setText(updatedClass);
        tvDepartment.setText(updatedDepartment);
        tvPhoneNumber.setText(updatedPhoneNumber);


        Toast.makeText(getContext(), "Thông tin đã được cập nhật!", Toast.LENGTH_SHORT).show();
    }
}
