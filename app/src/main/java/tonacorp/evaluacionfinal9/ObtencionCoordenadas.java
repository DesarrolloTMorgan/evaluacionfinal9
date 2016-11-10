package tonacorp.evaluacionfinal9;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by INTEKEL07 on 08/11/2016.
 */

public class ObtencionCoordenadas extends Fragment {

    TextView longitud, latitud;
    VariablesGlobales variablesGlobales = new VariablesGlobales();

    public ObtencionCoordenadas(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.localizacion_coordenadas, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        latitud = (TextView) view.findViewById(R.id.valor_latitud);
        longitud = (TextView) getView().findViewById(R.id.valor_longitud);
    }

    public void updateFragment(){

        if(isAdded()){
        latitud.setText(getResources().getString(R.string.latitud_descripcion) + variablesGlobales.getPasarLatitud());
        longitud.setText(variablesGlobales.getPasarLongitud());
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }
}
