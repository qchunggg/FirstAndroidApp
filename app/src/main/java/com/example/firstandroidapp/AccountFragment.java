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
        View view = inflater.inflate(R.layout.user, container, false);

        // Ánh xạ các TextView
        tvUserName = view.findViewById(R.id.tvUserName);
        tvStudentId = view.findViewById(R.id.tvStudentId);
        tvClass = view.findViewById(R.id.tvClass);
        tvDepartment = view.findViewById(R.id.tvDepartment);
        tvPhone = view.findViewById(R.id.tvPhone);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();

            DatabaseReference profileRef = FirebaseDatabase.getInstance().getReference("profile").child(uid);
            profileRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    User user = task.getResult().getValue(User.class);
                    if (user != null) {
                        tvUserName.setText(user.getUserName());
                        tvStudentId.setText(user.getStudentId());
                        tvClass.setText(user.getClassName());
                        tvDepartment.setText(user.getDepartment());
                        tvPhone.setText(user.getPhone());
                    } else {
                        tvUserName.setText("Không tìm thấy người dùng");
                    }
                } else {
                    Log.e("AccountFragment", "Lỗi khi tải dữ liệu profile", task.getException());
                }
            });

        } else {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            if (getActivity() != null) getActivity().finish();
        }

        return view;
    }
}
