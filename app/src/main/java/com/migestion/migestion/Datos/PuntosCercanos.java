package com.migestion.migestion.Datos;

public class PuntosCercanos {

    int idPunto;
    String nombrePunto;
    double latPunto;
    double lngPunto;
    int idTerminalAlta;
    int idTerminalPertenece;
    String foto;
    int diasSinRecaudacion;

    public PuntosCercanos(int idPunto, String nombrePunto, double latPunto, double lngPunto, int idTerminalAlta,
                          int idTerminalPertenece, String foto, int diasSinRecaudacion) {
        this.idPunto = idPunto;
        this.latPunto = latPunto;
        this.lngPunto = lngPunto;
        this.nombrePunto = nombrePunto;
        this.idTerminalAlta = idTerminalAlta;
        this.idTerminalPertenece = idTerminalPertenece;
        this.foto = foto;
        this.diasSinRecaudacion = diasSinRecaudacion;
    }

    public int getIdPunto() {
        return idPunto;
    }

    public double getLatPunto() {
        return latPunto;
    }

    public double getLngPunto() {
        return lngPunto;
    }

    public String getNombrePunto() {
        return nombrePunto;
    }

    public int getIdTerminalAlta() {
        return idTerminalAlta;
    }

    public int getIdTerminalPertenece() {
        return idTerminalPertenece;
    }

    public String getFoto() {
        return foto;
    }

    public void setIdPunto(int idPunto) {
        this.idPunto = idPunto;
    }

    public void setLatPunto(double latPunto) {
        this.latPunto = latPunto;
    }

    public void setLngPunto(double lngPunto) {
        this.lngPunto = lngPunto;
    }

    public void setNombrePunto(String nombrePunto) {
        this.nombrePunto = nombrePunto;
    }

    public void setIdTerminalAlta(int idTerminalAlta) {
        this.idTerminalAlta = idTerminalAlta;
    }

    public void setIdTerminalPertenece(int idTerminalPertenece) {
        this.idTerminalPertenece = idTerminalPertenece;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public int getDiasSinRecaudacion() {
        return diasSinRecaudacion;
    }

    public void setDiasSinRecaudacion(int diasSinRecaudacion) {
        this.diasSinRecaudacion = diasSinRecaudacion;
    }
}

