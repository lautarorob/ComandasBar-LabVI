package com.example.comandasbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText; // Importar EditText
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import at.favre.lib.crypto.bcrypt.BCrypt; // Importante: importar BCrypt
import database.AppDataBase;
import database.CamareroEntity;
import dao.CamareroDao;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etContrasena;
    private Button btnIniciarSesion;
    private TextView tvCrearCuenta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 1. Inicializar las vistas del layout
        etEmail = findViewById(R.id.editTextTextEmailAddress);
        etContrasena = findViewById(R.id.editTextTextPassword);
        btnIniciarSesion = findViewById(R.id.iniciarSesion);
        tvCrearCuenta = findViewById(R.id.crearCuenta);

        // 2. Configurar el listener para el botón de Iniciar Sesión
        btnIniciarSesion.setOnClickListener(v -> {

            realizarLogin();
        });

        // 3. Configurar el listener para ir a la pantalla de Crear Cuenta
        tvCrearCuenta.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, CrearCuentaActivity.class);
            startActivity(intent);
        });
    }

    private void realizarLogin() {
        String email = etEmail.getText().toString().trim();
        String contrasena = etContrasena.getText().toString();

        // Validaciones de los campos de entrada
        if (email.isEmpty() || contrasena.isEmpty()) {
            Toast.makeText(this, "Por favor, introduce el email y la contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        // Obtener la instancia de la base de datos y el DAO
        AppDataBase db = AppDataBase.getInstance(getApplicationContext());
        CamareroDao camareroDao = db.camareroDao();

        //  Operación de base de datos en un hilo de fondo para no bloquear la UI
        new Thread(() -> {
            // Buscamos al camarero por su email
            CamareroEntity camarero = camareroDao.findByEmail(email);

            // Volvemos al hilo principal para manejar el resultado
            runOnUiThread(() -> {
                if (camarero == null) {
                    // Caso 1: No se encontró ningún usuario con ese email
                    Toast.makeText(LoginActivity.this, "Email no registrado", Toast.LENGTH_LONG).show();
                } else {
                    // Caso 2: Se encontró un usuario. Ahora verificamos la contraseña.
                    // Usamos BCrypt para comparar la contraseña introducida con el hash guardado.
                    BCrypt.Result resultado = BCrypt.verifyer().verify(contrasena.toCharArray(), camarero.getContrasena());

                    if (resultado.verified) {
                        // ¡Contraseña correcta! Login exitoso.
                        Toast.makeText(LoginActivity.this, "¡Bienvenido, " + camarero.getNombreCompleto() + "!", Toast.LENGTH_LONG).show();

                        // Aquí navegarías a la siguiente actividad (ej. la pantalla principal de la app)
                        // Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        // intent.putExtra("CAMARERO_ID", camarero.getId()); // Opcional: pasar el ID del camarero
                        // startActivity(intent);
                        // finish(); // Cierra LoginActivity para que el usuario no pueda volver con el botón "atrás"
                    } else {
                        // Contraseña incorrecta
                        Toast.makeText(LoginActivity.this, "Contraseña incorrecta", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }).start();
    }
}
