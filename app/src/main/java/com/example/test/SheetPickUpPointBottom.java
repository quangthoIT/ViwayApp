package com.example.test;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.adapter.PickUpPointAdapter;
import com.example.test.response.ScheduleResponse;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;


import java.util.ArrayList;

public class SheetPickUpPointBottom extends BottomSheetDialogFragment {

    private ArrayList<ScheduleResponse> pickUpPointList;

    private PickUpPointAdapter.OnPickupPointSelectedListener listener;

    public void setOnPickupPointSelectedListener(PickUpPointAdapter.OnPickupPointSelectedListener listener) {
        this.listener = listener;
    }

    public SheetPickUpPointBottom() {

    }

    public static SheetPickUpPointBottom newInstance(ArrayList<ScheduleResponse> list) {
        SheetPickUpPointBottom fragment = new SheetPickUpPointBottom();
        Bundle args = new Bundle();
        args.putSerializable("pickup_points", list);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.bottom_sheet_pickup_point, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.item_PickUpPoint);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (getArguments() != null) {
            pickUpPointList = (ArrayList<ScheduleResponse>) getArguments().getSerializable("pickup_points");
        }

        PickUpPointAdapter adapter = new PickUpPointAdapter(pickUpPointList, selectedPoint -> {
            Bundle result = new Bundle();
            result.putSerializable("selected_pickup_point", selectedPoint);

            getParentFragmentManager().setFragmentResult("pickup_point_result", result);
            dismiss();
        });


        recyclerView.setAdapter(adapter);

        return view;
    }
}
