package com.dakotalal.timeapp.ui.TimeActivities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dakotalal.timeapp.R;
import com.dakotalal.timeapp.room.entities.TimeActivity;
import com.dakotalal.timeapp.ui.MainActivity;
import com.dakotalal.timeapp.viewmodel.TimeViewModel;

import java.util.List;

import androidx.annotation.ColorInt;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

public class TimeActivityListAdapter extends RecyclerView.Adapter<TimeActivityListAdapter.TimeActivityViewHolder> {
    private final LayoutInflater mInflater;
    private List<TimeActivity> timeActivities; // Cached copy of activity list
    private OnTimeActivityListener onTimeActivityListener;
    private TimeViewModel timeViewModel;
    private Context context;
    private Fragment fragment;
    private boolean actionButtons;

    public TimeActivityListAdapter(Context context, OnTimeActivityListener onTimeActivityListener, TimeViewModel viewModel, Boolean actionButtons, Fragment fragment) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
        this.onTimeActivityListener = onTimeActivityListener;
        this.timeViewModel = viewModel;
        this.actionButtons = actionButtons;
        this.fragment = fragment;
    }

    @Override
    public TimeActivityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.time_activity_recycler_item, parent, false);
        return new TimeActivityViewHolder(itemView, this.onTimeActivityListener);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(TimeActivityViewHolder holder, int position) {
        if (timeActivities != null) {
            TimeActivity current = timeActivities.get(position);
            int colour = current.getColour();
            int score = current.getProductivity();
            String label = current.getLabel();

            if (actionButtons) {
                holder.editButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DialogFragment frag = new EditTimeActivityDialogFragment();
                        frag.setTargetFragment(fragment, TimeActivityListFragment.EDITOR_FRAGMENT);
                        Bundle args = new Bundle();
                        args.putString("label", label);
                        args.putInt("colour", colour);
                        args.putInt("score", score);
                        frag.setArguments(args);
                        frag.show(fragment.getFragmentManager(), "Edit Time Activity");
                    }
                });
                // When the delete button is clicked, bring up a confirmation dialog.
                // Call to view model to delete the activity if confirmed
                holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new AlertDialog.Builder(view.getContext())
                                .setTitle("Delete " + label + "?")
                                .setMessage("Deleting an activity will not remove its entries from your timelog, but it will no longer appear in statistics.")
                                .setPositiveButton("Confirm", (dialog, whichButton) -> {
                                    timeViewModel.deleteTimeActivity(current);
                                    Toast.makeText(view.getContext(), label + " deleted!", Toast.LENGTH_SHORT).show();
                                })
                                .setNegativeButton("Cancel", null).show();
                    }
                });
            } else {
                holder.deleteButton.setVisibility(View.INVISIBLE);
                holder.editButton.setVisibility(View.INVISIBLE);
            }

            switch (current.getProductivity()) {
                case -2:
                    holder.scoreIcon.setImageResource(R.drawable.ic_neg_2);
                    break;
                case -1:
                    holder.scoreIcon.setImageResource(R.drawable.ic_neg_1);
                    break;
                case +1:
                    holder.scoreIcon.setImageResource(R.drawable.ic_plus_1);
                    break;
                case +2:
                    holder.scoreIcon.setImageResource(R.drawable.ic_plus_2);
                    break;
            }

            holder.timeActivityLabel.setText(label);
            holder.timeActivityLabel.setBackgroundColor(colour);
            holder.background.setBackgroundColor(colour);
            // set the colour of the text based on the background colour
            holder.timeActivityLabel.setTextColor(getContrastColor(colour));
        } else {
            // Covers the case of data not being ready yet.
            holder.timeActivityLabel.setText("No Activities");
        }
    }

    public void setTimeActivities(List<TimeActivity> timeActivities){
        this.timeActivities = timeActivities;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (timeActivities != null)
            return timeActivities.size();
        else return 0;
    }

    public TimeActivity getTimeActivityAtPosition(int position) {
        return timeActivities.get(position);
    }

    class TimeActivityViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView timeActivityLabel;
        private final TextView background;
        private final ImageView scoreIcon;
        OnTimeActivityListener onTimeActivityListener;
        ImageButton editButton, deleteButton;

        private TimeActivityViewHolder(View itemView, OnTimeActivityListener onTimeActivityListener) {
            super(itemView);
            timeActivityLabel = itemView.findViewById(R.id.activity_label_text);
            background = itemView.findViewById(R.id.activity_view);
            editButton = itemView.findViewById(R.id.button_edit_activity);
            deleteButton = itemView.findViewById(R.id.button_delete_activity);
            scoreIcon = itemView.findViewById(R.id.score_icon);
            this.onTimeActivityListener = onTimeActivityListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Log.d("TimeActivityListAdapter", "Click");
            onTimeActivityListener.onTimeActivityClick(getAdapterPosition());
        }
    }

    @ColorInt
    public static int getContrastColor(@ColorInt int color) {
        // Counting the perceptive luminance - human eye favors green color...
        double a = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        int d;
        if (a < 0.5) {
            d = 0; // bright colors - black font
        } else {
            d = 255; // dark colors - white font
        }
        return Color.rgb(d, d, d);
    }

    public interface OnTimeActivityListener {
        void onTimeActivityClick(int position);
    }
}
