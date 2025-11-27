package viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.comandasbar.api.ApiService;
import com.example.comandasbar.api.RetrofitCliente;
import com.example.comandasbar.api.model.LoginRequest;
import com.example.comandasbar.api.model.LoginResponse;
import dao.CamareroDao;
import database.AppDataBase;
import database.CamareroEntity;

// import at.favre.lib.crypto.bcrypt.BCrypt; // (Comentado: La verificación de hash la hace el servidor)

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginViewModel extends AndroidViewModel {

    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<CamareroEntity> loginExitoso = new MutableLiveData<>();

    public LoginViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<String> getError() { return error; }
    public LiveData<CamareroEntity> getLoginExitoso() { return loginExitoso; }

    public void iniciarSesion(String email, String contrasena) {
        if (email.isEmpty() || contrasena.isEmpty()) {
            error.setValue("Por favor, introduce el email y la contraseña");
            return;
        }

        /* -----------------------------------------------------------------------
           INICIO CÓDIGO ANTIGUO (LOGIN SOLO LOCAL) - COMENTADO
           -----------------------------------------------------------------------
        // Obtener DAO
        CamareroDao camareroDao = AppDataBase.getInstance(getApplication()).camareroDao();

        // Operación de BD en hilo de fondo
        new Thread(() -> {
            CamareroEntity camarero = camareroDao.findByEmail(email);

            if (camarero == null) {
                error.postValue("Email no registrado (Local)");
            } else {
                // email encontrado verifica la contraseña con BCrypt
                BCrypt.Result resultado = BCrypt.verifyer().verify(contrasena.toCharArray(), camarero.getContrasena());

                if (resultado.verified) {
                    loginExitoso.postValue(camarero);
                } else {
                    error.postValue("Contraseña incorrecta (Local)");
                }
            }
        }).start();
           -----------------------------------------------------------------------
           FIN CÓDIGO ANTIGUO
           ----------------------------------------------------------------------- */


        // --- NUEVA LÓGICA CON API (RETROFIT) ---

        LoginRequest request = new LoginRequest(email, contrasena);
        ApiService apiService = RetrofitCliente.getClient().create(ApiService.class);
        Call<LoginResponse> call = apiService.loginUsuario(request);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Login correcto en la nube
                    LoginResponse datosApi = response.body();
                    // Guardamos en local para futuras sesiones offline o persistencia
                    guardarYNotificar(datosApi, contrasena);
                } else {
                    if (response.code() == 401 || response.code() == 400) {
                        error.postValue("Email o contraseña incorrectos");
                    } else {
                        error.postValue("Error del servidor: " + response.code());
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                error.postValue("Error de conexión. Verifica tu internet.");
            }
        });
    }

    /**
     * Método auxiliar para guardar la respuesta de la API en la BD Local (Room)
     */
    private void guardarYNotificar(LoginResponse datosApi, String password) {
        new Thread(() -> {
            CamareroDao dao = AppDataBase.getInstance(getApplication()).camareroDao();
            CamareroEntity usuarioLocal = dao.findByEmail(datosApi.getEmail());

            // Unimos nombre y apellido porque nuestra Entity usa un solo campo
            String nombreCompleto = datosApi.getFirstName() + " " + datosApi.getLastName();

            CamareroEntity usuarioFinal;

            if (usuarioLocal != null) {
                // Actualizar existente
                usuarioLocal.setNombreCompleto(nombreCompleto);
                usuarioLocal.setContrasena(password);
                usuarioLocal.setSesionIniciada(true);
                dao.update(usuarioLocal);
                usuarioFinal = usuarioLocal;
            } else {
                // Crear nuevo
                usuarioFinal = new CamareroEntity(
                        datosApi.getEmail(),
                        password,
                        nombreCompleto,
                        "",
                        null
                );
                usuarioFinal.setSesionIniciada(true);
                long id = dao.insert(usuarioFinal);
                usuarioFinal.setIdCamarero(id);
            }

            loginExitoso.postValue(usuarioFinal);
        }).start();
    }
}