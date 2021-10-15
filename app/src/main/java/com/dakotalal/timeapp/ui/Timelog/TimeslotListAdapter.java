package com.dakotalal.timeapp.ui.Timelog;

import android.content.Context;
import android.graphics.Color;
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
        String start = Instant.ofEpochSecond(timeslot.getTimeStart()).atZone(ZoneId.systemDefault()).format(formatter);
        String end = Instant.ofEpochSecond(timeslot.getTimeEnd()).atZone(ZoneId.systemDefault()).format(formatter);
        holder.time.setText(MessageFormat.format("{0} - {1}", start, end));
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
    }

    class TimeslotViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView time;
        private final TextView activityLabel;
        OnTimeslotListener onTimeslotListener;

        private TimeslotViewHolder(View itemView, OnTimeslotListener onTimeslotListener) {
            super(itemView);
            time = itemView.findViewById(R.id.timeslot_time);
            activityLabel = itemView.findViewById(R.id.timeslot_activity);
            this.onTimeslotListener = onTimeslotListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onTimeslotListener.onTimeslotClick(getAdapterPosition());
        }
    }

    public interface OnTimeslotListener {
        void onTimeslotClick(int position);
    }
}
