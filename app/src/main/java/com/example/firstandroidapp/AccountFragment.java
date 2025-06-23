package com.example.firstandroidapp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AccountFragment extends Fragment {

    private TextView tvUserName, tvStudentId, tvClass, tvDepartment, tvPhone;

    private android.app.ProgressDialog progressDialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate layout của Fragment (gọi đến file XML)
        View view = inflater.inflate(R.layout.user, container, false);

        ImageView ivMenu = view.findViewById(R.id.ivMenu);
        ivMenu.setOnClickListener(v -> showPopupMenu(v));

        // Ánh xạ các TextView từ layout XML vào các đối tượng Java
        tvUserName = view.findViewById(R.id.tvUserName);
        tvStudentId = view.findViewById(R.id.tvStudentId);
        tvClass = view.findViewById(R.id.tvClass);
        tvDepartment = view.findViewById(R.id.tvDepartment);
        tvPhone = view.findViewById(R.id.tvPhone);

        // Log ra trạng thái của từng đối tượng để kiểm tra xem có bị null không
        Log.d("AccountFragment", "tvUserName: " + (tvUserName == null ? "null" : "initialized"));
        Log.d("AccountFragment", "tvStudentId: " + (tvStudentId == null ? "null" : "initialized"));
        Log.d("AccountFragment", "tvClass: " + (tvClass == null ? "null" : "initialized"));
        Log.d("AccountFragment", "tvDepartment: " + (tvDepartment == null ? "null" : "initialized"));
        Log.d("AccountFragment", "tvPhone: " + (tvPhone == null ? "null" : "initialized"));

        // Kiểm tra người dùng đã đăng nhập chưa
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // Lấy UID của người dùng đã đăng nhập
            String uid = currentUser.getUid();

            // Lấy dữ liệu người dùng từ Firebase Realtime Database
            DatabaseReference profileRef = FirebaseDatabase.getInstance().getReference("profile").child(uid);
            profileRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Lấy dữ liệu người dùng từ Firebase
                    User user = task.getResult().getValue(User.class);
                    if (user != null) {
                        // Cập nhật UI với dữ liệu người dùng
                        Log.d("AccountFragment", "User data: " + user.getUserName());
                        tvUserName.setText(user.getUserName());
                        tvStudentId.setText(user.getStudentId());
                        tvClass.setText(user.getClassName());
                        tvDepartment.setText(user.getDepartment());
                        tvPhone.setText(user.getPhone());
                    } else {
                        Log.e("AccountFragment", "User data is null");
                        tvUserName.setText("Không tìm thấy người dùng");
                    }
                } else {
                    Log.e("AccountFragment", "Lỗi khi tải dữ liệu profile", task.getException());
                }
            });
        } else {
            // Nếu người dùng chưa đăng nhập, chuyển đến màn hình đăng nhập
            if (getActivity() != null) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        }

        progressDialog = new android.app.ProgressDialog(getContext());
        progressDialog.setMessage("Đang đăng xuất...");
        progressDialog.setCancelable(false);

        return view;
    }

    private void showPopupMenu(View anchor) {
        View popupView = LayoutInflater.from(getContext()).inflate(R.layout.nav_menu, null);

        int popupWidth = (int) (getResources().getDisplayMetrics().widthPixels * 0.5);

        PopupWindow popupWindow = new PopupWindow(
                popupView,
                popupWidth,
                ViewGroup.LayoutParams.MATCH_PARENT,
                true
        );

        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#80000000")));
        popupWindow.setAnimationStyle(R.style.PopupAnimation);

        popupWindow.showAtLocation(anchor.getRootView(), Gravity.START, 0, 0);

        LinearLayout logoutLayout = popupView.findViewById(R.id.logout);
        if (logoutLayout != null) {
            logoutLayout.setOnClickListener(v -> {
                progressDialog.show(); // Hiện hiệu ứng loading

                logoutLayout.postDelayed(() -> {
                    FirebaseAuth.getInstance().signOut();

                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    progressDialog.dismiss(); // Tắt loading
                    startActivity(intent);
                }, 1300); // Loading trong 1.3 giây
            });
        }
    }
}
