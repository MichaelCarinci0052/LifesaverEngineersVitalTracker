package ca.lifesaver.engineers.it.vital.tracker;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SharedViewModal extends ViewModel {
    private final MutableLiveData<Boolean> switchStatus = new MutableLiveData<>(false);
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private MutableLiveData<Bitmap> profileImage = new MutableLiveData<>();
    public SharedViewModal() {
        Log.d("SharedViewModal", "ViewModel Constructor Called");
        fetchSwitchStatusFromFirestore();
    }


    private void fetchSwitchStatusFromFirestore() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user != null ? user.getUid() : null;

        if (uid != null) {

            DocumentReference docRef = db.collection("userId").document(uid);
            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Boolean notificationStatus = document.getBoolean("notifications");
                        if (notificationStatus != null) {
                            switchStatus.setValue(notificationStatus);
                            Log.d("SharedViewModal", "Data fetched successfully");
                        }
                    }
                } else {
                    Log.d("Firestore", "get failed with ", task.getException());
                }
            });
        }else{Log.d("Firestore", "get failed with ");}
    }
    public MutableLiveData<Boolean> getSwitchStatus() {
        return switchStatus;
    }

    public void setSwitchStatus(Boolean status) {
        switchStatus.setValue(status);
        updateSwitchStateInFirestore(status);
    }

    public void updateSwitchStateInFirestore(Boolean isChecked) {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            Map<String, Object> notification = new HashMap<>();
            notification.put("notifications", isChecked);
            db.collection("userId").document(uid).update(notification);
        }
    }

    public LiveData<Bitmap> getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(Bitmap bitmap) {
        profileImage.setValue(bitmap);
    }

}
