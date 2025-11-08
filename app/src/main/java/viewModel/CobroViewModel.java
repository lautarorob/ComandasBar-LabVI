package viewModel;

import android.app.Application;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.comandasbar.PedidoItem;

import java.util.List;
import java.util.Locale;
import dao.PedidoDao;
import database.AppDataBase;
import database.PedidoEntity;

public class CobroViewModel extends AndroidViewModel {

    private final MutableLiveData<Double> totalAPagar = new MutableLiveData<>();
    private final MutableLiveData<String> resultadoDivision = new MutableLiveData<>();
    private final MutableLiveData<Boolean> cobroFinalizado = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public CobroViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Double> getTotalAPagar() {
        return totalAPagar;
    }

    public LiveData<String> getResultadoDivision() {
        return resultadoDivision;
    }

    public LiveData<Boolean> getCobroFinalizado() {
        return cobroFinalizado;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void calcularTotal(List<PedidoItem> comanda) {
        double total = 0;
        for (PedidoItem item : comanda) {
            total += item.getSubtotal();
        }
        totalAPagar.setValue(total);
    }

    public void dividirCuenta(String numPersonasStr) {
        if (TextUtils.isEmpty(numPersonasStr)) {
            errorMessage.setValue("Ingrese un número de personas");
            return;
        }

        int numPersonas;
        try {
            numPersonas = Integer.parseInt(numPersonasStr);
        } catch (NumberFormatException e) {
            errorMessage.setValue("Ingrese un número válido");
            return;
        }

        if (numPersonas <= 0) {
            errorMessage.setValue("El número debe ser mayor a cero");
            return;
        }

        if (totalAPagar.getValue() != null) {
            double totalPorPersona = totalAPagar.getValue() / numPersonas;
            resultadoDivision.setValue(String.format(Locale.getDefault(), "Cada uno paga: $%.2f", totalPorPersona));
        }
    }

    public void finalizarCobro(int numeroMesa, List<PedidoItem> comandaParaCobrar, double totalAPagar) {
        StringBuilder detalle = new StringBuilder();
        for (int i = 0; i < comandaParaCobrar.size(); i++) {
            PedidoItem item = comandaParaCobrar.get(i);
            detalle.append(item.getCantidad()).append("x ").append(item.getProducto().getNombre());
            if (i < comandaParaCobrar.size() - 1) {
                detalle.append(", ");
            }
        }

        PedidoEntity pedido = new PedidoEntity(numeroMesa, detalle.toString(), totalAPagar, 60);

        new Thread(() -> {
            PedidoDao pedidoDao = AppDataBase.getInstance(getApplication()).pedidoDao();
            pedidoDao.insert(pedido);
            cobroFinalizado.postValue(true);
        }).start();
    }
}
