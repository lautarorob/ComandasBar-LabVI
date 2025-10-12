package dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

import database.CamareroEntity;

@Dao
public interface CamareroDao {
    @Insert
    long insert(CamareroEntity camarero);

    @Update
    int update(CamareroEntity camarero);

    @Delete
    void delete(CamareroEntity camarero);

    @Query("SELECT * FROM camarero WHERE email = :email LIMIT 1")
    CamareroEntity findByEmail(String email);

}
