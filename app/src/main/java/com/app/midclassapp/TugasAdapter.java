package com.app.midclassapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TugasAdapter extends RecyclerView.Adapter<TugasAdapter.ViewHolder> {

    private Context context;
    private ArrayList<TugasMateri> listMateriTugas;

    public TugasAdapter(Context context, ArrayList<TugasMateri> listMateriTugas) {
        this.context = context;
        this.listMateriTugas = listMateriTugas;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tugas, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TugasMateri tugasMateri = listMateriTugas.get(position);
        holder.tvTitle.setText(tugasMateri.getTitle());
        holder.tvDescription.setText(tugasMateri.getDescription());
        holder.tvDeadline.setText("Deadline: " + tugasMateri.getDeadline());
        holder.tvMatkul.setText("Mata Kuliah: " + tugasMateri.getMatkul());
    }

    @Override
    public int getItemCount() {
        return listMateriTugas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription, tvDeadline, tvMatkul;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvDeadline = itemView.findViewById(R.id.tvDeadline);
            tvMatkul = itemView.findViewById(R.id.tvMatkul);
        }
    }
}
