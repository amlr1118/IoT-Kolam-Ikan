package com.example.kolamikan;

public class ModelRiwayatSensor {
    private String id;
    private String waktu;
    private String relay;
    private String ph;
    private String kerjernihan;
    private String tgl;

    public ModelRiwayatSensor(String id, String waktu, String relay, String ph, String kerjernihan, String tgl) {
        this.id = id;
        this.waktu = waktu;
        this.relay = relay;
        this.ph = ph;
        this.kerjernihan = kerjernihan;
        this.tgl = tgl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWaktu() {
        return waktu;
    }

    public void setWaktu(String waktu) {
        this.waktu = waktu;
    }

    public String getRelay() {
        return relay;
    }

    public void setRelay(String relay) {
        this.relay = relay;
    }

    public String getPh() {
        return ph;
    }

    public void setPh(String ph) {
        this.ph = ph;
    }

    public String getKerjernihan() {
        return kerjernihan;
    }

    public void setKerjernihan(String kerjernihan) {
        this.kerjernihan = kerjernihan;
    }

    public String getTgl() {
        return tgl;
    }

    public void setTgl(String tgl) {
        this.tgl = tgl;
    }
}
