package com.migestion.migestion;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.migestion.migestion.Datos.ApiDatos;
import com.migestion.migestion.Interface.ApiInterface;
import com.migestion.migestion.Model.Posts;
import com.migestion.migestion.Servicio.LocationFragment;
import com.migestion.migestion.Servicio.Servicios;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Recaudacion extends AppCompatActivity {
    private static final String LOG = "Class Recaudacion";

    private List<Posts> datos;
    private ApiInterface apiInterface;

    Servicios Servicios = new Servicios();
    LottieAnimationView lottieEsperar;

    List<String> spinnerPuntosList = new ArrayList<String>();
    List<String> spinnerIdPuntosList = new ArrayList<String>();

    List<String> listEquipoId = new ArrayList<String>();
    List<String> listEquipoNombre = new ArrayList<String>();
    List<String> listEquipoRecaudacion = new ArrayList<String>();
    List<String> listEquipoPorciento = new ArrayList<String>();
    List<String> listEquipoNombreRecaudacion = new ArrayList<String>();

    Spinner spinnerPuntos;
    ListView listMaquinasRecaudar;
    ArrayAdapter<String> adapter;

    TextView textViewTotalRecaudadoNumero;
    TextView textViewTotalParaClienteNumero;
    TextView textViewTotalParaEmpresaNumero;

    Button buttonRecaudar;

    int ckl;

    String idEquipo;
    String recaudado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recaudacion);

        spinnerPuntos = findViewById(R.id.spinnerPuntos);
        listMaquinasRecaudar = findViewById(R.id.listMaquinasRecaudar);
        lottieEsperar = findViewById(R.id.esperar);

        textViewTotalRecaudadoNumero = findViewById(R.id.textViewTotalRecaudadoNumero);
        textViewTotalParaClienteNumero = findViewById(R.id.textViewTotalParaClienteNumero);
        textViewTotalParaEmpresaNumero = findViewById(R.id.textViewTotalParaEmpresaNumero);

        buttonRecaudar = findViewById(R.id.buttonRecaudar);

        buttonRecaudar.setEnabled(false);

        if (actualizarCoordenadas()) {
            if (Servicios.controlPuntosCercanos()) {
                filtroPuntosCercanos();
            } else {
                Toast.makeText(this, "Problemas 'controlPuntosCercanos'", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Coordenadas no estan actualizadas", Toast.LENGTH_SHORT).show();
        }

        Log.i(LOG, "LNG:");
    }

    private void filtroPuntosCercanos() {
        spinnerPuntosList.clear();
        spinnerIdPuntosList.clear();

        spinnerPuntosList.add("");
        spinnerIdPuntosList.add("0");

        for (ckl = 0; ckl < MainActivity.datosPuntosCercanos.size(); ckl++) {
            // Проверяю наличие машинок в пункте.
            boolean hayMaquinasEnPunto = false;
            for (int ckl2 = 0; ckl2 < MainActivity.datosEquipos.size(); ckl2++) {
                if (MainActivity.datosEquipos.get(ckl2).getIdPunto() == MainActivity.datosPuntosCercanos.get(ckl).getIdPunto()) {
                    hayMaquinasEnPunto = true;
                }
            }

            if (hayMaquinasEnPunto) { // Если в пункте стоит машинка, то добавляю пункт в список.
                spinnerPuntosList.add(MainActivity.datosPuntosCercanos.get(ckl).getNombrePunto() + " (" + MainActivity.datosPuntosCercanos.get(ckl).getDiasSinRecaudacion() + " día/s)");
                spinnerIdPuntosList.add(String.valueOf(MainActivity.datosPuntosCercanos.get(ckl).getIdPunto()));
            }
        }

        // Создаю и управляю Выпадающим списком Puntos
        final ArrayAdapter<String> adapterPuntos = new ArrayAdapter<String>(this, R.layout.item_spinner_puntos, spinnerPuntosList);
        adapterPuntos.setDropDownViewResource(R.layout.item_spinner_dropdown);
        spinnerPuntos.setAdapter(adapterPuntos);
        spinnerPuntos.setSelection(0);
        spinnerPuntos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i(LOG, "position:" + position);
                Log.i(LOG, "id:" + spinnerIdPuntosList.get(position));
                Log.i(LOG, "nombre:" + spinnerPuntosList.get(position));

                listEquipoId.clear();
                listEquipoNombre.clear();
                listEquipoRecaudacion.clear();
                listEquipoPorciento.clear();
                listEquipoNombreRecaudacion.clear();

                for (int ckl = 0; ckl < MainActivity.datosEquipos.size(); ckl++) {
                    //if (MainActivity.datosEquipos.get(ckl).getIdPunto() == MainActivity.datosPuntosCercanos.get(position).getIdPunto()) {
                    if (MainActivity.datosEquipos.get(ckl).getIdPunto() == Integer.parseInt(spinnerIdPuntosList.get(position))) {
                        listEquipoId.add(String.valueOf(MainActivity.datosEquipos.get(ckl).getIdEquipo()));
                        listEquipoNombre.add(String.valueOf(MainActivity.datosEquipos.get(ckl).getNombreEquipo()));
                        listEquipoRecaudacion.add("");
                        listEquipoPorciento.add(String.valueOf(MainActivity.datosEquipos.get(ckl).getPorcientoCliente()));
                        listEquipoNombreRecaudacion.add(String.valueOf(MainActivity.datosEquipos.get(ckl).getNombreEquipo()));
                    }
                }

                adapter = new ArrayAdapter<String>(Recaudacion.this, android.R.layout.simple_list_item_1, listEquipoNombre);
                listMaquinasRecaudar.setAdapter(adapter);

                listMaquinasRecaudar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
                        Log.d(LOG, listEquipoId.get(position));
                        Log.d(LOG, listEquipoNombre.get(position));
                        Log.d(LOG, listEquipoRecaudacion.get(position));

                        mostrarVentanaDeInformacion(1, Integer.parseInt(listEquipoId.get(position)), listEquipoNombre.get(position), Integer.parseInt(listEquipoPorciento.get(position)), position);
                    }
                });

                if (position != 0) {
                    listMaquinasRecaudar.setVisibility(View.VISIBLE);
                } else {
                    listMaquinasRecaudar.setVisibility(View.GONE);
                }

            }

            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    private boolean actualizarCoordenadas() {
        LocationFragment locaFrag = new LocationFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(locaFrag, "locaFrag")
                .commit();

        return true;
    }

    protected void mostrarVentanaDeInformacion(int numeroVentana, int idEquipo, String nombreEquipo, int porcientoCliente, final int listPosition) {
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);

        switch (numeroVentana) {
            case 1: // Информационное окно для ввода суммы сбора на указанной машинке.
                dialog.setContentView(R.layout.info_recaudacion);
                TextView textViewNombreMaquina = (TextView) dialog.findViewById(R.id.textViewNombreMaquina);
                final EditText editTextSumaRecaudacion = (EditText) dialog.findViewById(R.id.editTextSumaRecaudacion);
                Button buttonInfoOk = (Button) dialog.findViewById(R.id.buttonAcepto);
                Button buttonInfoCancelar = (Button) dialog.findViewById(R.id.buttonCancelar);

                textViewNombreMaquina.setText(nombreEquipo);

                buttonInfoOk.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if (!editTextSumaRecaudacion.getText().toString().equals("")) {
                            listEquipoRecaudacion.set(listPosition, editTextSumaRecaudacion.getText().toString());
                            listEquipoNombreRecaudacion.set(listPosition, listEquipoNombre.get(listPosition) + " --- " + editTextSumaRecaudacion.getText().toString() + "€");

                            adapter = new ArrayAdapter<String>(Recaudacion.this, android.R.layout.simple_list_item_1, listEquipoNombreRecaudacion);
                            listMaquinasRecaudar.setAdapter(adapter);

                            recalculoRecaudacion();
                        }
                        dialog.dismiss();
                    }
                });

                buttonInfoCancelar.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                break;
        }
        dialog.show();
    }

    private void recalculoRecaudacion() {
        int recaudado = 0;
        int totalRecaudado = 0;
        double totalCliente = 0;
        double paraCliente = 0;
        double totalEmpresa = 0;

        buttonRecaudar.setEnabled(true);

        for (ckl = 0; ckl < listEquipoId.size(); ckl++) {
            if (listEquipoRecaudacion.get(ckl).equals("")) {
                buttonRecaudar.setEnabled(false); // Если хоть одна машинка не собрана то блокирую кнопку общего сбора.
                recaudado = 0;
            } else {
                recaudado = Integer.parseInt(listEquipoRecaudacion.get(ckl));
            }

            totalRecaudado += recaudado;
            paraCliente = recaudado * Integer.parseInt(listEquipoPorciento.get(ckl)) / 100;
            paraCliente = (int) paraCliente * 100;
            paraCliente = paraCliente / 100;
            totalCliente += paraCliente;
            totalEmpresa = totalRecaudado - totalCliente;

            textViewTotalRecaudadoNumero.setText(String.valueOf(totalRecaudado));
            textViewTotalParaClienteNumero.setText(String.valueOf(totalCliente));
            textViewTotalParaEmpresaNumero.setText(String.valueOf(totalEmpresa));
        }
    }

    private void esperarStart() {
        lottieEsperar.setVisibility(View.VISIBLE);
        lottieEsperar.playAnimation();
    }

    private void esperarStop() {
        lottieEsperar.setVisibility(View.GONE);
        lottieEsperar.cancelAnimation();
    }

    public void enviarDatos(String actividad) {
        esperarStart();
        getPosts(actividad);
    }

    private void getPosts(String actividad) {
        apiInterface = ApiDatos.getApiDatos().create(ApiInterface.class);
        Call<List<Posts>> call = apiInterface.getDatos(actividad, MainActivity.idTerminal, idEquipo, recaudado, "", "");

        call.enqueue(new Callback<List<Posts>>() {
            @Override
            public void onResponse(Call<List<Posts>> call, Response<List<Posts>> response) { // Ответ с сервера.
                datos = response.body();
            }

            @SuppressLint("LongLogTag")
            public void onFailure(Call<List<Posts>> call, Throwable t) {
                Log.d("LOG", t.getMessage());
            }
        });
    }

    public void RecaudarPunto(View view) {
        for (ckl = 0; ckl < listEquipoId.size(); ckl++) {
            idEquipo = listEquipoId.get(ckl);
            recaudado = listEquipoRecaudacion.get(ckl);
            enviarDatos("6");
        }

        esperarStop();
        Intent intentInicio = new Intent(this, Inicio.class);
        startActivity(intentInicio);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }


    public void inicioClick(View view) {
        Intent intentInicio = new Intent(this, Inicio.class);
        startActivity(intentInicio);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    public void nuevoPuntoClick(View view) {
        Intent intentInicio = new Intent(this, NuevoPunto.class);
        startActivity(intentInicio);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    public void equipoClick(View view) {
        Intent intentInicio = new Intent(this, Equipo.class);
        startActivity(intentInicio);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    public void listaLocalesClick(View view) {
        Intent intentListaLocales = new Intent(this, ListaLocales.class);
        startActivity(intentListaLocales);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }
}
