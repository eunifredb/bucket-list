package com.example.bucketlist.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bucketlist.BuildConfig;
import com.example.bucketlist.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import co.paystack.android.Paystack;
import co.paystack.android.PaystackSdk;
import co.paystack.android.Transaction;
import co.paystack.android.model.Card;
import co.paystack.android.model.Charge;

public class CheckoutActivity extends AppCompatActivity {
    private TextInputLayout mCardNumber;
    private TextInputLayout mCardExpiry;
    private TextInputLayout mCardCVV;
    ProgressBar pb;
    FirebaseFirestore db;
    final String TAG = "CheckoutActivity";


    //    Button button;
    int amount;
    String email,eventId, uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        pb = findViewById(R.id.progress_bar);

        db = FirebaseFirestore.getInstance();

        Intent i = getIntent();
        amount = (int) i.getLongExtra("price", 0);
        email = i.getStringExtra("email");
        eventId = i.getStringExtra("eventId");
        uid =i.getStringExtra("userId");

        initializePaystack();
        initializeFormVariables();
    }
//
//    private void initViews() {
//        mCardNumber = findViewById(R.id.til_card_number);
//        mCardExpiry = findViewById(R.id.til_card_expiry);
//        mCardCVV = findViewById(R.id.til_card_cvv);
//
//
//       Button button = findViewById(R.id.btn_make_payment);
//        String btnLabel = "PAY GHC " + amount;
//        button.setText(btnLabel);
////        addTextWatcherToEditText();
//
//
//    }


    private void initializePaystack() {
        PaystackSdk.initialize(getApplicationContext());
        PaystackSdk.setPublicKey(BuildConfig.PSTK_PUBLIC_KEY);
    }

    private void initializeFormVariables() {

        mCardNumber = findViewById(R.id.til_card_number);
        mCardExpiry = findViewById(R.id.til_card_expiry);
        mCardCVV = findViewById(R.id.til_card_cvv);


//        Objects.requireNonNull(mCardExpiry.getEditText()).addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                if (s.toString().length() == 2 && !s.toString().contains("/")) {
//                    s.append("/");
//                }
//            }
//        });
        mCardNumber.getEditText().setText("4084084084084081");
        mCardExpiry.getEditText().setText("05/23");
        mCardCVV.getEditText().setText("408");

        Button button = findViewById(R.id.btn_make_payment);
        String btnLabel = "PAY GH₵" + amount;
        button.setText(btnLabel);
        button.setOnClickListener(v -> performCharge());

    }

    private void performCharge() {
        pb.setVisibility(View.VISIBLE);
        String cardNumber = mCardNumber.getEditText().getText().toString();
        String cardExpiry = mCardExpiry.getEditText().getText().toString();
        String cvv = mCardCVV.getEditText().getText().toString();
        String[] cardExpiryArray = cardExpiry.split("/");
        int expiryMonth = Integer.parseInt(cardExpiryArray[0]);
        int expiryYear = Integer.parseInt(cardExpiryArray[1]);
        amount *= 100;

        Card card = new Card(cardNumber, expiryMonth, expiryYear, cvv);
        Charge charge = new Charge();

        charge.setAmount(amount);
        charge.setEmail(email);
        charge.setCard(card);
        charge.setCurrency("GHS");


        PaystackSdk.chargeCard(this, charge, new Paystack.TransactionCallback() {
            @Override
            public void onSuccess(Transaction transaction) {
                saveBooking();
                pb.setVisibility(View.GONE);
                parseResponse(transaction.getReference());
                openMainActivity();
                Log.d("CheckoutActivity", "Payment Successful - " + transaction.getReference());
            }

            @Override
            public void beforeValidate(Transaction transaction) {
                Log.d("CheckoutActivity", "beforeValidate: " + transaction.getReference());
            }

            @Override
            public void onError(Throwable error, Transaction transaction) {
                Log.d("CheckoutActivity", "onError: " + error.getLocalizedMessage());
                Log.d("CheckoutActivity", "onError: " + error);
                Log.d("CheckoutActivity", "onError: " + charge.getCurrency());
            }
        });
    }

  void saveBooking (){
      DocumentReference eventRef = db.collection("events").document(eventId);
      Map<String, Object> booking = new HashMap<>();
      booking.put("bookingDate", new Timestamp(new Date()));
      booking.put("eventId", eventId);
      booking.put("uid", uid);
      booking.put("amount", amount);
      booking.put("email", email);
      booking.put("eventRef", eventRef);


      db.collection("sales")
              .add(booking)
              .addOnSuccessListener(unused -> Log.d(TAG, "DocumentSnapshot successfully written!")).addOnFailureListener(new OnFailureListener() {
                  @Override
                  public void onFailure(@NonNull Exception e) {
                      Log.w(TAG, "Error writing document", e);
                  }
              });


  }


    private void openMainActivity() {
        //        Take user back to sign in activity
        Intent i = new Intent(CheckoutActivity.this, MainActivity.class);
        i.putExtra("action", "gotoList");
        startActivity(i);
        finish();
    }

    private void parseResponse(String transactionReference) {
        String message = "Payment Successful - " + transactionReference;
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();

    }

}