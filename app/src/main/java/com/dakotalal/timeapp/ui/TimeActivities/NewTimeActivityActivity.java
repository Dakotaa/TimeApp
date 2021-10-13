package com.dakotalal.timeapp.ui.TimeActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.dakotalal.timeapp.R;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.ColorPickerView;
import com.skydoves.colorpickerview.listeners.ColorListener;

public class NewTimeActivityActivity extends AppCompatActivity {

    public static final String EXTRA_REPLY_COLOUR =
            "com.dakotalal.android.timeapp.REPLY_LABEL";
    public static final String EXTRA_REPLY_LABEL =
            "com.dakotalal.android.timeapp.REPLY_COLOUR";

    private EditText mEditWordView;
    private ColorPickerView colourPickerView;
    private int colour = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_time_activity);
        mEditWordView = findViewById(R.id.edit_activity_label);

        final Button button = findViewById(R.id.button_save);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent replyIntent = new Intent();
                if (TextUtils.isEmpty(mEditWordView.getText())) {
                    setResult(RESULT_CANCELED, replyIntent);
                } else {
                    String label = mEditWordView.getText().toString();
                    // reply intent with the label and colour
                    replyIntent.putExtra(EXTRA_REPLY_LABEL, label);
                    replyIntent.putExtra(EXTRA_REPLY_COLOUR, colour);
                    setResult(RESULT_OK, replyIntent);
                }
                finish();
            }
        });

        colourPickerView = findViewById(R.id.color_picker);
        // listen for changes from the colour picker
        colourPickerView.setColorListener((ColorListener) (color, fromUser) -> {
            // Set the background colour to the select colour
            ConstraintLayout constraintLayout = findViewById(R.id.create_time_activity);
            constraintLayout.setBackgroundColor(color);
            colour = color;
        });
    }
}