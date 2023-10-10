package ca.lifesaver.engineers.it.vital.tracker;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.os.Handler;

import java.util.Random;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("VITALS_CHANNEL_ID", "Vitals Alerts", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Notifications for abnormal vitals data");
            NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
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
        // Reference the TextViews
        TextView tvHeartRate = view.findViewById(R.id.heartRateValue);
        TextView tvOxygenLevel = view.findViewById(R.id.oxygenValue);
        TextView tvBodyTemp = view.findViewById(R.id.bodyTempValue);

        // Set dummy data
        tvHeartRate.setText("Heart Rate: 80 BPM");
        tvOxygenLevel.setText("Oxygen Level: 98%");
        tvBodyTemp.setText("Body Temperature: 98.6°F");
        updateRunnable = new Runnable() {
            @Override
            public void run() {
                // Generate random data
                int heartRate = 60 + random.nextInt(40);  // Random value between 60 and 100
                int oxygenLevel = 90 + random.nextInt(10);  // Random value between 90 and 100
                float bodyTemp = 97.0f + random.nextFloat() * 3.0f;  // Random value between 97.0 and 100.0


                if (heartRate < 60 || heartRate > 100) {
                    sendNotification("Abnormal Heart Rate", "Detected heart rate: " + heartRate + " BPM");
                }
                if (oxygenLevel < 91) {
                    sendNotification("Low Oxygen Level", "Detected oxygen level: " + oxygenLevel + "%");
                }
                if (bodyTemp < 97.0f || bodyTemp > 100.1f) {
                    sendNotification("Abnormal Body Temperature", String.format("Detected body temperature: %.1f°F", bodyTemp));
                }

                // Update the UI
                tvHeartRate.setText("Heart Rate: " + heartRate + " BPM");
                tvOxygenLevel.setText("Oxygen Level: " + oxygenLevel + "%");
                tvBodyTemp.setText(String.format("Body Temperature: %.1f°F", bodyTemp));
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
}