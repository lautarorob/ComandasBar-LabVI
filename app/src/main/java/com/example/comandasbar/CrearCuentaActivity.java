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

public class CrearCuentaActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_CODE = 100;

    private EditText nombreEditText, emailEditText, contactoEditText, contrasenaEditText;
    private ImageView imagenPerfilView;
    private Button botonGuardar;

    private ActivityResultLauncher<Uri> cameraLauncher;
    private Uri imagenUri; // Uri para la imagen capturada por la cámara

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_cuenta);

        // 1: INICIALIZAR TODAS LAS VISTAS
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

        // Realiza validaciones
        if (nombre.isEmpty() || email.isEmpty() || contacto.isEmpty() || contrasena.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }
        if (imagenUri == null) {
            Toast.makeText(this, "Por favor, toma una foto de perfil", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Por favor, introduce un formato de email válido", Toast.LENGTH_SHORT).show();
            emailEditText.setError("Formato de email inválido"); // Opcional: marca el campo con el error
            return;
        }
        if (contacto.length() < 9 || contacto.length() > 11 ){
            Toast.makeText(this, "El contacto debe tener entre 9 y 11 caracteres", Toast.LENGTH_SHORT).show();
            contactoEditText.setError("Formato(codigo de area)(1234567)");
            return;
        }


        // Procesa la imagen a byte[]
        byte[] imagenEnBytes;
        try {
            InputStream inputStream = getContentResolver().openInputStream(imagenUri);
            imagenEnBytes = getBytes(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al procesar la imagen", Toast.LENGTH_SHORT).show();
            return;
        }

        // Hashea la contraseña
        String hashConstrasena = BCrypt.withDefaults().hashToString(12, contrasena.toCharArray());

        // Crea la entidad
        CamareroEntity camarero = new CamareroEntity(email, hashConstrasena, nombre, contacto, imagenEnBytes);

        // Guarda en la base de datos en un hilo de fondo
        AppDataBase db = AppDataBase.getInstance(getApplicationContext());
        CamareroDao camareroDao = db.camareroDao();

        new Thread(() -> {
            camareroDao.insert(camarero);
            runOnUiThread(() -> {
                Toast.makeText(this, "Cuenta creada con éxito", Toast.LENGTH_SHORT).show();
                finish();
            });
        }).start();
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


    private byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }
}
