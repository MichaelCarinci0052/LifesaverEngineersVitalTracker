package ca.lifesaver.engineers.it.vital.tracker;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ca.lifesaver.engineers.it.vital.tracker.LoginActivity;

public class LoginActivityTest {

    private LoginActivity loginActivity;

    // Mock any necessary dependencies here
    @Mock
    private FirebaseAuth mockFirebaseAuth;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        loginActivity = new LoginActivity();
        // Initialize the mock Firebase Auth in LoginActivity
        loginActivity.mAuth = mockFirebaseAuth;
    }

    @Test
    public void testValidEmail() {
        assertTrue(loginActivity.isValidEmail("test@example.com"));
    }

    @Test
    public void testInvalidEmail() {
        assertFalse(loginActivity.isValidEmail(""));
    }

    @Test
    public void testValidPassword() {
        assertTrue(loginActivity.isValidPassword("123456"));
    }

    @Test
    public void testInvalidPassword() {
        assertFalse(loginActivity.isValidPassword(""));
    }

    @Test
    public void testFirebaseAuthNotNull() {
        assertNotNull(loginActivity.mAuth);
    }
}
