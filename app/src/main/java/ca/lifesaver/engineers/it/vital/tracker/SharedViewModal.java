package ca.lifesaver.engineers.it.vital.tracker;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModal extends ViewModel {
    private final MutableLiveData<Boolean> buttonStatus = new MutableLiveData<>(true);

    public MutableLiveData<Boolean> getButtonStatus() {
        return buttonStatus;
    }

    public void setButtonStatus(Boolean status) {
        buttonStatus.setValue(status);
    }
}
