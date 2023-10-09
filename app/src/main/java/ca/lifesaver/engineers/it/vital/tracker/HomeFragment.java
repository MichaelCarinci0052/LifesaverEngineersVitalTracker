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

public class HomeFragment extends Fragment {

    private TextView userAccountName;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        userAccountName = view.findViewById(R.id.userAccountName);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "Default User");

        // Load the GPS Fragment
        GPSFragment gpsFragment = new GPSFragment();
        getChildFragmentManager().beginTransaction()
                .replace(R.id.gpsContainer, gpsFragment)
                .commit();

        // Load the Vitals Fragment (if you want to display it)
        VitalsFragment vitalsFragment = new VitalsFragment();
        getChildFragmentManager().beginTransaction()
                .replace(R.id.vitalsContainer, vitalsFragment)
                .commit();

        return view;
    }

}
