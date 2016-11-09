package tonacorp.evaluacionfinal9;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Tona on 08/11/2016.
 */

public class ObtenerUbicacion extends Activity{
    private TextView direcion;
    private EditText longitud, latitud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //direcion = (TextView) findViewById(R.id.direccion);
        //longitud = (EditText) findViewById(R.id.longitud);
        //latitud = (EditText) findViewById(R.id.latitud);
    }

    public void buscar_direccion(View view){
        Geocoder geocoder = new Geocoder(this, Locale.ENGLISH);
        Double lati = Double.parseDouble(latitud.getText().toString());
        Double longi = Double.parseDouble(longitud.getText().toString());

        try {
            List<Address> addresses = geocoder.getFromLocation(lati, longi, 2);
            if(addresses != null){
                Address address = addresses.get(0);
                StringBuilder sb = new StringBuilder();

                for(int i=0; i<address.getMaxAddressLineIndex(); i++){
                    sb.append(address.getAddressLine(i)).append("\n");
                }

                direcion.setText("La dirección es: "+sb.toString());

            } else{
                direcion.setText("La dirección no ha sido encontrada :(");
            }
        } catch (IOException e){
            e.printStackTrace();
            Toast.makeText(this, "Servicio no disponible", Toast.LENGTH_SHORT).show();
        }
    }
}
