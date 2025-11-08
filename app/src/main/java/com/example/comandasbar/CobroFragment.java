package com.example.comandasbar;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import viewModel.CobroViewModel;

public class CobroFragment extends Fragment {

    private static final String ARG_COMANDA = "comanda_a_cobrar";
    private static final String ARG_NUMERO_MESA = "numero_mesa";

    private List<PedidoItem> comandaParaCobrar;
    private int numeroMesa;

    private CobroViewModel cobroViewModel;

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

        cobroViewModel = new ViewModelProvider(this).get(CobroViewModel.class);

        TextView txtTotal = view.findViewById(R.id.txt_cobro_total);
        EditText editPersonas = view.findViewById(R.id.edit_numero_personas);
        Button btnDividir = view.findViewById(R.id.btn_dividir_cuenta);
        TextView txtResultadoDivision = view.findViewById(R.id.txt_resultado_division);
        Button btnFinalizar = view.findViewById(R.id.btn_finalizar_cobro);

        cobroViewModel.getTotalAPagar().observe(getViewLifecycleOwner(), total -> {
            if (total != null) {
                txtTotal.setText(String.format(Locale.getDefault(), "Total a Pagar: $%.2f", total));
            }
        });

        cobroViewModel.getResultadoDivision().observe(getViewLifecycleOwner(), resultado -> {
            if (resultado != null) {
                txtResultadoDivision.setText(resultado);
            }
        });

        cobroViewModel.getCobroFinalizado().observe(getViewLifecycleOwner(), finalizado -> {
            if (finalizado != null && finalizado) {
                Toast.makeText(getContext(), "Pedido enviado a cocina!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(requireContext(), GestionMesasActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                requireActivity().finish();
            }
        });

        cobroViewModel.getErrorMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

        cobroViewModel.calcularTotal(comandaParaCobrar);

        btnDividir.setOnClickListener(v -> {
            String numPersonasStr = editPersonas.getText().toString();
            if (numPersonasStr == null || numPersonasStr.isEmpty()) {
                cobroViewModel.dividirCuenta("1");
            } else {

                cobroViewModel.dividirCuenta(numPersonasStr);
            }
        });

        btnFinalizar.setOnClickListener(v -> {
            Double total = cobroViewModel.getTotalAPagar().getValue();
            if (total != null) {
                cobroViewModel.finalizarCobro(numeroMesa, comandaParaCobrar, total);
            }
        });

        Button btnCancelarCobro = view.findViewById(R.id.btn_cancelar_cobro);
        btnCancelarCobro.setOnClickListener(v -> {
            if (getActivity() instanceof PedidoActivity) {
                ((PedidoActivity) getActivity()).cancelarCobro();
            }
        });

        ImageButton arrowBackButton = view.findViewById(R.id.arrowBack);
        // Listener para el botón de cancelar cobro (inferior)
        btnCancelarCobro.setOnClickListener(v -> {
            if (getActivity() instanceof PedidoActivity) {
                // Llama al metodo cancelarCobro de la PedidoActivity
                ((PedidoActivity) getActivity()).cancelarCobro();
            }
        });

        // listener para el ImageButton de volver atrás
        arrowBackButton.setOnClickListener(v -> {
            if (getActivity() instanceof PedidoActivity) {
                // Llama al mismo metodo cancelarCobro de la PedidoActivity
                ((PedidoActivity) getActivity()).cancelarCobro();
            }
        });
    }
}
