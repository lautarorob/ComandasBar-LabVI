package viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.comandasbar.PedidoItem;
import com.example.comandasbar.Producto;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PedidoViewModel extends ViewModel {

    // LiveData que contiene la lista actual de ítems en la comanda.
    private final MutableLiveData<List<PedidoItem>> comanda = new MutableLiveData<>();

    public PedidoViewModel() {

        comanda.setValue(new ArrayList<>());
    }

    // Getter público para que la Activity y los Fragments puedan observar los cambios.
    public LiveData<List<PedidoItem>> getComanda() {
        return comanda;
    }

    /**
     * Agrega un producto a la comanda actual.
     * Si el producto ya existe, incrementa su cantidad. Si no, lo añade como un nuevo ítem.
     * @param producto El producto a agregar.
     */
    public void agregarProducto(Producto producto) {
        // Obtenemos la lista actual del LiveData. Es importante crear una NUEVA lista
        // para notificar correctamente a los observadores.
        List<PedidoItem> listaActual = new ArrayList<>(comanda.getValue());

        boolean encontrado = false;
        for (PedidoItem item : listaActual) {
            if (item.getProducto().getNombre().equals(producto.getNombre())) {
                item.incrementarCantidad();
                encontrado = true;
                break;
            }
        }

        if (!encontrado) {
            listaActual.add(new PedidoItem(producto));
        }

        // Actualizamos el LiveData con la nueva lista modificada.
        // Esto notificará a todos los observadores (la Activity, ComandaFragment, etc.).
        comanda.setValue(listaActual);
    }

    /**
     * Disminuye la cantidad de un producto en la comanda.
     * Si la cantidad llega a 0 después de disminuir, el producto se elimina de la comanda.
     * @param producto El producto a restar.
     */
    public void restarProducto(Producto producto) {
        List<PedidoItem> listaActual = new ArrayList<>(comanda.getValue());
        // Usamos Iterator para poder eliminar elementos mientras iteramos.
        Iterator<PedidoItem> iterator = listaActual.iterator();
        while (iterator.hasNext()) {
            PedidoItem item = iterator.next();
            if (item.getProducto().getNombre().equals(producto.getNombre())) {
                item.decrementarCantidad();
                if (item.getCantidad() <= 0) {
                    iterator.remove(); // Elimina el PedidoItem si su cantidad es 0 o menos.
                }
                break; // Se encontró el producto, salimos del bucle.
            }
        }
        comanda.setValue(listaActual);
    }

    /**
     * Elimina completamente un producto de la comanda, sin importar su cantidad.
     * @param producto El producto a eliminar.
     */
    public void eliminarProducto(Producto producto) {
        List<PedidoItem> listaActual = new ArrayList<>(comanda.getValue());
        // Usamos Iterator para poder eliminar elementos mientras iteramos.
        Iterator<PedidoItem> iterator = listaActual.iterator();
        while (iterator.hasNext()) {
            PedidoItem item = iterator.next();
            if (item.getProducto().getNombre().equals(producto.getNombre())) {
                iterator.remove(); // Elimina el PedidoItem.
                break; // Se encontró el producto, salimos del bucle.
            }
        }
        comanda.setValue(listaActual);
    }
}
