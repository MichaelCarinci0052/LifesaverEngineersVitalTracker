package ca.lifesaver.engineers.it.vital.tracker;

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

        // Assuming you have a method or a way to get the user's account name from within the fragment
        String accountName = getAccountName();
        userAccountName.setText(accountName);

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

    private String getAccountName() {
        // Logic to get the user's account name.
        // For this example, returning a static name.
        return "John Doe";
    }
}
