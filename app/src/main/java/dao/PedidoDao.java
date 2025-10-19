package dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;
import database.PedidoEntity;

@Dao
public interface PedidoDao {

    @Insert
    long insert(PedidoEntity pedido);

    @Query("SELECT * FROM pedido ORDER BY tiempoInicio DESC")
    LiveData<List<PedidoEntity>> getAllPedidos();

    @Query("DELETE FROM pedido WHERE idPedido = :id")
    void eliminarPedido(int id);
}