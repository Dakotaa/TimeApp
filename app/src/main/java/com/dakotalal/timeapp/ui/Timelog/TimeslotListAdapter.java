package com.dakotalal.timeapp.ui.Timelog;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dakotalal.timeapp.R;
import com.dakotalal.timeapp.room.entities.TimeActivity;
import com.dakotalal.timeapp.room.entities.Timeslot;
import com.dakotalal.timeapp.ui.TimeActivities.TimeActivityListAdapter;
import com.dakotalal.timeapp.viewmodel.TimeViewModel;

import org.w3c.dom.Comment;

import java.text.MessageFormat;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

public class TimeslotListAdapter extends RecyclerView.Adapter<TimeslotListAdapter.TimeslotViewHolder> {
    private final LayoutInflater inflater;
    private List<Timeslot> timeslots;
    private ArrayList<Timeslot> checkedTimeslots;
    private final OnTimeslotListener onTimeslotListener;
    private final TimeViewModel timeViewModel;
    private final FragmentManager fragmentManager;

    TimeslotListAdapter(Context context, OnTimeslotListener onTimeslotListener, TimeViewModel timeViewModel, FragmentManager fragmentManager) {
        inflater = LayoutInflater.from(context);
        this.fragmentManager = fragmentManager;
        this.onTimeslotListener = onTimeslotListener;
        this.timeViewModel = timeViewModel;
        checkedTimeslots = new ArrayList<>();
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
            holder.activityLabel.setText(R.string.no_timeslots_found);
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
        holder.setAlterable(true);
        holder.checkbox.setVisibility(View.VISIBLE);
        holder.bind(timeslot);
        String timestamp;
        String label;
        int bgColour;
        int textColour;
        int typeface = Typeface.NORMAL;
        int scoreIcon = R.drawable.ic_zero;
        boolean displayScore = true;
        boolean displayAddComment = false;
        boolean displayViewComment = false;
        // get activity info
        if (activityLabel == null) {    // no activity has been set for this timeslot
            label = "No Activity";
            bgColour = Color.DKGRAY;
            displayScore = false;
        } else {    // timeslot has an activity set
            label = activityLabel;
            // get the colour for this activity, and the contrasting text colour
            bgColour = timeViewModel.getTimeActivityColour(activityLabel);
            // check if a comment is set
            if (timeslot.getComment().isEmpty()) {
                displayAddComment = true;
            } else {
                displayViewComment = true;
            }
            // set the score icon
            switch (timeViewModel.getTimeActivityScore(activityLabel)) {
                case -2:
                    scoreIcon = R.drawable.ic_neg_2;
                    break;
                case -1:
                    scoreIcon = R.drawable.ic_neg_1;
                    break;
                case +1:
                    scoreIcon = R.drawable.ic_plus_1;
                    break;
                case +2:
                    scoreIcon = R.drawable.ic_plus_2;
                    break;
            }
        }
        // Format the timeslot depending on whether it's past/present/future
        if (timeEnd > now) {
            if (timeStart < now) {   // Timeslot is the current timeslot
                timestamp = MessageFormat.format("{0} - {1} (NOW)", start, end);
                typeface = Typeface.BOLD;
            } else {    // Timeslot is in the future
                timestamp = MessageFormat.format("{0} - {1}", start, end);
                label = " ";
                typeface = Typeface.ITALIC;
                bgColour = Color.BLACK;
                holder.setAlterable(false);
                holder.checkbox.setVisibility(View.INVISIBLE);
            }
        } else {    // Timeslot is in the past
            timestamp = MessageFormat.format("{0} - {1}", start, end);
        }

        backgroundView.setBackgroundColor(bgColour);
        textColour = TimeActivityListAdapter.getContrastColor(bgColour);
        holder.activityLabel.setText(label);
        holder.activityLabel.setTextColor(textColour);
        holder.time.setTextColor(textColour);
        holder.time.setTypeface(null, typeface);
        holder.time.setText(timestamp);
        if (displayScore) {
            holder.scoreIcon.setImageResource(scoreIcon);
            holder.scoreIcon.setVisibility(View.VISIBLE);
        } else holder.scoreIcon.setVisibility(View.INVISIBLE);
        if (displayAddComment) holder.addComment.setVisibility(View.VISIBLE);
            else holder.addComment.setVisibility(View.INVISIBLE);
        if (displayViewComment) holder.viewComment.setVisibility(View.VISIBLE);
            else holder.viewComment.setVisibility(View.INVISIBLE);
        holder.viewComment.setOnClickListener(view -> {
            openCommentDialog(timeslot);
        });
        holder.addComment.setOnClickListener(view -> {
            openCommentDialog(timeslot);
        });
    }

    public void openCommentDialog(Timeslot timeslot) {
        CommentDialog dialog = new CommentDialog(timeslot, timeViewModel);
        dialog.show(fragmentManager, "comment_fragment");
    }

    public ArrayList<Timeslot> getCheckedTimeslots() {
        return checkedTimeslots;
    }

    public void clearCheckedTimeslots() {
        this.checkedTimeslots = new ArrayList<>();
    }

    class TimeslotViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView time;
        private final TextView activityLabel;
        private final ImageView scoreIcon;
        private final ImageView addComment;
        private final ImageView viewComment;
        private final CheckBox checkbox;
        private Timeslot timeslot;
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
            scoreIcon = itemView.findViewById(R.id.score_icon);
            addComment = itemView.findViewById(R.id.add_comment_icon);
            viewComment = itemView.findViewById(R.id.comment_icon);
            checkbox = itemView.findViewById(R.id.timeslot_checkbox);
            this.onTimeslotListener = onTimeslotListener;
            itemView.setOnClickListener(this);
            alterable = true;

            // listen for checkbox changes
            checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    checkbox.setChecked(true);
                    checkedTimeslots.add(this.timeslot);
                    onTimeslotListener.setButtonVisible(true);
                } else {
                    checkbox.setChecked(false);
                    checkedTimeslots.remove(this.timeslot);
                    if (checkedTimeslots.isEmpty()) {
                        onTimeslotListener.setButtonVisible(false);
                    }
                }
            });
        }

        public void setAlterable(boolean alterable) {
            this.alterable = alterable;
        }

        void bind(Timeslot timeslot) {
            this.timeslot = timeslot;
            checkbox.setChecked(checkedTimeslots.contains(timeslot));
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
            } else {
                Toast.makeText(itemView.getContext(), "Can't edit future timeslot!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public interface OnTimeslotListener {
        void onTimeslotClick(int position);
        void setButtonVisible(boolean b);
    }
}
