package ca.lifesaver.engineers.it.vital.tracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.functions.FirebaseFunctions;

import java.util.HashMap;

/**
 * Jason Macdonald N01246828 section: 0CB
 * Michael Carinci n01480052 section: 0CB
 * Patrik Prenga n01428752  section: 0CB
 * Nicholas Rafuse n01440073 section: 0CB
 */

public class SplashActivity extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 4000; 

    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mAuth = FirebaseAuth.getInstance();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (getIntent().getBooleanExtra("START_MAIN_ACTIVITY", false)) {
                    Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                    finish();
                } else {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        user.reload()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        // User exists
//                                        getProfilePictureFromFirebase();
//                                        nextIntent = new Intent(SplashActivity.this, MainActivity.class);
                                        getProfilePictureFromFirebase(() -> {
                                            Intent nextIntent = new Intent(SplashActivity.this, MainActivity.class);
                                            startActivity(nextIntent);
                                            finish();
                                        });
                                    } else {
                                        // User doesn't exist anymore
                                        Intent nextIntent = new Intent(SplashActivity.this, LoginActivity.class);
                                        startActivity(nextIntent);
                                        finish();
                                    }
                                });
                    } else {
                        Intent loginIntent = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(loginIntent);
                        finish();
                    }
                }
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    private void getProfilePictureFromFirebase(Runnable onCompleted){
        FirebaseFunctions.getInstance()
                .getHttpsCallable("getLatestImage")
                .call()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Task completed successfully
                        HashMap result = (HashMap) task.getResult().getData();
                        String imageUrl = (String) result.get("url");
                        Log.d("USER UID FOR PROFILE",mAuth.getUid());

                        if (imageUrl != null) {
                            //Use Glide to load the image
                            Glide.with(getApplicationContext())
                                    .load(imageUrl)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .preload();
                            SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("ProfileImageUrl", imageUrl);
                            editor.apply();
                        } else {
                            Log.d("MAIN APPLICATION", "No image found for the user.");
                        }
                    } else {
                        // Task failed with an exception
                        Exception e = task.getException();
                        Log.d("MainActivity", "Error: " + e.getMessage(), e);
                    }
                    onCompleted.run();
                });
    }
}
