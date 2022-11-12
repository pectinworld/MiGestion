package com.migestion.migestion;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.migestion.migestion.Datos.ApiDatos;
import com.migestion.migestion.Datos.Equipos;
import com.migestion.migestion.Datos.EquiposCercanos;
import com.migestion.migestion.Datos.Puntos;
import com.migestion.migestion.Datos.PuntosCercanos;
import com.migestion.migestion.Interface.ApiInterface;
import com.migestion.migestion.Model.Posts;
import com.migestion.migestion.Servicio.DescargaDatosEquipos;
import com.migestion.migestion.Servicio.DescargaDatosPuntos;
import com.migestion.migestion.Servicio.LocationFragment;
import com.splunk.mint.Mint;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private List<Posts> datos;
    private ApiInterface apiInterface;

    private static final String LOG = "MainActivity: ";
    public static final String PREFERENCES_LOGIN = "preferencesLogin";
    public static final String PREFERENCES_PASS = "preferencesPass";
    public static final int REQUEST_PERMISSION_CODE = 1;
    private static final int PERMISSION_REQUEST_CODE = 200;
    public static String baseUrl = "";
    public static String baseUrlFoto = "";
    public static String rutaGeneral = "";
    public static String idTerminal = "";

    public static final double radioDistancia = 1;

    public static final List<Puntos> datosPuntos = new ArrayList<>();
    public static final List<PuntosCercanos> datosPuntosCercanos = new ArrayList<>();
    public static final List<EquiposCercanos> datosEquiposCercanos = new ArrayList<>();
    public static final List<Equipos> datosEquipos = new ArrayList<>();

    DescargaDatosPuntos DescargaDatosPuntos = new DescargaDatosPuntos();
    DescargaDatosEquipos DescargaDatosEquipos = new DescargaDatosEquipos();

    SharedPreferences datosPreferences;
    LottieAnimationView lottieEsperar;
    ConstraintLayout formaEntrada;
    EditText editTextLogin;
    EditText editTextPass;
    Button buttonEntrar;

    String login, pass;

    File fileLogoLocal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Mint.initAndStartSession(this.getApplication(), "41156432");

        datosPreferences = this.getSharedPreferences("com.websmithing.gpstracker.prefs", Context.MODE_PRIVATE);
        lottieEsperar = findViewById(R.id.esperar);
        formaEntrada = findViewById(R.id.formaEntrada);
        editTextLogin = findViewById(R.id.editTextLogin);
        editTextPass = findViewById(R.id.editTextPass);
        buttonEntrar = findViewById(R.id.buttonEntrar);

        baseUrl = getString(R.string.baseUrl) + "/";
        baseUrlFoto = getString(R.string.baseUrlFoto) + "/";
        rutaGeneral = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MGTF"; // Место для хранения промежуточных фотографий

        if (shouldAskPermissions()) {
            askPermissions();
        }
    }

    // МОДУЛЬ ОТВЕЧАЮЩИЙ ЗА РАЗРЕШЕНИЯ
    //
    protected boolean shouldAskPermissions() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(23)
    protected void askPermissions() {
        String[] permissions = {
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE",
                "android.permission.INTERNET",
                "android.permission.CAMERA",
                "android.permission.ACCESS_FINE_LOCATION",
                "android.permission.ACCESS_COARSE_LOCATION"
        };
        int requestCode = 200;
        requestPermissions(permissions, requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) { // Принимаем ответ пользователя.
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) { // Проверяем код запроса. В данном случае: 200.
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) { // Анализирую ответ пользователя.
                // Разрешение предоставлено
                fileLogoLocal = new File(rutaGeneral);
                if (!fileLogoLocal.exists()) {
                    fileLogoLocal.mkdir();
                }

                LocationFragment locaFrag = new LocationFragment();
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(locaFrag, "locaFrag")
                        .commit();
                if (tengoLogin()) {
                    formaEntrada.setVisibility(View.GONE);
                    enviarDatos("0");
                } else {
                    formaEntrada.setVisibility(View.VISIBLE);
                    esperarStop();
                }
            } else {
                // Разрешения НЕ получены
                finish();
            }
        }
    }
    //
    // МОДУЛЬ ОТВЕЧАЮЩИЙ ЗА РАЗРЕШЕНИЯ

    private boolean tengoLogin() {
        boolean respuesta = false;

        login = datosPreferences.getString(PREFERENCES_LOGIN, ""); // Проверка на наличие сохраненного логина
        pass = datosPreferences.getString(PREFERENCES_PASS, ""); // Проверка на наличие сохраненного пароля

        if (!login.equals("") && !pass.equals("")) respuesta = true;

        return respuesta;
    }

    private void esperarStart() {
        lottieEsperar.setVisibility(View.VISIBLE);
        lottieEsperar.playAnimation();

        formaEntrada.setVisibility(View.GONE);
    }

    private void esperarStop() {
        lottieEsperar.setVisibility(View.GONE);
        lottieEsperar.cancelAnimation();
    }

    public void buttonEntrarClick(View view) {
        login = editTextLogin.getText().toString();
        pass = editTextPass.getText().toString();

        enviarDatos("0");
    }

    public void enviarDatos(String actividad) {
        esperarStart();
        getPosts(actividad);
    }

    private void getPosts(String actividad) {
        apiInterface = ApiDatos.getApiDatos().create(ApiInterface.class);
        Call<List<Posts>> call = apiInterface.getDatos(actividad, login, pass, "", "", "");

        call.enqueue(new Callback<List<Posts>>() {
            @Override
            public void onResponse(Call<List<Posts>> call, Response<List<Posts>> response) { // Ответ с сервера.
                datos = response.body();

                esperarStop();

                idTerminal = datos.get(0).getDataString01();

                if (idTerminal.equals("0")) { // Такой терминал не зарегистрирован в системе. Сообщаю об ошибке и возвращаю форму доступа.
                    mostrarVentanaDeInformacion(1);
                } else if (idTerminal.equals("-1")) { // Терминал заблокирован. Сообщаю об ошибке и возвращаю форму доступа.
                    mostrarVentanaDeInformacion(2);
                } else { // Терминал зарегистрирован в системе. Перехожу в рабочую зону.
                    guardarDatosDeAcesso();

                    DescargaDatosPuntos.descargaDatosPuntos();
                    DescargaDatosEquipos.descargaDatosEquipos();

                    Intent intentInicio = new Intent(MainActivity.this, Inicio.class);
                    startActivity(intentInicio);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();
                }
            }

            @SuppressLint("LongLogTag")
            public void onFailure(Call<List<Posts>> call, Throwable t) {
                Log.d("LOG", t.getMessage());
            }
        });
    }

    private void guardarDatosDeAcesso() {
        SharedPreferences.Editor datosPreferencesEditor = datosPreferences.edit();
        datosPreferencesEditor.putString(PREFERENCES_LOGIN, login);
        datosPreferencesEditor.putString(PREFERENCES_PASS, pass);
        datosPreferencesEditor.apply();
    }

    protected void mostrarVentanaDeInformacion(int numeroVentana) {
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);

        dialog.setContentView(R.layout.info_un_button_atencion);
        Button buttonInfoOk = (Button) dialog.findViewById(R.id.buttonAcepto);
        TextView textViewInfo = (TextView) dialog.findViewById(R.id.textViewInfo);

        switch (numeroVentana) {
            case 1: // Информационное окно о том, что терминал не зарегистрирован в системе.

                textViewInfo.setText("Datos de acceso no son correctos");

                buttonInfoOk.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        formaEntrada.setVisibility(View.VISIBLE);
                        dialog.dismiss();
                    }
                });
                break;
            case 2: // Информационное окно о том, что терминал заблокирован.
                //dialog.setContentView(R.layout.info_un_button_atencion);
                textViewInfo.setText("Terminal esta bloqueado");

                buttonInfoOk.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        formaEntrada.setVisibility(View.VISIBLE);
                        dialog.dismiss();
                    }
                });
                break;
        }
        dialog.show();
    }
}
