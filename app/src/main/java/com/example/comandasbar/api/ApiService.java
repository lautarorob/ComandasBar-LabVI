package com.example.comandasbar.api;

import com.example.comandasbar.api.model.LoginRequest;
import com.example.comandasbar.api.model.LoginResponse;
import com.example.comandasbar.api.model.RegisterRequest;
// import com.example.comandasbar.api.model.RegisterResponse; // Ya no la importaremos aquí directamente para el éxito

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import okhttp3.ResponseBody; // Importar ResponseBody

public interface ApiService {

    // --- REGISTRO ---
    @POST("auth/register")
    Call<ResponseBody> registrarUsuario(@Body RegisterRequest request); // Cambiado a ResponseBody

    // --- LOGIN (Nuevo) ---
    @POST("auth/login")
    Call<LoginResponse> loginUsuario(@Body LoginRequest request);
}