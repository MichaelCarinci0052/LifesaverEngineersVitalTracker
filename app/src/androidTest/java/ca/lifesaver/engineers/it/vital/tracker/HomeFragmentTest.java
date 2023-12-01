package ca.lifesaver.engineers.it.vital.tracker;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.Lifecycle;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.widget.TextView;

@RunWith(AndroidJUnit4.class)
public class HomeFragmentTest {

    private FragmentScenario<HomeFragment> scenario;



    @Before
    public void setUp() throws Exception {
        // Launch the fragment in a test environment
        scenario = FragmentScenario.launchInContainer(HomeFragment.class);
    }

    @Test
    public void testFragmentNotNull() {
        scenario.onFragment(fragment -> {
            assertNotNull(fragment);
        });
    }

    // Add more tests as necessary to test the non-UI logic of your fragment
    @Test
    public void testOnDataChanged() {
        // This test assumes that your onDataChanged method is public and you can call it directly.
        // If it's not public, you'll need to test this behavior by simulating whatever triggers the data change.
        scenario.onFragment(fragment -> {
            fragment.onDataChanged("120", "95", "36.5");
            TextView tvHeartRateHome = fragment.getView().findViewById(R.id.heartRate);
            TextView tvOxygenLevel = fragment.getView().findViewById(R.id.oxygenRate);
            TextView tvBodyTemp = fragment.getView().findViewById(R.id.temp);

            // Assert that the views display the data correctly
            assertNotNull(tvHeartRateHome);
            assertNotNull(tvOxygenLevel);
            assertNotNull(tvBodyTemp);
            assertTrue(tvHeartRateHome.getText().toString().equals("120"));
            assertTrue(tvOxygenLevel.getText().toString().equals("95"));
            assertTrue(tvBodyTemp.getText().toString().equals("36.5"));
        });
    }

    // You can also simulate interactions and assert on expected behaviors
    @Test
    public void testNavigationToGpsScreen() {
        scenario.onFragment(fragment -> {
            fragment.navigateToGpsScreen();
            // After navigation, you can check if the GPSFragment is displayed
            // This could be a little complex because it involves the FragmentManager
            // and possibly the back stack. Consider using Espresso for UI navigation testing.
        });
    }
}
