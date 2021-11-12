package com.dakotalal.timeapp.ui.Timelog;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.dakotalal.timeapp.R;
import com.dakotalal.timeapp.repository.TimeRepository;
import com.dakotalal.timeapp.room.entities.TimeActivity;
import com.dakotalal.timeapp.room.entities.Timeslot;
import com.dakotalal.timeapp.ui.TimeActivities.TimeActivityListAdapter;
import com.dakotalal.timeapp.viewmodel.TimeViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Dialog for viewing/editing the comments of a timeslot in the Timelog.
 */
public class CommentDialog extends BottomSheetDialogFragment {
    Timeslot timeslot;
    String comment;
    Button button;
    EditText editText;
    TimeViewModel viewModel;

    public CommentDialog(Timeslot timeslot, TimeViewModel viewModel) {
        this.timeslot = timeslot;
        this.viewModel = viewModel;
        this.comment = timeslot.getComment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceBundle) {
        View v = inflater.inflate(R.layout.dialog_comment, container, false);
        button = v.findViewById(R.id.button_submit);
        button.setOnClickListener(view -> editComment());
        editText = v.findViewById(R.id.edit_text_comment);
        if (!comment.isEmpty()) {
            editText.setText(comment);
        }
        return v;
    }

    /**
     * Update the comment of the timeslot when the submit button is clicked
     */
    public void editComment() {
        String newComment = editText.getText().toString();
        if (!newComment.isEmpty() && !newComment.equals(comment)) {
            timeslot.setComment(newComment);
            viewModel.updateTimeslot(timeslot);
        }
        this.dismiss();
    }
}
