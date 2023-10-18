package ca.lifesaver.engineers.it.vital.tracker;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

/**
 * Jason Macdonald N01246828 section: 0CB
 * Michael Carinci n01480052 section: 0CB
 * Patrik Prenga n01428752  section: 0CB
 * Nicholas Rafuse n01440073 section: 0CB
 */
public class RegisterActivity extends AppCompatActivity {
    private SharedViewModal viewModel;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_register);

        viewModel = new ViewModelProvider(this).get(SharedViewModal.class);

        Button buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }



}
