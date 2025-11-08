package com.example.comandasbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.View;
import java.util.ArrayList;

import viewModel.PedidoViewModel;

/**
 * PedidoActivity aloja y coordina los fragmentos para tomar un nuevo pedido (MenuFragment y ComandaFragment)
 * y el fragmento para procesar el cobro (CobroFragment).
 * Utiliza un PedidoViewModel compartido para mantener el estado de la comanda actual.
 */
public class PedidoActivity extends AppCompatActivity {

    private int numeroMesa;
    // Declara el ViewModel que será compartido con los fragmentos.
    private PedidoViewModel pedidoViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido);

        // Obtiene el ViewModel. Será la misma instancia para esta Activity y sus fragmentos.
        pedidoViewModel = new ViewModelProvider(this).get(PedidoViewModel.class);

        // Recupera el número de mesa del Intent
        numeroMesa = getIntent().getIntExtra("numeroMesa", -1);
        setTitle("Pedido Mesa N° " + numeroMesa);

        // Solo carga los fragmentos si es la primera vez que se crea la Activity
        if (savedInstanceState == null) {
            MenuFragment menuFragment = new MenuFragment();
            ComandaFragment comandaFragment = new ComandaFragment();

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_menu, menuFragment)
                    .replace(R.id.fragment_container_comanda, comandaFragment)
                    .commit();
        }
    }

    /**
     * Metodo llamado desde MenuFragment para añadir un producto.
     * Delega completamente la lógica al ViewModel.
     * @param producto El producto seleccionado en el menú.
     */
    public void agregarProductoAComanda(Producto producto) {
        // La Activity ya no gestiona la lista. Solo notifica al ViewModel del evento.
        pedidoViewModel.agregarProducto(producto);
    }

    /**
     * Inicia el proceso de cobro.
     * Reemplaza los fragmentos de toma de pedido por el CobroFragment.
     */
    public void iniciarCobro() {
        // Oculta la vista que contiene el menú y la comanda
        findViewById(R.id.layout_toma_pedido).setVisibility(View.GONE);

        // Crea una instancia de CobroFragment.
        // Pasa la comanda actual obteniéndola directamente desde el ViewModel.
        ArrayList<PedidoItem> comandaActual = new ArrayList<>(pedidoViewModel.getComanda().getValue());
        CobroFragment cobroFragment = CobroFragment.newInstance(comandaActual, numeroMesa);

        getSupportFragmentManager().beginTransaction()
                // Reemplaza el contenedor principal por el fragmento de cobro
                .replace(R.id.main_fragment_container, cobroFragment)
                .addToBackStack(null) // Permite volver atrás con el botón del sistema
                .commit();
    }

    /**
     * Cancela el proceso de cobro.
     * Muestra de nuevo la vista de toma de pedido y vuelve al estado anterior del FragmentManager.
     */
    public void cancelarCobro() {
        // Vuelve a hacer visible el layout del menú y la comanda
        findViewById(R.id.layout_toma_pedido).setVisibility(View.VISIBLE);
        // Elimina el CobroFragment de la pila y restaura los fragmentos anteriores
        getSupportFragmentManager().popBackStack();
    }
}
