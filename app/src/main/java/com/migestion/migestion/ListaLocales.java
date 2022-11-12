package com.migestion.migestion;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.migestion.migestion.Datos.Puntos;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ListaLocales extends AppCompatActivity {
    private static final String LOG = "Class ListaLocales";

    ListView listLocales;
    EditText editTextNombrePunto;

    List<Puntos> datosPuntosOrdenPorDiasRecaudacion = new ArrayList<>();
    List<String> listPuntosList = new ArrayList<String>();
    List<Puntos> datosPuntosTMP = new ArrayList<>();
    ArrayAdapter<String> adapterPuntos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_locales);

        listLocales = findViewById(R.id.listLocales);
        editTextNombrePunto = findViewById(R.id.editTextNombrePunto);

        listPuntosList.clear();

        datosPuntosTMP.addAll(MainActivity.datosPuntos);

        datosPuntosOrdenPorDiasRecaudacion = MainActivity.datosPuntos.stream().
                sorted(Comparator.comparingInt(Puntos::getDiasSinRecaudacion)).
                collect(Collectors.toList());

        for (int i = datosPuntosOrdenPorDiasRecaudacion.size() - 1; i >= 0; i--) {
            if (datosPuntosOrdenPorDiasRecaudacion.get(i).getDiasSinRecaudacion() != -1) {
                listPuntosList.add(datosPuntosOrdenPorDiasRecaudacion.get(i).getNombrePunto().concat(" (").
                        concat(String.valueOf(datosPuntosOrdenPorDiasRecaudacion.get(i).getDiasSinRecaudacion())).concat(" dia/s)"));
            }
        }

        adapterPuntos = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listPuntosList);
        listLocales.setAdapter(adapterPuntos);

        editTextNombrePunto.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                //Log.e(LOG, "afterTextChanged: " + editTextNombrePunto.getText());
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Log.e(LOG, "beforeTextChanged: " + editTextNombrePunto.getText());
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!(editTextNombrePunto.getText().toString().trim().length() > 0)) {
                    for (int i = datosPuntosOrdenPorDiasRecaudacion.size() - 1; i >= 0; i--) {
                        if (datosPuntosOrdenPorDiasRecaudacion.get(i).getDiasSinRecaudacion() != -1) {
                            listPuntosList.add(datosPuntosOrdenPorDiasRecaudacion.get(i).getNombrePunto().concat(" (").
                                    concat(String.valueOf(datosPuntosOrdenPorDiasRecaudacion.get(i).getDiasSinRecaudacion())).concat(" dia/s)"));
                        }
                    }
                    adapterPuntos.notifyDataSetChanged();
                } else {
                    listPuntosList.clear();
                    for (int i = 0; i < datosPuntosTMP.size(); i++) {
                        if (datosPuntosTMP.get(i).getNombrePunto().toLowerCase().contains(editTextNombrePunto.getText().toString().toLowerCase())) {
                            if (datosPuntosTMP.get(i).getDiasSinRecaudacion() != -1) {
                                listPuntosList.add(datosPuntosTMP.get(i).getNombrePunto().concat(" (").
                                        concat(String.valueOf(datosPuntosTMP.get(i).getDiasSinRecaudacion())).concat(" dia/s)"));
                            }
                        }
                    }
                    adapterPuntos.notifyDataSetChanged();
                }
            }
        });
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

    public void recaudacionClick(View view) {
        Intent intentInicio = new Intent(this, Recaudacion.class);
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
}