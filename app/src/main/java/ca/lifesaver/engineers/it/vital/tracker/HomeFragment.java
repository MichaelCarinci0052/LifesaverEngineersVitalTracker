package ca.lifesaver.engineers.it.vital.tracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
    public void onDataChanged(String heartRate, String oxygenLevel, String bodyTemp) {

        TextView tvHeartRateHome = getView().findViewById(R.id.heartRate);
        TextView tvOxygenLevel = getView().findViewById(R.id.oxygenRate);
        TextView tvBodyTemp = getView().findViewById(R.id.temp);
        tvHeartRateHome.setText(heartRate);
        tvOxygenLevel.setText(oxygenLevel);
        tvBodyTemp.setText(bodyTemp);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mAuth = FirebaseAuth.getInstance();
        userAccountName = view.findViewById(R.id.userAccountName);

        //SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        //String username = sharedPreferences.getString("username", "Default User");
        String username = mAuth.getCurrentUser().getDisplayName();
        userAccountName.setText(username);


        DeviceFragment deviceFragment = new DeviceFragment();
        getChildFragmentManager().beginTransaction()
                .replace(R.id.deviceContainer, deviceFragment)
                .commit();


        // Load the GPS Fragment
        GPSFragment gpsFragment = new GPSFragment();
        getChildFragmentManager().beginTransaction()
                .replace(R.id.gpsContainer, gpsFragment)
                .commit();

        // Load the Vitals Fragment (if you want to display it)
        VitalsFragment vitalsFragment = new VitalsFragment();
        getChildFragmentManager().beginTransaction()
                .add(vitalsFragment, "vitalfragment")
                .commit();

        return view;
    }

}
