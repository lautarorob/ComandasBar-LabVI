package com.example.comandasbar;

import java.io.Serializable;

public class PedidoItem implements Serializable {
    private Producto producto;
    private int cantidad;
    private String notas;

    public PedidoItem(Producto producto) {
        this.producto = producto;
        this.cantidad = 1; // Por defecto se agrega uno
        this.notas = "";
    }

    // --- Getters y Setters ---
    public Producto getProducto() {
        return producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    // --- Métodos útiles ---
    public void incrementarCantidad() {
        this.cantidad++;
    }

    public void decrementarCantidad() {
        this.cantidad--;
    }

    public double getSubtotal() {
        return this.producto.getPrecio() * this.cantidad;
    }
}
