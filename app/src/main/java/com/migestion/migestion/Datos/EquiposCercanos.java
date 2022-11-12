package com.migestion.migestion.Datos;

public class EquiposCercanos {

    int idEquipo;
    String nombreEquipo;
    int idPunto;
    int porcientoCliente;

    public EquiposCercanos(int idEquipo, String nombreEquipo, int idPunto, int porcientoCliente) {
        this.idEquipo = idEquipo;
        this.nombreEquipo = nombreEquipo;
        this.idPunto = idPunto;
        this.porcientoCliente = porcientoCliente;
    }

    public int getIdEquipo() {
        return idEquipo;
    }

    public String getNombreEquipo() {
        return nombreEquipo;
    }

    public int getIdPunto() {
        return idPunto;
    }

    public int getPorcientoCliente() {
        return porcientoCliente;
    }

    public void setIdEquipo(int idEquipo) {
        this.idEquipo = idEquipo;
    }

    public void setNombreEquipo(String nombreEquipo) {
        this.nombreEquipo = nombreEquipo;
    }

    public void setIdPunto(int idPunto) {
        this.idPunto = idPunto;
    }

    public void setPorcientoCliente(int porcientoCliente) {
        this.porcientoCliente = porcientoCliente;
    }
}

