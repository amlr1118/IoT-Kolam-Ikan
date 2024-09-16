package com.example.kolamikan;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterRiwayatSensor extends RecyclerView.Adapter<AdapterRiwayatSensor.VHRiwayatSensor> {

    ArrayList<ModelRiwayatSensor> modelRiwayatSensors;

    private AdapterRiwayatSensor.OnItemClickListener mListener;


    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(AdapterRiwayatSensor.OnItemClickListener listener){
        mListener = listener;
    }

    public class VHRiwayatSensor extends RecyclerView.ViewHolder {
        TextView tVWaktu;
        TextView tVPompa;
        TextView tVPh;
        TextView tVKejernihan;
        TextView tVTanggal;
        public VHRiwayatSensor(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);

            tVWaktu = itemView.findViewById(R.id.tVWaktu);
            tVPompa = itemView.findViewById(R.id.tVPompa);
            tVPh = itemView.findViewById(R.id.tVPh);
            tVKejernihan = itemView.findViewById(R.id.tVKejernihan);
            tVTanggal = itemView.findViewById(R.id.tVTanggal);

            itemView.setOnClickListener(view -> {
                if (listener != null){
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            });
        }
    }

    public AdapterRiwayatSensor(ArrayList<ModelRiwayatSensor> modelRiwayatSensors) {
        this.modelRiwayatSensors = modelRiwayatSensors;
    }

    @NonNull
    @Override
    public VHRiwayatSensor onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_data_riwayat_sensor,
                parent, false);

        VHRiwayatSensor vhRiwayatSensor = new VHRiwayatSensor(itemView, mListener);
        return vhRiwayatSensor;
    }

    @Override
    public void onBindViewHolder(@NonNull VHRiwayatSensor holder, int position) {
        holder.tVWaktu.setText(modelRiwayatSensors.get(position).getWaktu());
        holder.tVPompa.setText(modelRiwayatSensors.get(position).getRelay());
        holder.tVPh.setText(modelRiwayatSensors.get(position).getPh());
        holder.tVKejernihan.setText(modelRiwayatSensors.get(position).getKerjernihan());
        holder.tVTanggal.setText(modelRiwayatSensors.get(position).getTgl());
    }

    @Override
    public int getItemCount() {
        return modelRiwayatSensors.size();
    }


}
