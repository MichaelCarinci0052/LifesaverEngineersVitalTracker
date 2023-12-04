package ca.lifesaver.engineers.it.vital.tracker;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class GPSHistoryFragment extends Fragment {
    TextView[] timestampTextViews = new TextView[10];
    Button[] buttons = new Button[10];
    double[] latitude = new double[10];
    double[] longitude = new double[10];
    GPSSharedViewModel viewModel;

    public GPSHistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_g_p_s_history, container, false);
        timestampTextViews[0] = view.findViewById(R.id.textView1);
        timestampTextViews[1] = view.findViewById(R.id.textView2);
        timestampTextViews[2] = view.findViewById(R.id.textView3);
        timestampTextViews[3] = view.findViewById(R.id.textView4);
        timestampTextViews[4] = view.findViewById(R.id.textView5);
        timestampTextViews[5] = view.findViewById(R.id.textView6);
        timestampTextViews[6] = view.findViewById(R.id.textView7);
        timestampTextViews[7] = view.findViewById(R.id.textView8);
        timestampTextViews[8] = view.findViewById(R.id.textView9);
        timestampTextViews[9] = view.findViewById(R.id.textView10);

        buttons[0] = view.findViewById(R.id.button1);
        buttons[1] = view.findViewById(R.id.button2);
        buttons[2] = view.findViewById(R.id.button3);
        buttons[3] = view.findViewById(R.id.button4);
        buttons[4] = view.findViewById(R.id.button5);
        buttons[5] = view.findViewById(R.id.button6);
        buttons[6] = view.findViewById(R.id.button7);
        buttons[7] = view.findViewById(R.id.button8);
        buttons[8] = view.findViewById(R.id.button9);
        buttons[9] = view.findViewById(R.id.button10);

        viewModel = new ViewModelProvider(requireActivity()).get(GPSSharedViewModel.class);

        // Fetch and display location history
        fetchAndDisplayLocationHistory();


        buttons[0].setOnClickListener(v -> {
            viewModel.setLatitude(latitude[0]);
            viewModel.setLongitude(longitude[0]);
        });

        buttons[1].setOnClickListener(v -> {
            viewModel.setLatitude(latitude[1]);
            viewModel.setLongitude(longitude[1]);
        });

        buttons[2].setOnClickListener(v -> {
            viewModel.setLatitude(latitude[2]);
            viewModel.setLongitude(longitude[2]);
        });

        buttons[3].setOnClickListener(v -> {
            viewModel.setLatitude(latitude[3]);
            viewModel.setLongitude(longitude[3]);
        });

        buttons[4].setOnClickListener(v -> {
            viewModel.setLatitude(latitude[4]);
            viewModel.setLongitude(longitude[4]);
        });

        buttons[5].setOnClickListener(v -> {
            viewModel.setLatitude(latitude[5]);
            viewModel.setLongitude(longitude[5]);
        });

        buttons[6].setOnClickListener(v -> {
            viewModel.setLatitude(latitude[6]);
            viewModel.setLongitude(longitude[6]);
        });

        buttons[7].setOnClickListener(v -> {
            viewModel.setLatitude(latitude[7]);
            viewModel.setLongitude(longitude[7]);
        });

        buttons[8].setOnClickListener(v -> {
            viewModel.setLatitude(latitude[8]);
            viewModel.setLongitude(longitude[8]);
        });

        buttons[9].setOnClickListener(v -> {
            viewModel.setLatitude(latitude[9]);
            viewModel.setLongitude(longitude[9]);
        });

        return view;
    }
    private void fetchAndDisplayLocationHistory() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = Objects.requireNonNull(currentUser).getUid();


        CollectionReference locationHistoryRef = db.collection("userId").document(userId)
                .collection("location_history");

        locationHistoryRef.orderBy(FieldPath.documentId(), Query.Direction.DESCENDING).limit(10).get()
                .addOnCompleteListener(task -> {
                   if(task.isSuccessful()){
                       List<DocumentSnapshot> documents = task.getResult().getDocuments();
                       for(int i = 0; i < documents.size(); i++){
                           if(i < timestampTextViews.length){
                               String timestamp = documents.get(i).getId();
                               timestampTextViews[i].setText(formatTimestamp(timestamp));

                               latitude[i] = documents.get(i).getDouble("latitude");
                               longitude[i] = documents.get(i).getDouble("longitude");
                           }
                       }
                   }
                });
    }

    private String formatTimestamp(String timestamp) {
        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        SimpleDateFormat newFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        try {
            Date date = originalFormat.parse(timestamp);
            return newFormat.format(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }


}
