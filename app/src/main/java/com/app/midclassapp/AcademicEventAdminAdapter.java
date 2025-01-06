package com.app.midclassapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AcademicEventAdminAdapter extends RecyclerView.Adapter<AcademicEventAdminAdapter.ViewHolder> {

    private Context context;
    private List<AcademicEvent> eventList;
    private OnItemClickListener onItemClickListener;

    public AcademicEventAdminAdapter(Context context, List<AcademicEvent> eventList, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.eventList = eventList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_academic_event_admin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AcademicEvent event = eventList.get(position);
        holder.noTextView.setText(event.getNo());
        holder.eventNameTextView.setText(event.getEventName());
        holder.dateRangeTextView.setText(event.getDateRange());

        // Set OnClickListener to handle editing
        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(event));

        // Handle delete button click
        holder.deleteButton.setOnClickListener(v -> {
            // Call the delete method on the listener to delete event from Firestore
            onItemClickListener.onDeleteEvent(event.getNo());
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(AcademicEvent event);
        void onDeleteEvent(String eventNo);  // Add delete event listener
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView noTextView, eventNameTextView, dateRangeTextView;
        Button deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            noTextView = itemView.findViewById(R.id.noTextViewAdmin);
            eventNameTextView = itemView.findViewById(R.id.eventNameTextViewAdmin);
            dateRangeTextView = itemView.findViewById(R.id.dateRangeTextViewAdmin);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
