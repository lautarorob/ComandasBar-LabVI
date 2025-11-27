package viewModel;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.comandasbar.api.ApiService;
import com.example.comandasbar.api.RetrofitCliente;
import com.example.comandasbar.api.model.RegisterRequest;
import com.example.comandasbar.api.model.RegisterResponse;

import database.AppDataBase;
import database.CamareroEntity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import okhttp3.ResponseBody;
import com.google.gson.Gson; // NUEVO: Importar Gson
import com.google.gson.JsonSyntaxException; // NUEVO: Importar JsonSyntaxException

public class CrearCuentaViewModel extends AndroidViewModel {

    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> cuentaCreada = new MutableLiveData<>();

    public CrearCuentaViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<String> getError() { return error; }
    public LiveData<Boolean> getCuentaCreada() { return cuentaCreada; }

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

    public void guardarDatosDelCamarero(String nombreCompleto, String email, String contacto, String contrasena, Uri imagenUri) {
        // --- 1. VALIDACIONES ---
        if (nombreCompleto.isEmpty() || email.isEmpty() || contacto.isEmpty() || contrasena.isEmpty()) {
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

        // 1.1. VALIDACIÓN DE CONTRASEÑA (¡NUEVO!)
        if (contrasena.length() < 8) {
            error.setValue("La contraseña debe tener al menos 8 caracteres");
            return;
        }
        // Expresión regular para al menos un carácter alfabético, un dígito y un carácter especial
        Pattern passwordPattern = Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[^a-zA-Z0-9]).+$");
        Matcher passwordMatcher = passwordPattern.matcher(contrasena);
        if (!passwordMatcher.matches()) {
            error.setValue("La contraseña debe tener al menos una letra, un número y un Carác.Esp");
            return;
        }

        // Procesamiento de imagen (Se mantiene la lógica aunque la API register no la pida aun)
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

        // --- NUEVA LÓGICA CON API (RETROFIT) ---

        // 1. Separar nombre para el DTO
        String[] partesNombre = nombreCompleto.trim().split(" ", 2);
        String firstName = partesNombre[0];
        String lastName = (partesNombre.length > 1) ? partesNombre[1] : ""; // Ahora puede ser vacío si solo hay un nombre

        // 1.2. VALIDACIÓN DE NOMBRES (¡NUEVO!)
        // firstName: al menos dos caracteres, comenzar con una mayúscula y solo tener caracteres alfabéticos.
        if (firstName.length() < 2 || !Character.isUpperCase(firstName.charAt(0)) || !firstName.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ ]+$")) {
            error.setValue("El nombre debe tener al menos 2 caracteres, comenzar con mayúscula y contener solo letras.");
            return;
        }

        // lastName: al menos un carácter, comenzar con una mayúscula y solo tener caracteres alfabéticos.
        // Si el usuario no proporcionó un apellido, podemos asignar un valor por defecto o requerirlo.
        // Si se permite que sea opcional, la validación para lastName solo se aplica si existe.
        if (!lastName.isEmpty()) {
            if (lastName.length() < 1 || !Character.isUpperCase(lastName.charAt(0)) || !lastName.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ ]+$")) {
                error.setValue("El apellido debe tener al menos 1 carácter, comenzar con mayúscula y contener solo letras.");
                return;
            }
        } else {
            // Para evitar el 500, vamos a dejarlo como "SinApellido" si está vacío,
            // pero esto puede depender de la lógica del servidor si acepta este valor.
            lastName = "SinApellido"; 
        }

        // Logs para depuración: ¿Qué se está enviando?
        Log.d("CrearCuentaViewModel", "Enviando registro con:");
        Log.d("CrearCuentaViewModel", "  FirstName: " + firstName);
        Log.d("CrearCuentaViewModel", "  LastName: " + lastName);
        Log.d("CrearCuentaViewModel", "  Email: " + email);
        Log.d("CrearCuentaViewModel", "  Password (length): " + contrasena.length() + " (not logged for security)");

        // 2. Crear Request
        RegisterRequest request = new RegisterRequest(
                firstName,
                lastName,
                email,
                contrasena // Se envía SIN hash (el servidor lo hace)
        );

        // 3. Llamar a la API
        ApiService apiService = RetrofitCliente.getClient().create(ApiService.class);
        Call<ResponseBody> call = apiService.registrarUsuario(request);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String responseBodyString = response.body().string();
                        String contentType = response.headers().get("Content-Type");
                        
                        // Intentar leer como JSON si el Content-Type lo indica
                        if (contentType != null && contentType.contains("application/json")) {
                            Gson gson = new Gson();
                            try {
                                RegisterResponse registerResponse = gson.fromJson(responseBodyString, RegisterResponse.class);
                                if (registerResponse != null && registerResponse.getMessage() != null && registerResponse.getMessage().contains("Usuario registrado exitosamente")) {
                                    cuentaCreada.postValue(true);
                                } else {
                                    error.postValue("Registro exitoso, pero JSON de respuesta inesperado.");
                                }
                            } catch (JsonSyntaxException e) {
                                // Falló al parsear como JSON, intentar como texto plano
                                Log.e("API_PARSE_ERROR", "Fallo al parsear JSON, intentando como texto plano.", e);
                                if (responseBodyString.contains("Usuario registrado exitosamente")) {
                                    cuentaCreada.postValue(true);
                                } else {
                                    error.postValue("Registro exitoso, pero respuesta de texto inesperada: " + responseBodyString);
                                }
                            }
                        } else {
                            // No es JSON (es texto plano), leer como texto
                            if (responseBodyString.contains("Usuario registrado exitosamente")) {
                                cuentaCreada.postValue(true);
                            } else {
                                error.postValue("Registro exitoso, pero respuesta de texto inesperada: " + responseBodyString);
                            }
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        error.postValue("Error al leer la respuesta exitosa.");
                    }
                } else {
                    // Si no es exitoso (400, 404, 500, etc.)
                    try {
                        String errorBody = response.errorBody().string();
                        Log.e("API_ERROR_REAL", "Código: " + response.code() + " Cuerpo: " + errorBody);
                        // Se lo mostramos al usuario (una parte)
                        if (response.code() == 400) {
                            if (errorBody.contains("Email en uso")) {
                                error.postValue("Error: El email ya está registrado.");
                            }
                            else if (errorBody.contains("invalid credentials")) { // ejemplo de otro error 400 que podría enviar el servidor
                                error.postValue("Error 400: Credenciales inválidas.");
                            } else {
                                error.postValue("Error 400: Datos de registro inválidos.");
                            }
                        } else if (response.code() == 500) {
                             error.postValue("Error 500: Fallo en la validación del servidor o problema interno.");
                        }
                        else {
                            error.postValue("Error del servidor: " + response.code() + ": " + errorBody);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        error.postValue("Error desconocido al procesar la respuesta: " + response.code());
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                error.postValue("Fallo de conexión: " + t.getMessage());
                Log.e("API_FAILURE", "Error en la llamada a la API: ", t);
            }
        });
    }
}