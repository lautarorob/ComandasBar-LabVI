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
import android.widget.TextView;
import java.util.Locale;
import android.util.Log;
import viewModel.PedidoViewModel;

public class ComandaFragment extends Fragment {

    private PedidoViewModel pedidoViewModel;

    // Contenedores para las 3 categorías
    private LinearLayout containerEntradas;
    private LinearLayout containerPrincipales;
    private LinearLayout containerBebidas;

    // Títulos de las categorías (Para ocultarlos si no hay items)
    private TextView lblEntradas;
    private TextView lblPrincipales;
    private TextView lblBebidas;

    private TextView txtTotal;
    private TextView txtTituloMesa;
    private Button btnCobrar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_comanda, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Inicializar Vistas
        containerEntradas = view.findViewById(R.id.container_entradas);
        containerPrincipales = view.findViewById(R.id.container_principales);
        containerBebidas = view.findViewById(R.id.container_bebidas);

        // Inicializamos los títulos (Asegúrate de ponerles estos IDs en el XML)
        lblEntradas = view.findViewById(R.id.lbl_entradas);
        lblPrincipales = view.findViewById(R.id.lbl_principales);
        lblBebidas = view.findViewById(R.id.lbl_bebidas);

        txtTotal = view.findViewById(R.id.txt_total_comanda);
        txtTituloMesa = view.findViewById(R.id.txt_titulo_mesa);
        btnCobrar = view.findViewById(R.id.btn_cerrar_y_cobrar);

        // 2. Obtener número de mesa de la Activity padre
        if (getActivity() != null && getActivity().getIntent() != null) {
            int numMesa = getActivity().getIntent().getIntExtra("numeroMesa", 0);
            txtTituloMesa.setText("COMANDA MESA " + numMesa);
        }

        // 3. Conectar ViewModel
        pedidoViewModel = new ViewModelProvider(requireActivity()).get(PedidoViewModel.class);

        // 4. Observar cambios en la comanda
        pedidoViewModel.getComanda().observe(getViewLifecycleOwner(), comanda -> {

            // Limpiar contenedores antes de redibujar
            containerEntradas.removeAllViews();
            containerPrincipales.removeAllViews();
            containerBebidas.removeAllViews();

            double total = 0;

            if (comanda != null) {
                for (PedidoItem item : comanda) {

                    // Inflar la vista del item
                    View itemView = LayoutInflater.from(getContext())
                            .inflate(R.layout.item_comanda_producto, null, false);

                    // Conectar elementos del item
                    TextView txtNombre = itemView.findViewById(R.id.txt_item_nombre);
                    TextView txtCantidad = itemView.findViewById(R.id.txt_item_cantidad);
                    TextView txtSubtotal = itemView.findViewById(R.id.txt_item_subtotal);

                    ImageButton btnRestar = itemView.findViewById(R.id.btn_restar_producto);
                    ImageButton btnSumar = itemView.findViewById(R.id.btn_sumar_producto);
                    ImageButton btnEliminar = itemView.findViewById(R.id.btn_eliminar_producto);

                    // Poner datos
                    String productName = item.getProducto().getNombre();
                    Log.d("ComandaFragment", "Product Name: " + productName);
                    txtNombre.setText(productName);
                    txtCantidad.setText(String.valueOf(item.getCantidad()));
                    txtSubtotal.setText(String.format(Locale.getDefault(), "$%.2f", item.getSubtotal()));

                    // --- FUNCIONALIDAD BOTONES ---
                    btnRestar.setOnClickListener(v -> pedidoViewModel.restarProducto(item.getProducto()));
                    btnSumar.setOnClickListener(v -> pedidoViewModel.agregarProducto(item.getProducto()));
                    btnEliminar.setOnClickListener(v -> pedidoViewModel.eliminarProducto(item.getProducto()));

                    // --- CLASIFICACIÓN ---
                    String categoria = item.getProducto().getCategoria();

                    if ("Entradas".equalsIgnoreCase(categoria)) {
                        containerEntradas.addView(itemView);
                    } else if ("Principales".equalsIgnoreCase(categoria)) {
                        containerPrincipales.addView(itemView);
                    } else if ("Bebidas".equalsIgnoreCase(categoria)) {
                        containerBebidas.addView(itemView);
                    } else {
                        containerPrincipales.addView(itemView);
                    }

                    total += item.getSubtotal();
                }
            }

            // --- ACTUALIZAR VISIBILIDAD DE SECCIONES ---
            // Ocultamos o mostramos la sección completa (Título + Tarjeta) según si tiene items
            actualizarVisibilidadSeccion(containerEntradas, lblEntradas);
            actualizarVisibilidadSeccion(containerPrincipales, lblPrincipales);
            actualizarVisibilidadSeccion(containerBebidas, lblBebidas);

            txtTotal.setText(String.format(Locale.getDefault(), "$%.2f", total));
        });

        btnCobrar.setOnClickListener(v -> {
            if (getActivity() instanceof PedidoActivity) {
                ((PedidoActivity) getActivity()).iniciarCobro();
            }
        });
    }

    /**
     * Muestra u oculta la sección completa (Título y CardView) dependiendo de si hay items.
     * Busca el CardView padre subiendo en la jerarquía de vistas.
     */
    private void actualizarVisibilidadSeccion(LinearLayout container, TextView label) {
        // Obtenemos el CardView que envuelve al container (su padre inmediato)
        View cardPadre = (View) container.getParent();

        if (container.getChildCount() > 0) {
            // Si hay productos: MOSTRAR TODO
            if (label != null) label.setVisibility(View.VISIBLE);
            if (cardPadre != null) cardPadre.setVisibility(View.VISIBLE);
        } else {
            // Si NO hay productos: OCULTAR TODO
            if (label != null) label.setVisibility(View.GONE);
            if (cardPadre != null) cardPadre.setVisibility(View.GONE);
        }
    }
}
