package com.example.firstandroidapp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HomeFragment extends Fragment {

    private RecyclerView rvParticipatedCategories;
    private UserCategoryAdapter userCategoryAdapter;
    private TextView tvSeeMore, tvCount;
    private boolean isExpanded = false;
    static final int EDIT_PROFILE_REQUEST_CODE = 1; // Mã yêu cầu để nhận kết quả từ EditProfileActivity

    private static final Map<String, Integer> typeIconMap = new HashMap<>();
    static {
        typeIconMap.put("Tình nguyện", R.drawable.ic_category_volunteer);
        typeIconMap.put("Học tập", R.drawable.ic_category_academic);
        typeIconMap.put("Học thuật", R.drawable.ic_category_academic);
        typeIconMap.put("Thể thao", R.drawable.ic_category_sport);
        typeIconMap.put("Âm nhạc", R.drawable.ic_category_music);
//        Muốn thêm thì viết vào đây
    }

    private android.app.ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate layout của fragment
        View view = inflater.inflate(R.layout.activity_main, container, false);

        // Ánh xạ ImageView cho menu popup
        ImageView ivMenu = view.findViewById(R.id.ivMenu);
        ivMenu.setOnClickListener(v -> showPopupMenu(v));

        // Ánh xạ Button cho bảng xếp hạng
        Button btnRanking = view.findViewById(R.id.btnRanking);
        btnRanking.setOnClickListener(v -> {
            // Khi nhấn nút, chuyển sang RatingActivity
            Intent intent = new Intent(getContext(), RatingActivity.class);
            startActivity(intent);
        });

//        Hiển thị danh mục hoạt động
        rvParticipatedCategories = view.findViewById(R.id.rvParticipatedCategories);
        rvParticipatedCategories.setLayoutManager(new GridLayoutManager(getContext(), 2));
        userCategoryAdapter = new UserCategoryAdapter(getContext());
        rvParticipatedCategories.setAdapter(userCategoryAdapter);

        tvCount = view.findViewById(R.id.countactivity);

        tvSeeMore = view.findViewById(R.id.tvSeeMore);
        tvSeeMore.setOnClickListener(v -> {
            isExpanded = !isExpanded;
            userCategoryAdapter.setExpanded(isExpanded);
            tvSeeMore.setText(isExpanded ? "Thu gọn" : "Xem thêm");

            if (isExpanded) {
                rvParticipatedCategories.post(() -> {
                    rvParticipatedCategories.smoothScrollToPosition(userCategoryAdapter.getItemCount() - 1);
                });
            }
        });

        loadParticipatedCategoriesFromFirebase();

        progressDialog = new android.app.ProgressDialog(getContext());
        progressDialog.setMessage("Đang đăng xuất...");
        progressDialog.setCancelable(false);

        return view;
    }
    private void loadParticipatedCategoriesFromFirebase() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("history").child(userId);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Set<String> uniqueTypes = new LinkedHashSet<>();

                for (DataSnapshot snap : snapshot.getChildren()) {
                    HistoryModel model = snap.getValue(HistoryModel.class);
                    if (model != null && model.getType() != null) {
                        uniqueTypes.add(model.getType());
                    }
                }

                List<CategoryModel> categoryList = new ArrayList<>();
                for (String type : uniqueTypes) {
                    int icon = getIconForType(type);
                    categoryList.add(new CategoryModel(type, icon));
                }

                userCategoryAdapter.setData(categoryList);
                tvSeeMore.setVisibility(categoryList.size() > 4 ? View.VISIBLE : View.GONE);

                if (tvCount != null) {
                    long activityCount = snapshot.getChildrenCount();
                    tvCount.setText("Bạn đã tham gia " + activityCount + " hoạt động");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Tuỳ chọn: xử lý lỗi nếu cần
            }
        });
    }

    private int getIconForType(String type) {
        if (type == null) return R.drawable.ic_default_icon;
        return typeIconMap.getOrDefault(type.toLowerCase(), R.drawable.ic_default_icon);
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

        // Sự kiện chỉnh sửa hồ sơ
        LinearLayout editProfileLayout = popupView.findViewById(R.id.menu_edit_profile);
        if (editProfileLayout != null) {
            editProfileLayout.setOnClickListener(v -> {
                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    // Nếu người dùng chưa đăng nhập, chuyển hướng đến màn hình đăng nhập
                    Toast.makeText(getContext(), "Bạn chưa đăng nhập. Vui lòng đăng nhập trước.", Toast.LENGTH_SHORT).show();
                    Intent loginIntent = new Intent(getContext(), LoginActivity.class);
                    startActivity(loginIntent);
                } else {
                    // Nếu người dùng đã đăng nhập, mở màn hình chỉnh sửa hồ sơ
                    Intent intent = new Intent(getContext(), EditProfileActivity.class);
                    startActivityForResult(intent, EDIT_PROFILE_REQUEST_CODE); // Mở EditProfileActivity để sửa thông tin
                    popupWindow.dismiss();  // Đóng menu sau khi chọn
                }
            });
        }
    }

    // Nhận kết quả trả về từ EditProfileActivity sau khi người dùng lưu thông tin
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EDIT_PROFILE_REQUEST_CODE && resultCode == getActivity().RESULT_OK) {
            // Lấy dữ liệu sửa đổi từ EditProfileActivity
            String updatedUserName = data.getStringExtra("updatedUserName");
            String updatedStudentId = data.getStringExtra("updatedStudentId");
            String updatedClass = data.getStringExtra("updatedClass");
            String updatedDepartment = data.getStringExtra("updatedDepartment");
            String updatedPhoneNumber = data.getStringExtra("updatedPhoneNumber");

            // Cập nhật thông tin người dùng trong HomeFragment (ví dụ, cập nhật TextViews)
            updateUserInfo(updatedUserName, updatedStudentId, updatedClass, updatedDepartment, updatedPhoneNumber);
        }
    }

    // Cập nhật thông tin người dùng trong HomeFragment
    private void updateUserInfo(String updatedUserName, String updatedStudentId, String updatedClass,
                                String updatedDepartment, String updatedPhoneNumber) {
        // Ánh xạ TextViews và kiểm tra null trước khi cập nhật
        TextView tvUserName = getView().findViewById(R.id.tvUserName);
        TextView tvStudentId = getView().findViewById(R.id.tvStudentId);
        TextView tvClass = getView().findViewById(R.id.tvClass);
        TextView tvDepartment = getView().findViewById(R.id.tvDepartment);
        TextView tvPhoneNumber = getView().findViewById(R.id.tvPhone);

        if (tvUserName != null) {
            tvUserName.setText(updatedUserName);
        }
        if (tvStudentId != null) {
            tvStudentId.setText(updatedStudentId);
        }
        if (tvClass != null) {
            tvClass.setText(updatedClass);
        }
        if (tvDepartment != null) {
            tvDepartment.setText(updatedDepartment);
        }
        if (tvPhoneNumber != null) {
            tvPhoneNumber.setText(updatedPhoneNumber);
        }

        Toast.makeText(getContext(), "Thông tin đã được cập nhật!", Toast.LENGTH_SHORT).show();
    }
}
