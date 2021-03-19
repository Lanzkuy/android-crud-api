package com.example.apitest;

public class biodataModel {

    private int id;
    private String nama, nohp, email, keterangan;

    public biodataModel(int id, String nama, String nohp, String email, String keterangan)
    {
        this.id=id;
        this.nama=nama;
        this.nohp=nohp;
        this.email=email;
        this.keterangan=keterangan;
    }

    public int getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public String getNohp() {
        return nohp;
    }

    public String getEmail() {
        return email;
    }

    public String getKeterangan() {
        return keterangan;
    }
}
