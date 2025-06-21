package com.example.firstandroidapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AccountFragment extends Fragment {

    private TextView tvUserName, tvStudentId, tvClass, tvDepartment, tvPoints, tvPhone, tvSemesterRank, tvYearRank;
    private String userId;

    public AccountFragment() {
        // Required empty public constructor
    }

    // Factory method để truyền userId vào Fragment
    public static AccountFragment newInstance(String userId) {
        AccountFragment fragment = new AccountFragment();
        Bundle args = new Bundle();
        args.putString("USER_ID", userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Lấy userId từ arguments
        if (getArguments() != null) {
            userId = getArguments().getString("USER_ID");
        }

        // Nếu không có userId thì hardcode để test
        if (userId == null || userId.trim().isEmpty()) {
            userId = "userId123"; // <-- đúng với Firebase bạn đang dùng
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user, container, false);

        // Ánh xạ các view
        tvUserName = view.findViewById(R.id.tvUserName);
        tvStudentId = view.findViewById(R.id.tvStudentId);
        tvClass = view.findViewById(R.id.tvClass);
        tvDepartment = view.findViewById(R.id.tvDepartment);
        tvPoints = view.findViewById(R.id.tvPoints);
        tvPhone = view.findViewById(R.id.tvPhone);
        tvSemesterRank = view.findViewById(R.id.tvSemesterRank);
        tvYearRank = view.findViewById(R.id.tvYearRank);

        // Tải dữ liệu từ Firebase
        loadUserData();

        return view;
    }

    private void loadUserData() {
        // 1. Lấy thông tin học tập từ "profile/userId"
        DatabaseReference profileRef = FirebaseDatabase.getInstance().getReference("profile").child(userId);
        profileRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        tvStudentId.setText("Mã sinh viên: " + user.getStudentId());
                        tvClass.setText(user.getClassName());
                        tvDepartment.setText(user.getDepartment());
                        tvPoints.setText(user.getAccumulatedPoints());
                        tvPhone.setText(user.getPhoneNumber());
                        tvSemesterRank.setText(user.getSemesterRank());
                        tvYearRank.setText(user.getYearRank());
                    }
                } else {
                    Log.w("AccountFragment", "Không tìm thấy profile với UID: " + userId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("AccountFragment", "Lỗi khi đọc profile", error.toException());
            }
        });

        // 2. Lấy tên từ "users/userId"
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    if (name != null) {
                        tvUserName.setText(name);
                    }
                } else {
                    Log.w("AccountFragment", "Không tìm thấy user với UID: " + userId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("AccountFragment", "Lỗi khi đọc user name", error.toException());
            }
        });
    }
}
