package ca.lifesaver.engineers.it.vital.tracker;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import androidx.appcompat.app.AppCompatActivity;

public class TestHostActivity extends AppCompatActivity implements HomeFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout frameLayout = new FrameLayout(this);
        frameLayout.setId(R.id.homescreen); // Make sure this ID doesn't conflict with other IDs
        setContentView(frameLayout);

        if (savedInstanceState == null) {
            // When the activity is first created, inject the HomeFragment
            getSupportFragmentManager().beginTransaction()
                    .add(frameLayout.getId(), new HomeFragment(), "HomeFragment")
                    .commit();
        }
    }

    // Implement the HomeFragment.OnFragmentInteractionListener interface methods
    @Override
    public void onSwitchToVitalsFragment() {
        // Provide the logic to switch to VitalsFragment or leave blank for testing
    }

    @Override
    public void onSwitchToGPSFragment() {
        // Provide the logic to switch to GPSFragment or leave blank for testing
    }

    @Override
    public void onSwitchToDeviceFragment() {
        // Provide the logic to switch to DeviceFragment or leave blank for testing
    }
}
