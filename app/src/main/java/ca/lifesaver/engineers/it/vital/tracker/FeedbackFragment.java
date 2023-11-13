package ca.lifesaver.engineers.it.vital.tracker;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

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
        
        firstname = view.findViewById(R.id.firstname);
        lastname = view.findViewById(R.id.lastname);
        email = view.findViewById(R.id.Email);
        phone = view.findViewById(R.id.PhoneNumber);
        comments = view.findViewById(R.id.comments);
        ratingBar = view.findViewById(R.id.ratingBar);
        submit = view.findViewById(R.id.submitBtn);

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

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = Objects.requireNonNull(currentUser).getUid();
        DocumentReference feedbackRef = db.collection("userId").document(userId);

        Map<String, Object> feedbackMap = new HashMap<>();
        feedbackMap.put("first_name", first);
        feedbackMap.put("last_name", last);
        feedbackMap.put("email", emailreceive);
        feedbackMap.put("phone_number", phoneNumber);
        feedbackMap.put("rating", ratingString);
        feedbackMap.put("comment", comment);

        feedbackRef.collection("feedback").document("data").set(feedbackMap)
                .addOnSuccessListener(documentReference -> {
                    showToast("Feedback submitted!");
                    clearInputFields();
                })
                .addOnFailureListener(e -> {
                    showToast("Error submitting feedback. Please try again.");
                });
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
}