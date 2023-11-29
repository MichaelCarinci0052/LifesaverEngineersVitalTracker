package ca.lifesaver.engineers.it.vital.tracker;

import android.content.DialogInterface;
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

/**
 * Jason Macdonald N01246828 section: 0CB
 * Michael Carinci n01480052 section: 0CB
 * Patrik Prenga n01428752  section: 0CB
 * Nicholas Rafuse n01440073 section: 0CB
 */

public class DeviceFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_device, container, false);
        TextView battery = view.findViewById(R.id.batteryLifeTextView);
        // Find the existing toggle device button and set up the click listener
        Button toggleDeviceButton = view.findViewById(R.id.toggleDeviceButton);
        TextView deviceSelectedTextView = view.findViewById(R.id.deviceselected); // Add this line
        toggleDeviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentText = toggleDeviceButton.getText().toString();
                if ("Turn On".equals(currentText)) {
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
        builder.setItems(sensors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle the sensor selection
                String selectedSensor = sensors[which];
                Toast.makeText(getActivity(), sensors[which] + " selected", Toast.LENGTH_SHORT).show();
                deviceSelectedTextView.setText("Selected Sensor: " + selectedSensor);
            }
        });
        builder.show();
    }
}
