package dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import database.MesaEntity;

@Dao
public interface MesaDao {

    // Insertar una mesa
    @Insert
    long insert(MesaEntity mesa);

    // Actualizar una mesa existente
    @Update
    int update(MesaEntity mesa);

    // Borrar una mesa
    @Delete
    int delete(MesaEntity mesa);

    // Obtener todas las mesas
    @Query("SELECT * FROM mesa ORDER BY numero ASC")
    LiveData<List<MesaEntity>> getAllMesas();

    // Obtener mesas libres
    @Query("SELECT * FROM mesa WHERE estado = :estado ORDER BY numero ASC")
    LiveData<List<MesaEntity>> getMesasByEstado(MesaEntity.EstadoMesa estado);

    // Obtener una mesa por id
    @Query("SELECT * FROM mesa WHERE idMesa = :id")
    MesaEntity getMesaById(int id);
    @Query("SELECT * FROM mesa ORDER BY numero ASC")
    List<MesaEntity> getAllMesasSync();

}
