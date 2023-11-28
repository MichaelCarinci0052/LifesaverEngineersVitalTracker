package ca.lifesaver.engineers.it.vital.tracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * Jason Macdonald N01246828 section: 0CB
 * Michael Carinci n01480052 section: 0CB
 * Patrik Prenga n01428752  section: 0CB
 * Nicholas Rafuse n01440073 section: 0CB
 */

public class Menu extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.topmenu,menu);
        return true;
    }
    private void switchTopMenuFragment(Fragment fragment, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Check if the fragment is already in the back stack, if so, pop back stack to it
        if (fragmentManager.findFragmentByTag(tag) != null) {
            fragmentManager.popBackStack(tag, 0);
        } else {
            // Add the transaction to the back stack with a unique tag
            fragmentManager.beginTransaction()
                    .replace(R.id.activity_main_frame_layout, fragment, tag)
                    .addToBackStack(tag)
                    .commit();
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.locationHistoryLayout:
                switchTopMenuFragment(new GPSHistoryFragment(), "GPSHistoryFragment");
                return true;
            case R.id.settings:
                switchTopMenuFragment(new SettingsFragment(), "SettingsFragment");
                return true;
            case R.id.config:
                switchTopMenuFragment(new ConfigurationFragment(), "ConfigurationFragment");
                return true;
            case R.id.feedback:
                switchTopMenuFragment(new FeedbackFragment(), "FeedbackFragment");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}