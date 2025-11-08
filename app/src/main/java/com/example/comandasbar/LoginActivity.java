package com.example.comandasbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import viewModel.LoginViewModel;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etContrasena;
    private Button btnIniciarSesion;
    private TextView tvCrearCuenta;
    private LoginViewModel viewModel;
    private CheckBox cbRecuerdame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 1. Inicializar las vistas del layout
        etEmail = findViewById(R.id.editTextTextEmailAddress);
        etContrasena = findViewById(R.id.editTextTextPassword);
        btnIniciarSesion = findViewById(R.id.iniciarSesion);
        tvCrearCuenta = findViewById(R.id.crearCuenta);
        cbRecuerdame = findViewById(R.id.checkBoxRecuerdame);

        // Obtenemos la instancia del ViewModel
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        // Configuramos los observadores para reaccionar a los eventos del ViewModel
        setupObservers();

        // Cargar preferencias guardadas al iniciar
        cargarPreferencias();

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

    private void setupObservers() {
        // Observador para los mensajes de error
        viewModel.getError().observe(this, mensajeError -> {
            Toast.makeText(this, mensajeError, Toast.LENGTH_SHORT).show();
        });

        // Observador para login exitoso
        viewModel.getLoginExitoso().observe(this, camarero -> {
            Toast.makeText(this, "Bienvenido, " + camarero.getNombreCompleto() + "!", Toast.LENGTH_SHORT).show();

            // Obtenemos los datos introducidos por el usuario
            String email = etEmail.getText().toString().trim();
            String contrasena = etContrasena.getText().toString();


            boolean recordarCredenciales = false;
            if (cbRecuerdame != null) { // Comprobación defensiva
                recordarCredenciales = cbRecuerdame.isChecked();
            }
            guardarSesion(camarero.getIdCamarero(), camarero.getNombreCompleto(), email, contrasena, recordarCredenciales);

            // Navegación a la siguiente actividad
            Intent intent = new Intent(LoginActivity.this, GestionMesasActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void realizarLogin() {
        String email = etEmail.getText().toString().trim();
        String contrasena = etContrasena.getText().toString();
        // El ViewModel ya valida que no estén vacíos.
        viewModel.iniciarSesion(email, contrasena);
    }

    /**
     * Guarda los datos de la sesión y, si la opción está marcada, también las credenciales.
     * @param recordarCredenciales Indica si las credenciales deben ser guardadas.
     */
    private void guardarSesion(long idCamarero, String nombreCompleto, String email, String contrasena, boolean recordarCredenciales) {
        SharedPreferences prefs = getSharedPreferences("SesionCamarero", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("idCamarero", idCamarero);
        editor.putString("nombreCamarero", nombreCompleto);

        if (recordarCredenciales) { // Usamos el parámetro que ya tiene el estado del CheckBox
            editor.putString("saved_email", email);
            editor.putString("saved_password", contrasena);
            editor.putBoolean("remember_me", true);
        } else {
            editor.remove("saved_email");
            editor.remove("saved_password");
            editor.putBoolean("remember_me", false);
        }
        editor.apply();
    }

    /**
     * Carga las preferencias al iniciar la Activity. Si \"Recuérdame\" estaba activo,
     * rellena los campos de email y contraseña.
     */
    private void cargarPreferencias() {
        SharedPreferences prefs = getSharedPreferences("SesionCamarero", MODE_PRIVATE);
        boolean rememberMe = prefs.getBoolean("remember_me", false);

        if (rememberMe) {
            String email = prefs.getString("saved_email", "");
            String password = prefs.getString("saved_password", "");
            etEmail.setText(email);
            etContrasena.setText(password);
            if (cbRecuerdame != null) { // Comprobación defensiva antes de usar cbRecuerdame
                cbRecuerdame.setChecked(true);
            }
        }
    }
}
