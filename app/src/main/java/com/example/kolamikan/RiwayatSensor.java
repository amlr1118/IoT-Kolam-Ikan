package com.example.kolamikan;

import android.os.Bundle;
import android.text.format.DateUtils;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class RiwayatSensor extends AppCompatActivity {

    private ArrayList<ModelRiwayatSensor> model;

    private RecyclerView rVData;
    private RecyclerView.LayoutManager layoutManager;

    private  AdapterRiwayatSensor adapterRiwayatSensor;

    private String airKolam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_riwayat_sensor);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        init();
        model = new ArrayList<>();
        getRiwayatSensor();
    }

    private void getRiwayatSensor(){
        String url = getString(R.string.api_server)+"/getRiwayatSensor";
        new Thread(new Runnable() {
            @Override
            public void run() {
                Http http = new Http(RiwayatSensor.this, url);
                http.setToken(true);
                http.send();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Integer code = http.getStatusCode();
                        if (code == 200){
                            try {
                                JSONObject response = new JSONObject(http.getResponse());
                                JSONArray dataArray = response.getJSONArray("data");
                                model.clear();

                                for (int i = 0; i < dataArray.length(); i++) {
                                    JSONObject riwayatSensor = dataArray.getJSONObject(i);

                                    // Ambil created_at dari JSON
                                    String createdAtString = riwayatSensor.getString("created_at");
                                    String waktu = riwayatSensor.getString("created_at");
                                    String kejernihan = riwayatSensor.getString("kejernihan");

                                    if (Integer.parseInt(kejernihan) > 80 ){
                                        airKolam = "Kotor";
                                    }else {
                                        airKolam = "Bersih";
                                    }



                                    // Format string tanggal dari API ke dalam objek Date
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                                    Date createdAtDate = null;
                                    try {
                                        createdAtDate = sdf.parse(createdAtString);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    // Dapatkan waktu sekarang
                                    long now = System.currentTimeMillis();

                                    // Tampilkan waktu relatif
                                    String timeAgo = "";
                                    if (createdAtDate != null) {
                                        long createdAtMillis = createdAtDate.getTime();
                                        timeAgo = DateUtils.getRelativeTimeSpanString(
                                                createdAtMillis,
                                                now,
                                                DateUtils.MINUTE_IN_MILLIS
                                        ).toString();
                                    }

                                    // Tambahkan ke model dengan waktu relatif yang dihitung
                                    ModelRiwayatSensor modelRiwayatPengaduan = new ModelRiwayatSensor(
                                            riwayatSensor.getString("id"),
                                            waktu,
                                            "Pompa                : "+riwayatSensor.getString("relay"),
                                            "Nilai Ph               : "+riwayatSensor.getString("ph"),
                                            "Nilai Kejernihan : "+riwayatSensor.getString("kejernihan")+" | "+airKolam,
                                            // Ubah format waktu di sini
                                            timeAgo
                                    );
                                    model.add(modelRiwayatPengaduan);

                                    //Log.d("Data Materi", String.valueOf(model));
                                }

                                adapterRiwayatSensor = new AdapterRiwayatSensor(model);
                                rVData.setAdapter(adapterRiwayatSensor);
                                adapterRiwayatSensor.setOnItemClickListener(new AdapterRiwayatSensor.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(int position) {
                                        //idPengaduan = model.get(position).getWaktu();

                                    }
                                });

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        else{
                            Toast.makeText(RiwayatSensor.this, "Error "+code, Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }).start();
    }

    private void init(){


        rVData = findViewById(R.id.rVData);
        rVData.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        rVData.setLayoutManager(layoutManager);

    }
}