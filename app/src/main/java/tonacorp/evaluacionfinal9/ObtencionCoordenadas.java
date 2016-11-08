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

    TextView longitud, latitud, as;

    public ObtencionCoordenadas(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //View view = inflater.inflate(R.layout.localizacion_coordenadas, container, false);
        //latitud = (TextView) view.findViewById(R.id.valor_latitud);
        //longitud = (TextView) getView().findViewById(R.id.valor_longitud);

        return inflater.inflate(R.layout.localizacion_coordenadas, container, false);
    }

}
