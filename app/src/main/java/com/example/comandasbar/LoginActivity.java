package com.example.comandasbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import database.CamareroEntity;

public class LoginActivity extends AppCompatActivity {

    // Simulación de la instancia del Camarero guardada en SharedPreferences
    private CamareroEntity camareroActual;
    private static final String PREFS_NAME = "CamareroPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Asume que tienes un layout llamado activity_perfil con los campos de login/registro/update
        setContentView(R.layout.activity_login);

        // 1. CREAR CUENTA (Registro)
        TextView btnCrearCuenta = findViewById(R.id.crearCuenta);
        btnCrearCuenta.setOnClickListener(v -> crearCuenta("mesero1", "pass1", "Juan Pérez", "555-1234"));

        // 2. INICIAR SESIÓN
        Button btnIniciarSesion = findViewById(R.id.iniciarSesion);
        btnIniciarSesion.setOnClickListener(v -> iniciarSesion("mesero1", "pass1"));


        // Cargar el perfil al iniciar la Activity (si existe)
        cargarPerfilGuardado();

        //Me lleva a la pantalla de crear cuenta
        listenerCrearCuenta(btnCrearCuenta);
    }

    private void listenerCrearCuenta(TextView btnCrearCuenta) {
        btnCrearCuenta.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, CrearCuentaActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Gestión de Perfil: Crear cuenta (El Camarero registra su perfil inicial).
     */
    private void crearCuenta(String usuario, String pass, String nombre, String contacto) {
        // En una app real, aquí se verificaría si el usuario ya existe en la base de datos
        camareroActual = new CamareroEntity(usuario, pass, nombre, contacto);
        guardarPerfil();
        Toast.makeText(this, "Cuenta creada para " + nombre, Toast.LENGTH_LONG).show();
    }

    /**
     * Gestión de Perfil: Iniciar sesión (El Camarero accede a la aplicación).
     */
    private void iniciarSesion(String usuario, String pass) {
        cargarPerfilGuardado(); // Intentar cargar si ya existe un perfil

        if (camareroActual == null) {
            Toast.makeText(this, "Error: Perfil no registrado. Cree una cuenta primero.", Toast.LENGTH_LONG).show();
            return;
        }

        if (camareroActual.iniciarSesion(usuario, pass)) {
            guardarEstadoSesion(true);
            Toast.makeText(this, "Sesión iniciada para: " + camareroActual.getNombreCompleto(), Toast.LENGTH_LONG).show();
            // Aquí se navegaría a la siguiente Activity (ej. Pantalla de Pedidos)
        } else {
            Toast.makeText(this, "Fallo al iniciar sesión.", Toast.LENGTH_LONG).show();
        }
    }


    // --- Métodos de Persistencia (SharedPreferences) ---

    // Guarda los datos del perfil (Nombre, Contacto, etc.)
    private void guardarPerfil() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Guardar solo los datos necesarios para la simulación
        editor.putString("nombre", camareroActual.getNombreCompleto());
        editor.putString("contacto", camareroActual.getContacto());
        editor.putString("fotoUrl", camareroActual.getFotoPerfilURL());
        // En una app real, guardarías usuario y contraseña (hash)

        editor.apply();
    }

    // Carga los datos guardados al iniciar la Activity
    private void cargarPerfilGuardado() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        String nombre = prefs.getString("nombre", null);
        String contacto = prefs.getString("contacto", null);
        String fotoUrl = prefs.getString("fotoUrl", "default.jpg");

        // Si hay datos, crea una instancia simulada (la contraseña y usuario faltarían aquí)
        if (nombre != null) {
            // Nota: Se simula un perfil. En la realidad, se cargaría de una BD
            // Usamos datos dummy de usuario/pass para que la clase Camarero funcione
            camareroActual = new CamareroEntity("mesero1", "pass1", nombre, contacto);
            camareroActual.setFotoPerfilURL(fotoUrl);

            // Cargar el estado de la sesión
            boolean sesionActiva = prefs.getBoolean("sesionActiva", false);
            if(sesionActiva) {
                // Simula que la sesión sigue activa después de reabrir la app
                camareroActual.iniciarSesion("mesero1", "pass1");
            }
        }
    }

    // Guarda el estado de la sesión (para que se mantenga al reabrir la app)
    private void guardarEstadoSesion(boolean activa) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean("sesionActiva", activa).apply();
    }

}