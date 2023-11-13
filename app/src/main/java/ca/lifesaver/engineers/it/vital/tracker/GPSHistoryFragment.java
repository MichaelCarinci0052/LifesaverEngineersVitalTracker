package ca.lifesaver.engineers.it.vital.tracker;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;
import java.util.Objects;

public class GPSHistoryFragment extends Fragment {
    private LinearLayout locationHistoryLayout;

    public GPSHistoryFragment() {
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
        View view = inflater.inflate(R.layout.fragment_g_p_s_history, container, false);
        locationHistoryLayout = view.findViewById(R.id.locationHistoryLayout);

        // Fetch and display location history
        fetchAndDisplayLocationHistory();

        return view;
    }
    @SuppressLint("SetTextI18n")
    private void fetchAndDisplayLocationHistory() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = Objects.requireNonNull(currentUser).getUid();

        CollectionReference locationHistoryRef = db.collection(userId).document("location").collection("location_history");

        Query query = locationHistoryRef.orderBy("timestamp", Query.Direction.DESCENDING).limit(50);

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                locationHistoryLayout.removeAllViews();

                for (DocumentSnapshot document : task.getResult()) {
                    Map<String, Object> coordinates = (Map<String, Object>) document.get("coordinates");
                    if (coordinates != null) {
                        double latitude = (double) coordinates.get("latitude");
                        double longitude = (double) coordinates.get("longitude");

                        TextView entryTextView = new TextView(requireContext());
                        entryTextView.setText("Latitude: " + latitude + "\nLongitude: " + longitude);
                        locationHistoryLayout.addView(entryTextView);
                    }
                }
            }
        });
    }
}
