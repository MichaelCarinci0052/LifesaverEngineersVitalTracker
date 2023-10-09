package ca.lifesaver.engineers.it.vital.tracker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.os.Handler;
import java.util.Random;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;

public class VitalsFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private Handler handler;
    private Runnable updateRunnable;
    private Random random;
    public VitalsFragment() {
        // Required empty public constructor
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

                // Update the UI
                tvHeartRate.setText("Heart Rate: " + heartRate + " BPM");
                tvOxygenLevel.setText("Oxygen Level: " + oxygenLevel + "%");
                tvBodyTemp.setText(String.format("Body Temperature: %.1f°F", bodyTemp));

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
}
