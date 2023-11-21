package ca.lifesaver.engineers.it.vital.tracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.telecom.Call;
import android.util.Base64;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.module.AppGlideModule;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.common.api.Response;
import com.google.android.gms.common.util.IOUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import javax.security.auth.callback.Callback;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

/**
 * Jason Macdonald N01246828 section: 0CB
 * Michael Carinci n01480052 section: 0CB
 * Patrik Prenga n01428752  section: 0CB
 * Nicholas Rafuse n01440073 section: 0CB
 */
public class AccountFragment extends Fragment  {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TextView usernameTextView;
    private EditText passwordEditText;
    private TextView fullname;
    private Button changePasswordButton;
    private Button buttonLogout;
    FirebaseAuth mAuth  ;
    private FloatingActionButton fabInsertImage;
    private ImageView profileImageView;

    private ActivityResultLauncher<Intent> activityResultLauncher;
    private LruCache<String, Bitmap> memoryCache;
    public AccountFragment() {
        // Required empty public constructor
    }


    public static AccountFragment newInstance(String param1, String param2) {
        AccountFragment fragment = new AccountFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        memoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }
        };

        loadProfilePicture();
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            Uri imageUri = data.getData();
                            uploadImageToFirebase(imageUri);
                        }
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        mAuth = FirebaseAuth.getInstance();
        profileImageView = view.findViewById(R.id.imageView5); // Replace with your ImageView ID
        fabInsertImage = view.findViewById(R.id.fabInsertImage);
        usernameTextView = view.findViewById(R.id.username3);
        passwordEditText = view.findViewById(R.id.editTextpassword);
        changePasswordButton = view.findViewById(R.id.changepassword);
        buttonLogout = view.findViewById(R.id.buttonLogout);
        fullname = view.findViewById(R.id.fullname);

        //SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        //String user = sharedPreferences.getString("username", "username3");

        FirebaseUser user = mAuth.getCurrentUser();
        String email = user.getEmail();
        String firstname = user.getDisplayName();
        usernameTextView.setText(email);
        fullname.setText(firstname);

        fabInsertImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            activityResultLauncher.launch(Intent.createChooser(intent, "Select Picture"));
        });
        //handle logout
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                requireActivity().finish();
            }
        });
        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // no function for now
            }
        });


        return view;
    }
    private void uploadImageToFirebase(Uri imageUri) {
        if (imageUri != null) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                String userId = user.getUid();
                StorageReference profileImageRef = FirebaseStorage.getInstance().getReference("profileImages/" + userId + "/" + "images");
                profileImageRef.putFile(imageUri)
                        .addOnSuccessListener(taskSnapshot -> {
                            // Image upload success, get the download URL
                            profileImageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                                // Use this download URL to display the image or store it in your database
                                updateProfilePicture(downloadUri);
                            });
                        })
                        .addOnFailureListener(e -> {
                            // Handle unsuccessful uploads
                            Toast.makeText(getContext(), "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            } else {
                // Handle the case where there is no user logged in
            }
        }
    }

    private void updateProfilePicture(Uri downloadUri) {
        Glide.with(this).load(downloadUri).into(profileImageView);
    }
    private void loadProfilePicture() {
        Bitmap cachedBitmap = getBitmapFromMemCache("profileImage");
        if (cachedBitmap != null) {
            profileImageView.setImageBitmap(cachedBitmap);
        }else {
            FirebaseFunctions.getInstance()
                    .getHttpsCallable("getLatestImage")
                    .call()
                    .continueWith(task -> {
                        Map<String, String> result = (Map<String, String>) task.getResult().getData();
                        return result.get("ref");
                    })
                    .addOnSuccessListener(ref -> {
                        if (ref != null) {
                            //Use Firebase Storage SDK to download the file using the reference
                            StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(ref);
                            long ONE_MEGABYTE = 1024 * 1024 * 10; // Define the size for the download (10MB)
                            storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                profileImageView.setImageBitmap(bitmap);
                                addBitmapToMemoryCache("profileImage", bitmap);
                            }).addOnFailureListener(exception -> {
                                // Handle any errors
                                Log.e("ProfilePicture", "Failed to download image", exception);
                            });

                        } else {
                            Log.e("ProfilePicture", "Reference is null");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("ProfilePicture", "Failed to load image", e);
                    });
        }
    }
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
    private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            memoryCache.put(key, bitmap);
        }
    }

    private Bitmap getBitmapFromMemCache(String key) {
        return memoryCache.get(key);
    }
}