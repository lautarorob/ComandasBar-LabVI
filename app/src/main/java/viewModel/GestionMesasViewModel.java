package viewModel;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import dao.CamareroDao;
import dao.MesaDao;
import dao.PedidoDao;
import database.AppDataBase;
import database.CamareroEntity;
import database.MesaEntity;
import database.PedidoEntity;

public class GestionMesasViewModel extends AndroidViewModel {

    private final MesaDao mesaDao;
    private final PedidoDao pedidoDao;
    private final CamareroDao camareroDao;

    // LiveData para la UI
    private final LiveData<List<MesaEntity>> mesas;
    private final LiveData<List<PedidoEntity>> pedidos;
    private final MutableLiveData<CamareroEntity> camareroActual = new MutableLiveData<>();

    // Este LiveData actuará como un "tick" de reloj cada segundo.
    private final MutableLiveData<Void> cronometroTick = new MutableLiveData<>();

    //para el temporizador
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable actualizadorCronometros;

    public GestionMesasViewModel(@NonNull Application application) {
        super(application);
        AppDataBase db = AppDataBase.getInstance(application);
        mesaDao = db.mesaDao();
        pedidoDao = db.pedidoDao();
        camareroDao = db.camareroDao();

        // Los LiveData de Room se actualizan solos cuando la tabla cambia.
        mesas = mesaDao.getAllMesas();
        pedidos = pedidoDao.getAllPedidos();

        // Lógica para el Handler
        actualizadorCronometros = new Runnable() {
            @Override
            public void run() {
                // Emite un evento "tick" cada segundo. La Activity lo observará.
                cronometroTick.setValue(null);
                // Se programa a sí mismo para ejecutarse de nuevo en 1 segundo.
                handler.postDelayed(this, 1000);
            }
        };
    }

    // "Getters" públicos para que la Activity observe los datos
    public LiveData<List<MesaEntity>> getMesas() {
        return mesas;
    }

    public LiveData<List<PedidoEntity>> getPedidos() {
        return pedidos;
    }

    public LiveData<CamareroEntity> getCamareroActual() {
        return camareroActual;
    }

    /**
     * LiveData que emite un evento nulo cada segundo para notificar a la UI
     * que debe refrescar los cronómetros.
     */
    public LiveData<Void> getCronometroTick() {
        return cronometroTick;
    }

    // Metodo para limpiar el handler cuando el ViewModel se destruye
    @Override
    protected void onCleared() {
        super.onCleared();
        handler.removeCallbacks(actualizadorCronometros);
    }

    // Metodo que la Activity llamará una sola vez en onCreate
    public void iniciar(long idCamarero) {
        // Carga los datos del camarero
        new Thread(() -> {
            CamareroEntity camarero = camareroDao.findById(idCamarero);
            camareroActual.postValue(camarero);
        }).start();

        // Inicializa las mesas si es la primera vez que se abre la app
        new Thread(() -> {
            if (mesaDao.getAllMesasSync().isEmpty()) {
                for (int i = 1; i <= 6; i++) {
                    mesaDao.insert(new MesaEntity(i, MesaEntity.EstadoMesa.LIBRE, -1));
                }
            }
        }).start();

        // Inicia el actualizador de cronómetros
        handler.post(actualizadorCronometros);
    }

    // Acción: Asignarse una mesa
    public void asignarMesa(MesaEntity mesa, long idCamarero) {
        mesa.setIdCamarero(idCamarero);
        mesa.setEstado(MesaEntity.EstadoMesa.ASIGNADA);
        new Thread(() -> mesaDao.update(mesa)).start();
    }

    // Acción: Desasignarse una mesa
    public void desasignarMesa(MesaEntity mesa) {
        mesa.setIdCamarero(-1);
        mesa.setEstado(MesaEntity.EstadoMesa.LIBRE);
        new Thread(() -> mesaDao.update(mesa)).start();
    }

    // Acción: Entregar un pedido
    public void entregarPedido(PedidoEntity pedido) {
        new Thread(() -> pedidoDao.eliminarPedido(pedido.getIdPedido())).start();
    }
}
