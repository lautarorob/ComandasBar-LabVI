package com.example.comandasbar;

import java.io.Serializable;

public class Producto implements Serializable {
    private String nombre;
    private double precio;
    private String categoria; // "Entradas", "Bebidas", etc.

    public Producto(String nombre, double precio, String categoria) {
        this.nombre = nombre;
        this.precio = precio;
        this.categoria = categoria;
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
}
