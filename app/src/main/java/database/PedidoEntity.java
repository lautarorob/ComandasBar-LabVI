package database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "pedido")
public class PedidoEntity {
    @PrimaryKey(autoGenerate = true)
    private int idPedido;

    private int numeroMesa;
    private String detalle; // Ej: "2x Hamburguesa, 1x Cerveza"
    private double total;
    private long tiempoInicio; // System.currentTimeMillis()
    private int segundosPreparacion; // 60 segundos = 1 minuto

    public PedidoEntity(int numeroMesa, String detalle, double total, int segundosPreparacion) {
        this.numeroMesa = numeroMesa;
        this.detalle = detalle;
        this.total = total;
        this.tiempoInicio = System.currentTimeMillis();
        this.segundosPreparacion = segundosPreparacion;
    }

    // Calcular segundos restantes
    public int getSegundosRestantes() {
        long transcurrido = (System.currentTimeMillis() - tiempoInicio) / 1000;
        int restante = segundosPreparacion - (int)transcurrido;
        return Math.max(0, restante);
    }

    public boolean estaListo() {
        return getSegundosRestantes() == 0;
    }

    // Getters y Setters
    public int getIdPedido() { return idPedido; }
    public void setIdPedido(int idPedido) { this.idPedido = idPedido; }

    public int getNumeroMesa() { return numeroMesa; }
    public void setNumeroMesa(int numeroMesa) { this.numeroMesa = numeroMesa; }

    public String getDetalle() { return detalle; }
    public void setDetalle(String detalle) { this.detalle = detalle; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public long getTiempoInicio() { return tiempoInicio; }
    public void setTiempoInicio(long tiempoInicio) { this.tiempoInicio = tiempoInicio; }

    public int getSegundosPreparacion() { return segundosPreparacion; }
    public void setSegundosPreparacion(int segundosPreparacion) { this.segundosPreparacion = segundosPreparacion; }
}