package com.migestion.migestion.Servicio;

import android.annotation.SuppressLint;

import com.migestion.migestion.Datos.EquiposCercanos;
import com.migestion.migestion.Datos.PuntosCercanos;
import com.migestion.migestion.Inicio;
import com.migestion.migestion.MainActivity;

public class Servicios {

    public boolean nombrePuntoExiste(String nombrePunto) { // Проверка, что название точки уже используется.
        boolean respuesta = false;

        for (int ckl = 0; ckl < MainActivity.datosPuntos.size(); ckl++) {
            if (nombrePunto.equals(MainActivity.datosPuntos.get(ckl).getNombrePunto())) {
                respuesta = true;
                break;
            }
        }
        return respuesta;
    }

    public boolean controlPuntosCercanos() {
        double lat1 = 0, lng1 = 0;
        double distancia = 0;

        lat1 = Double.parseDouble(Inicio.coordenadasLAT);
        lng1 = Double.parseDouble(Inicio.coordenadasLNG);

        MainActivity.datosPuntosCercanos.clear();
        MainActivity.datosPuntosCercanos.add(new PuntosCercanos(-1, "", 0, 0, 0, 0, "", 0));

        for (int ckl = 0; ckl < MainActivity.datosPuntos.size(); ckl++) {
            distancia = 111.2 * Math.sqrt((lng1 - MainActivity.datosPuntos.get(ckl).getLngPunto()) * (lng1 - MainActivity.datosPuntos.get(ckl).getLngPunto()) + (lat1 - MainActivity.datosPuntos.get(ckl).getLatPunto()) * Math.cos(Math.PI * lng1 / 180) * (lat1 - MainActivity.datosPuntos.get(ckl).getLatPunto()) * Math.cos(Math.PI * lng1 / 180));

            if (distancia <= MainActivity.radioDistancia) {
                MainActivity.datosPuntosCercanos.add(new PuntosCercanos(MainActivity.datosPuntos.get(ckl).getIdPunto(),
                        MainActivity.datosPuntos.get(ckl).getNombrePunto(),
                        MainActivity.datosPuntos.get(ckl).getLatPunto(),
                        MainActivity.datosPuntos.get(ckl).getLngPunto(),
                        MainActivity.datosPuntos.get(ckl).getIdTerminalAlta(),
                        MainActivity.datosPuntos.get(ckl).getIdTerminalPertenece(),
                        MainActivity.datosPuntos.get(ckl).getFoto(),
                        MainActivity.datosPuntos.get(ckl).getDiasSinRecaudacion()));
            }
        }

        return true;
    }

    public void filtroEquiposCercanos() {
        controlPuntosCercanos();

        MainActivity.datosEquiposCercanos.clear();
        MainActivity.datosEquiposCercanos.add(new EquiposCercanos(-1, "", 0, 0));

        for (int ckl = 0; ckl < MainActivity.datosPuntosCercanos.size(); ckl++) {

            for (int ckl2 = 0; ckl2 < MainActivity.datosEquipos.size(); ckl2++) {
                if (MainActivity.datosEquipos.get(ckl2).getIdPunto() == MainActivity.datosPuntosCercanos.get(ckl).getIdPunto()) {
                    MainActivity.datosEquiposCercanos.add(new EquiposCercanos(MainActivity.datosEquipos.get(ckl2).getIdEquipo(),
                            MainActivity.datosEquipos.get(ckl2).getNombreEquipo(),
                            MainActivity.datosEquipos.get(ckl2).getIdPunto(),
                            MainActivity.datosEquipos.get(ckl2).getPorcientoCliente()));
                }
            }
        }
    }

    public void instalarEquipo(final int idEquipo, final int idPunto, final int porciento) {
        new Thread(new Runnable() {
            @SuppressLint("LongLogTag")
            public void run() {
                for (int ckl = 0; ckl < MainActivity.datosEquipos.size(); ckl++) {
                    if (MainActivity.datosEquipos.get(ckl).getIdEquipo() == idEquipo) {
                        MainActivity.datosEquipos.get(ckl).setIdPunto(idPunto);
                        MainActivity.datosEquipos.get(ckl).setPorcientoCliente(porciento);
                    }
                }
            }
        }).start();
    }

    public void desinstalarEquipo(final int idEquipo) {
        new Thread(new Runnable() {
            @SuppressLint("LongLogTag")
            public void run() {
                for (int ckl = 0; ckl < MainActivity.datosEquipos.size(); ckl++) {
                    if (MainActivity.datosEquipos.get(ckl).getIdEquipo() == idEquipo) {
                        MainActivity.datosEquipos.get(ckl).setIdPunto(0);
                        MainActivity.datosEquipos.get(ckl).setPorcientoCliente(0);
                    }
                }
            }
        }).start();
    }

    public String nombrePuntoPorIdEquipo(int idEquipo) {
        String respuesta = "";
        int idPunto = 0;

        for (int ckl = 0; ckl < MainActivity.datosEquipos.size(); ckl++) {
            if (idEquipo == MainActivity.datosEquipos.get(ckl).getIdEquipo()) {
                idPunto = MainActivity.datosEquipos.get(ckl).getIdPunto();
                break;
            }
        }

        if (idPunto != 0) {
            for (int ckl = 0; ckl < MainActivity.datosPuntos.size(); ckl++) {
                if (idPunto == MainActivity.datosPuntos.get(ckl).getIdPunto()) {
                    respuesta = MainActivity.datosPuntos.get(ckl).getNombrePunto();
                    break;
                }
            }
        }

        return respuesta;
    }
}
