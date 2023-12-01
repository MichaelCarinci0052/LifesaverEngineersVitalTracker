package ca.lifesaver.engineers.it.vital.tracker;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.functions.FirebaseFunctions;

import java.util.HashMap;

/**
 * Jason Macdonald N01246828 section: 0CB
 * Michael Carinci n01480052 section: 0CB
 * Patrik Prenga n01428752  section: 0CB
 * Nicholas Rafuse n01440073 section: 0CB
 */

public class HomeFragment extends Fragment implements VitalsFragment.OnVitalsDataChangedListener {
    private FirebaseAuth mAuth;
    private TextView userAccountName;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mAuth = FirebaseAuth.getInstance();
        userAccountName = view.findViewById(R.id.userAccountName);
        String username = mAuth.getCurrentUser().getDisplayName();
        userAccountName.setText(capitalizeFirstLetter(username));

        //DeviceFragment deviceFragment = new DeviceFragment();
        //getChildFragmentManager().beginTransaction()
                //.replace(R.id.deviceContainer, deviceFragment)
                //.commit();

        GPSFragment gpsFragment = new GPSFragment();
        getChildFragmentManager().beginTransaction()
                .replace(R.id.gpsContainer, gpsFragment)
                .addToBackStack(null)
                .commit();

        VitalsFragment vitalsFragment = new VitalsFragment();
        getChildFragmentManager().beginTransaction()
                .add(vitalsFragment, "vitalfragment")
                .commit();

        FrameLayout vitalsContainer = view.findViewById(R.id.vitalsContainer);
        FrameLayout gpsContainer = view.findViewById(R.id.gpsContainer);
        FrameLayout deviceContainer = view.findViewById(R.id.deviceContainer);

        vitalsContainer.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onSwitchToVitalsFragment();
            }
        });
        gpsContainer.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onSwitchToGPSFragment();
            }
        });

        deviceContainer.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onSwitchToDeviceFragment();
            }
        });

        Button btnSimulateFall = view.findViewById(R.id.btnSimulateFall);
        btnSimulateFall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFallDetectionDialog();
            }
        });

        Button turnon = view.findViewById(R.id.turnon);
        TextView battery = view.findViewById(R.id.devicebattery);
        turnon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentText = turnon.getText().toString();
                if ("Turn On".equals(currentText)) {
                    turnon.setText("Turn Off");
                    // Logic to turn the device ON goes here
                    Toast.makeText(getActivity(), "Device turned ON", Toast.LENGTH_SHORT).show();
                    battery.setText("Battery Level: 100%");
                } else {
                    turnon.setText("Turn On");
                    // Logic to turn the device OFF goes here
                    battery.setText("Battery Level: OFF");
                    Toast.makeText(getActivity(), "Device turned OFF", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }


    private void showFallDetectionDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Fall Detected")
                .setMessage("We've detected a fall, would you like to alert authorities?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Code to alert authorities
                    }
                })
                .setNegativeButton("No", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public interface OnFragmentInteractionListener {
        void onSwitchToVitalsFragment();
        void onSwitchToGPSFragment();
        void onSwitchToDeviceFragment();
    }

    private OnFragmentInteractionListener mListener;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        StringBuilder capitalizedString = new StringBuilder();
        String[] words = input.split("\\s+");

        for (String word : words) {
            String capitalizedWord = word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
            capitalizedString.append(capitalizedWord).append(" ");
        }

        return capitalizedString.toString().trim(); // Return the concatenated string
    }
    void navigateToGpsScreen() {
        GPSFragment gpsFragment = new GPSFragment();
        // Optionally add arguments to the fragment before adding it
        // gpsFragment.setArguments(bundle);

        getChildFragmentManager().beginTransaction()
                .replace(R.id.gpsContainer, gpsFragment) // Replace 'container' with the ID of your fragment container
                .addToBackStack(null)
                .commit();
    }

    private void navigateToDeviceScreen() {
        DeviceFragment deviceFragment = new DeviceFragment();
        // Optionally add arguments to the fragment before adding it
        // deviceFragment.setArguments(bundle);

        getChildFragmentManager().beginTransaction()
                .replace(R.id.deviceContainer, deviceFragment) // Replace 'container' with the ID of your fragment container
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onDataChanged(String heartRate, String oxygenLevel, String bodyTemp) {
        TextView tvHeartRateHome = getView().findViewById(R.id.heartRate);
        TextView tvOxygenLevel = getView().findViewById(R.id.oxygenRate);
        TextView tvBodyTemp = getView().findViewById(R.id.temp);

        tvHeartRateHome.setText(heartRate);
        tvOxygenLevel.setText(oxygenLevel);
        tvBodyTemp.setText(bodyTemp);
    }

}
