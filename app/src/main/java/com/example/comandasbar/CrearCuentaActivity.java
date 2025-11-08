package com.example.comandasbar;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import at.favre.lib.crypto.bcrypt.BCrypt;
import dao.CamareroDao;
import database.AppDataBase;
import database.CamareroEntity;
import viewModel.CrearCuentaViewModel;

public class CrearCuentaActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_CODE = 100;

    private EditText nombreEditText, emailEditText, contactoEditText, contrasenaEditText;
    private ImageView imagenPerfilView;
    private Button botonGuardar;

    private ActivityResultLauncher<Uri> cameraLauncher;
    private Uri imagenUri; // Uri para la imagen capturada por la cámara

    private CrearCuentaViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new CrearCuentaViewModel(getApplication());

        //observador para msjs de error
        viewModel.getError().observe(this, mensajeError -> {
            Toast.makeText(this, mensajeError, Toast.LENGTH_SHORT).show();
        });

        //observador para cuenta creada con exito
        viewModel.getCuentaCreada().observe(this, creada ->{
            if(creada){
                Toast.makeText(this, "Cuenta creada con éxito", Toast.LENGTH_SHORT).show();
                finish();//cierra la actividad y vuelve al login
            }
        });

        setContentView(R.layout.activity_crear_cuenta);

        // 1: INICIALIZAR DE CAMPOS Y BOTONES
        nombreEditText = findViewById(R.id.ETnombre);
        emailEditText = findViewById(R.id.ETemail);
        contactoEditText = findViewById(R.id.ETcontacto);
        contrasenaEditText = findViewById(R.id.ETcontrasena);
        imagenPerfilView = findViewById(R.id.imagenPerfil);
        botonGuardar = findViewById(R.id.botonGuardar);

        //2: CONFIGURAR LOS LAUNCHERS
        // Launcher para la cámara
        cameraLauncher = registerForActivityResult(new ActivityResultContracts.TakePicture(),
                success -> { // Usando una expresión lambda para simplificar
                    if (success) {
                        imagenPerfilView.setImageURI(imagenUri); // Muestra la imagen capturada
                    } else {
                        imagenUri = null; // Si el usuario cancela, reseteamos la Uri
                        Toast.makeText(this, "Captura de foto cancelada", Toast.LENGTH_SHORT).show();
                    }
                });

        //comprovacion de permisos
        if (checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }


        //3: CONFIGURAR LOS LISTENERS

        // Listener para la imagen de perfil (abre la cámara)
        imagenPerfilView.setOnClickListener(v -> {
            imagenUri = createImageUri();
            if (imagenUri != null) {
                cameraLauncher.launch(imagenUri);
            }
        });

        // Listener para el botón de guardar
        botonGuardar.setOnClickListener(v -> {
            guardarDatosDelCamarero();
        });

        arrowBackListener();


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void arrowBackListener() {
        ImageButton arrowBack = findViewById(R.id.arrowBack);
        arrowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CrearCuentaActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    /**
     * Este método se llama SOLO cuando se pulsa el botón Guardar.
     */
    private void guardarDatosDelCamarero() {
        // Obtiene el texto introducido por el usuario
        String nombre = nombreEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String contacto = contactoEditText.getText().toString().trim();
        String contrasena = contrasenaEditText.getText().toString();

        viewModel.guardarDatosDelCamarero(nombre, email, contacto, contrasena, imagenUri);
    }


    private Uri createImageUri() {
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = getExternalFilesDir("Pictures");
            File imageFile = File.createTempFile(imageFileName, ".jpg", storageDir);

            return FileProvider.getUriForFile(this, "com.example.comandasbar.provider", imageFile);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al preparar la cámara", Toast.LENGTH_SHORT).show();
            return null;
        }
    }


}
