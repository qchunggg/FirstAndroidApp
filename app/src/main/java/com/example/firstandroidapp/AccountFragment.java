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

    private TextView tvUserName;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user, container, false);

        // Kiểm tra xem người dùng có đăng nhập hay không
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail();  // Lấy email thay vì UID

            // Tiến hành tải dữ liệu người dùng từ Firebase bằng email
            DatabaseReference profileRef = FirebaseDatabase.getInstance().getReference("profile")
                    .child(email.replace(".", ","));  // Firebase không cho phép dấu chấm trong key, thay đổi thành dấu phẩy

            profileRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Cập nhật UI với thông tin người dùng
                    User user = task.getResult().getValue(User.class);
                    if (user != null) {
                        tvUserName = view.findViewById(R.id.tvUserName);
                        tvUserName.setText(user.getUserName());
                    }
                } else {
                    // Xử lý lỗi nếu không thể tải dữ liệu
                    Log.e("AccountFragment", "Error loading user data.");
                }
            });
        } else {
            // Nếu không có người dùng đăng nhập, yêu cầu đăng nhập
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish(); // Đóng activity hiện tại
        }

        return view;
    }
}
