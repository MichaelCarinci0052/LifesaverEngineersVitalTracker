package ca.lifesaver.engineers.it.vital.tracker;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.os.Handler;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * Jason Macdonald N01246828 section: 0CB
 * Michael Carinci n01480052 section: 0CB
 * Patrik Prenga n01428752  section: 0CB
 * Nicholas Rafuse n01440073 section: 0CB
 */

public class VitalsFragment extends Fragment {

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
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        handler = new Handler();
        random = new Random();



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_vitals, container, false);
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
                // Generate random data
                int heartRate = 60 + random.nextInt(40);  // Random value between 60 and 100
                int oxygenLevel = 90 + random.nextInt(10);  // Random value between 90 and 100
                float bodyTemp = 97.0f + random.nextFloat() * 3.0f;  // Random value between 97.0 and 100.0

                if (currentUser != null) {
                    String userId2 = currentUser.getUid();

                    // Reference to the specific 'vitals' document inside the user's document
                    DocumentReference vitalsDocRef = db.collection("userId").document(userId).collection("vitals").document("data");

                    Map<String, Object> vitalsDataMap = new HashMap<>();
                    vitalsDataMap.put("heartRate", heartRate);
                    vitalsDataMap.put("oxygenLevel", oxygenLevel);
                    vitalsDataMap.put("bodyTemp", bodyTemp);

                    vitalsDocRef.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Document exists, update it
                                List<Map<String, Object>> currentData = (List<Map<String, Object>>) document.get("vitalsData");
                                if (currentData == null) {
                                    currentData = new ArrayList<>();
                                }
                                currentData.add(vitalsDataMap);
                                vitalsDocRef.update("vitalsData", currentData);
                            } else {
                                // Document doesn't exist, create it
                                List<Map<String, Object>> initialData = new ArrayList<>();
                                initialData.add(vitalsDataMap);
                                vitalsDocRef.set(Collections.singletonMap("vitalsData", initialData));
                            }
                        } else {
                            Log.d("Data retrieval failed: ", task.getException().toString());
                        }
                    });
                }


                if (heartRate < 60 || heartRate > 100) {
                    if (notifs)  {sendNotification("Abnormal Heart Rate", "Detected heart rate: " + heartRate + " BPM");};
                }
                if (oxygenLevel < 91) {
                    if (notifs)  {sendNotification("Low Oxygen Level", "Detected oxygen level: " + oxygenLevel + "%");};
                }
                if (bodyTemp < 97.0f || bodyTemp > 97.1f) {
                    if (notifs)  {
                        sendNotification("Abnormal Body Temperature", String.format("Detected body temperature: %.1f°F", bodyTemp)
                        );};
                }

                // Update the UI
                tvHeartRate.setText( heartRate + " BPM");
                tvOxygenLevel.setText(oxygenLevel + "%");
                tvBodyTemp.setText(String.format("%.1f°F", bodyTemp));
                if (mListener != null) {
                    mListener.onDataChanged(
                            "Heart Rate: " + heartRate + " BPM",
                            "Oxygen Level: " + oxygenLevel + "%",
                            String.format("Body Temperature: %.1f°F", bodyTemp)
                    );
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
}