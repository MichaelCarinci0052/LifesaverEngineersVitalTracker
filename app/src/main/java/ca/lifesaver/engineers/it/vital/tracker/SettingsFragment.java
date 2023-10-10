package ca.lifesaver.engineers.it.vital.tracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

/**
 * Jason Macdonald N01246828 section: 0CB
 * Michael Carinci n01480052 section: 0CB
 * Patrik Prenga n01428752  section: 0CB
 * Nicholas Rafuse n01440073 section: 0CB
 */
public class SettingsFragment extends Fragment {



    public SettingsFragment() {
        // Required empty public constructor
    }

    TextInputLayout textInputLayout;
    TextInputEditText editText;
    SharedPreferences sharedPreferences;
    Button apply;
    TextView current;
    Switch lockswitch;
    String SWITCH_STATE_KEY = "switch_state";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        textInputLayout = view.findViewById(R.id.locationlayout);
        editText = view.findViewById(R.id.locationtext);
        apply = view.findViewById(R.id.apply);
        current = view.findViewById(R.id.current);
        lockswitch = view.findViewById(R.id.lockswitch);

        sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        String savedText = sharedPreferences.getString("userText", "");
        editText.setText(savedText);

        sharedPreferences = getActivity().getSharedPreferences("lockoption", Context.MODE_PRIVATE);

        boolean isSwitchOn = sharedPreferences.getBoolean(SWITCH_STATE_KEY, false);
        lockswitch.setChecked(isSwitchOn);
        updateSwitchText(isSwitchOn);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                apply.setEnabled(!editable.toString().isEmpty());
            }
        });

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userInput = Objects.requireNonNull(editText.getText()).toString();
                saveInputToSharedPreferences(userInput);
            }
        });

        String savedTextForTextView = sharedPreferences.getString("userText", "");
        String currenthome = "Current Home: " + savedTextForTextView;
        current.setText(currenthome);

        lockswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    lockswitch.setText(R.string.on);
                } else {
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                    lockswitch.setText(R.string.off);
                }

                sharedPreferences.edit().putBoolean(SWITCH_STATE_KEY, isChecked).apply();

                updateSwitchText(isChecked);
            }
        });

        return view;
    }

    private void saveInputToSharedPreferences(String input) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userText", input);
        editor.apply();

        String currenthome2 = "Current Home: " + input;
        current.setText(currenthome2);
    }

    private void updateSwitchText(boolean isSwitchOn) {
        lockswitch.setText(isSwitchOn ? "On" : "Off");
    }


}