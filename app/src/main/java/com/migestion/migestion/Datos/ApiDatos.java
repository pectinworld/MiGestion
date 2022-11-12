package com.migestion.migestion.Datos;

import com.migestion.migestion.MainActivity;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiDatos {
    public static final String BASE_URL = MainActivity.baseUrl;
    public static Retrofit retrofit2 = null;

    public static Retrofit getApiDatos() {
        if (retrofit2 == null) {
            retrofit2 = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        }
        return retrofit2;
    }
}
