package com.example.comandasbar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.example.comandasbar.PedidoItem;

import java.util.ArrayList;
import java.util.List;

public class PedidoActivity extends AppCompatActivity {

    private List<PedidoItem> comandaActual = new ArrayList<>();
    private int numeroMesa;
    private ComandaFragment comandaFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido);

        numeroMesa = getIntent().getIntExtra("numeroMesa", -1);
        setTitle("Pedido Mesa N° " + numeroMesa);

        if (savedInstanceState == null) {
            // Crear instancias de los fragments
            MenuFragment menuFragment = new MenuFragment();
            comandaFragment = new ComandaFragment();

            // Cargarlos en sus contenedores
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_menu, menuFragment)
                    .replace(R.id.fragment_container_comanda, comandaFragment)
                    .commit();
        }
    }

    // Método para agregar un producto a la comanda
    public void agregarProductoAComanda(Producto producto) {
        boolean encontrado = false;
        for (PedidoItem item : comandaActual) {
            if (item.getProducto().getNombre().equals(producto.getNombre())) {
                item.incrementarCantidad();
                encontrado = true;
                break;
            }
        }
        if (!encontrado) {
            comandaActual.add(new PedidoItem(producto));
        }
        // Notificar al ComandaFragment que la lista ha cambiado
        comandaFragment.actualizarComanda(comandaActual);
    }

    // Método para obtener la comanda actual (lo usará el ComandaFragment)
    public List<PedidoItem> getComandaActual() {
        return comandaActual;
    }

    // Método para iniciar el proceso de cobro
    public void iniciarCobro() {
        // Ocultar el layout de toma de pedido
        findViewById(R.id.layout_toma_pedido).setVisibility(View.GONE);

        // Crear el fragment de cobro
        CobroFragment cobroFragment = CobroFragment.newInstance(new ArrayList<>(comandaActual));

        // Cargar el fragment en el contenedor principal (ocupa toda la pantalla)
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container, cobroFragment)
                .addToBackStack(null)
                .commit();
    }

    // Método para cancelar el cobro y volver al pedido
    public void cancelarCobro() {
        // Mostrar nuevamente el layout de pedido
        findViewById(R.id.layout_toma_pedido).setVisibility(View.VISIBLE);

        // Volver atrás en el back stack
        getSupportFragmentManager().popBackStack();
    }

}