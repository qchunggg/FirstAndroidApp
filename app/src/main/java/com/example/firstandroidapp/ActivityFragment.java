package com.example.firstandroidapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ActivityFragment extends Fragment {

    private RecyclerView recyclerView;
    private ActivityAdapter activityAdapter;
    private ArrayList<ActivityModel> activityList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate layout activity_main.xml cho HomeFragment
        View view = inflater.inflate(R.layout.activities, container, false);

        // Khởi tạo RecyclerView và Adapter
        recyclerView = view.findViewById(R.id.rvActivities);

        // Tạo một danh sách mẫu
        activityList = new ArrayList<>();
        activityList.add(new ActivityModel("Hoạt động 1", "Tình nguyện", "Mô tả ngắn về hoạt động", "20/05", "45/50"));
        activityList.add(new ActivityModel("Hoạt động 2", "Giải trí", "Mô tả ngắn về hoạt động", "21/05", "30/50"));
        // Thêm các hoạt động khác vào danh sách...

        // Gắn Adapter vào RecyclerView
        activityAdapter = new ActivityAdapter(activityList);
        recyclerView.setAdapter(activityAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Trả về view đã được cấu hình
        return view;
    }
}
