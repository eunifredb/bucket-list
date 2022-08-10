package com.example.bucketlist.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bucketlist.R;

import java.util.Calendar;
import java.util.Date;

public class HomeFragment extends Fragment {
    ProgressBar progressBar1;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);


    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setGreeting(view);
        Context context = getContext();

        progressBar1 = view.findViewById(R.id.progress_bar_1);


//        Set up recyclerView
        RecyclerView mRecommendedRecyclerView = view.findViewById(R.id.recyclerView_recommended);
        RecyclerView mTopRecyclerView = view.findViewById(R.id.recyclerView_top);
        RecyclerView mNewRecyclerView = view.findViewById(R.id.recyclerView_new);


        RecommendedEventsAdapter mRAdapter = new RecommendedEventsAdapter(context);
        TopEventsAdapter mTAdapter = new TopEventsAdapter(context);
        NewEventsAdapter mNAdapter = new NewEventsAdapter(context);


        mRecommendedRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        mTopRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        mNewRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

        mRecommendedRecyclerView.setAdapter(mRAdapter);
        mTopRecyclerView.setAdapter(mTAdapter);
        mNewRecyclerView.setAdapter(mNAdapter);


        FragmentActivity fragmentActivity = requireActivity();
        LifecycleOwner lifecycleOwner = getViewLifecycleOwner();

        HomeViewModel viewModel = new ViewModelProvider(fragmentActivity).get(HomeViewModel.class);

        viewModel.getTopEvents().observe(lifecycleOwner, events -> {
            mTAdapter.setEvents(events);
            progressBar1.setVisibility(View.GONE);
        });
        viewModel.getRecommendedEvents().observe(lifecycleOwner, events ->
                mRAdapter.setEvents(events)

        );
        viewModel.getNewEvents().observe(lifecycleOwner, events ->
                mNAdapter.setEvents(events)

        );

    }

    private void setGreeting(View v) {
        TextView mGreeting = v.findViewById(R.id.textView_greeting);
        String greeting;
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int hour = cal.get(Calendar.HOUR_OF_DAY);


        if (hour >= 5 && hour <= 12) {
            greeting = "Good morning";
        } else if (hour > 12 && hour < 18) {
            greeting = "Good afternoon";
        } else {
            greeting = "Good evening";
        }
        mGreeting.setText(greeting);

    }

}