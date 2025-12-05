package com.example.comandasbar;

import java.io.Serializable;

public class Producto implements Serializable {
    private String nombre;
    private double precio;
    private String categoria; // "Entradas", "Bebidas", etc.
    private String urlImagen; // <--- NUEVO CAMPO

    // Actualizamos el constructor
    public Producto(String nombre, double precio, String categoria, String urlImagen) {
        this.nombre = nombre;
        this.precio = precio;
        this.categoria = categoria;
        this.urlImagen = urlImagen;
    }

    // --- Getters ---
    public String getNombre() {
        return nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public String getCategoria() {
        return categoria;
    }
    public String getUrlImagen() { return urlImagen; } // <--- NUEVO GETTER
}
