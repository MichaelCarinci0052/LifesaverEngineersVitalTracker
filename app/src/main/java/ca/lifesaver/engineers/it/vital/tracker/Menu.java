package ca.lifesaver.engineers.it.vital.tracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.view.MenuInflater;
import android.view.MenuItem;

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
                fragmentManager.beginTransaction().replace(R.id.activity_main_frame_layout, settings).commit();
                return true;
            case R.id.config:
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

}