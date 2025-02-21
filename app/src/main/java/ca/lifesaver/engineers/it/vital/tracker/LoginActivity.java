package ca.lifesaver.engineers.it.vital.tracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;

import java.util.HashMap;

/**
 * Jason Macdonald N01246828 section: 0CB
 * Michael Carinci n01480052 section: 0CB
 * Patrik Prenga n01428752  section: 0CB
 * Nicholas Rafuse n01440073 section: 0CB
 */

public class LoginActivity extends AppCompatActivity {
    private GoogleSignInClient mGoogleSignInClient;

    static EditText editTextUsername;
    static EditText editTextPassword;
    private static TextInputLayout passwordLayout;
    private static TextInputLayout emailLayout;
    private Button buttonLogin;
    private Button buttonRegister;

    FirebaseAuth mAuth;
    private static final int RC_SIGN_IN = 9001;
    private CheckBox checkBoxRememberMe;
    private SharedViewModal viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        viewModel = new ViewModelProvider(this).get(SharedViewModal.class);
        passwordLayout = findViewById(R.id.passwordLayout);
        emailLayout = findViewById(R.id.emailLayout);
        mAuth = FirebaseAuth.getInstance();
        checkBoxRememberMe = findViewById(R.id.checkBoxRememberMe);

        SharedPreferences preferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);

        //google Sign in button
        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setColorScheme(0);
        signInButton.setSize(1);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("927967858924-bavg9t7kc1q6j7ucfe4ce0b5r4g8jpea.apps.googleusercontent.com")
                .requestEmail()
                .requestScopes(new Scope("https://www.googleapis.com/auth/user.phonenumbers.read"))
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonRegister = findViewById(R.id.buttonRegister);


        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            };

        });
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGoogleSignInClient.signOut().addOnCompleteListener(LoginActivity.this, task -> {
                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();

                    startActivityForResult(signInIntent, RC_SIGN_IN);
                });


            }
        });
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValidEmail("valid.email@example.com")&&isValidPassword("123456")){
                    String username = editTextUsername.getText().toString().trim();
                    String password = editTextPassword.getText().toString().trim();

                    //Firebase initialization
                    mAuth.signInWithEmailAndPassword(username, password)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    // Sign in success
                                    getProfilePictureFromFirebase();
                                    Intent intent = new Intent(LoginActivity.this, SplashActivity.class);
                                    intent.putExtra("START_MAIN_ACTIVITY", true);
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putBoolean("RememberMe", checkBoxRememberMe.isChecked());
                                    editor.apply();
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // Sign in failed
                                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Authentication Failed!", Snackbar.LENGTH_SHORT);
                                    View snackbarView = snackbar.getView();
                                    FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)snackbarView.getLayoutParams();
                                    params.gravity = Gravity.TOP;
                                    snackbarView.setLayoutParams(params);
                                    snackbar.show();
                                }
                            });
                }

            }
        });
        editTextUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing here
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Do nothing here
            }

            @Override
            public void afterTextChanged(Editable s) {
                isValidEmail("valid.email@example.com");
            }
        });

        editTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing here
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Do nothing here
            }

            @Override
            public void afterTextChanged(Editable s) {
                isValidPassword("123456");
            }
        });


    }
    public boolean isValidEmail(String s) {
        if (editTextUsername.length() == 0) {
            emailLayout.setError("This field is required");
            return false;
        }
        emailLayout.setError(null); // Clear error
        return true;
    }

    public boolean isValidPassword(String s) {
        if (editTextPassword.length() == 0) {
            passwordLayout.setError("Password is required");
            return false;
        } else if (editTextPassword.length() < 6) {
            passwordLayout.setError("Password must be minimum 6 characters");
            return false;
        }
        passwordLayout.setError(null); // Clear error
        return true;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Log.d("Google log in", "Google Sign-In failed with code: " + e.getStatusCode());
                Log.d("Google log in", "Error message: " + e.getMessage());
            }
        }
    }


    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        SharedPreferences preferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();

                        if (user != null) {
                            getProfilePictureFromFirebase();
                            DocumentReference userDocRef = db.collection("userId").document(user.getUid());
                            userDocRef.get().addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    DocumentSnapshot document = task1.getResult();
                                    if (document != null && !document.exists()) {
                                        // User doesn't exist in Firestore, add them with preset fields
                                        UserData userData = new UserData(user.getDisplayName(), user.getEmail(), user.getPhoneNumber(), false);
                                        userDocRef.set(userData);
                                    }
                                } else {
                                    Log.w("Firestore", "Error getting document.", task1.getException());
                                }
                            });
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putBoolean("RememberMe", checkBoxRememberMe.isChecked());
                            editor.apply();
                            Intent intent = new Intent(LoginActivity.this, SplashActivity.class);
                            intent.putExtra("START_MAIN_ACTIVITY", true);
                            startActivity(intent);
                            finish();
                        } else {
                            // Sign in failed
                            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Authentication Failed!", Snackbar.LENGTH_SHORT);
                            View snackbarView = snackbar.getView();
                            FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)snackbarView.getLayoutParams();
                            params.gravity = Gravity.TOP;
                            snackbarView.setLayoutParams(params);
                            snackbar.show();
                        }
                    }
                });
    }
    private void getProfilePictureFromFirebase(){
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
                });
    }
}
