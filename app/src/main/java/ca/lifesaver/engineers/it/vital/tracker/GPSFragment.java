package ca.lifesaver.engineers.it.vital.tracker;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * Jason Macdonald N01246828 section: 0CB
 * Michael Carinci n01480052 section: 0CB
 * Patrik Prenga n01428752  section: 0CB
 * Nicholas Rafuse n01440073 section: 0CB
 */
@SuppressWarnings("deprecation")
public class GPSFragment extends Fragment implements OnMapReadyCallback{
    public GPSFragment() {
        // Required empty public constructor
    }

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    public GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private boolean locationUpdatesStarted = false;
    private DocumentReference fbLocation;

    private DocumentReference docRef;
    private long lastLocationUpdateInterval = 0;
    private GPSSharedViewModel viewModel;
    double vmLat = 0.0;
    double vmLong = 0.0;
    boolean isLatitudeSet = false;
    boolean isLongitudeSet = false;
    boolean markerDelete;
    private Marker marker;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_g_p_s, container, false);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = Objects.requireNonNull(currentUser).getUid();
        fbLocation = db.collection("userId").document(userId);

        docRef = fbLocation.collection("current_location").document("location");

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
        viewModel = new ViewModelProvider(requireActivity()).get(GPSSharedViewModel.class);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
        createLocationCallback();

        viewModel.getLatitude().observe(getViewLifecycleOwner(), latitude -> {
            if(latitude != null) {
                vmLat = viewModel.getLatitude().getValue();
                isLatitudeSet = true;
                if(isLongitudeSet){
                    LatLng vmLoc = new LatLng(vmLat, vmLong);
                    mMap.addMarker(new MarkerOptions().position(vmLoc));
                    isLongitudeSet = false;
                    isLatitudeSet = false;
                }
            }
        });

        viewModel.getLongitude().observe(getViewLifecycleOwner(), longitude -> {
            if(longitude != null){
                vmLong = viewModel.getLongitude().getValue();
                isLongitudeSet = true;
                if(isLatitudeSet){
                    LatLng vmLoc = new LatLng(vmLat, vmLong);
                    mMap.addMarker(new MarkerOptions().position(vmLoc));
                    isLongitudeSet = false;
                    isLatitudeSet = false;
                }
            }
        });

        viewModel.getDelete().observe(getViewLifecycleOwner(), delete -> {
            boolean mapMarkers = viewModel.getDelete().getValue();
            if(mapMarkers){
                mMap.clear();
                viewModel.setDelete(false);
            }
        });

        return view;
    }



    private void updateLocationToFirebase(double latitude, double longitude) {
        Map<String, Object> locationMap = new HashMap<>();
        locationMap.put("latitude", latitude);
        locationMap.put("longitude", longitude);

        fbLocation.collection("current_location")
                .document("location")
                .set(locationMap);

        long currentTimeMillis = System.currentTimeMillis();
        //An hour
        int locationHistoryInterval = 3600;

        if (currentTimeMillis - lastLocationUpdateInterval >= locationHistoryInterval) {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());

            fbLocation.collection("location_history")
                    .document(timestamp)
                    .set(locationMap);

            lastLocationUpdateInterval = currentTimeMillis;
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            startLocationUpdates();
        } else {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    void createLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (isAdded()) {
                    for (Location location : locationResult.getLocations()) {
                        // Update the user's current location on the map
                        // updateLocationToFirebase(location.getLatitude(), location.getLongitude());
                        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                double fbLatitude = 0;
                                double fbLongitude = 0;
                                if (documentSnapshot.exists()) {
                                    fbLatitude = documentSnapshot.getDouble("latitude");
                                    fbLongitude = documentSnapshot.getDouble("longitude");
                                }

                                LatLng currentlocation = new LatLng(fbLatitude, fbLongitude);
                                if(marker != null){
                                    marker.remove();
                                }
                                marker = mMap.addMarker(new MarkerOptions().position(currentlocation));
                            }
                        });
                    }
                }
            }
        };
    }

    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            //A minute
            int currentLocationInterval = 60 * 1000;
            LocationRequest locationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(currentLocationInterval);

            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
            locationUpdatesStarted = true;
        } else {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onMapReady(mMap);
                showPermissionSnackbar(getString(R.string.granted));
            } else {
                showPermissionSnackbar(getString(R.string.denied));
            }
        }
    }
    private void showPermissionSnackbar(String message) {
        View rootView = getView(); // Get the root view of the fragment
        if (rootView != null) {
            Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show();
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
    }
    @Override
    public void onResume() {
        super.onResume();
        if (locationUpdatesStarted) {
            startLocationUpdates();
        }
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
        locationUpdatesStarted = false;
    }


}
