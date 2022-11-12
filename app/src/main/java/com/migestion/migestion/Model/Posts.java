package com.migestion.migestion.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Posts {
    @SerializedName("actividad")
    private String Actividad;
    @SerializedName("cantidadDatos")
    private String CantidadDatos;
    @SerializedName("dataString01")
    private String dataString01;
    @SerializedName("dataString02")
    private String dataString02;

    @SerializedName("dataList01")
    private List dataList01;
    @SerializedName("dataList02")
    private List dataList02;
    @SerializedName("dataList03")
    private List dataList03;
    @SerializedName("dataList04")
    private List dataList04;
    @SerializedName("dataList05")
    private List dataList05;
    @SerializedName("dataList06")
    private List dataList06;
    @SerializedName("dataList07")
    private List dataList07;
    @SerializedName("dataList08")
    private List dataList08;
    @SerializedName("dataList09")
    private List dataList09;
    @SerializedName("dataList10")
    private List dataList10;

    public String getActividad() {
        return Actividad;
    }

    public String getCantidadDatos() {
        return CantidadDatos;
    }

    public String getDataString01() {
        return dataString01;
    }

    public String getDataString02() {
        return dataString02;
    }

    public List getDataList01() {
        return dataList01;
    }

    public List getDataList02() {
        return dataList02;
    }

    public List getDataList03() {
        return dataList03;
    }

    public List getDataList04() {
        return dataList04;
    }

    public List getDataList05() {
        return dataList05;
    }

    public List getDataList06() {
        return dataList06;
    }

    public List getDataList07() {
        return dataList07;
    }

    public List getDataList08() {
        return dataList08;
    }

    public List getDataList09() {
        return dataList09;
    }

    public List getDataList10() {
        return dataList10;
    }

    public void setActividad(String actividad) {
        Actividad = actividad;
    }

    public void setCantidadDatos(String cantidadDatos) {
        CantidadDatos = cantidadDatos;
    }

    public void setDataString01(String dataString01) {
        this.dataString01 = dataString01;
    }

    public void setDataString02(String dataString02) {
        this.dataString02 = dataString02;
    }

    public void setDataList01(List dataList01) {
        this.dataList01 = dataList01;
    }

    public void setDataList02(List dataList02) {
        this.dataList02 = dataList02;
    }

    public void setDataList03(List dataList03) {
        this.dataList03 = dataList03;
    }

    public void setDataList04(List dataList04) {
        this.dataList04 = dataList04;
    }

    public void setDataList05(List dataList05) {
        this.dataList05 = dataList05;
    }

    public void setDataList06(List dataList06) {
        this.dataList06 = dataList06;
    }

    public void setDataList07(List dataList07) {
        this.dataList07 = dataList07;
    }

    public void setDataList08(List dataList08) {
        this.dataList08 = dataList08;
    }

    public void setDataList09(List dataList09) {
        this.dataList09 = dataList09;
    }

    public void setDataList10(List dataList10) {
        this.dataList10 = dataList10;
    }
}