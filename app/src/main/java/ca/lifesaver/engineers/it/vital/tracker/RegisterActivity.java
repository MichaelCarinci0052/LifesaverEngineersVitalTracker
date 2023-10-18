package ca.lifesaver.engineers.it.vital.tracker;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * Jason Macdonald N01246828 section: 0CB
 * Michael Carinci n01480052 section: 0CB
 * Patrik Prenga n01428752  section: 0CB
 * Nicholas Rafuse n01440073 section: 0CB
 */
public class RegisterActivity extends AppCompatActivity {

    private SharedViewModal viewModel;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    EditText editTextFirstName, editTextLastName, editTextUsername, editTextPassword, editTextConfirmPassword, editTextPhoneNumber;
    TextInputLayout emailLayout, firstNameLayout, lastNameLayout, phoneNumberLayout, passwordLayout, conPasswordLayout;
    String email;
    FirebaseFirestore db;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_register);

        viewModel = new ViewModelProvider(this).get(SharedViewModal.class);
        mAuth = FirebaseAuth.getInstance();
        editTextFirstName = findViewById(R.id.editTextFirstName);
        editTextLastName = findViewById(R.id.editTextLastName);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        emailLayout = findViewById(R.id.emailLayout);
        firstNameLayout = findViewById(R.id.firstNameLayout);
        lastNameLayout = findViewById(R.id.lastNameLayout);
        phoneNumberLayout = findViewById(R.id.PhoneNumberLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        conPasswordLayout = findViewById(R.id.conPasswordLayout);
        db = FirebaseFirestore.getInstance();
        Button buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button buttonRegister = findViewById(R.id.buttonRegister);
        buttonRegister.setOnClickListener(v -> {
            email = editTextUsername.getText().toString().trim();
            if (TextUtils.isEmpty(email)) {
                editTextUsername.setError("Please enter an email.");
                return;
            }
            String email = editTextUsername.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();
            String phoneNumber = editTextPhoneNumber.getText().toString().trim();
            String displayName = editTextFirstName.getText().toString().trim()+" "+editTextLastName.getText().toString().trim();
            registerUser(email, password, phoneNumber, displayName);
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

    }

    private boolean isValidEmail() {
        if (editTextUsername.length() == 0) {
            emailLayout.setError("This field is required");
            return false;
        }
        emailLayout.setError(null); // Clear error
        return true;
    }

    private void registerUser(String email, String password, String phoneNumber, String displayName) {
        if(validateInputs()){
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(getApplicationContext(), "Registered Successfully", Toast.LENGTH_SHORT).show();
                            if (user != null) {
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(displayName)
                                        .build();
                                user.updateProfile(profileUpdates)
                                        .addOnCompleteListener(task2 -> {
                                            if (task2.isSuccessful()) {
                                                Log.d("FirebaseAuth", "User profile updated.");
                                            }
                                        });
                                DocumentReference userDocRef = db.collection("userId").document(user.getUid());
                                userDocRef.get().addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        DocumentSnapshot document = task1.getResult();
                                        if (document != null && !document.exists()) {
                                            // User doesn't exist in Firestore, add them with preset fields
                                            UserData userData = new UserData(displayName, user.getEmail(), phoneNumber, false);
                                            userDocRef.set(userData);
                                        }
                                    } else {
                                        Log.w("Firestore", "Error getting document.", task1.getException());
                                    }
                                });
                            }
                            Intent intent = new Intent(RegisterActivity.this, SplashActivity.class);
                            intent.putExtra("START_MAIN_ACTIVITY", true);
                            startActivity(intent);
                            finish();
                        } else {
                            if (task.getException() != null) {
                                String errorMessage = task.getException().getMessage();
                                if (errorMessage != null && errorMessage.contains("The email address is already in use by another account.")) {
                                    emailLayout.setError("Email already exists.");
                                } else {
                                    Toast.makeText(getApplicationContext(), "Registration Failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
        }

    }


    private boolean validateInputs() {
        String firstName = editTextFirstName.getText().toString().trim();
        String lastName = editTextLastName.getText().toString().trim();
        String phoneNumber = editTextPhoneNumber.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();
        if (firstName.isEmpty()) {
            firstNameLayout.setError("First Name is required");
            return false;
        }

        if (lastName.isEmpty()) {
            lastNameLayout.setError("Last Name is required");
            return false;
        }

        if (phoneNumber.isEmpty()) {
            phoneNumberLayout.setError("Phone number is required");
            return false;
        }

        if (password.isEmpty()) {
            passwordLayout.setError("Password is required");
            return false;
        }
        if (confirmPassword.isEmpty()) {
            conPasswordLayout.setError("Confirm your password");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            conPasswordLayout.setError("Passwords do not match");
            return false;
        }

        return true;
    }
}

