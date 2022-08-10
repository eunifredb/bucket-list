package com.example.bucketlist.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.bucketlist.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Transaction;

public class DetailActivity extends AppCompatActivity {
    FirebaseFirestore db;
    CollectionReference userListRef;
    CollectionReference eventsRef;
    FirebaseAuth auth;
    public static final String TAG = ".DetailActivity.TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);


        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        String uid = user.getUid();
        String email = user.getEmail();

        userListRef = db.collection("users").document(uid).collection("list");
        eventsRef = db.collection("events");


        Intent intent = getIntent();
        String eventId = intent.getStringExtra("id");
        String curator = intent.getStringExtra("curator");

//        loadEvent(eventId);
//        Grab textViews
        TextView eventTitle = findViewById(R.id.textView_detail_title);
        TextView eventPrice = findViewById(R.id.detail_price);
        TextView description = findViewById(R.id.textView_description);
        ImageView imgBanner = findViewById(R.id.imageView_detail);
        RatingBar ratingBar = findViewById(R.id.ratingBar_detail);

//        Grab buttons
        Button addToList = findViewById(R.id.button_add);
        Button bookEvent = findViewById(R.id.button_book);

//        Set TextViews
        eventTitle.setText(intent.getStringExtra("title"));
        description.setText(intent.getStringExtra("info"));
        Long price = intent.getLongExtra("price", 0);
        eventPrice.setText(Float.toString(price));
        ratingBar.setRating((float) intent.getDoubleExtra("rating", 2));
//        set Image banner
        Glide.with(getApplicationContext()).load(getIntent().getStringExtra("image_resource")).placeholder(R.drawable.photo).into(imgBanner);
//        Glide.with(mContext).load(current.getImageResource()).placeholder(R.drawable.photo)
//                .fitCenter().into(bannerImg);


//        Book event
        bookEvent.setOnClickListener(v -> {
            Intent i = new Intent(DetailActivity.this, CheckoutActivity.class);
            i.putExtra("eventId", eventId);
            i.putExtra("userId", uid);
            i.putExtra("price", price);
            i.putExtra("email", email);
            i.putExtra("curator", curator);
            startActivity(i);
            finish();
        });

//        Add event to bucket list
        addToList.setOnClickListener(v -> db.runTransaction((Transaction.Function<Void>) transaction -> {
            DocumentSnapshot snapShot = transaction.get(eventsRef.document(eventId));
            transaction.set(userListRef.document(eventId), snapShot.getData());
            return null;
        }).addOnSuccessListener(unused -> {
            Log.d(TAG, "Transaction success!");
            v.setEnabled(false);
            showSnackBar(intent.getStringExtra("title"));
        }).addOnFailureListener(e -> Log.w(TAG, "Transaction failure.", e)));


    }

    private void showSnackBar(String title) {

        Toast.makeText(this, title + " added to bucket list", Toast.LENGTH_SHORT
        ).show();
    }


}