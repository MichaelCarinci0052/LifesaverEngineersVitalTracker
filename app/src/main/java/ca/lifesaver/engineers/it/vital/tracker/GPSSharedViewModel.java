package ca.lifesaver.engineers.it.vital.tracker;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GPSSharedViewModel extends ViewModel {
    private MutableLiveData<Double> latitude = new MutableLiveData<>();
    private MutableLiveData<Double> longitude = new MutableLiveData<>();
    private MutableLiveData<Boolean> delete = new MutableLiveData<>();

    public GPSSharedViewModel(){
        delete.setValue(false);
    }

    public void setLatitude(double lat) {
        latitude.setValue(lat);
    }

    public void setLongitude(double lon) {
        longitude.setValue(lon);
    }

    public void setDelete(Boolean tog) {
        delete.setValue(tog);
    }

    public LiveData<Double> getLatitude() {
        return latitude;
    }

    public LiveData<Double> getLongitude() {
        return longitude;
    }

    public MutableLiveData<Boolean> getDelete(){
        return delete;
    }


}
