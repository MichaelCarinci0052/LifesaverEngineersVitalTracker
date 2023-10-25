package ca.lifesaver.engineers.it.vital.tracker;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Jason Macdonald N01246828 section: 0CB
 * Michael Carinci n01480052 section: 0CB
 * Patrik Prenga n01428752  section: 0CB
 * Nicholas Rafuse n01440073 section: 0CB
 */

public class SplashActivity extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 3000;

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
                                    Intent nextIntent;
                                    if (task.isSuccessful()) {
                                        // User exists
                                        nextIntent = new Intent(SplashActivity.this, MainActivity.class);
                                    } else {
                                        // User doesn't exist anymore
                                        nextIntent = new Intent(SplashActivity.this, LoginActivity.class);
                                    }
                                    startActivity(nextIntent);
                                    finish();
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
}
