package com.example.comandasbar;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CobroFragment extends Fragment {

    // Clave para pasar la lista de items en el bundle
    private static final String ARG_COMANDA = "comanda_a_cobrar";

    private List<PedidoItem> comandaParaCobrar;
    private double totalAPagar = 0;

    private TextView txtTotal;
    private EditText editPersonas;
    private Button btnDividir;
    private TextView txtResultadoDivision;
    private Button btnFinalizar;

    /**
     * Este es el método correcto para crear instancias de un Fragment y pasarle datos.
     * Usar un Bundle asegura que los datos se conservan si la app se reconstruye (ej. al girar la pantalla).
     */
    public static CobroFragment newInstance(ArrayList<PedidoItem> comanda) {
        CobroFragment fragment = new CobroFragment();
        Bundle args = new Bundle();
        // Nota: PedidoItem debe implementar la interfaz Serializable para poder pasarlo en un Bundle.
        // Ve a tu clase PedidoItem y añade "implements Serializable"
        args.putSerializable(ARG_COMANDA, comanda);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            comandaParaCobrar = (ArrayList<PedidoItem>) getArguments().getSerializable(ARG_COMANDA);
        }
        if (comandaParaCobrar == null) {
            comandaParaCobrar = new ArrayList<>(); // Evitar null pointer
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cobro, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Enlazar vistas
        txtTotal = view.findViewById(R.id.txt_cobro_total);
        editPersonas = view.findViewById(R.id.edit_numero_personas);
        btnDividir = view.findViewById(R.id.btn_dividir_cuenta);
        txtResultadoDivision = view.findViewById(R.id.txt_resultado_division);
        btnFinalizar = view.findViewById(R.id.btn_finalizar_cobro);

        // Calcular y mostrar el total
        calcularTotal();
        txtTotal.setText(String.format(Locale.getDefault(), "Total a Pagar: $%.2f", totalAPagar));

        // Configurar listeners
        btnDividir.setOnClickListener(v -> dividirCuenta());
        btnFinalizar.setOnClickListener(v -> finalizarCobro());
        Button btnCancelarCobro = view.findViewById(R.id.btn_cancelar_cobro);
        btnCancelarCobro.setOnClickListener(v -> {
            if (getActivity() instanceof PedidoActivity) {
                ((PedidoActivity) getActivity()).cancelarCobro();
            }
        });
    }

    private void calcularTotal() {
        totalAPagar = 0;
        for (PedidoItem item : comandaParaCobrar) {
            totalAPagar += item.getSubtotal();
        }
    }

    private void dividirCuenta() {
        String numPersonasStr = editPersonas.getText().toString();
        if (TextUtils.isEmpty(numPersonasStr)) {
            Toast.makeText(getContext(), "Ingrese un número de personas", Toast.LENGTH_SHORT).show();
            return;
        }

        int numPersonas = Integer.parseInt(numPersonasStr);
        if (numPersonas <= 0) {
            Toast.makeText(getContext(), "El número debe ser mayor a cero", Toast.LENGTH_SHORT).show();
            return;
        }

        double totalPorPersona = totalAPagar / numPersonas;
        txtResultadoDivision.setText(String.format(Locale.getDefault(), "Cada uno paga: $%.2f", totalPorPersona));
    }

    private void finalizarCobro() {
        Toast.makeText(getContext(), "Cobro finalizado. ¡Gracias!", Toast.LENGTH_LONG).show();
        // Opcional: Volver a la pantalla de mesas
        if (getActivity() != null) {
            // Esto cierra la PedidoActivity y vuelve a la pantalla anterior (GestionMesas)
            getActivity().finish();
        }
    }
}