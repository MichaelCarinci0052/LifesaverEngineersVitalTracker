
package ca.lifesaver.engineers.it.vital.tracker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.os.Bundle;

/**
 * Jason Macdonald N01246828 section: 0CB
 * Michael Carinci n01480052 section: 0CB
 * Patrik Prenga n01428752  section: 0CB
 * Nicholas Rafuse n01440073 section: 0CB
 */
public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.configureToolBar();
        this.configureBottomNavigationView();
    }

    @Override
    public void onBackPressed() {
        showExitConfirmationDialog();
    }

    private void showExitConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.mipmap.app_logo);
        builder.setTitle(R.string.confirmexit);
        builder.setMessage(R.string.areyousure);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                MainActivity.super.onBackPressed();
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void configureToolBar(){
        this.toolbar = (Toolbar) findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(toolbar);
    }

    private void configureBottomNavigationView(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        this.bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.activity_main_drawer_account:
                    AccountFragment account = new AccountFragment();
                    fragmentManager.beginTransaction().replace(R.id.activity_main_frame_layout, account).commit();
                    break;
                case R.id.activity_main_drawer_gps:
                    GPSFragment gps = new GPSFragment();
                    fragmentManager.beginTransaction().replace(R.id.activity_main_frame_layout, gps).commit();
                    break;
                case R.id.activity_main_drawer_vitals:
                    VitalsFragment vitals = new VitalsFragment();
                    fragmentManager.beginTransaction().replace(R.id.activity_main_frame_layout, vitals).commit();
                    break;
            }
            return true;
        });
    }
}
