package dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface CamareroDao {
    @Insert
    long insert(CamareroDao camarero);

    @Update
    int update(CamareroDao camarero);

    @Delete
    void delete(CamareroDao camarero);

    @Query("SELECT * FROM camarero")
    LiveData<List<CamareroDao>> getAllCamareros();

}
