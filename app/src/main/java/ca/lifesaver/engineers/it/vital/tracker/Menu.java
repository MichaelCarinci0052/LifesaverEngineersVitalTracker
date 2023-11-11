package ca.lifesaver.engineers.it.vital.tracker;

import androidx.appcompat.app.AppCompatActivity;
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

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch(item.getItemId()){
            case R.id.settings:
                SettingsFragment settings = new SettingsFragment();
                fragmentManager.beginTransaction().replace(R.id.activity_main_frame_layout, settings).addToBackStack(null).commit();
                return true;
            case R.id.config:
                ConfigurationFragment config = new ConfigurationFragment();
                fragmentManager.beginTransaction().replace(R.id.activity_main_frame_layout, config).commit();
                return true;
            case R.id.feedback:
                FeedbackFragment feedback = new FeedbackFragment();
                fragmentManager.beginTransaction().replace(R.id.activity_main_frame_layout, feedback).commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

}