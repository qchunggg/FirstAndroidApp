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

    private TextView tvUserName, tvStudentId, tvClass, tvDepartment, tvPhone;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate layout của Fragment (gọi đến file XML)
        View view = inflater.inflate(R.layout.user, container, false);

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

        return view;
    }
}
