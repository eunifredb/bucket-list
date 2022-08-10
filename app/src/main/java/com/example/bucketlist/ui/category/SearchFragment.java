package com.example.bucketlist.ui.category;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bucketlist.R;


public class SearchFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String CATEGORY = "category";
    private String mCategory;

//
    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment newInstance(String category) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(CATEGORY, category);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCategory = getArguments().getString(CATEGORY);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView search_title = view.findViewById(R.id.booked_title);
        search_title.setText("Events in "+mCategory + " category." );

        RecyclerView mRecyclerView = view.findViewById(R.id.recyclerView_list);
        CategoryAdapter mAdapter = new CategoryAdapter(getContext());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        SearchViewModel viewModel = new ViewModelProvider(requireActivity()).get(SearchViewModel.class);
        viewModel.getEventsByCategory(mCategory).observe(getViewLifecycleOwner(), events -> mAdapter.setEvents(events));

    }
}