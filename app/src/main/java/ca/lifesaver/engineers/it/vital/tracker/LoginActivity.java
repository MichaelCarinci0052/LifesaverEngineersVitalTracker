package ca.lifesaver.engineers.it.vital.tracker;

import static java.security.AccessController.getContext;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

/**
 * Jason Macdonald N01246828 section: 0CB
 * Michael Carinci n01480052 section: 0CB
 * Patrik Prenga n01428752  section: 0CB
 * Nicholas Rafuse n01440073 section: 0CB
 */

public class LoginActivity extends AppCompatActivity {
    private GoogleSignInClient mGoogleSignInClient;

    private EditText editTextUsername;
    private EditText editTextPassword;
    private TextInputLayout passwordLayout;
    private TextInputLayout emailLayout;
    private Button buttonLogin;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private static final int RC_SIGN_IN = 9001;

    private SharedViewModal viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        viewModel = new ViewModelProvider(this).get(SharedViewModal.class);
        passwordLayout = findViewById(R.id.passwordLayout);
        emailLayout = findViewById(R.id.emailLayout);
        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);


        //google Sign in button
        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setColorScheme(0);
        signInButton.setSize(1);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("927967858924-bavg9t7kc1q6j7ucfe4ce0b5r4g8jpea.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);



        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                mGoogleSignInClient.signOut().addOnCompleteListener(LoginActivity.this, task -> {
                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, RC_SIGN_IN);
                });


            }
        });
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValidEmail()&&isValidPassword()){
                    String username = editTextUsername.getText().toString().trim();
                    String password = editTextPassword.getText().toString().trim();
                    //Firebase initialization
                    mAuth.signInWithEmailAndPassword(username, password)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    // Sign in success
                                    SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("username", username);
                                    editor.apply();
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
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
                isValidEmail();
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
                isValidPassword();
            }
        });


    }
    private boolean isValidEmail() {
        if (editTextUsername.length() == 0) {
            emailLayout.setError("This field is required");
            return false;
        }
        emailLayout.setError(null); // Clear error
        return true;
    }

    private boolean isValidPassword() {
        if (editTextPassword.length() == 0) {
            passwordLayout.setError("Password is required");
            return false;
        } else if (editTextPassword.length() < 8) {
            passwordLayout.setError("Password must be minimum 8 characters");
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
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d("firebase authentication-google","Success");
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
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
