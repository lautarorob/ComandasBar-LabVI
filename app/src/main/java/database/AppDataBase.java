package database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import dao.CamareroDao;
import dao.MesaDao;

import database.CamareroEntity;
import database.MesaEntity;


// Lista  entidades
@Database(entities = {MesaEntity.class, CamareroEntity.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class AppDataBase extends RoomDatabase {


    public abstract MesaDao mesaDao();
    public abstract CamareroDao camareroDao();

    // El resto del código del Singleton se queda igual...
    private static volatile AppDataBase INSTANCE;

    public static AppDataBase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDataBase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDataBase.class, "comandas_bar_db")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
