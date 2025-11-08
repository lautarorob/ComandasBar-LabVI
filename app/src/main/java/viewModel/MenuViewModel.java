package viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.comandasbar.Producto;
import java.util.ArrayList;
import java.util.List;

public class MenuViewModel extends ViewModel {

    // LiveData que contendrá la lista de productos.
    private final MutableLiveData<List<Producto>> menuItems = new MutableLiveData<>();

    // Getter público para que el Fragment pueda observar los datos.
    public LiveData<List<Producto>> getMenuItems() {
        return menuItems;
    }

    /**
     * Carga la lista de productos y la postea en el LiveData.
     * La Activity/Fragment llamará a este metodo para iniciar la carga.
     */
    public void cargarMenu() {
        // Llama al metodo privado para generar los datos.
        List<Producto> productos = cargarProductosDeEjemplo();
        // Actualiza el LiveData con la lista de productos.
        menuItems.setValue(productos);
    }

    /**
     * Crea y devuelve una lista estática de productos de ejemplo.
     * Esta es la "fuente de datos" para este ViewModel.
     * @return Una lista de productos.
     */
    private List<Producto> cargarProductosDeEjemplo() {
        List<Producto> listaProductos = new ArrayList<>();
        listaProductos.add(new Producto("Empanada", 150.0, "Entradas"));
        listaProductos.add(new Producto("Papas Fritas", 700.0, "Entradas"));
        listaProductos.add(new Producto("Milanesa con Fritas", 1500.0, "Principales"));
        listaProductos.add(new Producto("Bife de Chorizo", 2200.0, "Principales"));
        listaProductos.add(new Producto("Coca Cola", 200.0, "Bebidas"));
        listaProductos.add(new Producto("Pepsi", 300.0, "Bebidas"));
        return listaProductos;
    }
}
