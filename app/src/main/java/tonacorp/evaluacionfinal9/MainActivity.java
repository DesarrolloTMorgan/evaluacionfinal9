package tonacorp.evaluacionfinal9;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    protected static final String TAG = "Actualizar_Ubicacion";

    public static final long TIEMPO_ACTUALIZACION = 15000; // 10 segundos.
    public static final long INTERVALO_ACTUALIZACION = TIEMPO_ACTUALIZACION / 2;

    protected final static String CLAVE_ACTUALIZACION = "CA";
    protected final static String CLAVE_UBICACION = "CU";
    protected final static String ULTIMA_ACTUALIZACION = "UA";

    protected GoogleApiClient gac;
    protected LocationRequest lr;
    protected Location location;


    TextView longitud, latitud, tiempo, ubicacionR;
    Button iniciar, parar;

    protected Boolean ubicacion;
    protected String tiempo_actualizacion;

    String guardarUbicacion1, guardarUbicacion2;
    String regresar, error;
    VariablesGlobales variabesGlobales = new VariablesGlobales();
    ObtencionCoordenadas fragmentCoordenadas = new ObtencionCoordenadas();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iniciar = (Button) findViewById(R.id.iniciar);
        parar = (Button) findViewById(R.id.parar);
        latitud = (TextView) findViewById(R.id.latitudR);
        longitud = (TextView) findViewById(R.id.longitudR);
        //tiempo = (TextView) findViewById(R.id.tiempo);
        ubicacionR = (TextView) findViewById(R.id.ubiR);

        ubicacion = false;
        tiempo_actualizacion = "";

        updateValuesFromBundle(savedInstanceState);

        buildGoogleApiClient();

    }

    /*
    *
    *
    *
    *
    *
    *
    * Metodos para obtener las coordenadas
    *
    *
    *
    *
    *
    *
    *
     */

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        Log.i(TAG, "Actualizando valores.");
        if (savedInstanceState != null) {
            if (savedInstanceState.keySet().contains(CLAVE_ACTUALIZACION)) {
                ubicacion = savedInstanceState.getBoolean(
                        CLAVE_ACTUALIZACION);
                setButtonEnabledState();
            }
            if (savedInstanceState.keySet().contains(CLAVE_UBICACION)) {
                location = savedInstanceState.getParcelable(CLAVE_UBICACION);
            }
            if (savedInstanceState.keySet().contains(ULTIMA_ACTUALIZACION)) {
                tiempo_actualizacion = savedInstanceState.getString(ULTIMA_ACTUALIZACION);
            }
            updateUI();
        }

    }

    protected synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Creando GoogleApiClient");
        gac = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        createLocationRequest();
    }

    protected void createLocationRequest() {
        lr = new LocationRequest();
        lr.setInterval(TIEMPO_ACTUALIZACION);
        lr.setFastestInterval(INTERVALO_ACTUALIZACION);
        lr.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public void startUpdatesButtonHandler(View view) {
        if (!ubicacion) {
            ubicacion = true;
            setButtonEnabledState();
            startLocationUpdates();
        }
    }

    public void stopUpdatesButtonHandler(View view) {
        if (ubicacion) {
            ubicacion = false;
            setButtonEnabledState();
            stopLocationUpdates();
        }
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(gac, lr, this);
    }


    private void setButtonEnabledState() {
        if (ubicacion) {
            iniciar.setEnabled(false);
            parar.setEnabled(true);
        } else {
            iniciar.setEnabled(true);
            parar.setEnabled(false);
        }
    }

    private void updateUI() {
        latitud.setText("La latitud es: " + String.valueOf(location.getLatitude()));
        longitud.setText("La longitud es: " + String.valueOf(location.getLongitude()));
        guardarUbicacion1 = String.valueOf(location.getLatitude());
        guardarUbicacion2 = String.valueOf(location.getLongitude());
        ubicacionR.setText(buscar_direccion(guardarUbicacion1, guardarUbicacion2));

        variabesGlobales.setPasarLatitud(guardarUbicacion1);
        variabesGlobales.setPasarLongitud(guardarUbicacion2);
        fragmentCoordenadas.updateFragment();

        //tiempo.setText("Útima actualización: " + tiempo_actualizacion);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(gac, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        gac.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (gac.isConnected() && ubicacion) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        stopLocationUpdates();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (gac.isConnected()) {
            gac.disconnect();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.i(TAG, "Conexión caída");

    }

    @Override
    public void onLocationChanged(Location location) {

        location = location;
        tiempo_actualizacion = DateFormat.getTimeInstance().format(new Date());
        updateUI();
        Toast.makeText(this, "Localización Actualizada", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onConnected(Bundle bundle) {

        Log.i(TAG, "Conectado GoogleApiClient");

        if (location == null) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            location = LocationServices.FusedLocationApi.getLastLocation(gac);
            tiempo_actualizacion = DateFormat.getTimeInstance().format(new Date());
            updateUI();
        }

        if (ubicacion) {
            startLocationUpdates();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

        Log.i(TAG, "Conexión suspendida");
        gac.connect();

    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(CLAVE_ACTUALIZACION, ubicacion);
        savedInstanceState.putParcelable(CLAVE_UBICACION, location);
        savedInstanceState.putString(ULTIMA_ACTUALIZACION, tiempo_actualizacion);
        super.onSaveInstanceState(savedInstanceState);
    }

    /*
    *
    *
    *
    *
    *
    *
    *
    *
    *
    *
    *
    * Metodo para obtener ubicación
    *
    *
    *
    *
    *
    *
    * */


    public String buscar_direccion(String lat, String lon) {
        Geocoder geocoder = new Geocoder(this, Locale.ENGLISH);
        Double lati = Double.parseDouble(lat);
        Double longi = Double.parseDouble(lon);

        try {
            List<Address> addresses = geocoder.getFromLocation(lati, longi, 2);
            if (addresses != null) {
                Address address = addresses.get(0);
                StringBuilder sb = new StringBuilder();

                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    sb.append(address.getAddressLine(i)).append("\n");
                }

                regresar = ("La dirección es: " + sb.toString());
                return regresar;

            } else {
                regresar = "La dirección no ha sido encontrada";
                return regresar;
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Servicio no disponible", Toast.LENGTH_SHORT).show();
            error = "Error weon";
            return error;
        }

    }

    /*
    *
    *
    *
    *
    *
    *
    *
    *
    *
    *
    *
    * Enviar información
    *
    *
    *
    *
    *
    *
    *
    *
    * */

    public String enviarCoordenadas(){

        String envio;
        guardarUbicacion1 = String.valueOf(location.getLatitude());
        guardarUbicacion2 = String.valueOf(location.getLongitude());

        envio = guardarUbicacion1 + ", " + guardarUbicacion2;

        return envio;

    }
}
