package ca.lifesaver.engineers.it.vital.tracker;

import android.app.Application;
import android.util.Log;

import com.google.firebase.FirebaseApp;

public class ApplicationInit extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        Log.d("here","here");
    }
}
