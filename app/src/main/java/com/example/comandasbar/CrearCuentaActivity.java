package com.example.comandasbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import at.favre.lib.crypto.bcrypt.BCrypt;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CrearCuentaActivity extends AppCompatActivity {


    private EditText nombreEditText, emailEditText, contactoEditText, contrasenaEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_crear_cuenta);

        insertarCamarero();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void insertarCamarero() {
        nombreEditText = findViewById(R.id.ETnombre);
        emailEditText = findViewById(R.id.ETemail);
        contactoEditText = findViewById(R.id.ETcontacto);
        contrasenaEditText = findViewById(R.id.ETcontrasena);

        Button botonGuardar = findViewById(R.id.botonGuardar);

        botonGuardar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String nombre = nombreEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String contacto = contactoEditText.getText().toString();
                String contrasena = contrasenaEditText.getText().toString();

                String hashConstrasena = BCrypt.withDefaults().hashToString(12, contrasena.toCharArray());
            }
        });


    }
}