package tonacorp.evaluacionfinal9;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by INTEKEL07 on 08/11/2016.
 */

public class ObtecionUbicacion extends Fragment {

    TextView ubicacion;

    public ObtecionUbicacion (){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //View view = inflater.inflate(R.layout.localizacion_ubicacion, container, false);
        //ubicacion = (TextView) view.findViewById(R.id.valor_ubicacion);

        return inflater.inflate(R.layout.localizacion_ubicacion, container, false);
    }
}
