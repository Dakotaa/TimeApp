package com.dakotalal.timeapp.ui.TimeActivities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.dakotalal.timeapp.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.skydoves.colorpickerview.ColorPickerView;
import com.skydoves.colorpickerview.listeners.ColorListener;

import androidx.fragment.app.DialogFragment;

public class EditTimeActivityDialogFragment extends BottomSheetDialogFragment {
    private ColorPickerView colourPickerView;
    private int score;
    private int colour;
    private String label;

    @SuppressLint("NonConstantResourceId")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if (getArguments() != null) {
            colour = getArguments().getInt("colour");
            label = getArguments().getString("label");
            score = getArguments().getInt("score");
        } else {
            colour = 0;
            score = 0;
            label = "";
        }
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_edit_time_activity, null);
        colourPickerView = view.findViewById(R.id.color_picker);
        colourPickerView.setInitialColor(colour);
        RadioGroup radioGroup = view.findViewById(R.id.productivity_buttons);
        // select the score for this activity
        RadioButton b;
        switch (score) {
            case -2:
                 b = view.findViewById(R.id.productivity_neg_2);
                b.setChecked(true);
                break;
            case -1:
                 b = view.findViewById(R.id.productivity_neg_1);
                b.setChecked(true);
                break;
            case 1:
                 b = view.findViewById(R.id.productivity_plus_1);
                b.setChecked(true);
                break;
            case 2:
                 b = view.findViewById(R.id.productivity_plus_2);
                b.setChecked(true);
                break;
            default:
                 b = view.findViewById(R.id.productivity_zero);
                b.setChecked(true);
                break;
        }

        // get the selected score
        radioGroup.setOnCheckedChangeListener((group, i) -> {
            switch (i) {
                case R.id.productivity_neg_2:
                    score = -2;
                    break;
                case R.id.productivity_neg_1:
                    score = -1;
                    break;
                case R.id.productivity_plus_1:
                    score = 1;
                    break;
                case R.id.productivity_plus_2:
                    score = 2;
                    break;
                default:
                    score = 0;
                    break;
            }
        });

        builder.setView(view);
        builder.setTitle("Edit " + label)
                .setPositiveButton("Update Activity", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Intent i = new Intent()
                                .putExtra("label", label)
                                .putExtra("colour", colourPickerView.getColor())
                                .putExtra("score", score);
                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EditTimeActivityDialogFragment.this.getDialog().cancel();
                    }
                });

        // listen for changes from the colour picker
        colourPickerView.setColorListener((ColorListener) (color, fromUser) -> {
            // Set the background colour to the select colour
            colourPickerView.setBackgroundColor(color);
        });

        return builder.create();
    }
}