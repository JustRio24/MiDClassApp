package com.app.midclassapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AcademicEventAdapter extends RecyclerView.Adapter<AcademicEventAdapter.ViewHolder> {

    private List<AcademicEvent> events;

    public AcademicEventAdapter(List<AcademicEvent> events) {
        this.events = events;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_academic_event, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AcademicEvent event = events.get(position);
        holder.noTextView.setText(event.getNo());
        holder.eventNameTextView.setText(event.getEventName());
        holder.dateRangeTextView.setText(event.getDateRange());
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView noTextView, eventNameTextView, dateRangeTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            noTextView = itemView.findViewById(R.id.noDataTextView);
            eventNameTextView = itemView.findViewById(R.id.eventNameTextView);
            dateRangeTextView = itemView.findViewById(R.id.dateRangeTextView);
        }
    }
}
