package com.nqtam_lab03.nqtam_lab03_Phann2.nqtentity;

public class nqtMonHoc {
    private String mamh; // Mã Môn Học
    private String tenmh; // Tên Môn Học
    private int sotiet; // Số Tiết
    public nqtMonHoc(){

    }
    public String getMamh() {
        return mamh;
    }

    public void setMamh(String mamh) {
        this.mamh = mamh;
    }

    public String getTenmh() {
        return tenmh;
    }

    public void setTenmh(String tenmh) {
        this.tenmh = tenmh;
    }

    public int getSotiet() {
        return sotiet;
    }

    public void setSotiet(int sotiet) {
        this.sotiet = sotiet;
    }

    public nqtMonHoc(String mamh, String tenmh, int sotiet) {
        this.mamh = mamh;
        this.tenmh = tenmh;
        this.sotiet = sotiet;
    }
}