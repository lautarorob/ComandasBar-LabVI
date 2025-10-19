package com.example.comandasbar;



import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {

    private MaterialButton btnGestionMesas, btnCrearCuenta, btnPerfil, btnCerrarSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnGestionMesas = findViewById(R.id.btnGestionMesas);

        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        btnGestionMesas.setOnClickListener(v -> {
            Intent intent = new Intent(this, GestionMesasActivity.class);
            startActivity(intent);
        });


        btnCerrarSesion.setOnClickListener(v -> {
            // Limpiar sesión y volver a LoginActivity
            Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}
