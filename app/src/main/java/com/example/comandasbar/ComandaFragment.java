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
import android.widget.TextView;
import android.widget.Toast;

import com.example.comandasbar.PedidoActivity;
import com.example.comandasbar.PedidoItem;
import com.example.comandasbar.R;

import java.util.List;
import java.util.Locale;

public class ComandaFragment extends Fragment {

    private LinearLayout layoutItems;
    private TextView txtTotal;
    private Button btnEnviar, btnCobrar;

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

        btnCobrar.setOnClickListener(v -> {
            if (getActivity() instanceof PedidoActivity) {
                ((PedidoActivity) getActivity()).iniciarCobro();
            }
        });

        // Cargar la comanda inicial si ya existe (al rotar pantalla, por ejemplo)
        if (getActivity() instanceof PedidoActivity) {
            actualizarComanda(((PedidoActivity) getActivity()).getComandaActual());
        }
    }

    public void actualizarComanda(List<PedidoItem> comanda) {
        if (layoutItems == null) return; // Si la vista no está creada aún

        layoutItems.removeAllViews();
        double total = 0;

        for (PedidoItem item : comanda) {
            TextView itemTextView = new TextView(getContext());
            String textoItem = String.format(Locale.getDefault(), "%d x %s - $%.2f",
                    item.getCantidad(), item.getProducto().getNombre(), item.getSubtotal());
            itemTextView.setText(textoItem);
            itemTextView.setTextSize(16);
            layoutItems.addView(itemTextView);
            total += item.getSubtotal();
        }

        txtTotal.setText(String.format(Locale.getDefault(), "Total: $%.2f", total));
    }
}