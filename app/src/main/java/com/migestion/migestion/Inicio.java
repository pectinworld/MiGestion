package com.migestion.migestion;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.migestion.migestion.Servicio.LocationFragment;

public class Inicio extends AppCompatActivity {
    private static final String LOG = "Inicio: ";

    public static String coordenadasLAT = "", coordenadasLNG = "";
    public static Long coordenadasTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        actualizarCoordenadas();
        Log.i(LOG, "LAT: " + coordenadasLAT);
        Log.i(LOG, "LNG: " + coordenadasLNG);
    }

    private void actualizarCoordenadas() {
        LocationFragment locaFrag = new LocationFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(locaFrag, "locaFrag")
                .commit();
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

    public void listaLocalesClick(View view) {
        Intent intentInicio = new Intent(this, ListaLocales.class);
        startActivity(intentInicio);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }
}
