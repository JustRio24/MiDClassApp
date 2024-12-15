package com.app.midclassapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TugasAdapter extends RecyclerView.Adapter<TugasAdapter.TugasViewHolder> {

    private ArrayList<String> tugasList;

    public TugasAdapter(ArrayList<String> tugasList) {
        this.tugasList = tugasList;
    }

    @NonNull
    @Override
    public TugasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tugas, parent, false);
        return new TugasViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TugasViewHolder holder, int position) {
        String tugas = tugasList.get(position);
        holder.tvTugas.setText(tugas);
    }

    @Override
    public int getItemCount() {
        return tugasList.size();
    }

    public void updateTugasList(ArrayList<String> newTugasList) {
        this.tugasList = newTugasList;
        notifyDataSetChanged();
    }

    static class TugasViewHolder extends RecyclerView.ViewHolder {
        TextView tvTugas;

        public TugasViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTugas = itemView.findViewById(R.id.tvTugas);
        }
    }
}
