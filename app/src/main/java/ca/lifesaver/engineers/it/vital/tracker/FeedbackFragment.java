package ca.lifesaver.engineers.it.vital.tracker;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Jason Macdonald N01246828 section: 0CB
 * Michael Carinci n01480052 section: 0CB
 * Patrik Prenga n01428752  section: 0CB
 * Nicholas Rafuse n01440073 section: 0CB
 */
public class FeedbackFragment extends Fragment {

    private EditText firstname;
    private EditText lastname;
    private EditText email;
    private EditText phone;
    private EditText comments;
    private RatingBar ratingBar;
    private Button submit;
    private ProgressBar progressBar;

    public FeedbackFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_feedback, container, false);
        progressBar = view.findViewById(R.id.progressBar);
        firstname = view.findViewById(R.id.firstname);
        lastname = view.findViewById(R.id.lastname);
        email = view.findViewById(R.id.Email);
        phone = view.findViewById(R.id.PhoneNumber);
        comments = view.findViewById(R.id.comments);
        ratingBar = view.findViewById(R.id.ratingBar);
        submit = view.findViewById(R.id.submitBtn);
        checkLastSubmission();
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitFeedback();
            }
        });

        return view;
    }

    private void submitFeedback() {
        String first = firstname.getText().toString().trim();
        String last = lastname.getText().toString().trim();
        String emailreceive = email.getText().toString().trim();
        String phoneNumber = phone.getText().toString().trim();
        float rating = ratingBar.getRating();
        String ratingString = Float.toString(rating);
        String comment = comments.getText().toString().trim();

        if (first.isEmpty() || last.isEmpty() || emailreceive.isEmpty() || phoneNumber.isEmpty() || comment.isEmpty()) {
            showToast("Please fill in all fields");
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Map<String, Object> feedbackMap = new HashMap<>();
                feedbackMap.put("first", first);
                feedbackMap.put("last", last);
                feedbackMap.put("email", emailreceive);
                feedbackMap.put("phone", phoneNumber);
                feedbackMap.put("rating", ratingString);
                feedbackMap.put("comment", comment);
                FirebaseFunctions.getInstance()
                        .getHttpsCallable("submitFeedback")
                        .call(feedbackMap)
                        .addOnSuccessListener(result -> {
                            showToast("Feedback submitted!");
                            clearInputFields();
                            progressBar.setVisibility(View.GONE);
                            new AlertDialog.Builder(getActivity())
                                    .setTitle("Confirmation")
                                    .setMessage("Your feedback has been submitted successfully.")
                                    .setPositiveButton("OK", (dialog, which) -> {
                                        // Handle the OK button click
                                        dialog.dismiss();
                                    })
                                    .show();

                        })
                        .addOnFailureListener(e -> {
                            if (e instanceof FirebaseFunctionsException) {
                                FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                                FirebaseFunctionsException.Code code = ffe.getCode();
                                String message = ffe.getMessage();
                                Log.e("FeedbackError", "Error code: " + code + " Message: " + message);
                                if (code == FirebaseFunctionsException.Code.FAILED_PRECONDITION) {
                                    showToast("You can only submit feedback once every 24 hours.");
                                } else {
                                    showToast("Error submitting feedback: " + message);
                                }
                            } else {
                                showToast("Error submitting feedback. Please try again.");
                                Log.e("FeedbackError", "Non-Firebase error", e);
                            }
                        });
            }
        }, 4000);
    }

    private void clearInputFields() {
        firstname.getText().clear();
        lastname.getText().clear();
        email.getText().clear();
        phone.getText().clear();
        ratingBar.setRating(0);
        comments.getText().clear();
    }

    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }

    private void checkLastSubmission(){
        FirebaseFunctions.getInstance()
                .getHttpsCallable("checkLastSubmission")
                .call()
                .addOnSuccessListener(result -> {
                    // Extract data as a Map
                    Map<String, Object> resultMap = (Map<String, Object>) result.getData();
                    // Extract the "canSubmit" value from the Map
                    Boolean canSubmit = (Boolean) resultMap.get("canSubmit");
                    // Now use canSubmit to set the enabled state of the submit button
                    submit.setEnabled(canSubmit != null && canSubmit);
                })
                .addOnFailureListener(e -> {
                    Log.d("Submission", "Error checking last submission", e);
                    // Consider disabling the submit button or informing the user of the error
                });
    }

}