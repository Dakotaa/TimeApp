package com.dakotalal.timeapp.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import com.dakotalal.timeapp.R;
import com.dakotalal.timeapp.ui.MainActivity;

public class SetupActivity extends AppCompatActivity {

    private int intervalLength;
    private String name;
    private EditText nameEditText;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intervalLength = 0;
        name = "";
        setContentView(R.layout.activity_setup);
        submitButton = findViewById(R.id.button_continue);
        checkInfo();
        nameEditText = findViewById(R.id.edit_text_name);
        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                name = editable.toString();
                checkInfo();
            }
        });
    }

    public void completeSetup(View view) {
        SharedPreferences prefs = getSharedPreferences("com.timeapp.prefs", MODE_PRIVATE);
        prefs.edit().putString(MainActivity.PREFS_NAME, name).commit();
        prefs.edit().putInt(MainActivity.PREFS_INTERVAL_LENGTH, intervalLength).commit();
        prefs.edit().putBoolean(MainActivity.PREFS_SETUP_COMPLETE, true).commit();
        finish();
    }

    @SuppressLint("NonConstantResourceId")
    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch(view.getId()) {
            case R.id.radio_button_15:
                if (checked)
                    intervalLength = 15;
                    checkInfo();
                    break;
            case R.id.radio_button_30:
                if (checked)
                    intervalLength = 30;
                    checkInfo();
                    break;
            case R.id.radio_button_60:
                if (checked)
                    intervalLength = 60;
                    checkInfo();
                    break;
        }
    }

    /**
     * Checks whether the name and timeslot intervals are filled out, if they are, make the submit
     * button clickable
     */
    public void checkInfo() {
        if (name.length() > 1 && intervalLength > 0) {
            submitButton.setVisibility(View.VISIBLE);
        } else {
            submitButton.setVisibility(View.INVISIBLE);
        }
    }
}