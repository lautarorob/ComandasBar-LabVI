package viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import at.favre.lib.crypto.bcrypt.BCrypt;
import dao.CamareroDao;
import database.AppDataBase;
import database.CamareroEntity;

public class LoginViewModel extends AndroidViewModel {

    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<CamareroEntity> loginExitoso = new MutableLiveData<>();

    public LoginViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<CamareroEntity> getLoginExitoso() {
        return loginExitoso;
    }

    public void iniciarSesion(String email, String contrasena) {
        if (email.isEmpty() || contrasena.isEmpty()) {
            error.setValue("Por favor, introduce el email y la contraseña");
            return;
        }
        // Obtener DAO
        CamareroDao camareroDao = AppDataBase.getInstance(getApplication()).camareroDao();

        // Operación de BD en hilo de fondo
        new Thread(() -> {
            CamareroEntity camarero = camareroDao.findByEmail(email);

            if (camarero == null) {
                // email no encontrado
                error.postValue("Email no registrado");
            } else {
                // email encontrado verifica la contraseña
                BCrypt.Result resultado = BCrypt.verifyer().verify(contrasena.toCharArray(), camarero.getContrasena());

                if (resultado.verified) {
                    // camarero encontrado notifica a la Activity con los datos del camarero
                    loginExitoso.postValue(camarero);
                } else {
                    // Contraseña incorrecta
                    error.postValue("Contraseña incorrecta");
                }
            }
        }).start();
    }


}
