
package ca.lifesaver.engineers.it.vital.tracker;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
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
            return switchFragment(item.getItemId());
        });
    }

    private boolean switchFragment(int itemId) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (itemId) {
            case R.id.activity_main_drawer_account:
                fragmentManager.beginTransaction().replace(R.id.activity_main_frame_layout, new AccountFragment()).commit();
                break;
            case R.id.activity_main_drawer_home:
                fragmentManager.beginTransaction().replace(R.id.activity_main_frame_layout, new HomeFragment()).commit();
                break;
            case R.id.activity_main_drawer_gps:
                fragmentManager.beginTransaction().replace(R.id.activity_main_frame_layout, new GPSFragment()).commit();
                break;
            case R.id.activity_main_drawer_vitals:
                fragmentManager.beginTransaction().replace(R.id.activity_main_frame_layout, new VitalsFragment()).commit();
                break;
            case R.id.navigation_device:
                fragmentManager.beginTransaction().replace(R.id.activity_main_frame_layout, new DeviceFragment()).commit();
                break;
            default:
                return false;
        }
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
