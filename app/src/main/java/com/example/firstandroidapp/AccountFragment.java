package com.example.firstandroidapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AccountFragment extends Fragment {

    private TextView tvUserName, tvClass, tvDepartment, tvPhone;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user, container, false);

        // Ánh xạ các TextView để hiển thị thông tin
        tvUserName = view.findViewById(R.id.tvUserName);
        tvClass = view.findViewById(R.id.tvClass);
        tvDepartment = view.findViewById(R.id.tvDepartment);
        tvPhone = view.findViewById(R.id.tvPhone);

        // Kiểm tra xem người dùng có đăng nhập hay không
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();  // Lấy UID của người dùng

            // Lấy tên người dùng từ FirebaseAuth
            String userName = currentUser.getDisplayName();  // Lấy tên người dùng từ FirebaseAuth
            if (userName != null) {
                tvUserName.setText(userName);  // Hiển thị tên người dùng từ FirebaseAuth
            } else {
                tvUserName.setText("Tên người dùng không có");  // Nếu không có tên, bạn có thể hiển thị thông báo khác
            }

            // Tiến hành tải các thông tin khác từ "profile"
            DatabaseReference profileRef = FirebaseDatabase.getInstance().getReference("profile").child(uid);
            profileRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Lấy thông tin từ "profile"
                    User user = task.getResult().getValue(User.class);  // Sử dụng lớp UserProfile
                    if (user != null) {
                        tvClass.setText(user.getClassName());
                        tvDepartment.setText(user.getDepartment());
                        tvPhone.setText(user.getPhone());
                    }
                } else {
                    Log.e("AccountFragment", "Error loading profile data.");
                }
            });

        } else {
            // Nếu không có người dùng đăng nhập, yêu cầu đăng nhập
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish(); // Đóng Activity hiện tại
        }

        return view;
    }
}
