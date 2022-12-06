package com.migestion.migestion;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.migestion.migestion.Datos.ApiDatos;
import com.migestion.migestion.Interface.ApiInterface;
import com.migestion.migestion.Model.Posts;
import com.migestion.migestion.Servicio.DescargaDatosPuntos;
import com.migestion.migestion.Servicio.LocationFragment;
import com.migestion.migestion.Servicio.Servicios;
import com.migestion.migestion.Servicio.SubidaFoto;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NuevoPunto extends AppCompatActivity {

    Servicios Servicios = new Servicios();
    DescargaDatosPuntos DescargaDatosPuntos = new DescargaDatosPuntos();
    Date date = new Date();

    private static final String LOG = "Nuevo punto: ";
    private static final int TYPE_PHOTO = 1;
    private static final int CAM_REQUEST_UBICACION = 1416;

    LottieAnimationView lottieEsperar;
    ImageView imageViewFoto;
    Button buttonCrearPunto;
    EditText editTextNombrePunto;
    TextView textView4NombrePunto;
    TextView textViewExiste;

    boolean tecladoAbierto = false;
    boolean tengoFoto = false;
    boolean tengoNombrePunto = false;
    public static String rutaFotoUbicacion;
    String nombreFoto = "", nombrePunto = "", respuestaServidor = "";
    long timeLocal;
    int cklFalloGps = 0;

    private List<Posts> datos;
    private ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_punto);

        lottieEsperar = findViewById(R.id.esperar);
        imageViewFoto = findViewById(R.id.imageViewFoto);
        buttonCrearPunto = findViewById(R.id.buttonCrearPunto);
        editTextNombrePunto = findViewById(R.id.editTextNombrePunto);
        textView4NombrePunto = findViewById(R.id.textView4NombrePunto);
        textViewExiste = findViewById(R.id.textViewExiste);

        buttonCrearPunto.setEnabled(false);

        KeyboardVisibilityEvent.setEventListener( // Реагирую на открытие/закрытие клавиатуры.
                this,
                new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        if (isOpen) {
                            imageViewFoto.setVisibility(View.GONE);
                            tecladoAbierto = true;
                        } else {
                            imageViewFoto.setVisibility(View.VISIBLE);
                            tecladoAbierto = false;
                        }
                        buttonCrearPuntoActivar();
                    }
                });

        editTextNombrePunto.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (editTextNombrePunto.length() > 0) {
                    tengoNombrePunto = true;
                } else {
                    tengoNombrePunto = false;
                }
                buttonCrearPuntoActivar();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
    }

    @SuppressLint("LongLogTag")
    private Uri generateFileUri(int type) {

        File file = null;
        file = new File(rutaFotoUbicacion);

        Log.d(LOG, "fileName = " + file);
        return Uri.fromFile(file);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAM_REQUEST_UBICACION) {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            //options.inSampleSize = 5;
            Bitmap bMap = BitmapFactory.decodeFile(rutaFotoUbicacion, options);
            if (bMap != null) {
                bMap = scaleDown(bMap, 1024, true); // Изменяю размер фото
                try (FileOutputStream out = new FileOutputStream(rutaFotoUbicacion)) {
                    bMap.compress(Bitmap.CompressFormat.JPEG, 100, out); // Уменьшаю размер фото
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imageViewFoto.setImageBitmap(bMap);
                tengoFoto = true;
                buttonCrearPuntoActivar();
            }
        }
    }

    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize, boolean filter) {
        float ratio = Math.min((float) maxImageSize / realImage.getWidth(), (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width, height, filter);
        return newBitmap;
    }

    private void buttonCrearPuntoActivar() { // Активировать или нет кропку создания точки.
        if (!Servicios.nombrePuntoExiste(editTextNombrePunto.getText().toString())) {
            textViewExiste.setVisibility(View.GONE);
            if (!tecladoAbierto && tengoFoto && tengoNombrePunto) {
                buttonCrearPunto.setEnabled(true);
            } else {
                buttonCrearPunto.setEnabled(false);
            }
        } else {
            buttonCrearPunto.setEnabled(false);
            textViewExiste.setVisibility(View.VISIBLE); // Имя точки повторяется.
        }
    }

    public void crearFoto(View view) {
        Date currentTime = new Date();
        nombreFoto = MainActivity.idTerminal + String.valueOf(currentTime.getTime()) + ".png";
        rutaFotoUbicacion = MainActivity.rutaGeneral + "/" + nombreFoto;

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, generateFileUri(TYPE_PHOTO));
        startActivityForResult(intent, CAM_REQUEST_UBICACION);
    }

    public void crearPunto(View view) {
        new SubidaFoto("").execute(); // Загружаю фото на сервер
        nombrePunto = editTextNombrePunto.getText().toString();
        actualizarCoordenadas();
        enviarDatos("1");
    }

    public void enviarDatos(String actividad) {
        esperarStart();


        timeLocal = date.getTime();

        if (Inicio.coordenadasTime == null) Inicio.coordenadasTime = Long.valueOf(0);
        if (timeLocal - Inicio.coordenadasTime > 60000) {
            cklFalloGps++;
            Log.d(LOG, "cklFalloGps = " + cklFalloGps);
            if (cklFalloGps <= 600) {
                actualizarCoordenadas();

            } else {
                mostrarVentanaDeInformacion(3);
            }
        } else {
            cklFalloGps = 0;
            getPosts(actividad);
        }


    }

    private void getPosts(String actividad) {
        apiInterface = ApiDatos.getApiDatos().create(ApiInterface.class);
        Call<List<Posts>> call = apiInterface.getDatos(actividad, MainActivity.idTerminal, Inicio.coordenadasLAT, Inicio.coordenadasLNG, nombrePunto, nombreFoto);

        call.enqueue(new Callback<List<Posts>>() {
            @Override
            public void onResponse(Call<List<Posts>> call, Response<List<Posts>> response) { // Ответ с сервера.
                datos = response.body();

                respuestaServidor = datos.get(0).getActividad();

                esperarStop();
                if (respuestaServidor.equals("nuevoPuntoEcho")) {
                    DescargaDatosPuntos.descargaDatosPuntos();
                    mostrarVentanaDeInformacion(1);
                } else if (respuestaServidor.equals("nuevoPuntoExiste")) {
                    imageViewFoto.setVisibility(View.VISIBLE);
                    buttonCrearPunto.setVisibility(View.VISIBLE);
                    editTextNombrePunto.setVisibility(View.VISIBLE);
                    textView4NombrePunto.setVisibility(View.VISIBLE);
                    mostrarVentanaDeInformacion(2);
                }
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

        imageViewFoto.setVisibility(View.GONE);
        buttonCrearPunto.setVisibility(View.GONE);
        editTextNombrePunto.setVisibility(View.GONE);
        textView4NombrePunto.setVisibility(View.GONE);
    }

    private void esperarStop() {
        lottieEsperar.setVisibility(View.GONE);
        lottieEsperar.cancelAnimation();
    }

    private void actualizarCoordenadas() {
        LocationFragment locaFrag = new LocationFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(locaFrag, "locaFrag")
                .commit();
    }

    protected void mostrarVentanaDeInformacion(int numeroVentana) {
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);

        switch (numeroVentana) {
            case 1: // Информационное окно о том, что новая точка создана.
                dialog.setContentView(R.layout.info_un_button_enhorabuena);
                Button buttonInfoAcepto = (Button) dialog.findViewById(R.id.buttonAcepto);
                TextView textViewInfo = (TextView) dialog.findViewById(R.id.textViewInfo);

                textViewInfo.setText("Punto esta creado con exito");

                buttonInfoAcepto.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        Intent intentInicio = new Intent(NuevoPunto.this, Inicio.class);
                        startActivity(intentInicio);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        finish();

                        dialog.dismiss();
                    }
                });
                break;
            case 2: // Информационное окно о том, что название новой точки уже существует.
                dialog.setContentView(R.layout.info_un_button_atencion);
                Button buttonInfoAtencionAcepto = (Button) dialog.findViewById(R.id.buttonAcepto);
                TextView textViewAtencionInfo = (TextView) dialog.findViewById(R.id.textViewInfo);

                textViewAtencionInfo.setText("Nombre de punto existe. No puedes tener dos puntos con mismo nombre.");
                buttonInfoAtencionAcepto.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        dialog.dismiss();
                    }
                });
                break;
            case 3: // Информационное окно о том, что долго не могу получить нормальные координаты.
                dialog.setContentView(R.layout.info_un_button_atencion);
                Button buttonInfoCoordenadasAcepto = (Button) dialog.findViewById(R.id.buttonAcepto);
                TextView textViewCoordenadasInfo = (TextView) dialog.findViewById(R.id.textViewInfo);

                textViewCoordenadasInfo.setText("No se puede concegir coordenadas actuales. Aregla problema.");
                buttonInfoCoordenadasAcepto.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        dialog.dismiss();
                    }
                });
                break;
        }
        dialog.show();
    }

    public void inicioClick(View view) {
        Intent intentInicio = new Intent(this, Inicio.class);
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
        Intent intent = new Intent(NuevoPunto.this, ListaLocales.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }
}