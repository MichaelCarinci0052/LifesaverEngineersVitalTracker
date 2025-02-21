package ca.lifesaver.engineers.it.vital.tracker;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import java.util.HashMap;
import java.util.Map;

/**
 * Jason Macdonald N01246828 section: 0CB
 * Michael Carinci n01480052 section: 0CB
 * Patrik Prenga n01428752  section: 0CB
 * Nicholas Rafuse n01440073 section: 0CB
 */

public class DeviceFragment extends Fragment {
    private TextView battery;
    private Map<String, Boolean> sensorStates;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_device, container, false);
        battery = view.findViewById(R.id.batteryLifeTextView);
        // Find the existing toggle device button and set up the click listener
        TextView deviceSelectedTextView = view.findViewById(R.id.deviceselected); // Add this line
        Button toggleDeviceButton = view.findViewById(R.id.toggleDeviceButton);

        sensorStates = new HashMap<>();
        sensorStates.put("Heartbeat and Oxygen Sensor", true);
        sensorStates.put("Accelerometer", true);
        sensorStates.put("GPS", true);
        sensorStates.put("Temperature Sensor", true);

        toggleDeviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isTurnedOn = AppPreferencesManager.isDeviceTurnedOn(requireContext());
                if (!isTurnedOn) {
                    toggleDeviceButton.setText("Turn Off");
                    // Logic to turn the device ON goes here
                    Toast.makeText(getActivity(), "Device turned ON", Toast.LENGTH_SHORT).show();
                    battery.setText("100%");
                } else {
                    toggleDeviceButton.setText("Turn On");
                    // Logic to turn the device OFF goes here
                    battery.setText("OFF");
                    Toast.makeText(getActivity(), "Device turned OFF", Toast.LENGTH_SHORT).show();
                }

                // Toggle the device state in the shared preferences
                AppPreferencesManager.setDeviceState(requireContext(), !isTurnedOn);
                updateDeviceStateText();
            }
        });

        // Add a new button for sensors and set up the click listener
        Button sensorsButton = view.findViewById(R.id.buttonSensors);
        sensorsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSensorsList(deviceSelectedTextView);
            }
        });



        return view;
    }

    private void showSensorsList(TextView deviceSelectedTextView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("List of Sensors");

        String[] sensors = {"Heartbeat and Oxygen Sensor", "Accelerometer", "GPS", "Temperature Sensor"};
        boolean[] checkedItems = new boolean[sensors.length];
        for (int i = 0; i < sensors.length; i++) {
            checkedItems[i] = sensorStates.getOrDefault(sensors[i], false);
        }

        builder.setMultiChoiceItems(sensors, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                // Update sensor state
                sensorStates.put(sensors[which], isChecked);
            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Optional: Handle the "OK" button click here
            }
        });

        builder.show();
    }

    private void updateDeviceStateText() {
        boolean isTurnedOn = AppPreferencesManager.isDeviceTurnedOn(requireContext());
        TextView deviceStateTextView = getActivity().findViewById(R.id.batteryLifeTextView);
        if (deviceStateTextView != null) {
            deviceStateTextView.setText(isTurnedOn ? "100%" : "OFF");
        }
    }
}
