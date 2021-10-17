package com.dakotalal.timeapp.ui.Timelog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dakotalal.timeapp.R;
import com.dakotalal.timeapp.room.entities.TimeActivity;
import com.dakotalal.timeapp.room.entities.Timeslot;
import com.dakotalal.timeapp.ui.TimeActivities.TimeActivityListAdapter;
import com.dakotalal.timeapp.viewmodel.TimeViewModel;

import java.text.MessageFormat;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

public class TimeslotListAdapter extends RecyclerView.Adapter<TimeslotListAdapter.TimeslotViewHolder> {
    private final LayoutInflater inflater;
    private List<Timeslot> timeslots;
    private OnTimeslotListener onTimeslotListener;
    private TimeViewModel timeViewModel;

    TimeslotListAdapter(Context context, OnTimeslotListener onTimeslotListener, TimeViewModel timeViewModel) {
        inflater = LayoutInflater.from(context);
        this.onTimeslotListener = onTimeslotListener;
        this.timeViewModel = timeViewModel;
    }

    @Override
    public TimeslotViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.timeslot_recycler_item, parent, false);
        return new TimeslotViewHolder(itemView, this.onTimeslotListener);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onBindViewHolder(TimeslotViewHolder holder, int position) {
        if (timeslots != null) {
            Timeslot current = timeslots.get(position);
            formatTimeslotHolder(holder, current);
        } else {
            holder.activityLabel.setText("No timeslots!");
        }
    }

    void setTimeslots(List<Timeslot> timeslots) {
        this.timeslots = timeslots;
        notifyDataSetChanged();
    }

    void updateTimeActivities() {
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (timeslots != null)
            return timeslots.size();
        else return 0;
    }

    public Timeslot getTimeslotAtPosition(int position) {
        return timeslots.get(position);
    }

    /**
     * Formats the given TimeslotHolder to show the correct information
     * @param holder a TimeslotViewHolder
     * @param timeslot the timeslot whose representation is being formatted
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void formatTimeslotHolder(TimeslotViewHolder holder, Timeslot timeslot) {
        // Format the timeslot time
        // Convert the start and end time to readable format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        long timeStart = timeslot.getTimeStart();
        long timeEnd = timeslot.getTimeEnd();
        long now = System.currentTimeMillis()/1000;
        String start = Instant.ofEpochSecond(timeStart).atZone(ZoneId.systemDefault()).format(formatter);
        String end = Instant.ofEpochSecond(timeEnd).atZone(ZoneId.systemDefault()).format(formatter);

        // Format the activity label and colour the timeslot based on the activity colour
        String activityLabel = timeslot.getActivityLabel();
        View backgroundView = holder.itemView.findViewById(R.id.timeslot_background);
        if (activityLabel == null) {    // no activity has been set for this timeslot
            holder.activityLabel.setText(R.string.activity_not_set);
            backgroundView.setBackgroundColor(Color.GRAY);
        } else {    // timeslot has an activity set
            holder.activityLabel.setText(activityLabel);
            // get the colour for this activity, and the contrasting text colour
            int backgroundColour = timeViewModel.getTimeActivityColour(activityLabel);
            int textColour = TimeActivityListAdapter.getContrastColor(backgroundColour);
            backgroundView.setBackgroundColor(backgroundColour);
            holder.time.setTextColor(textColour);
            holder.activityLabel.setTextColor(textColour);
        }

        // Format the timeslot depending on whether it's past/present/future
        if (timeEnd > now) {
            if (timeStart < now) {   // Timeslot is the current timeslot
                holder.time.setText(MessageFormat.format("{0} - {1} (NOW)", start, end));
                holder.time.setTypeface(null, Typeface.BOLD);
            } else {    // Timeslot is in the future
                holder.time.setText(MessageFormat.format("{0} - {1}", start, end));
                holder.activityLabel.setText(" ");
                holder.time.setTypeface(null, Typeface.ITALIC);
                backgroundView.setBackgroundColor(Color.DKGRAY);
                holder.setAlterable(false);
            }
        } else {    // Timeslot is in the past
            holder.time.setText(MessageFormat.format("{0} - {1}", start, end));
        }
    }

    class TimeslotViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView time;
        private final TextView activityLabel;
        OnTimeslotListener onTimeslotListener;
        // Flag for whether this timeslot is alterable (can be clicked and activity can be selected)
        private boolean alterable;

        /**
         * Creates a viewholder for a timeslot
         * @param itemView The view that this viewholder exists in
         * @param onTimeslotListener A listener for when the timeslot is clicked in the list
         */
        private TimeslotViewHolder(View itemView, OnTimeslotListener onTimeslotListener) {
            super(itemView);
            time = itemView.findViewById(R.id.timeslot_time);
            activityLabel = itemView.findViewById(R.id.timeslot_activity);
            this.onTimeslotListener = onTimeslotListener;
            itemView.setOnClickListener(this);
            alterable = true;
        }

        public void setAlterable(boolean alterable) {
            this.alterable = alterable;
        }

        /**
         * Handles the clicking of an individual timeslot.
         * Timeslots can only be clicked if they are alterable
         * @param view The view that timeslot is part of
         */
        @Override
        public void onClick(View view) {
            if (alterable) {
                onTimeslotListener.onTimeslotClick(getAdapterPosition());
            }
        }
    }

    public interface OnTimeslotListener {
        void onTimeslotClick(int position);
    }
}
