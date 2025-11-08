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
import java.util.List;
import java.util.Locale;
import viewModel.PedidoViewModel;

public class ComandaFragment extends Fragment {

    private PedidoViewModel pedidoViewModel;
    private LinearLayout layoutItems;
    private TextView txtTotal;
    private Button btnCobrar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_comanda, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        layoutItems = view.findViewById(R.id.layout_items_comanda);
        txtTotal = view.findViewById(R.id.txt_total_comanda);
        btnCobrar = view.findViewById(R.id.btn_cerrar_y_cobrar);

        pedidoViewModel = new ViewModelProvider(requireActivity()).get(PedidoViewModel.class);

        pedidoViewModel.getComanda().observe(getViewLifecycleOwner(), comanda -> {
            layoutItems.removeAllViews();
            double total = 0;

            if (comanda != null) {
                for (PedidoItem item : comanda) {
                    // --- Inflar el nuevo layout para cada item de la comanda ---
                    View itemView = LayoutInflater.from(getContext()).inflate(R.layout.item_comanda_producto, layoutItems, false);

                    TextView txtCantidad = itemView.findViewById(R.id.txt_item_cantidad);
                    TextView txtNombreSubtotal = itemView.findViewById(R.id.txt_item_nombre_subtotal);
                    ImageButton btnRestar = itemView.findViewById(R.id.btn_restar_producto);
                    ImageButton btnEliminar = itemView.findViewById(R.id.btn_eliminar_producto);

                    // --- Rellenar los datos del item ---
                    txtCantidad.setText(String.format(Locale.getDefault(), "%dx", item.getCantidad()));
                    txtNombreSubtotal.setText(String.format(Locale.getDefault(), "%s - $%.2f",
                            item.getProducto().getNombre(), item.getSubtotal()));

                    // --- Configurar Listeners para los botones de ajustar cantidad ---
                    // Pasamos el objeto Producto del PedidoItem para que el ViewModel sepa qué ajustar.
                    btnRestar.setOnClickListener(v -> {
                        pedidoViewModel.restarProducto(item.getProducto());
                    });

                    btnEliminar.setOnClickListener(v -> {
                        pedidoViewModel.eliminarProducto(item.getProducto());
                    });

                    layoutItems.addView(itemView);
                    total += item.getSubtotal();
                }
            }

            txtTotal.setText(String.format(Locale.getDefault(), "Total: $%.2f", total));
        });

        btnCobrar.setOnClickListener(v -> {
            if (getActivity() instanceof PedidoActivity) {
                ((PedidoActivity) getActivity()).iniciarCobro();
            }
        });
    }
}
