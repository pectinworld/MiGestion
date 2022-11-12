package com.migestion.migestion;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.migestion.migestion.Datos.ApiDatos;
import com.migestion.migestion.Interface.ApiInterface;
import com.migestion.migestion.Model.Posts;
import com.migestion.migestion.Servicio.LocationFragment;
import com.migestion.migestion.Servicio.Servicios;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Equipo extends AppCompatActivity {

    Servicios Servicios = new Servicios();

    private static final String LOG = "Equipo";
    private List<Posts> datos;
    private ApiInterface apiInterface;

    LottieAnimationView lottieEsperar;
    TextView textViewActividad;
    ConstraintLayout layoutInstalacion;
    ConstraintLayout layoutDesinstalacion;
    EditText editTextPorcentaje;
    Button buttonInstalar;
    Button buttonDesinstalar;
    TextView textViewDePunto;

    Spinner spinnerPuntos;
    Spinner spinnerEquipoInstalar;
    Spinner spinnerEquipoDesinstalar;

    int ckl;
    String idEquipoInstalar = "0";
    int idEquipoDesinstalar = 0;
    String idPuntoInstalar = "0";
    String idPuntoDesinstalar = "0";
    String porcientoInstalar = "0";
    boolean tecladoAbierto = false;

    List<String> spinnerPuntosList = new ArrayList<String>();
    List<String> spinnerIdPuntosList = new ArrayList<String>();
    List<String> spinnerEquipoInstalarList = new ArrayList<String>();
    List<String> spinnerIdEquipoInstalarList = new ArrayList<String>();
    List<String> spinnerEquipoDesinstalarList = new ArrayList<String>();
    List<String> spinnerIdEquipoDesinstalarList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipo);

        Servicios.controlPuntosCercanos();
        Servicios.filtroEquiposCercanos();

        lottieEsperar = findViewById(R.id.esperar);

        textViewActividad = findViewById(R.id.textViewActividad);
        layoutInstalacion = findViewById(R.id.layoutInstalacion);
        layoutDesinstalacion = findViewById(R.id.layoutDesinstalacion);
        spinnerPuntos = findViewById(R.id.spinnerPuntos);
        spinnerEquipoInstalar = findViewById(R.id.spinnerEquipoInstalar);
        editTextPorcentaje = findViewById(R.id.editTextPorcentaje);
        buttonInstalar = findViewById(R.id.buttonInstalar);
        buttonDesinstalar = findViewById(R.id.buttonDesinstalar);
        spinnerEquipoDesinstalar = findViewById(R.id.spinnerEquipoDesinstalar);
        textViewDePunto = findViewById(R.id.textViewDePunto);

        layoutInstalacion.setVisibility(View.GONE);
        layoutDesinstalacion.setVisibility(View.GONE);

        actualizarCoordenadas();
        filtroPuntosCercanos();
        filtroEquiposLibres();
        filtroEquiposNoLibres();

        KeyboardVisibilityEvent.setEventListener( // Реагирую на открытие/закрытие клавиатуры.
                this,
                new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        if (isOpen) {
                            tecladoAbierto = true;
                        } else {
                            tecladoAbierto = false;
                        }
                        buttonInstalarEquipoActivar();
                    }
                });
    }

    private void actualizarCoordenadas() {
        LocationFragment locaFrag = new LocationFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(locaFrag, "locaFrag")
                .commit();
    }

    private void filtroPuntosCercanos() {
        spinnerPuntosList.clear();
        spinnerIdPuntosList.clear();

        for (ckl = 0; ckl < MainActivity.datosPuntosCercanos.size(); ckl++) {
            spinnerPuntosList.add(MainActivity.datosPuntosCercanos.get(ckl).getNombrePunto());
            spinnerIdPuntosList.add(String.valueOf(MainActivity.datosPuntosCercanos.get(ckl).getIdPunto()));
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
                idPuntoInstalar = spinnerIdPuntosList.get(position);
                buttonInstalarEquipoActivar();
            }

            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    private void filtroEquiposLibres() {
        spinnerEquipoInstalarList.clear();
        spinnerIdEquipoInstalarList.clear();

        spinnerEquipoInstalarList.add("");
        spinnerIdEquipoInstalarList.add("-1");
        for (ckl = 0; ckl < MainActivity.datosEquipos.size(); ckl++) {
            if (MainActivity.datosEquipos.get(ckl).getIdPunto() == 0) {
                spinnerEquipoInstalarList.add(MainActivity.datosEquipos.get(ckl).getNombreEquipo());
                spinnerIdEquipoInstalarList.add(String.valueOf(MainActivity.datosEquipos.get(ckl).getIdEquipo()));
            }
        }

        // Создаю и управляю Выпадающим списком не установленных машинок
        final ArrayAdapter<String> adapterPuntos = new ArrayAdapter<String>(this, R.layout.item_spinner_puntos, spinnerEquipoInstalarList);
        adapterPuntos.setDropDownViewResource(R.layout.item_spinner_dropdown);
        spinnerEquipoInstalar.setAdapter(adapterPuntos);
        spinnerEquipoInstalar.setSelection(0);
        spinnerEquipoInstalar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i(LOG, "position:" + position);
                Log.i(LOG, "id:" + spinnerIdEquipoInstalarList.get(position));
                Log.i(LOG, "nombre:" + spinnerEquipoInstalarList.get(position));
                idEquipoInstalar = spinnerIdEquipoInstalarList.get(position);
                buttonInstalarEquipoActivar();
            }

            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    private void filtroEquiposNoLibres() {
        spinnerEquipoDesinstalarList.clear();
        spinnerIdEquipoDesinstalarList.clear();

        for (ckl = 0; ckl < MainActivity.datosEquiposCercanos.size(); ckl++) {
            spinnerEquipoDesinstalarList.add(MainActivity.datosEquiposCercanos.get(ckl).getNombreEquipo());
            spinnerIdEquipoDesinstalarList.add(String.valueOf(MainActivity.datosEquiposCercanos.get(ckl).getIdEquipo()));

        }

        // Создаю и управляю Выпадающим списком не установленных машинок
        final ArrayAdapter<String> adapterPuntos = new ArrayAdapter<String>(this, R.layout.item_spinner_puntos, spinnerEquipoDesinstalarList);
        adapterPuntos.setDropDownViewResource(R.layout.item_spinner_dropdown);
        spinnerEquipoDesinstalar.setAdapter(adapterPuntos);
        spinnerEquipoDesinstalar.setSelection(0);
        spinnerEquipoDesinstalar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i(LOG, "position:" + position);
                Log.i(LOG, "id:" + spinnerIdEquipoDesinstalarList.get(position));
                Log.i(LOG, "nombre:" + spinnerEquipoDesinstalarList.get(position));
                idEquipoDesinstalar = Integer.parseInt(spinnerIdEquipoDesinstalarList.get(position));
                buttonInstalarEquipoActivar();
                textViewDePunto.setText("de punto: " + Servicios.nombrePuntoPorIdEquipo(idEquipoDesinstalar));

                if (position != 0) {
                    buttonDesinstalar.setEnabled(true);
                } else {
                    buttonDesinstalar.setEnabled(false);
                }
            }

            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    public void enviarDatos(String actividad) {
        esperarStart();
        getPosts(actividad);
    }

    private void getPosts(final String actividad) {
        apiInterface = ApiDatos.getApiDatos().create(ApiInterface.class);

        Call<List<Posts>> call = apiInterface.getDatos(actividad, MainActivity.idTerminal, idEquipoInstalar, idPuntoInstalar, editTextPorcentaje.getText().toString(), "");

        if (actividad.equals("5")) {
            call = apiInterface.getDatos(actividad, MainActivity.idTerminal, String.valueOf(idEquipoDesinstalar), "", "", "");
        }

        call.enqueue(new Callback<List<Posts>>() {
            @Override
            public void onResponse(Call<List<Posts>> call, Response<List<Posts>> response) { // Ответ с сервера.
                datos = response.body();

                textViewActividad.setVisibility(View.GONE);
                esperarStop();

                if (actividad.equals("4")) {
                    Servicios.instalarEquipo(Integer.parseInt(idEquipoInstalar), Integer.parseInt(idPuntoInstalar), Integer.parseInt(editTextPorcentaje.getText().toString()));
                } else {
                    Servicios.desinstalarEquipo(idEquipoDesinstalar);
                }

                Intent intentInicio = new Intent(Equipo.this, Inicio.class);
                startActivity(intentInicio);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }

            @SuppressLint("LongLogTag")
            public void onFailure(Call<List<Posts>> call, Throwable t) {
                Log.d("LOG", t.getMessage());
            }
        });
    }

    private void esperarStart() {
        lottieEsperar.setVisibility(View.VISIBLE);
        lottieEsperar.playAnimation();

        layoutInstalacion.setVisibility(View.GONE);
    }

    private void esperarStop() {
        lottieEsperar.setVisibility(View.GONE);
        lottieEsperar.cancelAnimation();
    }

    private void buttonInstalarEquipoActivar() { // Активировать или нет кропку создания точки.
        if (!tecladoAbierto && !idEquipoInstalar.equals("-1") && !idPuntoInstalar.equals("-1") && !editTextPorcentaje.getText().toString().equals("")) {
            buttonInstalar.setEnabled(true);
        } else {
            buttonInstalar.setEnabled(false);
        }
    }

    public void buttonInstalarClick(View view) {
        enviarDatos("4");
    }

    public void buttonDesinstalarClick(View view) {
        enviarDatos("5");
    }

    public void instalacionClick(View view) {
        textViewActividad.setText("INSTALACIÓN");
        layoutInstalacion.setVisibility(View.VISIBLE);
        layoutDesinstalacion.setVisibility(View.GONE);
    }

    public void desinstalacionClick(View view) {
        textViewActividad.setText("DESINSTALACIÓN");
        layoutInstalacion.setVisibility(View.GONE);
        layoutDesinstalacion.setVisibility(View.VISIBLE);
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

    public void listaLocalesClick(View view) {
        Intent intent = new Intent(this, ListaLocales.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }
}