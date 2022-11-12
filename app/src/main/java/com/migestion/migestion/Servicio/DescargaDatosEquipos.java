package com.migestion.migestion.Servicio;

import android.annotation.SuppressLint;
import android.util.Log;

import com.migestion.migestion.Datos.ApiDatos;
import com.migestion.migestion.Datos.Equipos;
import com.migestion.migestion.Interface.ApiInterface;
import com.migestion.migestion.MainActivity;
import com.migestion.migestion.Model.Posts;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DescargaDatosEquipos {
    private static final String LOG = "Class DescargaDatosEquipos ";

    private List<Posts> datos;
    private ApiInterface apiInterface;

    int ckl;

    public void descargaDatosEquipos() {
        apiInterface = ApiDatos.getApiDatos().create(ApiInterface.class);
        Call<List<Posts>> call = apiInterface.getDatos("3", MainActivity.idTerminal, "", "", "", "");

        call.enqueue(new Callback<List<Posts>>() {
            @Override
            public void onResponse(Call<List<Posts>> call, Response<List<Posts>> response) { // Ответ с сервера.
                datos = response.body();

                MainActivity.datosEquipos.clear();

                new Thread(new Runnable() {
                    @SuppressLint("LongLogTag")
                    public void run() {
                        for (ckl = 0; ckl < Integer.parseInt(datos.get(0).getCantidadDatos()); ckl++) {
                            setDataEquipos();
                        }
                        Log.d(LOG, "Данные Ubicaciones загружены");
                    }
                }).start();

            }

            @SuppressLint("LongLogTag")
            public void onFailure(Call<List<Posts>> call, Throwable t) {
                Log.d(LOG, t.getMessage());
            }
        });
    }

    private void setDataEquipos() {
        MainActivity.datosEquipos.add(new Equipos(Integer.parseInt(datos.get(0).getDataList01().get(ckl).toString()),
                datos.get(0).getDataList02().get(ckl).toString(),
                Integer.parseInt(datos.get(0).getDataList03().get(ckl).toString()),
                Integer.parseInt(datos.get(0).getDataList04().get(ckl).toString())));
    }
}

