package com.example.comandasbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

import dao.MesaDao;
import dao.PedidoDao;
import database.AppDataBase;
import database.CamareroEntity;
import database.MesaEntity;
import database.PedidoEntity;

public class GestionMesasActivity extends AppCompatActivity {

    private GridLayout gridMesas;
    private LinearLayout layoutPedidos;
    private MesaDao mesaDao;
    private PedidoDao pedidoDao;
    private long idCamareroActual;
    private TextView txtNombreCamarero;
    private ImageView imgFotoCamarero;
    private Button btnCerrarSesion;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_mesas);

        txtNombreCamarero = findViewById(R.id.txtNombreCamarero);
        imgFotoCamarero = findViewById(R.id.imgFotoCamarero);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        gridMesas = findViewById(R.id.gridMesas);
        layoutPedidos = findViewById(R.id.layoutPedidos);

        mesaDao = AppDataBase.getInstance(getApplicationContext()).mesaDao();
        pedidoDao = AppDataBase.getInstance(getApplicationContext()).pedidoDao();

        SharedPreferences prefs = getSharedPreferences("SesionCamarero", MODE_PRIVATE);
        idCamareroActual = prefs.getLong("idCamarero", -1);
        if (idCamareroActual == -1) {
            Toast.makeText(this, "Error: No hay camarero logueado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        mostrarCamarero();

        btnCerrarSesion.setOnClickListener(v -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        inicializarMesas();
        cargarPedidos();
        iniciarActualizacionCronometros();
    }

    private void iniciarActualizacionCronometros() {
        handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                actualizarCronometros();
                handler.postDelayed(this, 1000);
            }
        }, 1000);
    }

    private void actualizarCronometros() {
        pedidoDao.getAllPedidos().observe(this, pedidos -> {
            if (pedidos != null) {
                mostrarPedidos(pedidos);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacks(null);
        }
    }

    private void cargarPedidos() {
        pedidoDao.getAllPedidos().observe(this, this::mostrarPedidos);
    }

    private void mostrarPedidos(List<PedidoEntity> pedidos) {
        layoutPedidos.removeAllViews();

        for (PedidoEntity pedido : pedidos) {
            View itemView = LayoutInflater.from(this).inflate(R.layout.item_pedido, layoutPedidos, false);

            TextView txtMesa = itemView.findViewById(R.id.txt_pedido_mesa);
            TextView txtDetalle = itemView.findViewById(R.id.txt_pedido_detalle);
            TextView txtTotal = itemView.findViewById(R.id.txt_pedido_total);
            TextView txtCronometro = itemView.findViewById(R.id.txt_cronometro);
            Button btnEntregar = itemView.findViewById(R.id.btn_entregar);

            txtMesa.setText("Mesa " + pedido.getNumeroMesa());
            txtDetalle.setText(pedido.getDetalle());
            txtTotal.setText(String.format(Locale.getDefault(), "Total: $%.2f", pedido.getTotal()));

            int segundosRestantes = pedido.getSegundosRestantes();
            int minutos = segundosRestantes / 60;
            int segundos = segundosRestantes % 60;
            txtCronometro.setText(String.format(Locale.getDefault(), "%02d:%02d", minutos, segundos));

            if (pedido.estaListo()) {
                txtCronometro.setTextColor(ContextCompat.getColor(this, R.color.green));
                txtCronometro.setText("¡LISTO!");
                Toast.makeText(this, "¡Pedido Mesa " + pedido.getNumeroMesa() + " listo!", Toast.LENGTH_SHORT).show();
            }

            btnEntregar.setOnClickListener(v -> {
                new Thread(() -> {
                    pedidoDao.eliminarPedido(pedido.getIdPedido());
                    runOnUiThread(() -> Toast.makeText(this, "Pedido entregado", Toast.LENGTH_SHORT).show());
                }).start();
            });

            layoutPedidos.addView(itemView);
        }
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
                        imgFotoCamarero.setImageResource(android.R.drawable.sym_def_app_icon);
                    }
                }
            });
        }).start();
    }

    private void inicializarMesas() {
        new Thread(() -> {
            if (mesaDao.getAllMesasSync().isEmpty()) {
                for (int i = 1; i <= 6; i++) {
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

            if (mesa.getEstado() == MesaEntity.EstadoMesa.OCUPADA) {
                mesaLayout.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.red));
            } else if (mesa.getIdCamarero() != -1) {
                mesaLayout.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.orange));
            } else {
                mesaLayout.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.green));
            }

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