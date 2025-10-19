package com.example.comandasbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import dao.MesaDao;
import database.AppDataBase;
import database.CamareroEntity;
import database.MesaEntity;

public class GestionMesasActivity extends AppCompatActivity {

    private GridLayout gridMesas;
    private MesaDao mesaDao;
    private long idCamareroActual;
    private TextView txtNombreCamarero;
    private ImageView imgFotoCamarero;
    private Button btnCerrarSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_mesas);

        // Vistas
        txtNombreCamarero = findViewById(R.id.txtNombreCamarero);
        imgFotoCamarero = findViewById(R.id.imgFotoCamarero);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        gridMesas = findViewById(R.id.gridMesas);

        mesaDao = AppDataBase.getInstance(getApplicationContext()).mesaDao();

        // Recuperar id camarero
        SharedPreferences prefs = getSharedPreferences("SesionCamarero", MODE_PRIVATE);
        idCamareroActual = prefs.getLong("idCamarero", -1);
        if (idCamareroActual == -1) {
            Toast.makeText(this, "Error: No hay camarero logueado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Mostrar nombre y foto del camarero
        mostrarCamarero();

        // Cerrar sesión
        btnCerrarSesion.setOnClickListener(v -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        inicializarMesas();
    }

    private void mostrarCamarero() {
        new Thread(() -> {
            CamareroEntity camarero = AppDataBase.getInstance(getApplicationContext())
                    .camareroDao()
                    .findById(idCamareroActual);

            runOnUiThread(() -> {
                if (camarero != null) {
                    txtNombreCamarero.setText("Camarero: " + camarero.getNombreCompleto());

                    byte[] foto = camarero.getFotoPerfil();
                    if (foto != null && foto.length > 0) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(foto, 0, foto.length);
                        imgFotoCamarero.setImageBitmap(bitmap);
                    } else {
                        // Aquí usamos el drawable por defecto de Android
                        imgFotoCamarero.setImageResource(android.R.drawable.sym_def_app_icon);
                    }
                }
            });
        }).start();
    }


    private void inicializarMesas() {
        new Thread(() -> {
            if (mesaDao.getAllMesasSync().isEmpty()) {
                for (int i = 1; i <= 6; i++) { // Solo 6 mesas
                    MesaEntity mesa = new MesaEntity(i, MesaEntity.EstadoMesa.LIBRE, -1);
                    mesaDao.insert(mesa);
                }
            }
            runOnUiThread(this::cargarMesas);
        }).start();
    }

    private void cargarMesas() {
        mesaDao.getAllMesas().observe(this, this::mostrarMesas);
    }

    private void mostrarMesas(List<MesaEntity> mesas) {
        gridMesas.removeAllViews();

        for (MesaEntity mesa : mesas) {
            LinearLayout mesaLayout = new LinearLayout(this);
            mesaLayout.setOrientation(LinearLayout.VERTICAL);
            mesaLayout.setPadding(20, 20, 20, 20);
            mesaLayout.setBackgroundResource(R.drawable.bg_mesa);

            // Color según estado
            if (mesa.getEstado() == MesaEntity.EstadoMesa.OCUPADA) {
                mesaLayout.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.red));
            } else if (mesa.getIdCamarero() != -1) {
                mesaLayout.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.orange));
            } else {
                mesaLayout.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.green));
            }

            // Número y estado
            TextView txtNumero = new TextView(this);
            String estadoTexto;
            if (mesa.getIdCamarero() == -1) {
                estadoTexto = "Libre";
            } else if (mesa.getIdCamarero() == idCamareroActual) {
                estadoTexto = "Asignada a TI";
            } else {
                estadoTexto = "Asignada a camarero ID: " + mesa.getIdCamarero();
                mesaLayout.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.red));
            }
            txtNumero.setText("Mesa " + mesa.getNumero() + "\n" + estadoTexto);
            txtNumero.setTextColor(ContextCompat.getColor(this, R.color.white));
            txtNumero.setTextSize(16);
            txtNumero.setPadding(0, 0, 0, 10);
            mesaLayout.addView(txtNumero);

            // Botones dinámicos
            if (mesa.getIdCamarero() == -1) {
                Button btnAsignar = new Button(this);
                btnAsignar.setText("Asignarme");
                btnAsignar.setOnClickListener(v -> {
                    mesa.setIdCamarero(idCamareroActual);
                    mesa.setEstado(MesaEntity.EstadoMesa.ASIGNADA);
                    new Thread(() -> {
                        mesaDao.update(mesa);
                        runOnUiThread(this::cargarMesas);
                    }).start();
                });
                mesaLayout.addView(btnAsignar);
            }

            if (mesa.getIdCamarero() == idCamareroActual) {
                Button btnPedido = new Button(this);
                btnPedido.setText("Nuevo pedido");
                btnPedido.setOnClickListener(v -> {
                    Intent intent = new Intent(this, PedidoActivity.class);
                    intent.putExtra("numeroMesa", mesa.getNumero());
                    startActivity(intent);
                });
                mesaLayout.addView(btnPedido);

                Button btnDesasignar = new Button(this);
                btnDesasignar.setText("Desasignar");
                btnDesasignar.setOnClickListener(v -> {
                    mesa.setIdCamarero(-1);
                    mesa.setEstado(MesaEntity.EstadoMesa.LIBRE);
                    new Thread(() -> {
                        mesaDao.update(mesa);
                        runOnUiThread(this::cargarMesas);
                    }).start();
                });
                mesaLayout.addView(btnDesasignar);
            }

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.setMargins(16, 16, 16, 16);
            mesaLayout.setLayoutParams(params);

            gridMesas.addView(mesaLayout);
        }
    }
}
