package com.example.comandasbar.adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.comandasbar.Producto;
import com.example.comandasbar.R;

import java.util.ArrayList;
import java.util.List;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {

    private List<Producto> listaOriginal;
    private List<Producto> listaFiltrada;
    private Context context;
    private final OnProductoClickListener listener;

    // Interfaz para gestionar el click (avisar al Fragment)
    public interface OnProductoClickListener {
        void onProductoClick(Producto producto);
    }

    public MenuAdapter(Context context, List<Producto> productos, OnProductoClickListener listener) {
        this.context = context;
        this.listaOriginal = productos;
        this.listaFiltrada = new ArrayList<>(productos); // Al principio mostramos todo
        this.listener = listener;
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Usamos el diseño "item_producto_menu.xml" que creamos en el paso anterior
        View view = LayoutInflater.from(context).inflate(R.layout.item_producto_menu, parent, false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        Producto producto = listaFiltrada.get(position);

        holder.txtNombre.setText(producto.getNombre());
        holder.txtPrecio.setText("$" + producto.getPrecio());

        // Cargar foto con Glide
        if (producto.getUrlImagen() != null && !producto.getUrlImagen().isEmpty()) {
            Glide.with(context)
                    .load(producto.getUrlImagen())
                    .placeholder(android.R.drawable.ic_menu_camera)
                    .error(android.R.drawable.ic_delete)
                    .centerCrop()
                    .into(holder.imgFoto);
        } else {
            holder.imgFoto.setImageResource(android.R.drawable.ic_menu_camera);
        }

        // Configurar el click en la tarjeta
        holder.itemView.setOnClickListener(v -> listener.onProductoClick(producto));
    }

    @Override
    public int getItemCount() {
        return listaFiltrada.size();
    }

    // --- LÓGICA DE FILTRADO PARA EL NAVBAR ---
    public void filtrarPorCategoria(String categoria) {
        listaFiltrada.clear();
        if (categoria.equals("Todos")) {
            listaFiltrada.addAll(listaOriginal);
        } else {
            for (Producto p : listaOriginal) {
                if (p.getCategoria().equalsIgnoreCase(categoria)) {
                    listaFiltrada.add(p);
                }
            }
        }
        notifyDataSetChanged(); // Avisa a la lista que se actualice
    }

    public void actualizarListaCompleta(List<Producto> nuevosProductos) {
        this.listaOriginal = nuevosProductos;
        filtrarPorCategoria("Todos"); // Resetea al cargar nuevos datos
    }

    // Clase interna ViewHolder (Referencia a los elementos visuales)
    public static class MenuViewHolder extends RecyclerView.ViewHolder {
        ImageView imgFoto;
        TextView txtNombre, txtPrecio;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFoto = itemView.findViewById(R.id.img_producto);
            txtNombre = itemView.findViewById(R.id.txt_nombre_producto);
            txtPrecio = itemView.findViewById(R.id.txt_precio_producto);
        }
    }
}