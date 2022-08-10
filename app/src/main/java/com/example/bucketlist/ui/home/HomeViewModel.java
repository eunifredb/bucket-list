package com.example.bucketlist.ui.home;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.bucketlist.models.Event;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {
    private final MutableLiveData<List<Event>> topEvents = new MutableLiveData<>();
    private final MutableLiveData<List<Event>> recEvents = new MutableLiveData<>();
    private final MutableLiveData<List<Event>> newEvents = new MutableLiveData<>();

    public static final String TAG = "homeviewmodel";
    private FirebaseFirestore db;




    public HomeViewModel() {
        this.db = FirebaseFirestore.getInstance();

        loadTopEvents();
        loadRecommendedEvents();
        loadNewEvents();
    }


    public LiveData<List<Event>> getTopEvents() {
        return topEvents;
    }

    public LiveData<List<Event>> getRecommendedEvents() {
        return recEvents;
    }

    public LiveData<List<Event>> getNewEvents() {
        return newEvents;
    }


    // find all events with rating 4.0 and above
    private void loadTopEvents() {

        List<Event> list = new ArrayList<>();
        CollectionReference eventsRef = db.collection("events");
        eventsRef.whereGreaterThan("rating", 4.0)
                .orderBy("rating", Query.Direction.DESCENDING)
                .limit(6)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            list.add(document.toObject(Event.class));
                        }
                        topEvents.setValue(list);
                    }
                });
    }

    //    find events based on user's interests
    private void loadRecommendedEvents() {


        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final DocumentReference userRef = db.collection("users").document(user.getUid());
        final CollectionReference eventsRef = db.collection("events");

        List<Event> list = new ArrayList<>();
        List<String> interests = new ArrayList<>();
        Task<QuerySnapshot> task = eventsRef.get();

        db.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {

//                find user document
                DocumentSnapshot snapshot = transaction.get(userRef);
//                find user interests
                interests.addAll((List<String>) snapshot.get("interests"));

//                find events collection
                if (task.isSuccessful()) {
//                    if user interest matches a tag in an event, add that event to list
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        List<String> tags = (List<String>) document.get("tags");
                        if (tags != null) {
                            test:
                            for (String tag : tags) {
                                if (interests.contains(tag) && list.size() < 12) {
                                    list.add(document.toObject(Event.class));
                                    break test;

                                }
                            }
                        }
                    }
                }
                return null;
            }

        }).addOnSuccessListener(unused -> {
            recEvents.setValue(list);
        }).addOnFailureListener(e -> Log.w(TAG, "Transaction failure.", e));

    }

    //find all events that were posted recently
    private void loadNewEvents() {
        final CollectionReference eventsRef = db.collection("events");
        List<Event> list = new ArrayList<>();
        eventsRef.orderBy("posted", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            list.add(document.toObject(Event.class));
                        }
                        newEvents.setValue(list);
                    }
                });
    }
}
