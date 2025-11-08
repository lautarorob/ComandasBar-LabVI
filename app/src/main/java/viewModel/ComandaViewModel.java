package viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.comandasbar.PedidoItem;

import java.util.ArrayList;
import java.util.List;

public class ComandaViewModel extends AndroidViewModel {

    private final MutableLiveData<List<PedidoItem>> comandaItems = new MutableLiveData<>();

    private final MutableLiveData<Double> totalComanda = new MutableLiveData<>();

    public ComandaViewModel(@NonNull Application application) {
        super(application);
        comandaItems.setValue(new ArrayList<>());
        totalComanda.setValue(0.0);
    }

    public LiveData<List<PedidoItem>> getComandaItems() {
        return comandaItems;
    }

    public LiveData<Double> gettotalComanda() {
        return totalComanda;
    }

    public void actualizaComanda(List<PedidoItem> nuevaComanda) {
        comandaItems.setValue(nuevaComanda); //actualiza el livedata de items

        //calculo del total
        double total = 0;
        if (nuevaComanda!=null){
            for(PedidoItem item : nuevaComanda){
                total += item.getSubtotal();
            }
        }
        totalComanda.setValue(total); //actualiza el livedata de total
    }

}
