package ca.lifesaver.engineers.it.vital.tracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {Build.VERSION_CODES.O_MR1}, manifest = Config.NONE)
public class SettingsFragmentTest {

    private SharedPreferences sharedPreferences;
    private FragmentScenario<SettingsFragment> scenario;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        sharedPreferences = context.getSharedPreferences("TestPrefs", Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().commit(); // Clear SharedPreferences before each test
        scenario = FragmentScenario.launchInContainer(SettingsFragment.class);
    }

    @Test
    public void testApplyButtonSavesText() {
        scenario.onFragment(fragment -> {
            Button applyButton = fragment.apply;
            TextView currentTextView = fragment.current;
            fragment.editText.setText("New Home");

            applyButton.performClick();

            assertEquals("New Home", sharedPreferences.getString("userText", null));
            assertEquals("Current Home: New Home", currentTextView.getText().toString());
        });
    }

    @Test
    public void testLockSwitchChangesPreference() {
        scenario.onFragment(fragment -> {
            Switch lockSwitch = fragment.lockswitch;
            assertFalse(lockSwitch.isChecked()); // Default state

            lockSwitch.performClick(); // Toggle the switch

            assertTrue(lockSwitch.isChecked());
            assertTrue(sharedPreferences.getBoolean(fragment.SWITCH_STATE_KEY, false));
        });
    }

    @Test
    public void testNotifSwitchChangesPreference() {
        scenario.onFragment(fragment -> {
            Switch notifSwitch = fragment.notifswitch;
            assertFalse(notifSwitch.isChecked()); // Default state

            notifSwitch.performClick(); // Toggle the switch

            assertTrue(notifSwitch.isChecked());
            assertTrue(sharedPreferences.getBoolean(SettingsFragment.SWITCH_STATE, false));
        });
    }

    @Test
    public void testEditTextEnablesApplyButton() {
        scenario.onFragment(fragment -> {
            Button applyButton = fragment.apply;
            assertFalse(applyButton.isEnabled()); // Button should be disabled by default

            fragment.editText.setText("Test"); // Set text to enable the button

            assertTrue(applyButton.isEnabled());
        });
    }

    @Test
    public void testRestoreSwitchStateRestoresState() {
        sharedPreferences.edit().putBoolean(SettingsFragment.SWITCH_STATE, true).commit();

        scenario.onFragment(fragment -> {
            Switch notifSwitch = fragment.notifswitch;
            assertTrue(notifSwitch.isChecked());
        });
    }
}
