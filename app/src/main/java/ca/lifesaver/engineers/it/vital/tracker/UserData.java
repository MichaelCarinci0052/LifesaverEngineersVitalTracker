package ca.lifesaver.engineers.it.vital.tracker;

import android.graphics.Bitmap;

/**
 * Jason Macdonald N01246828 section: 0CB
 * Michael Carinci n01480052 section: 0CB
 * Patrik Prenga n01428752  section: 0CB
 * Nicholas Rafuse n01440073 section: 0CB
 */
public class UserData {
    public String displayName;
    public String email;
    public String phoneNumber;
    public boolean notifications;

    public UserData() {
    }

    public UserData(String displayName, String email, String phoneNumber, Boolean notifications) {
        this.displayName = displayName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.notifications = notifications;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isNotifications() {
        return notifications;
    }

    public void setNotifications(boolean notifications) {
        this.notifications = notifications;
    }

}
