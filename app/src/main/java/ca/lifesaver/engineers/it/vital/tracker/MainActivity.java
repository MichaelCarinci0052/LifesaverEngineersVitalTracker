
package ca.lifesaver.engineers.it.vital.tracker;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


/**
 * Jason Macdonald N01246828 section: 0CB
 * Michael Carinci n01480052 section: 0CB
 * Patrik Prenga n01428752  section: 0CB
 * Nicholas Rafuse n01440073 section: 0CB
 */
@SuppressWarnings("deprecation")
public class MainActivity extends Menu implements HomeFragment.OnFragmentInteractionListener {

    private BottomNavigationView bottomNavigationView;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private View rootView;
    private SharedViewModal viewModel;
    private int currentFragmentId = R.id.activity_main_drawer_home; // default to home fragment

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            NotificationChannel existingChannel = notificationManager.getNotificationChannel("VITALS_CHANNEL_ID");

            if (existingChannel == null) {
                NotificationChannel channel = new NotificationChannel("VITALS_CHANNEL_ID", "Vitals Alerts", NotificationManager.IMPORTANCE_HIGH);
                channel.setDescription("Notifications for abnormal vitals data");
                notificationManager.createNotificationChannel(channel);
            }
        }
        String deviceName = Build.MODEL;
        Map<String,Object> modelMap = new HashMap<>();
        modelMap.put("phone_model", deviceName);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = Objects.requireNonNull(currentUser).getUid();
        DocumentReference phoneModel = db.collection("userId").document(userId);

        phoneModel.collection("phone_data")
                  .document("model")
                  .set(modelMap);

        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            Fragment currentFragment = getCurrentFragment();

            if (currentFragment instanceof HomeFragment && bottomNavigationView.getSelectedItemId() != R.id.activity_main_drawer_home) {
                bottomNavigationView.setSelectedItemId(R.id.activity_main_drawer_home);
            } else if (currentFragment instanceof VitalsFragment && bottomNavigationView.getSelectedItemId() != R.id.activity_main_drawer_vitals) {
                bottomNavigationView.setSelectedItemId(R.id.activity_main_drawer_vitals);
            } else if (currentFragment instanceof AccountFragment && bottomNavigationView.getSelectedItemId() != R.id.activity_main_drawer_account) {
                bottomNavigationView.setSelectedItemId(R.id.activity_main_drawer_account);
            } else if (currentFragment instanceof GPSFragment && bottomNavigationView.getSelectedItemId() != R.id.activity_main_drawer_gps) {
                bottomNavigationView.setSelectedItemId(R.id.activity_main_drawer_gps);
            } else if (currentFragment instanceof DeviceFragment && bottomNavigationView.getSelectedItemId() != R.id.navigation_device) {
                bottomNavigationView.setSelectedItemId(R.id.navigation_device);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (currentFragmentId == R.id.activity_main_drawer_home && getSupportFragmentManager().getBackStackEntryCount() <= 1) {
            // On home screen and no other fragments in the back stack
            showExitConfirmationDialog();
        } else {
            // Not on home screen or other fragments are in the back stack
            super.onBackPressed();
            // Update the currentFragmentId based on the new top fragment in the stack
            Fragment currentFragment = getCurrentFragment();
            if (currentFragment instanceof HomeFragment) {
                currentFragmentId = R.id.activity_main_drawer_home;
            } else if (currentFragment instanceof VitalsFragment) {
                currentFragmentId = R.id.activity_main_drawer_vitals;
            }
        }
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
            return switchFragment(item.getItemId());
        });
    }

    private boolean switchFragment(int itemId) {
        if (itemId == currentFragmentId) {
            return true; // Do nothing if the fragment is already displayed
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment newFragment = fragmentManager.findFragmentByTag(String.valueOf(itemId));
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if (newFragment == null) {
            // Create new fragment instance based on itemId
            switch (itemId) {
                case R.id.activity_main_drawer_account:
                    newFragment = new AccountFragment();
                    break;
                case R.id.activity_main_drawer_home:
                    newFragment = new HomeFragment();
                    break;
                case R.id.activity_main_drawer_gps:
                    newFragment = new GPSFragment();
                    break;
                case R.id.activity_main_drawer_vitals:
                    newFragment = new VitalsFragment();
                    break;
                case R.id.navigation_device:
                    newFragment = new DeviceFragment();
                    break;
                default:
                    return false;
            }
        } else {
            // Clear the existing instance from the back stack
            fragmentManager.popBackStack(String.valueOf(itemId), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        // Hide the current fragment
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.activity_main_frame_layout);
        if (currentFragment != null) {
            transaction.hide(currentFragment);
        }

        // Show the new fragment
        if (!newFragment.isAdded()) {
            transaction.add(R.id.activity_main_frame_layout, newFragment, String.valueOf(itemId));
        } else {
            transaction.show(newFragment);
        }

        transaction.addToBackStack(String.valueOf(itemId));
        transaction.commit();
        currentFragmentId = itemId;
        return true;

    }

    // Implement the interface method
    @Override
    public void onSwitchToVitalsFragment() {
        // Switch to the VitalsFragment tab
        bottomNavigationView.setSelectedItemId(R.id.activity_main_drawer_vitals);
    }
    @Override
    public void onSwitchToGPSFragment() {
        bottomNavigationView.setSelectedItemId(R.id.activity_main_drawer_gps);
    }

    @Override
    public void onSwitchToDeviceFragment() {
        bottomNavigationView.setSelectedItemId(R.id.navigation_device);
    }






    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Handling location permission
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showPermissionSnackbar(getString(R.string.granted));
            } else {
                showPermissionSnackbar(getString(R.string.denied));
            }
        }

        // Handling phone call permission for HomeFragment
        if (requestCode == HomeFragment.REQUEST_PHONE_CALL) {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.activity_main_frame_layout);
            if (fragment instanceof HomeFragment) {
                fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }

    private void showPermissionSnackbar(String message) {
        if (rootView != null) {
            Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show();
        }
    }
    private Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.activity_main_frame_layout);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            // ... handle other menu items ...
        }
        return super.onOptionsItemSelected(item);
    }
}
