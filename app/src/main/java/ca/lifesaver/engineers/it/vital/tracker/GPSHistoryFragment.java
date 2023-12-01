package ca.lifesaver.engineers.it.vital.tracker;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

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

        // Fetch and display location history
        fetchAndDisplayLocationHistory();

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

                               SimpleDateFormat originalFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
                               SimpleDateFormat newFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
                               Date date = null;
                               try {
                                   date = originalFormat.parse(timestamp);
                               } catch (ParseException e) {
                                   throw new RuntimeException(e);
                               }
                               String formattedTimestamp = newFormat.format(date);


                               timestampTextViews[i].setText(formattedTimestamp);
                           }

                       }
                   }
                });
    }
}
