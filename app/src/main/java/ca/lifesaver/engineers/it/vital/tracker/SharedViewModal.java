package ca.lifesaver.engineers.it.vital.tracker;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModal extends ViewModel {
    private final MutableLiveData<Boolean> switchStatus = new MutableLiveData<>(true);

    public MutableLiveData<Boolean> getSwitchStatus() {
        return switchStatus;
    }

    public void setSwitchStatus(Boolean status) {
        switchStatus.setValue(status);
    }
}
