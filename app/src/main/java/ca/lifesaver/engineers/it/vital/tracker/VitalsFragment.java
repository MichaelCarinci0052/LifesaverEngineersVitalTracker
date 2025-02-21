package ca.lifesaver.engineers.it.vital.tracker;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

/**
 * Jason Macdonald N01246828 section: 0CB
 * Michael Carinci n01480052 section: 0CB
 * Patrik Prenga n01428752  section: 0CB
 * Nicholas Rafuse n01440073 section: 0CB
 */

public class VitalsFragment extends Fragment {
    private static final int BATCH_SIZE = 10;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private SharedViewModal viewModel;
    private Boolean notifs;
    private String mParam1;
    private String mParam2;
    private OnVitalsDataChangedListener mListener;

    private Handler handler;
    private Runnable updateRunnable;
    private Random random;
    private List<Map<String, Object>> vitalsDataBatch = new ArrayList<>();
    private List<Map<String, Object>> writeVitalsDataBatch = new ArrayList<>();
    ConnectivityManager connectivityManager;


    private boolean online = true;

    public VitalsFragment() {
        // Required empty public constructor
    }

    public interface OnVitalsDataChangedListener {
        void onDataChanged(String heartRate, String oxygenLevel, String bodyTemp);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Fragment parentFragment = getParentFragment();
        if (parentFragment instanceof OnVitalsDataChangedListener) {
            mListener = (OnVitalsDataChangedListener) parentFragment;
        }
    }

    public static VitalsFragment newInstance(String param1, String param2) {
        VitalsFragment fragment = new VitalsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
        random = new Random();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vitals, container, false);


        return view;
    }
    private String getCurrentFormattedDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button btnOpenGraph = view.findViewById(R.id.btnGraph);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser.getUid();
        DocumentReference vitalsRef = db.collection(userId).document("vitals");
        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModal.class);
        viewModel.getSwitchStatus().observe(getViewLifecycleOwner(), isChecked  -> {
            if (isChecked ) {
                notifs = true;
            } else {
                notifs = false;
            }
        });
        // Reference the TextViews
        TextView tvHeartRate = view.findViewById(R.id.heartRateValue);
        TextView tvOxygenLevel = view.findViewById(R.id.oxygenValue);
        TextView tvBodyTemp = view.findViewById(R.id.bodyTempValue);
        btnOpenGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGraph();
            }
        });
        updateRunnable = new Runnable() {
            @Override
            public void run() {
                ConnectivityManager connectivityManager = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                if(connectivityManager != null) {
                    NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
                    if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
                        // Generate random data
                        int heartRate = 60 + random.nextInt(40);  // Random value between 60 and 100
                        int oxygenLevel = 90 + random.nextInt(10);  // Random value between 90 and 100
                        float bodyTemp = 97.0f + random.nextFloat() * 3.0f;  // Random value between 97.0 and 100.0

                        String formattedDateTime = getCurrentFormattedDateTime();
                        String formattedDate = getCurrentFormattedDate();
                        DocumentReference vitalsDocRef = db.collection("userId").document(userId).collection("vitals").document(formattedDate);

                        Map<String, Object> vitalsDataMap = new HashMap<>();
                        vitalsDataMap.put("heartRate", heartRate);
                        vitalsDataMap.put("oxygenLevel", oxygenLevel);
                        vitalsDataMap.put("bodyTemp", bodyTemp);
                        vitalsDataMap.put("timestamp", formattedDateTime);
                        vitalsDataBatch.add(vitalsDataMap);
                        if (vitalsDataBatch.size() >= BATCH_SIZE) {
                            writeBatchToFirestore();
                            vitalsDataBatch.clear();
                        }


                        if (heartRate < 60 || heartRate > 100) {
                            if (notifs) {
                                sendNotification("Abnormal Heart Rate", "Detected heart rate: " + heartRate + " BPM");
                            }
                        }
                        if (oxygenLevel < 91) {
                            if (notifs) {
                                sendNotification("Low Oxygen Level", "Detected oxygen level: " + oxygenLevel + "%");
                            }
                        }
                        if (bodyTemp < 97.0f || bodyTemp > 97.1f) {
                            if (notifs) {
                                sendNotification("Abnormal Body Temperature", String.format("Detected body temperature: %.1f°F", bodyTemp)
                                );
                            }
                        }

                        // Update the UI
                        tvHeartRate.setText(heartRate + " BPM");
                        tvOxygenLevel.setText(oxygenLevel + "%");
                        tvBodyTemp.setText(String.format("%.1f°F", bodyTemp));
                        if (mListener != null) {
                            mListener.onDataChanged(
                                    "Heart Rate: " + heartRate + " BPM",
                                    "Oxygen Level: " + oxygenLevel + "%",
                                    String.format("Body Temperature: %.1f°F", bodyTemp)
                            );
                        }
                    } else {
                        tvHeartRate.setText("Offline");
                        tvOxygenLevel.setText("Offline");
                        tvBodyTemp.setText(String.format("Offline"));
                        if (mListener != null) {
                            mListener.onDataChanged(
                                    "Heart Rate: Offline",
                                    "Oxygen Level: Offline",
                                    String.format("Body Temperature: Offline")
                            );
                        }
                    }
                }
                // Schedule the next update
                handler.postDelayed(this, 2000);  // Update every 2 seconds
            }
        };

        // Start the updates
        handler.post(updateRunnable);
    }

    public void onDestroyView() {
        super.onDestroyView();
        // Stop the updates when the fragment is destroyed
        handler.removeCallbacks(updateRunnable);
    }


    private void sendNotification(String title, String message) {


        Notification.Builder notificationBuilder = new Notification.Builder(requireContext(), "VITALS_CHANNEL_ID")
                .setSmallIcon(R.drawable.ic_vitals)  // replace with your icon
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            notificationBuilder.setForegroundServiceBehavior(Notification.FOREGROUND_SERVICE_IMMEDIATE);
        }
        // Get the NotificationManager service
        NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);

        // Issue the notification
        int notificationId = random.nextInt();  // Generate a random ID for the notification
        notificationManager.notify(notificationId, notificationBuilder.build());
    }

    private void openGraph() {
        GraphHistory GraphHistory = new GraphHistory();

        // Fragment transaction to replace the current fragment with the new one
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.activity_main_frame_layout, GraphHistory);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private String getCurrentFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        return sdf.format(new Date());
    }

    private void writeBatchToFirestore() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String formattedDate = getCurrentFormattedDate();
        writeVitalsDataBatch.addAll(vitalsDataBatch);
        DocumentReference vitalsDocRef = FirebaseFirestore.getInstance()
                .collection("userId")
                .document(userId)
                .collection("vitals")
                .document(formattedDate);


        vitalsDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Update existing document
                    List<Map<String, Object>> currentData = (List<Map<String, Object>>) document.get("vitalsData");
                    if (currentData == null) {
                        currentData = new ArrayList<>();
                    }
                    Log.d("VitalsFragment", "vitals batch here: "+writeVitalsDataBatch);
                    currentData.addAll(writeVitalsDataBatch);
                    vitalsDocRef.update("vitalsData", currentData)
                            .addOnSuccessListener(aVoid -> Log.d("VitalsFragment", "Batch data successfully updated"))
                            .addOnFailureListener(e -> Log.e("VitalsFragment", "Error updating batch data", e));
                } else {
                    // Create a new document
                    Map<String, Object> batchData = new HashMap<>();
                    batchData.put("vitalsData", vitalsDataBatch);
                    vitalsDocRef.set(batchData)
                            .addOnSuccessListener(aVoid -> Log.d("VitalsFragment", "Batch data successfully written"))
                            .addOnFailureListener(e -> Log.e("VitalsFragment", "Error writing batch data", e));
                }
                writeVitalsDataBatch.clear();
            } else {
                Log.d("Data retrieval failed: ", task.getException().toString());
            }
        });
    }
}