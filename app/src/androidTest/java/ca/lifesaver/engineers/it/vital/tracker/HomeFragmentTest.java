package ca.lifesaver.engineers.it.vital.tracker;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.view.View;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@MediumTest
public class HomeFragmentTest {

    @Rule
    public ActivityScenarioRule<TestHostActivity> activityRule = new ActivityScenarioRule<>(TestHostActivity.class);

    @Before
    public void setUp() {
        activityRule.getScenario().onActivity(activity -> {
            HomeFragment fragment = new HomeFragment();
            activity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.homescreen, fragment)
                    .commitNow();
        });
    }

    @Test
    public void testVitalsContainerClick() {
        onView(withId(R.id.vitalsContainer)).perform(click());
        // Check if the Heart Rate text is displayed
       // onView(withId(R.id.heartRate)).check(matches(withText("Heart Rate: --")));
    }

    @Test
    public void testGpsContainerClick() {
        onView(withId(R.id.gpsContainer)).perform(click());
        onView(withId(R.id.map)).check(matches(isDisplayed()));
    }

    @Test
    public void testDeviceContainerClick() {
        onView(withId(R.id.deviceContainer)).perform(click());
        // Check if the Device Name text is displayed
        onView(withId(R.id.devicename2)).check(matches(withText("Device Name:")));
    }

    @Test
    public void testSimulateFallButtonClick() {
        onView(withId(R.id.btnSimulateFall)).perform(scrollTo()).perform(click());
    }

    @Test
    public void testDeviceStateText_Off() {
        onView(withId(R.id.devicebattery)).check(matches(withText("OFF")));
    }

}
