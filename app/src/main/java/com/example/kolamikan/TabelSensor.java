package com.example.kolamikan;

import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TabelSensor extends AppCompatActivity {

    private TableLayout tableLayout;
    private String airKolam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tabel_sensor);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tableLayout = findViewById(R.id.tableLayout);
        getRiwayatSensor();
        //xxxxxxhxhxhxhx


    }

    private void getRiwayatSensor(){
        String url = getString(R.string.api_server)+"/getRiwayatSensor";
        new Thread(new Runnable() {
            @Override
            public void run() {
                Http http = new Http(TabelSensor.this, url);
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

                                for (int i = 0; i < dataArray.length(); i++) {
                                    JSONObject riwayatSensor = dataArray.getJSONObject(i);

                                    // Ambil created_at dari JSON
                                    String createdAtString = riwayatSensor.getString("created_at");
                                    String waktu = riwayatSensor.getString("created_at");
                                    String kejernihan = riwayatSensor.getString("kejernihan");
                                    String ph = riwayatSensor.getString("ph");

                                    if (Double.parseDouble(kejernihan) > 80 && Double.parseDouble(ph) > 8){
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
//xxxxxxxxggggg
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
                                    TableRow newRow = new TableRow(TabelSensor.this);
                                    //Log.d("Data Materi", String.valueOf(model));


                                    TextView waktuTextView = new TextView(TabelSensor.this);
                                    waktuTextView.setText(timeAgo);  // Tanggal
                                    waktuTextView.setPadding(8, 8, 8, 8);
                                    waktuTextView.setTextColor(Color.BLACK);
                                    //waktuTextView.setBackgroundColor(Color.YELLOW);

                                    TextView kondisiAirTextView = new TextView(TabelSensor.this);
                                    kondisiAirTextView.setText(airKolam);  // Kondisi air
                                    waktuTextView.setPadding(8, 8, 8, 8);
                                    kondisiAirTextView.setTextColor(Color.BLACK);
                                    //kondisiAirTextView.setBackgroundColor(Color.YELLOW);

                                    TextView kejernihanTextView = new TextView(TabelSensor.this);
                                    kejernihanTextView.setText(kejernihan);  // Nilai kejernihan
                                    waktuTextView.setPadding(8, 8, 8, 8);
                                    kejernihanTextView.setTextColor(Color.BLACK);
                                    //kejernihanTextView.setBackgroundColor(Color.YELLOW);


                                    TextView phTextView = new TextView(TabelSensor.this);
                                    phTextView.setText(ph);  // Nilai pH
                                    waktuTextView.setPadding(8, 8, 8, 8);
                                    phTextView.setTextColor(Color.BLACK);
                                    //phTextView.setBackgroundColor(Color.YELLOW);

                                    // Tambahkan TextView ke dalam baris
                                    newRow.addView(waktuTextView);
                                    newRow.addView(kondisiAirTextView);
                                    newRow.addView(kejernihanTextView);
                                    newRow.addView(phTextView);

                                    // Tambahkan baris ke dalam TableLayout
                                    tableLayout.addView(newRow);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        else{
                            Toast.makeText(TabelSensor.this, "Error "+code, Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }).start();
    }
}