package com.example.comandasbar;

import android.content.Intent;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dao.PedidoDao;
import database.AppDataBase;
import database.PedidoEntity;

public class CobroFragment extends Fragment {

    private static final String ARG_COMANDA = "comanda_a_cobrar";
    private static final String ARG_NUMERO_MESA = "numero_mesa";

    private List<PedidoItem> comandaParaCobrar;
    private int numeroMesa;
    private double totalAPagar = 0;

    private TextView txtTotal;
    private EditText editPersonas;
    private Button btnDividir;
    private TextView txtResultadoDivision;
    private Button btnFinalizar;

    public static CobroFragment newInstance(ArrayList<PedidoItem> comanda, int numeroMesa) {
        CobroFragment fragment = new CobroFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_COMANDA, comanda);
        args.putInt(ARG_NUMERO_MESA, numeroMesa);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            comandaParaCobrar = (ArrayList<PedidoItem>) getArguments().getSerializable(ARG_COMANDA);
            numeroMesa = getArguments().getInt(ARG_NUMERO_MESA, -1);
        }
        if (comandaParaCobrar == null) {
            comandaParaCobrar = new ArrayList<>();
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

        txtTotal = view.findViewById(R.id.txt_cobro_total);
        editPersonas = view.findViewById(R.id.edit_numero_personas);
        btnDividir = view.findViewById(R.id.btn_dividir_cuenta);
        txtResultadoDivision = view.findViewById(R.id.txt_resultado_division);
        btnFinalizar = view.findViewById(R.id.btn_finalizar_cobro);

        calcularTotal();
        txtTotal.setText(String.format(Locale.getDefault(), "Total a Pagar: $%.2f", totalAPagar));

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
        // Crear detalle simple
        StringBuilder detalle = new StringBuilder();
        for (int i = 0; i < comandaParaCobrar.size(); i++) {
            PedidoItem item = comandaParaCobrar.get(i);
            detalle.append(item.getCantidad()).append("x ").append(item.getProducto().getNombre());
            if (i < comandaParaCobrar.size() - 1) {
                detalle.append(", ");
            }
        }

        // Crear pedido (60 segundos = 1 minuto de preparación)
        PedidoEntity pedido = new PedidoEntity(numeroMesa, detalle.toString(), totalAPagar, 60);

        // Guardar en base de datos
        new Thread(() -> {
            PedidoDao pedidoDao = AppDataBase.getInstance(requireContext()).pedidoDao();
            pedidoDao.insert(pedido);

            requireActivity().runOnUiThread(() -> {
                Toast.makeText(getContext(), "Pedido enviado a cocina!", Toast.LENGTH_SHORT).show();

                // Volver a GestionMesas
                Intent intent = new Intent(requireContext(), GestionMesasActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                requireActivity().finish();
            });
        }).start();
    }
}