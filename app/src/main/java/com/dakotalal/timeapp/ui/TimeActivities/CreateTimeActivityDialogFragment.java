package com.dakotalal.timeapp.ui.TimeActivities;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dakotalal.timeapp.R;
import com.skydoves.colorpickerview.ColorPickerView;
import com.skydoves.colorpickerview.listeners.ColorListener;

public class CreateTimeActivityDialogFragment extends DialogFragment {

    public static final String EXTRA_REPLY_COLOUR =
            "com.dakotalal.android.timeapp.REPLY_LABEL";
    public static final String EXTRA_REPLY_LABEL =
            "com.dakotalal.android.timeapp.REPLY_COLOUR";

    private EditText mEditWordView;
    private ColorPickerView colourPickerView;
    private int colour = 0;




    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_new_time_activity, null);
        mEditWordView = view.findViewById(R.id.edit_activity_label);
        colourPickerView = view.findViewById(R.id.color_picker);
        builder.setView(view);
        builder.setTitle("Create an Activity")
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (mEditWordView.getText().toString().length() > 0) {
                            Intent i = new Intent()
                                    .putExtra("label", mEditWordView.getText().toString())
                                    .putExtra("colour", colourPickerView.getColor());
                            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        CreateTimeActivityDialogFragment.this.getDialog().cancel();
                    }
                });

        // listen for changes from the colour picker
        colourPickerView.setColorListener((ColorListener) (color, fromUser) -> {
            // Set the background colour to the select colour
            RelativeLayout relativeLayout = view.findViewById(R.id.create_time_activity);
            relativeLayout.setBackgroundColor(color);
            colour = color;
        });

        return builder.create();
    }
}