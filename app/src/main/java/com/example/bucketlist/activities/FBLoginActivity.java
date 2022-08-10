package com.example.bucketlist.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bucketlist.R;
import com.example.bucketlist.util.PreferencesManager;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FBLoginActivity extends AppCompatActivity {

    public static final String TAG = ".FBLoginActivity.TAG";
    private PreferencesManager mPreferences;
    private Button mSignInButton;
    FirebaseAuth auth;
    FirebaseFirestore db;

    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            result -> onSignInResult(result)
    );


    ActivityResultLauncher<Intent> mStartInterest = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent intent = result.getData();
                    // Handle the Intent
                    ArrayList interests = intent.getStringArrayListExtra(InterestsActivity.INTERESTS);
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    saveUser(interests, user.getUid(), user.getEmail(), user.getDisplayName(), user.getPhoneNumber());

                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_auth_ui);
        db = FirebaseFirestore.getInstance();
        mPreferences = new PreferencesManager(this);


        Button mSignOutButton = (Button) findViewById(R.id.button_signOut);
        mSignOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        // Grab button
        mSignInButton = (Button) findViewById(R.id.button_signIn);
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createSignInIntent();
            }
        });

//        Create instance of FirebaseAuth
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            // already signed in
            startMainActivity();
//            finish();
        } else {
            // not signed in
            createSignInIntent();
        }

    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if(requestCode == 2){
//           ArrayList interest =  data.getStringArrayListExtra(InterestsActivity.INTERESTS);
//            Log.d("onActivityResult", String.valueOf(interest));
//
//        }
//    }

    public void createSignInIntent() {
        // [START auth_fui_create_intent]
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build()
                // new AuthUI.IdpConfig.PhoneBuilder().build(),
//                ,new AuthUI.IdpConfig.TwitterBuilder().build()
        );

        // Create and launch sign-in intent
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build();
        signInLauncher.launch(signInIntent);
        // [END auth_fui_create_intent]
    }


    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK) {
            // Successfully signed in
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//            startInterestActivity();
            showToast("You signed in successfully");
            mPreferences = new PreferencesManager(this);
            if (mPreferences.isFirstRun()) {
                mStartInterest.launch(new Intent(FBLoginActivity.this, InterestsActivity.class));
                mPreferences.setFirstRun();
            } else {
                startMainActivity();
            }


            // ...
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
            showToast("You signed out!");
            return;
        }
    }
//
//    private void startInterestActivity() {
//       Intent i = new Intent(FBLoginActivity.this, InterestsActivity.class);
//       startActivityForResult(i, 2);
//    }


    // TODO: 4/24/2022 handle back click during sign in and no internet connection during sign in


    protected void startMainActivity() {
//        Start the main activity
        mPreferences.setFirstRun();
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
        finish();
    }

    private void showToast(String msg) {
        Toast toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    void signOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
showToast("you signed out!");
                    }
                });
    }


    public void saveUser(ArrayList interests, String uid, String email, String displayName, String phoneNumber) {

        Map<String, Object> user = new HashMap<>();

        user.put("uid", uid);
        user.put("email", email);
        user.put("name", displayName);
        user.put("phone", phoneNumber);
        user.put("interests", interests);


        db.collection("users").document(uid)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        startMainActivity();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error writing document", e);
            }
        });


    }

}


