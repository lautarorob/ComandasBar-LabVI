package database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "mesa")
public class MesaEntity {
    @PrimaryKey(autoGenerate = true)
    private int idMesa;

    private int numero;
    public enum EstadoMesa {
        ASIGNADA,
        LIBRE,
        OCUPADA
    }
    private EstadoMesa estado;
    private long idCamarero; // quién la atiende

    public MesaEntity(int numero, EstadoMesa estado, long idCamarero) {
        this.numero = numero;
        this.estado = estado;
        this.idCamarero = idCamarero;
    }

    public int getIdMesa() {
        return idMesa;
    }

    public void setIdMesa(int idMesa) {
        this.idMesa = idMesa;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public EstadoMesa getEstado() {
        return estado;
    }

    public void setEstado(EstadoMesa estado) {
        this.estado = estado;
    }

    public long getIdCamarero() {
        return idCamarero;
    }

    public void setIdCamarero(long idCamarero) {
        this.idCamarero = idCamarero;
    }

    @Override
    public String toString() {
        return "Mesa{" +
                "idMesa=" + idMesa +
                ", numero=" + numero +
                ", estado=" + estado +
                ", idCamarero=" + idCamarero +
                '}';
    }
    public boolean isOcupada() {
        return estado == EstadoMesa.OCUPADA;
    }

    public void setOcupada(boolean ocupada) {
        this.estado = ocupada ? EstadoMesa.OCUPADA : EstadoMesa.LIBRE;
    }




}
