package tonacorp.evaluacionfinal9;

/**
 * Created by Tona on 08/11/2016.
 */

import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.util.Date;

public class ObtenerCoordenadas extends ActionBarActivity implements ConnectionCallbacks,
        OnConnectionFailedListener, LocationListener {

    protected static final String TAG = "Actualizar_Ubicacion";

    public static final long TIEMPO_ACTUALIZACION = 5000; // 10 segundos.
    public static final long INTERVALO_ACTUALIZACION = TIEMPO_ACTUALIZACION / 2;

    protected final static String CLAVE_ACTUALIZACION = "CA";
    protected final static String CLAVE_UBICACION = "CU";
    protected final static String ULTIMA_ACTUALIZACION = "UA";

    protected GoogleApiClient gac;
    protected LocationRequest lr;
    protected Location location;


    TextView longitud, latitud, tiempo;
    Button iniciar, parar;

    protected Boolean ubicacion;
    protected String tiempo_actualizacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //iniciar = (Button) findViewById(R.id.iniciar);
        //parar = (Button) findViewById(R.id.parar);
        //latitud = (TextView) findViewById(R.id.latitudR);
        //longitud = (TextView) findViewById(R.id.longitudR);
        //tiempo = (TextView) findViewById(R.id.tiempo);

        ubicacion = false;
        tiempo_actualizacion = "";

        updateValuesFromBundle(savedInstanceState);

        buildGoogleApiClient();

    }

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
        tiempo.setText("Útima actualización: " + tiempo_actualizacion);
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

        if(ubicacion) {
            startLocationUpdates();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

        Log.i(TAG, "Conexión suspendida");
        gac.connect();

    }

    public void onSaveInstanceState (Bundle savedInstanceState) {
        savedInstanceState.putBoolean(CLAVE_ACTUALIZACION, ubicacion);
        savedInstanceState.putParcelable(CLAVE_UBICACION, location);
        savedInstanceState.putString(ULTIMA_ACTUALIZACION, tiempo_actualizacion);
        super.onSaveInstanceState(savedInstanceState);
    }
}
