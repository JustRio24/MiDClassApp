package com.app.midclassapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.app.midclassapp.Jadwal; // Sesuaikan dengan model yang Anda gunakan
import java.util.List;

public class JadwalAdminAdapter extends RecyclerView.Adapter<JadwalAdminAdapter.ViewHolder> {
    private Context context;
    private List<Jadwal> jadwalList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Jadwal jadwal);
    }

    public JadwalAdminAdapter(Context context, List<Jadwal> jadwalList, OnItemClickListener listener) {
        this.context = context;
        this.jadwalList = jadwalList;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_jadwal_admin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Jadwal jadwal = jadwalList.get(position);
        holder.dosenTextView.setText(jadwal.getDosen());
        holder.matkulTextView.setText(jadwal.getMatkul());
        holder.hariTextView.setText(jadwal.getHari());
        holder.ruangTextView.setText(jadwal.getRuang());
        holder.waktuTextView.setText(jadwal.getWaktuMulai() + " - " + jadwal.getWaktuSelesai());

        holder.itemView.setOnClickListener(v -> {
            listener.onItemClick(jadwal);
        });

        // Tombol delete
        holder.deleteButton.setOnClickListener(v -> {
            if (context instanceof AdminJadwalActivity) {
                ((AdminJadwalActivity) context).deleteJadwal(jadwal.getId()); // Memanggil method delete pada activity
            }
        });
    }

    @Override
    public int getItemCount() {
        return jadwalList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView dosenTextView, matkulTextView, hariTextView, ruangTextView, waktuTextView;
        Button deleteButton;  // Tombol delete

        public ViewHolder(View itemView) {
            super(itemView);
            dosenTextView = itemView.findViewById(R.id.dosenTextViewAdmin);
            matkulTextView = itemView.findViewById(R.id.matkulTextViewAdmin);
            hariTextView = itemView.findViewById(R.id.hariTextViewAdmin);
            ruangTextView = itemView.findViewById(R.id.ruangTextViewAdmin);
            waktuTextView = itemView.findViewById(R.id.waktuTextViewAdmin);
            deleteButton = itemView.findViewById(R.id.deleteButton);  // Menambahkan referensi deleteButton
        }
    }
}
