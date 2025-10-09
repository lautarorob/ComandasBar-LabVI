package database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "camarero")
public class CamareroEntity {
    @PrimaryKey(autoGenerate = true)
    private long idCamarero;
    private String gmail;
    private String contrasena; // NOTA: ¡Nunca almacenar en texto plano en una app real!
    private String nombreCompleto;
    private String contacto;
    private String fotoPerfilURL;

    private boolean sesionIniciada;

    // Constructor para Creación de Cuenta (Registro)
    public CamareroEntity(String gmail, String contrasena, String nombreCompleto, String contacto) {
        this.gmail = gmail;
        this.contrasena = contrasena;
        this.nombreCompleto = nombreCompleto;
        this.contacto = contacto;
        this.fotoPerfilURL = "default.jpg";
        this.sesionIniciada = false;
    }

    // --- Métodos de Sesión (Lógica de Negocio) ---

    // Iniciar Sesión
    public boolean iniciarSesion(String user, String pass) {
        if (this.gmail.equals(user) && this.contrasena.equals(pass)) {
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

    public String getFotoPerfilURL() {
        return fotoPerfilURL;
    }

    public void setNombreCompleto(String nuevoNombre) {
        this.nombreCompleto = nuevoNombre;
    }

    public void setContacto(String nuevoContacto) {
        this.contacto = nuevoContacto;
    }

    public void setFotoPerfilURL(String nuevaFotoURL) {
        this.fotoPerfilURL = nuevaFotoURL;
    }
}
