package database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "camarero")
public class CamareroEntity {
    @PrimaryKey(autoGenerate = true)
    private long idCamarero;
    private String email;
    private String contrasena; // NOTA: ¡Nunca almacenar en texto plano en una app real!
    private String nombreCompleto;
    private String contacto;

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB) //BLOB para almacenar imágenes(binarias)
    private byte[] fotoPerfil;

    private boolean sesionIniciada;

    // Constructor para Creación de Cuenta (Registro)
    public CamareroEntity(String email, String contrasena, String nombreCompleto, String contacto, byte[] fotoPerfil) {
        this.email = email;
        this.contrasena = contrasena;
        this.nombreCompleto = nombreCompleto;
        this.contacto = contacto;
        this.fotoPerfil = fotoPerfil;
        this.sesionIniciada = false;
    }

    // --- Métodos de Sesión (Lógica de Negocio) ---

    // Iniciar Sesión
    public boolean iniciarSesion(String user, String pass) {
        if (this.email.equals(user) && this.contrasena.equals(pass)) {
            this.sesionIniciada = true;
            return true;
        }
        return false;
    }

    // Cerrar Sesión
    public void cerrarSesion() {
        this.sesionIniciada = false;
    }

    public boolean isSesionIniciada() {
        return sesionIniciada;
    }

    // --- Métodos de Gestión de Perfil (Administrador de información personal) ---

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public String getContacto() {
        return contacto;
    }

    public byte[] getFotoPerfil() {
        return fotoPerfil;
    }

    public void setNombreCompleto(String nuevoNombre) {
        this.nombreCompleto = nuevoNombre;
    }

    public void setContacto(String nuevoContacto) {
        this.contacto = nuevoContacto;
    }

    public void setFotoPerfil(byte[] nuevaFotoURL) {
        this.fotoPerfil = nuevaFotoURL;
    }

    public long getIdCamarero() {
        return idCamarero;
    }

    public void setIdCamarero(long idCamarero) {
        this.idCamarero = idCamarero;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public void setSesionIniciada(boolean sesionIniciada) {
        this.sesionIniciada = sesionIniciada;
    }
}
