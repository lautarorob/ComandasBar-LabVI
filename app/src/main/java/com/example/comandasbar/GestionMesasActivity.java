package com.example.comandasbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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

import database.CamareroEntity;
import database.MesaEntity;
import database.PedidoEntity;
import viewModel.GestionMesasViewModel;


public class GestionMesasActivity extends AppCompatActivity {

    // Vistas de la interfaz de usuario
    private GridLayout gridMesas; // Contenedor para mostrar las mesas
    private LinearLayout layoutPedidos; // Contenedor para la lista de pedidos
    private TextView txtNombreCamarero; // Muestra el nombre del camarero logueado
    private ImageView imgFotoCamarero; // Muestra la foto de perfil del camarero
    private Button btnCerrarSesion;

    // ID del camarero que ha iniciado sesión, recuperado de SharedPreferences
    private long idCamareroActual;

    // Instancia del ViewModel que gestiona la lógica y los datos de esta pantalla
    private GestionMesasViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_mesas);

        // --- 1. Inicialización de las Vistas ---
        txtNombreCamarero = findViewById(R.id.txtNombreCamarero);
        imgFotoCamarero = findViewById(R.id.imgFotoCamarero);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        gridMesas = findViewById(R.id.gridMesas);
        layoutPedidos = findViewById(R.id.layoutPedidos);

        // --- 2. Obtención del ViewModel ---
        // Se obtiene la instancia del ViewModel asociada a esta Activity.
        // Si la Activity se recrea (ej. por rotación), recibirá la misma instancia existente de ViewModel.
        viewModel = new ViewModelProvider(this).get(GestionMesasViewModel.class);

        // --- 3. Recuperación de la Sesión y Arranque del ViewModel ---
        SharedPreferences prefs = getSharedPreferences("SesionCamarero", MODE_PRIVATE);
        idCamareroActual = prefs.getLong("idCamarero", -1);

        // Si no hay un ID válido, no se puede continuar. Se cierra la Activity.
        if (idCamareroActual == -1) {
            Toast.makeText(this, "Error: sesión no iniciada.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Se llama al metodo iniciar del ViewModel para que cargue los datos iniciales (camarero, mesas, etc.).
        viewModel.iniciar(idCamareroActual);

        // --- 4. Configuración de Observadores ---
        // Se establece la UI para que reaccione a los cambios de datos en el ViewModel.
        setupObservers();

        // --- 5. Configuración de Listeners ---
        btnCerrarSesion.setOnClickListener(v -> {
            // Limpia los datos de la sesión guardados
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();
            // Redirige a la pantalla de Login
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish(); // Cierra esta Activity para que el usuario no pueda volver con el botón "atrás"
        });
    }

    /**
     * Configura los observadores de LiveData. La UI se actualizará automáticamente
     * cuando los datos en el ViewModel cambien.
     */
    private void setupObservers() {
        // Observador para los datos del camarero
        viewModel.getCamareroActual().observe(this, camarero -> {
            if (camarero != null) {
                txtNombreCamarero.setText("Camarero: " + camarero.getNombreCompleto());
                byte[] foto = camarero.getFotoPerfil();
                if (foto != null && foto.length > 0) {
                    // Si hay una foto, la decodifica de byte[] a Bitmap y la muestra
                    Bitmap bitmap = BitmapFactory.decodeByteArray(foto, 0, foto.length);
                    imgFotoCamarero.setImageBitmap(bitmap);
                } else {
                    // Si no hay foto, muestra un ícono por defecto
                    imgFotoCamarero.setImageResource(android.R.drawable.sym_def_app_icon);
                }
            }
        });

        // Observador para la lista de mesas. Usa una referencia de metodo (this::mostrarMesas).
        // Cada vez que la lista de mesas cambie en la base de datos, se llamará a mostrarMesas.
        viewModel.getMesas().observe(this, this::mostrarMesas);

        // Observador para la lista de pedidos. Similar al de mesas.
        // Se llama a mostrarPedidos cada vez que la lista de pedidos cambia.
        viewModel.getPedidos().observe(this, this::mostrarPedidos);

        // Observador del "tick" del cronómetro.
        // Se ejecuta cada segundo para refrescar la UI de los pedidos.
        viewModel.getCronometroTick().observe(this, tick -> {
            // Vuelve a llamar a mostrarPedidos con la lista de pedidos actual que ya tenemos.
            // Esto fuerza a que se recalculen y redibujen los cronómetros.
            if (viewModel.getPedidos().getValue() != null) {
                mostrarPedidos(viewModel.getPedidos().getValue());
            }
        });
    }

    /**
     * Renderiza la lista de mesas en el GridLayout. Este m3todo es "tonto",
     * solo se encarga de dibujar lo que recibe, sin lógica de negocio.
     * @param mesas La lista de mesas a mostrar.
     */
    private void mostrarMesas(List<MesaEntity> mesas) {
        gridMesas.removeAllViews(); // Limpia las vistas anteriores para no duplicar
        for (MesaEntity mesa : mesas) {
            // Crea el layout para una mesa individual
            LinearLayout mesaLayout = new LinearLayout(this);
            mesaLayout.setOrientation(LinearLayout.VERTICAL);
            mesaLayout.setPadding(20, 20, 20, 20);
            mesaLayout.setBackgroundResource(R.drawable.bg_mesa);

            // Cambia el color de fondo según el estado de la mesa
            if (mesa.getEstado() == MesaEntity.EstadoMesa.OCUPADA) {
                mesaLayout.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.red));
            } else if (mesa.getIdCamarero() != -1) {
                mesaLayout.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.orange));
            } else {
                mesaLayout.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.green));
            }

            // Configura el texto que muestra el número y estado de la mesa
            TextView txtNumero = new TextView(this);
            String estadoTexto;
            if (mesa.getIdCamarero() == -1) {
                estadoTexto = "Libre";
            } else if (mesa.getIdCamarero() == idCamareroActual) {
                estadoTexto = "Asignada a TI";
            } else {
                estadoTexto = "Asignada a otro";
                mesaLayout.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.red)); // Visualmente bloqueada
            }
            txtNumero.setText("Mesa " + mesa.getNumero() + "\n" + estadoTexto);
            txtNumero.setTextColor(ContextCompat.getColor(this, R.color.white));
            txtNumero.setTextSize(16);
            txtNumero.setPadding(0, 0, 0, 10);
            mesaLayout.addView(txtNumero);

            // --- Lógica para mostrar botones de acción ---

            // Si la mesa está libre, muestra el botón "Asignarme"
            if (mesa.getIdCamarero() == -1) {
                Button btnAsignar = new Button(this);
                btnAsignar.setText("Asignarme");
                // Al hacer clic, delega la acción al ViewModel
                btnAsignar.setOnClickListener(v -> viewModel.asignarMesa(mesa, idCamareroActual));
                mesaLayout.addView(btnAsignar);
            }

            // Si la mesa está asignada AL CAMARERO ACTUAL, muestra los botones de acción
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
                // Al hacer clic, delega la acción al ViewModel
                btnDesasignar.setOnClickListener(v -> viewModel.desasignarMesa(mesa));
                mesaLayout.addView(btnDesasignar);
            }

            // Configura los parámetros de layout para que cada mesa ocupe el espacio adecuado en el grid
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.setMargins(16, 16, 16, 16);
            mesaLayout.setLayoutParams(params);

            gridMesas.addView(mesaLayout);
        }
    }

    /**
     * Renderiza la lista de pedidos en el LinearLayout. Al igual que mostrarMesas,
     * solo dibuja la información que recibe.
     * @param pedidos La lista de pedidos a mostrar.
     */
    private void mostrarPedidos(List<PedidoEntity> pedidos) {
        layoutPedidos.removeAllViews(); // Limpia la lista anterior
        for (PedidoEntity pedido : pedidos) {
            // Infla la vista de un item de pedido desde el archivo XML
            View itemView = LayoutInflater.from(this).inflate(R.layout.item_pedido, layoutPedidos, false);

            // Enlaza las vistas del item
            TextView txtMesa = itemView.findViewById(R.id.txt_pedido_mesa);
            TextView txtDetalle = itemView.findViewById(R.id.txt_pedido_detalle);
            TextView txtTotal = itemView.findViewById(R.id.txt_pedido_total);
            TextView txtCronometro = itemView.findViewById(R.id.txt_cronometro);
            Button btnEntregar = itemView.findViewById(R.id.btn_entregar);

            // Rellena los datos del pedido
            txtMesa.setText("Mesa " + pedido.getNumeroMesa());
            txtDetalle.setText(pedido.getDetalle());
            txtTotal.setText(String.format(Locale.getDefault(), "Total: $%.2f", pedido.getTotal()));

            // Calcula y formatea el tiempo restante del cronómetro
            int segundosRestantes = pedido.getSegundosRestantes();
            int minutos = segundosRestantes / 60;
            int segundos = segundosRestantes % 60;
            txtCronometro.setText(String.format(Locale.getDefault(), "%02d:%02d", minutos, segundos));

            // Si el pedido está listo, cambia el texto y el color del cronómetro
            if (pedido.estaListo()) {
                txtCronometro.setTextColor(ContextCompat.getColor(this, R.color.green));
                txtCronometro.setText("¡LISTO!");
            }

            // Configura el botón de entregar
            btnEntregar.setOnClickListener(v -> {
                // Delega la acción de eliminar el pedido al ViewModel
                viewModel.entregarPedido(pedido);
                Toast.makeText(this, "Pedido entregado", Toast.LENGTH_SHORT).show();
            });

            layoutPedidos.addView(itemView);
        }
    }
}
