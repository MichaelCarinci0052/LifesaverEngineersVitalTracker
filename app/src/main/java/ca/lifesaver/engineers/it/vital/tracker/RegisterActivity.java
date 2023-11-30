package ca.lifesaver.engineers.it.vital.tracker;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private CheckBox checkBoxRememberMe;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_register);
        checkBoxRememberMe = findViewById(R.id.checkBoxRememberMe);

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
            String firstName = capitalizeFirstLetter(editTextFirstName.getText().toString().trim());
            String lastName = capitalizeFirstLetter(editTextLastName.getText().toString().trim());
            String displayName = firstName+" "+lastName;
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
        editTextPhoneNumber.addTextChangedListener(new TextWatcher() {
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
                isValidPhoneNumber();
            }
        });

    }


    private boolean isValidPassword() {
        String password = editTextPassword.getText().toString().trim();

        if (password.length() < 6) {
            passwordLayout.setError("Password must be at least 6 characters long");
            return false;
        }

        if (!password.equals(password.toLowerCase()) && !password.equals(password.toUpperCase())) {
            // It has both upper and lowercase characters, so it's fine
        } else if (!password.equals(password.toLowerCase())) {
            passwordLayout.setError("Password must contain at least one uppercase letter");
            return false;
        } else {
            passwordLayout.setError("Password must contain at least one lowercase letter");
            return false;
        }

        if (!password.matches(".*\\d.*")) {
            passwordLayout.setError("Password must contain at least one number");
            return false;
        }

        if (!password.matches(".*[!@#$%^&*()-+=].*")) {
            passwordLayout.setError("Password must contain at least one special character (e.g., !, @, #, etc.)");
            return false;
        }

        if (password.contains(" ")) {
            passwordLayout.setError("Password should not contain spaces");
            return false;
        }

        passwordLayout.setError(null);
        return true;
    }

    private boolean isValidEmail() {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (editTextUsername.length() == 0) {
            emailLayout.setError("This field is required");
            return false;
        }else if (!editTextUsername.getText().toString().trim().matches(emailPattern)) {
            emailLayout.setError("Invalid email format");
            return false;
        }
        emailLayout.setError(null); // Clear error
        return true;
    }

    private boolean isValidPhoneNumber() {
        String phoneNumber = editTextPhoneNumber.getText().toString().trim();
        Pattern pattern = Pattern.compile("^\\+1[0-9]{10}$");
        Matcher matcher = pattern.matcher(phoneNumber);

        if (phoneNumber.isEmpty()) {
            phoneNumberLayout.setError("Phone number is required");
            return false;
        }
        if(!matcher.matches()){
            phoneNumberLayout.setError("Must input area code for example +1");
            return false;
        }
        phoneNumberLayout.setError(null);
        return true;
    }
    private void registerUser(String email, String password, String phoneNumber, String displayName) {
        SharedPreferences preferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        if(validateInputs() && isValidPassword()){
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            user.sendEmailVerification();
                            Toast.makeText(getApplicationContext(), "Email verification sent! Check your spam", Toast.LENGTH_SHORT).show();

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
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putBoolean("RememberMe", checkBoxRememberMe.isChecked());
                            editor.apply();
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

    private String capitalizeFirstLetter(String str) {
        if (str == null || str.length() == 0) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}

