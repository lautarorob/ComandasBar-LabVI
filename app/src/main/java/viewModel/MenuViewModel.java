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
// ENTRADAS
        // URL estable de Wikimedia Commons para Empanadas
        listaProductos.add(new Producto("Empanada", 150.0, "Entradas",
                "https://media.istockphoto.com/id/1158987157/photo/a-closeup-of-argentinian-empanadas-with-sauces-and-wine-on-a-dark-rustic-wooden-background.jpg?s=1024x1024&w=is&k=20&c=Hr1SeX4X5mUong6ymGta6XAYk6Efy0kFThKTlCTPLsk="));

        // URL estable de Wikimedia Commons para Papas Fritas
        listaProductos.add(new Producto("Papas Fritas", 700.0, "Entradas",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/8/83/French_Fries.JPG/640px-French_Fries.JPG"));

        // PRINCIPALES

        listaProductos.add(new Producto("Milanesa con Fritas", 1500.0, "Principales",
                "https://media.istockphoto.com/id/1393883161/photo/german-pork-schnitzel-with-hand-made-spaetzle.jpg?s=2048x2048&w=is&k=20&c=A4iHG6sJqPIRQVl4o8aCg5-CDiPMDiEXOu0cKsJFUn0=")); // URL inventada de ejemplo, busca una real

        listaProductos.add(new Producto("Hamburguesas", 1500.0, "Principales",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/4/47/Hamburger_%28black_bg%29.jpg/800px-Hamburger_%28black_bg%29.jpg")); //

        // BEBIDAS

        listaProductos.add(new Producto("Coca Cola", 200.0, "Bebidas",
                "https://cdn.pixabay.com/photo/2017/02/25/23/12/coca-cola-2099000_1280.jpg"));

        listaProductos.add(new Producto("Pepsi", 200.0, "Bebidas",
                "https://cdn.pixabay.com/photo/2020/05/10/05/14/pepsi-5152332_1280.jpg"));

        return listaProductos;
    }




}
