package com.dakotalal.timeapp.ui.TimeActivities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.dakotalal.timeapp.R;
import com.dakotalal.timeapp.room.entities.TimeActivity;
import com.dakotalal.timeapp.ui.MainActivity;
import com.dakotalal.timeapp.viewmodel.TimeViewModel;

import java.util.List;

import androidx.annotation.ColorInt;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

public class TimeActivityListAdapter extends RecyclerView.Adapter<TimeActivityListAdapter.TimeActivityViewHolder> {
    private final LayoutInflater mInflater;
    private List<TimeActivity> timeActivities; // Cached copy of activity list
    private OnTimeActivityListener onTimeActivityListener;
    private TimeViewModel timeViewModel;
    private boolean actionButtons;

    public TimeActivityListAdapter(Context context, OnTimeActivityListener onTimeActivityListener, TimeViewModel viewModel, Boolean actionButtons) {
        mInflater = LayoutInflater.from(context);
        this.onTimeActivityListener = onTimeActivityListener;
        this.timeViewModel = viewModel;
        this.actionButtons = actionButtons;
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

            if (actionButtons) {
                // TODO implement edit button functionality

                // When the delete button is clicked, bring up a confirmation dialog.
                // Call to view model to delete the activity if confirmed
                holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new AlertDialog.Builder(view.getContext())
                                .setTitle("Delete " + current.getLabel() + "?")
                                .setMessage("Deleting an activity will not remove its entries from your timelog, but it will no longer appear in statistics.")
                                .setPositiveButton("Confirm", (dialog, whichButton) -> {
                                    timeViewModel.deleteTimeActivity(current);
                                    Toast.makeText(view.getContext(), current.getLabel() + " deleted!", Toast.LENGTH_SHORT).show();
                                })
                                .setNegativeButton("Cancel", null).show();
                    }
                });
            } else {
                holder.deleteButton.setVisibility(View.INVISIBLE);
                holder.editButton.setVisibility(View.INVISIBLE);
            }

            holder.timeActivityLabel.setText(current.getLabel());
            holder.timeActivityLabel.setBackgroundColor(colour);
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
        OnTimeActivityListener onTimeActivityListener;
        ImageButton editButton, deleteButton;

        private TimeActivityViewHolder(View itemView, OnTimeActivityListener onTimeActivityListener) {
            super(itemView);
            timeActivityLabel = itemView.findViewById(R.id.activity_view);
            editButton = itemView.findViewById(R.id.button_edit_activity);
            deleteButton = itemView.findViewById(R.id.button_delete_activity);
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
