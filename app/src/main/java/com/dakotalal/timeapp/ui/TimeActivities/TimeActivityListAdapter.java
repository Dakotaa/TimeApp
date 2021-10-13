package com.dakotalal.timeapp.ui.TimeActivities;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dakotalal.timeapp.R;
import com.dakotalal.timeapp.room.entities.TimeActivity;

import java.util.List;

import androidx.annotation.ColorInt;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

public class TimeActivityListAdapter extends RecyclerView.Adapter<TimeActivityListAdapter.TimeActivityViewHolder> {
    private final LayoutInflater mInflater;
    private List<TimeActivity> timeActivities; // Cached copy of activity list
    private OnTimeActivityListener onTimeActivityListener;

    public TimeActivityListAdapter(Context context, OnTimeActivityListener onTimeActivityListener) {
        mInflater = LayoutInflater.from(context);
        this.onTimeActivityListener = onTimeActivityListener;
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

    // getItemCount() is called many times, and when it is first called,
    // mWords has not been updated (means initially, it's null, and we can't return null).
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

        private TimeActivityViewHolder(View itemView, OnTimeActivityListener onTimeActivityListener) {
            super(itemView);
            timeActivityLabel = itemView.findViewById(R.id.activity_view);
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

//    class TimeActivityListener extends RecyclerView.ViewHolder implements View.OnClickListener {
//        OnTimeActivityListener onTimeActivityListener;
//
//        private TimeActivityListener(View itemView, OnTimeActivityListener onTimeActivityListener) {
//            super(itemView);
//            this.onTimeActivityListener = onTimeActivityListener;
//            itemView.setOnClickListener(this);
//        }
//
//
//    }

    public interface OnTimeActivityListener {
        void onTimeActivityClick(int position);
    }
}
