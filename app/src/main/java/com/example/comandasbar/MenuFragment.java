package com.example.comandasbar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comandasbar.adapter.MenuAdapter;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

import viewModel.MenuViewModel;

public class MenuFragment extends Fragment {

    private MenuViewModel menuViewModel;
    private RecyclerView recyclerMenu;
    private TabLayout tabLayout;
    private MenuAdapter adaptador;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Inicializar Vistas
        recyclerMenu = view.findViewById(R.id.recyclerMenu);
        tabLayout = view.findViewById(R.id.tabLayoutMenu);
        ImageButton arrowBackButton = view.findViewById(R.id.arrowBack);

        // 2. Configurar el RecyclerView (Grilla de 2 columnas)
        recyclerMenu.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // 3. Inicializar el Adaptador
        adaptador = new MenuAdapter(getContext(), new ArrayList<>(), producto -> {
            if (getActivity() instanceof PedidoActivity) {
                ((PedidoActivity) getActivity()).agregarProductoAComanda(producto);
            }
        });
        recyclerMenu.setAdapter(adaptador);

        // 4. Configurar las Pestañas (Navbar)
        configurarTabs();

        // 5. ViewModel y Datos
        menuViewModel = new ViewModelProvider(this).get(MenuViewModel.class);

        menuViewModel.getMenuItems().observe(getViewLifecycleOwner(), productos -> {
            adaptador.actualizarListaCompleta(productos);

            // Seleccionar primera pestaña por defecto
            if (tabLayout.getTabCount() > 0 && tabLayout.getSelectedTabPosition() == -1) {
                TabLayout.Tab tab = tabLayout.getTabAt(0);
                if (tab != null) tab.select();
            }
        });

        menuViewModel.cargarMenu();

        // Botón atrás
        if (arrowBackButton != null) {
            arrowBackButton.setOnClickListener(v -> {
                if (getActivity() != null) {
                    getActivity().finish();
                }
            });
        }
    }

    private void configurarTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("Todos"));
        tabLayout.addTab(tabLayout.newTab().setText("Entradas"));
        tabLayout.addTab(tabLayout.newTab().setText("Principales"));
        tabLayout.addTab(tabLayout.newTab().setText("Bebidas"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getText() != null) {
                    String categoria = tab.getText().toString();
                    adaptador.filtrarPorCategoria(categoria);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }
}

