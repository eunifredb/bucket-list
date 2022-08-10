package com.example.bucketlist.ui.list;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bucketlist.R;
import com.example.bucketlist.models.Event;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;


public class ListFragment extends Fragment {
    CollectionReference userListRef, sales;
    private FirebaseFirestore db;
    FirebaseAuth auth;
    String uid;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_list, container, false);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        uid = user.getUid();
        userListRef = db.collection("users").document(uid).collection("list");

        Context context = getContext();


        //        setUp recyclerView
        RecyclerView bucketListRecyclerView = view.findViewById(R.id.recyclerView_list);
        BucketListAdapter adapter = new BucketListAdapter(context);

        RecyclerView bookedRecyclerView = view.findViewById(R.id.recyclerView_booked);
        BookedAdapter booked_adapter = new BookedAdapter(context);

        bucketListRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        bucketListRecyclerView.setAdapter(adapter);

        bookedRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        bookedRecyclerView.setAdapter(booked_adapter);

        FragmentActivity fragmentActivity = requireActivity();
        LifecycleOwner lifecycleOwner = getViewLifecycleOwner();

        ListViewModel viewModel = new ViewModelProvider(fragmentActivity).get(ListViewModel.class);
        viewModel.getBucketList().observe(lifecycleOwner, events -> {
                    ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                        @Override
                        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
//                int from = viewHolder.getAdapterPosition();
//                int to = target.getAdapterPosition();
//                Collections.swap(mSportsData, from, to);
//                mAdapter.notifyItemMoved(from, to);
                            return true;
                        }

                        @Override
                        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                            Event e = events.get(viewHolder.getAdapterPosition());
                            userListRef.document(e.getId())
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("ListFragment", e.getTitle()+ " Successfully deleted!");

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("ListFragment", "Error deleting document", e);
                                        }
                                    });



//                            adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                        }
                    });
                    helper.attachToRecyclerView(bucketListRecyclerView);
            adapter.setEvents(events);
                });
        viewModel.getBookings().observe(lifecycleOwner, bookings -> booked_adapter.setEvents(bookings));

    }
}