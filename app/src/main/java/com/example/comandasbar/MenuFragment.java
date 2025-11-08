package com.example.comandasbar;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.util.List;

import viewModel.MenuViewModel;

public class MenuFragment extends Fragment {

    private MenuViewModel menuViewModel;
    private LinearLayout layoutEntradas;
    private LinearLayout layoutPrincipales;
    private LinearLayout layoutBebidas;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicializa las vistas
        layoutEntradas = view.findViewById(R.id.layout_entradas);
        layoutPrincipales = view.findViewById(R.id.layout_principales);
        layoutBebidas = view.findViewById(R.id.layout_bebidas);
        ImageButton arrowBackButton = view.findViewById(R.id.arrowBack); // <-- Inicializa el ImageButton

        // Obtiene la instancia del ViewModel
        menuViewModel = new ViewModelProvider(this).get(MenuViewModel.class);

        // Configura el observador. Cuando los datos lleguen, se llamará a mostrarMenu.
        menuViewModel.getMenuItems().observe(getViewLifecycleOwner(), productos -> {
            mostrarMenu(productos);
        });

        // Pide al ViewModel que empiece a cargar los datos.
        // El observador de arriba se activará cuando la carga termine.
        menuViewModel.cargarMenu();

        // Listener para el ImageButton de volver atrás
        if (arrowBackButton != null) {
            arrowBackButton.setOnClickListener(v -> {
                // Para volver a GestionMesasActivity desde MenuFragment finaliza PedidoActivity.
                if (getActivity() != null) {
                    getActivity().finish();
                }
            });
        }
    }

    /**
     * Este metodo se encarga únicamente de la lógica de la UI: dibujar los botones del menú.
     * @param listaProductos La lista de productos a mostrar.
     */
    private void mostrarMenu(List<Producto> listaProductos) {
        // Limpiamos las vistas para no duplicar botones si este metodo se llamara de nuevo
        if (layoutEntradas == null || layoutPrincipales == null || layoutBebidas == null) return;
        layoutEntradas.removeAllViews();
        layoutPrincipales.removeAllViews();
        layoutBebidas.removeAllViews();

        // Recorre la lista de productos recibida y crea un botón para cada uno
        for (Producto p : listaProductos) {
            Button btnProducto = new Button(getContext());
            btnProducto.setText(p.getNombre() + " - $" + p.getPrecio());
            btnProducto.setOnClickListener(v -> {
                // Se comunica con la Activity para agregar el producto a la comanda actual
                if (getActivity() instanceof PedidoActivity) {
                    ((PedidoActivity) getActivity()).agregarProductoAComanda(p);
                }
            });

            // Añade el botón a la categoría correcta
            if ("Entradas".equals(p.getCategoria())) {
                layoutEntradas.addView(btnProducto);
            } else if ("Principales".equals(p.getCategoria())) {
                layoutPrincipales.addView(btnProducto);
            } else if ("Bebidas".equals(p.getCategoria())) {
                layoutBebidas.addView(btnProducto);
            }
        }
    }
}
