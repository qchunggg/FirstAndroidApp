package com.example.firstandroidapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

public class HistoryFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate layout activity_main.xml cho HomeFragment
        View view = inflater.inflate(R.layout.history, container, false);

        // Có thể thêm logic xử lý dữ liệu tại đây
        return view;
    }
}
