package ca.lifesaver.engineers.it.vital.tracker;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@MediumTest
public class HomeFragmentTest {

    @Before
    public void setUp() {
        FragmentScenario.launchInContainer(HomeFragment.class);
    }

    @Test
    public void testVitalsContainerClick() {
        onView(withId(R.id.vitalsContainer)).perform(click());
        // Check if the Heart Rate text is displayed
        onView(withId(R.id.heartRate)).check(matches(withText("Heart Rate: --")));
    }

    @Test
    public void testGpsContainerClick() {
        onView(withId(R.id.gpsContainer)).perform(click());
        // Assuming there's a UI change in the GPSFragment that can be verified
        // Add an assertion here if there's a specific element to check in the GPS fragment
    }

    @Test
    public void testDeviceContainerClick() {
        onView(withId(R.id.deviceContainer)).perform(click());
        // Check if the Device Name text is displayed
        onView(withId(R.id.devicename2)).check(matches(withText("Device Name:")));
    }

    @Test
    public void testSimulateFallButtonClick() {
        onView(withId(R.id.btnSimulateFall)).perform(click());
        // Check if the AlertDialog title is displayed (needs a custom matcher)
        // Add an assertion here if there's a specific element to check in the AlertDialog
    }

    @Test
    public void testDeviceStateText_Off() {
        onView(withId(R.id.devicebattery)).check(matches(withText("OFF")));
    }

    @Test
    public void testDeviceStateText_On() {
        // You'll need to modify the state in your actual code to test this
        // onView(withId(R.id.devicebattery)).check(matches(withText("100%")));
    }

    // Additional setup for mocking or controlling states can be added as needed
}
