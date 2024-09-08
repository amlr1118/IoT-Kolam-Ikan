package com.example.kolamikan;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONException;
import org.json.JSONObject;

public class Home extends AppCompatActivity {

    private TextView tVStatus;
    private TextView tVJernih;
    private TextView tVPH;
    private TextView tVStatusAir;
    private ToggleButton tglRelay;

    private String relayStatus;
    private String id;

    private Handler handler = new Handler();
    private Runnable runnable;
    private final int DELAY = 5000; // 5 detik

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        init();
        id ="1";
        cekStatusRelay();
        bacaSensor();
        tVStatus.setText("");

        tglRelay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (tglRelay.isChecked()){
                    relayStatus = "1";
                    setRelay();
                    //bacaSensor();
                    //btnReset.setVisibility(View.INVISIBLE);
                }else{
                    relayStatus = "0";
                    setRelay();
                }
            }
        });

    }

    private void bacaSensor(){
        String url = getString(R.string.api_server) + "/baca-sensor-ph-kejernihan";

        runnable = new Runnable() {
            @Override
            public void run() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Http http = new Http(Home.this, url);
                        http.send();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Integer code = http.getStatusCode();
                                if (code == 200) {
                                    try {
                                        JSONObject response = new JSONObject(http.getResponse());
                                        String ph = response.getString("ph");
                                        String kejernihan = response.getString("kejernihan");

                                        tVJernih.setText("Nilai Kejernihan : " + kejernihan);
                                        tVPH.setText("Ph : " + ph);

                                        if (Integer.parseInt(kejernihan) > 80 && Integer.parseInt(ph) > 8){
                                           tVStatusAir.setText("Air kolam kotor, harus diganti !!!");
                                        }else {
                                            tVStatusAir.setText("Air kolam jernih");
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    Toast.makeText(Home.this, "Error " + code, Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                }).start();

                // Mengulangi runnable setiap beberapa detik
                handler.postDelayed(runnable, DELAY);
            }
        };

        // Memulai polling
        handler.post(runnable);
    }

    private void cekStatusRelay(){
        String url = getString(R.string.api_server)+"/baca-relay-kolam-ikan";

        new Thread(new Runnable() {
            @Override
            public void run() {
                Http http = new Http(Home.this, url);
                http.send();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Integer code = http.getStatusCode();
                        if (code == 200){
                            try {
                                // Mengambil response sebagai string dan mengonversinya menjadi integer
                                String responseString = http.getResponse();
                                int relayStatus = Integer.parseInt(responseString.trim());

                                // Gunakan relayStatus sesuai kebutuhan
                                if (relayStatus == 0){
                                    tglRelay.setChecked(false);
                                }else{
                                    tglRelay.setChecked(true);
                                }

                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                                Toast.makeText(Home.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(Home.this, "Error "+code, Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }).start();
    }

    private void setRelay(){
        JSONObject params = new JSONObject();
        try {
            params.put("relay", relayStatus);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String data = params.toString();
        String url = getString(R.string.api_server)+"/update-relay/"+id;

        new Thread(new Runnable() {
            @Override
            public void run() {
                Http http = new Http(Home.this, url);
                http.setMethod("put");
                http.setData(data);
                http.send();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Integer code = http.getStatusCode();
                        if (code == 200){
                            if (relayStatus=="1"){
                                tVStatus.setText("Pompa Aktif");
                            }else{
                                tVStatus.setText("Pompa Mati");
                            }
                            try {
                                JSONObject response = new JSONObject(http.getResponse());

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        else if (code == 422) {
                            try {
                                JSONObject response = new JSONObject(http.getResponse());
                                String msg = response.getString("message");
                                alertFail(""+msg);
                                //Toast.makeText(Register.this, ""+msg, Toast.LENGTH_LONG).show();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                        else if (code == 401) {
                            try {
                                JSONObject response = new JSONObject(http.getResponse());
                                String msg = response.getString("message");
                                alertFail(""+msg);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                        else{
                            Toast.makeText(Home.this, "Error "+code, Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }).start();


    }

    private void alertFail(String s) {
        new AlertDialog.Builder(this)
                .setTitle("Failed")
                .setIcon(R.drawable.baseline_error_24)
                .setMessage(s)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
    }

    private void init(){
        tVStatus = findViewById(R.id.tVStatus);
        tVJernih = findViewById(R.id.tVJernih);
        tVPH = findViewById(R.id.tVPH);
        tglRelay = findViewById(R.id.tglRelay);
        tVStatusAir = findViewById(R.id.tVStatusAir);
    }
}