package ca.lifesaver.engineers.it.vital.tracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.auth.FirebaseAuth;

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
        userAccountName.setText(username);



        DeviceFragment deviceFragment = new DeviceFragment();
        getChildFragmentManager().beginTransaction()
                .replace(R.id.deviceContainer, deviceFragment)
                .commit();


         //Load the GPS Fragment
        GPSFragment gpsFragment = new GPSFragment();
        getChildFragmentManager().beginTransaction()
                .replace(R.id.gpsContainer, gpsFragment)
                .addToBackStack(null)
                .commit();

        // Load the Vitals Fragment (if you want to display it)
        VitalsFragment vitalsFragment = new VitalsFragment();
        vitalsFragment.setOnVitalsDataChangedListener(this); // 'this' refers to HomeFragment which implements OnVitalsDataChangedListener
        getChildFragmentManager().beginTransaction()
                .add(vitalsFragment, "vitalfragment")
                .commit();




        setupContainerClickListeners(view);

        return view;
    }


    private void setupContainerClickListeners(View view) {
        View vitalsContainer = view.findViewById(R.id.vitalsContainer);
        vitalsContainer.setOnClickListener(v -> navigateToVitalsFragment());

        View gpsContainer = view.findViewById(R.id.gpsContainer);
        gpsContainer.setOnClickListener(v -> navigateToGPSFragment());

        View deviceContainer = view.findViewById(R.id.deviceContainer);
        deviceContainer.setOnClickListener(v -> navigateToDeviceFragment());
    }

    private void navigateToVitalsFragment() {

    }

    private void navigateToGPSFragment() {

    }

    private void navigateToDeviceFragment() {
      
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
