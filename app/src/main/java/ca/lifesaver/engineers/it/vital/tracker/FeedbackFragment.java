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

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

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
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference feedbackRef = db.collection("feedback");

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
        String comment = comments.getText().toString().trim();

        if (first.isEmpty() || last.isEmpty() || emailreceive.isEmpty() || phoneNumber.isEmpty() || comment.isEmpty()) {
            showToast("Please fill in all fields");
            return;
        }

        FeedbackModel feedback = new FeedbackModel(first + " " + last, emailreceive, phoneNumber, rating, comment);

        feedbackRef.add(feedback)
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

    public class FeedbackModel {

        private String name;
        private String email;
        private String phoneNumber;
        private float rating;
        private String comments;

        public FeedbackModel() {
        }

        public FeedbackModel(String name, String email, String phoneNumber, float rating, String comments) {
            this.name = name;
            this.email = email;
            this.phoneNumber = phoneNumber;
            this.rating = rating;
            this.comments = comments;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public float getRating() {
            return rating;
        }

        public String getComments() {
            return comments;
        }
    }
}