package viewModel;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import at.favre.lib.crypto.bcrypt.BCrypt;
import database.AppDataBase;
import database.CamareroEntity;

public class CrearCuentaViewModel extends AndroidViewModel {

    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> cuentaCreada = new MutableLiveData<>();

    public CrearCuentaViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Boolean> getCuentaCreada() {
        return cuentaCreada;
    }

    private byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    public void guardarDatosDelCamarero(String nombre, String email, String contacto, String contrasena, Uri imagenUri) {
        // Realiza validaciones
        if (nombre.isEmpty() || email.isEmpty() || contacto.isEmpty() || contrasena.isEmpty()) {
            error.setValue("Completa los campos obligatorios (*)");
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            error.setValue("Por favor, introduce un formato de email válido");
            return;
        }
        if (contacto.length() < 9 || contacto.length() > 11) {
            error.setValue("El contacto debe tener entre 9 y 11 caracteres");
            return;
        }

        // Procesa la imagen a byte[]
        byte[] imagenEnBytes = null;
        if (imagenUri != null) {
            try {
                InputStream inputStream = getApplication().getContentResolver().openInputStream(imagenUri);
                imagenEnBytes = getBytes(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
                error.setValue("Error al procesar la imagen");
                return;
            }
        }

        // Hashea la contraseña
        String hashConstrasena = BCrypt.withDefaults().hashToString(12, contrasena.toCharArray());
        CamareroEntity camarero = new CamareroEntity(email, hashConstrasena, nombre, contacto, imagenEnBytes);

        // 4. Guardado en BD
        new Thread(() -> {
            AppDataBase.getInstance(getApplication()).camareroDao().insert(camarero);
            // Avisa a la Activity que todo salio bien
            cuentaCreada.postValue(true);
        }).start();
    }
}
