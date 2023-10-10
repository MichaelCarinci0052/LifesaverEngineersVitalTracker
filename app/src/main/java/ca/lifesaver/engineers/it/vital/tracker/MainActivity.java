
package ca.lifesaver.engineers.it.vital.tracker;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

/**
 * Jason Macdonald N01246828 section: 0CB
 * Michael Carinci n01480052 section: 0CB
 * Patrik Prenga n01428752  section: 0CB
 * Nicholas Rafuse n01440073 section: 0CB
 */
@SuppressWarnings("deprecation")
public class MainActivity extends Menu {

    private BottomNavigationView bottomNavigationView;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.configureToolBar();
        this.configureBottomNavigationView();
        rootView = findViewById(android.R.id.content);

        // Set the default fragment to HomeFragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.activity_main_frame_layout, new HomeFragment())
                    .commit();
            bottomNavigationView.setSelectedItemId(R.id.activity_main_drawer_home); // Assuming this is the ID of the home item
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        showExitConfirmationDialog();
    }

    private void showExitConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.mipmap.app_logo);
        builder.setTitle(R.string.confirmexit);
        builder.setMessage(R.string.areyousure);
        builder.setPositiveButton(R.string.yes, (dialog, which) -> {
            dialog.dismiss();
            MainActivity.super.onBackPressed();
        });
        builder.setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void configureToolBar(){
        Toolbar toolbar = findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(toolbar);
    }

    @SuppressLint("NonConstantResourceId")
    private void configureBottomNavigationView(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        this.bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.activity_main_drawer_account:
                    AccountFragment account = new AccountFragment();
                    fragmentManager.beginTransaction().replace(R.id.activity_main_frame_layout, account).commit();
                    break;
                case R.id.activity_main_drawer_home:
                    HomeFragment home = new HomeFragment();
                    fragmentManager.beginTransaction().replace(R.id.activity_main_frame_layout, home).commit();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showPermissionSnackbar(getString(R.string.granted));
            } else {
                showPermissionSnackbar(getString(R.string.denied));
            }
        }
    }
    private void showPermissionSnackbar(String message) {
        if (rootView != null) {
            Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show();
        }
    }
}
