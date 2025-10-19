package com.example.comandasbar;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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
            MenuFragment menuFragment = new MenuFragment();
            comandaFragment = new ComandaFragment();

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_menu, menuFragment)
                    .replace(R.id.fragment_container_comanda, comandaFragment)
                    .commit();
        }
    }

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
        comandaFragment.actualizarComanda(comandaActual);
    }

    public List<PedidoItem> getComandaActual() {
        return comandaActual;
    }

    public void iniciarCobro() {
        findViewById(R.id.layout_toma_pedido).setVisibility(View.GONE);
        CobroFragment cobroFragment = CobroFragment.newInstance(new ArrayList<>(comandaActual), numeroMesa);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container, cobroFragment)
                .addToBackStack(null)
                .commit();
    }

    public void cancelarCobro() {
        findViewById(R.id.layout_toma_pedido).setVisibility(View.VISIBLE);
        getSupportFragmentManager().popBackStack();
    }
}