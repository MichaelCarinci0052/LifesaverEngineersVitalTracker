package ca.lifesaver.engineers.it.vital.tracker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import ca.lifesaver.engineers.it.vital.tracker.R;

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

        // Find the button and set up the click listener
        Button toggleDeviceButton = view.findViewById(R.id.toggleDeviceButton);
        toggleDeviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentText = toggleDeviceButton.getText().toString();
                if ("Turn On".equals(currentText)) {
                    toggleDeviceButton.setText("Turn Off");
                    // Logic to turn the device ON goes here
                } else {
                    toggleDeviceButton.setText("Turn On");
                    // Logic to turn the device OFF goes here
                }
            }
        });

        return view;
    }
}
