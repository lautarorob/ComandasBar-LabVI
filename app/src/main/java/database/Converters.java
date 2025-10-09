package database;

import androidx.room.TypeConverter;

public class Converters {
    @TypeConverter
    public static MesaEntity.EstadoMesa fromString(String value) {
        return value == null ? null : MesaEntity.EstadoMesa.valueOf(value);
    }

    @TypeConverter
    public static String estadoMesaToString(MesaEntity.EstadoMesa estado) {
        return estado == null ? null : estado.name();
    }
}

