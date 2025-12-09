package com.nqtam_lab03.nqtam_lab03_Phann2.nqtentity;

public class nqtKhoa {
    private String makh; // Mã Khoa
    private String tenkh; // Tên Khoa

    public nqtKhoa(){

    }
    public String getMakh() {
        return makh;
    }

    public void setMakh(String makh) {
        this.makh = makh;
    }

    public String getTenkh() {
        return tenkh;
    }

    public void setTenkh(String tenkh) {
        this.tenkh = tenkh;
    }

    public nqtKhoa(String makh, String tenkh) {
        this.makh = makh;
        this.tenkh = tenkh;
    }

}