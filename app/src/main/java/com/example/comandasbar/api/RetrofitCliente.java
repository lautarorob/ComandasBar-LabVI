package com.example.comandasbar.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitCliente {

    private static Retrofit retrofit = null;
    // URL Base según tu documentación (debe terminar en /)
    private static final String BASE_URL = "http://159.203.74.212/api/v1/";

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}