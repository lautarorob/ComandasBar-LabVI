package com.example.comandasbar;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.comandasbar.Producto;
import com.example.comandasbar.R;

import java.util.ArrayList;
import java.util.List;

public class MenuFragment extends Fragment {

    private List<Producto> listaProductos = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Como no quieres BD, creamos los productos aquí
        cargarProductosDeEjemplo();

        LinearLayout layoutEntradas = view.findViewById(R.id.layout_entradas);
        LinearLayout layoutPrincipales = view.findViewById(R.id.layout_principales);
        LinearLayout layoutBebidas = view.findViewById(R.id.layout_bebidas);


        // Limpiamos vistas por si acaso
        layoutEntradas.removeAllViews();
        layoutPrincipales.removeAllViews();
        layoutBebidas.removeAllViews();

        for (Producto p : listaProductos) {
            Button btnProducto = new Button(getContext());
            btnProducto.setText(p.getNombre() + " - $" + p.getPrecio());
            btnProducto.setOnClickListener(v -> {
                // Comunicarse con la Activity para agregar el producto
                if (getActivity() instanceof PedidoActivity) {
                    ((PedidoActivity) getActivity()).agregarProductoAComanda(p);
                }
            });

            if (p.getCategoria().equals("Entradas")) {
                layoutEntradas.addView(btnProducto);
            } else if (p.getCategoria().equals("Principales")) {
                layoutPrincipales.addView(btnProducto);
            } else if (p.getCategoria().equals("Bebidas")) {
                layoutBebidas.addView(btnProducto);
            }
        }
    }

    private void cargarProductosDeEjemplo() {
        listaProductos.add(new Producto("Empanada", 150.0, "Entradas"));
        listaProductos.add(new Producto("Papas Fritas", 700.0, "Entradas"));
        listaProductos.add(new Producto("Milanesa con Fritas", 1500.0, "Principales"));
        listaProductos.add(new Producto("Bife de Chorizo", 2200.0, "Principales"));
        listaProductos.add(new Producto("Coca Cola", 200.0, "Bebidas"));
        listaProductos.add(new Producto("Pepsi", 300.0, "Bebidas"));
    }
}