package com.example.comandasbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Revisa las SharedPreferences
        SharedPreferences prefs = getSharedPreferences("SesionCamarero", MODE_PRIVATE);
        boolean sesionRecordada = prefs.getBoolean("remember_me", false); // Usamos la misma clave que ya guardas

        Intent intent;
        if (sesionRecordada) {
            // 2. Si la sesión debe ser recordada, ve directo a GestionMesasActivity
            intent = new Intent(this, GestionMesasActivity.class);
        } else {
            // 3. Si no, ve a la pantalla de LoginActivity
            intent = new Intent(this, LoginActivity.class);
        }

        startActivity(intent);
        finish();
    }
}