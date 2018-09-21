package com.example.ccong.MyTFM;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;

/**
 * Listado de usuarios cercanos a la posicion actual
 */
public class UsersMoment extends AppCompatActivity {

    private ListView momento; //Lista de usuarios
    private String user; //Usuario
    //public static ArrayAdapter<String> adapter;

    //Objetos para las coordenadas
    private LocationListener listener;
    private LocationManager locman;
    private Location loc;
    private double latitude = 360;
    private double longitude = 360;


    boolean act;

    private Button parada;//Boton para parar la actualización automática de la lista

    /**
     * Hilo encargado de actualizar la información de forma periodica
     */
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        public void run() {
             busca(  );
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_users_moment );


        this.user = SaveInfo.load( this ).getEmail(); //Se carga el email del usuario
        parada = (Button) findViewById( R.id.stop );
		momento = (ListView) findViewById( R.id.usersMom_list );

        /**
         * Funcionalidad del boton de parada
         */
        parada.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeCallbacks( runnable );
            }
        } );


        runnable.run();//Se lanza el hilo
    }

    /**
     * Actualiza la popsicion del usuario
     */
    private  void actualizarPosicion(){

        if (ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED) {


            try {
                locman = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
                act = locman.isProviderEnabled( LocationManager.GPS_PROVIDER );
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (act) {
                listener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {

                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                };

                locman.requestLocationUpdates( locman.GPS_PROVIDER, 10000, 10, listener );

                loc = locman.getLastKnownLocation( locman.GPS_PROVIDER );
                if (loc != null) {
                    this.latitude = loc.getLatitude();
                    this.longitude = loc.getLongitude();
                }
                else {
                    //Log.d ("loc", "nullo");
                }

            }//if activo
            else {
                //Log.d( "activo", "No estoy activo" );
            }
        }//permisos
        else
            //Log.d("permisos", "No hay");
    }

    /**
     * Busca los usuarios cercanos
     */
    private void busca() {

        actualizarPosicion();


        if (this.latitude != 360 && this.longitude != 360) {

            AsyncHttpClient client = new AsyncHttpClient( );
            String url = "http://adminweb.ddns.net/connect.php";
            RequestParams parametros = new RequestParams(  );
            parametros.put("operation", "buscarU");
            parametros.put("ma", this.user);
            parametros.put("lat", this.latitude);
            parametros.put("lon", this.longitude);

            client.post( url, parametros, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    if(statusCode == 200)
                    {
                        Toast.makeText(getApplicationContext(), "Ubicación actualizada", Toast.LENGTH_LONG).show();
						CargaLista( obtDatosJson( new String( responseBody ) ) );
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Toast.makeText(getApplicationContext(), "No se pudo almacenar", Toast.LENGTH_LONG).show();

                }
            } );
            // posText.setText( "Ahora estás en \n" + this.ubicationName.getText() );
        }//end if
        else{
            //posText.setText( "Fallo de ubicación" );
        }

        handler.postDelayed(runnable, 30000);//Indicamos cada cuanto tiempo queremos que se repita la accion
                                                        //En este caso 30 seg

    }
	
	/**
     * Actualiza la lista de usuarios encontrados
     * @param datos
     */
    public void CargaLista(final ArrayList<String> datos) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>( this, android.R.layout.simple_list_item_1, datos );

        momento.setAdapter( adapter );
        momento.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String s = datos.get( position ); //Obtencion de la informacon del usuario
                String [] usuario = s.split( "\n" );

                Intent intent = new Intent(UsersByPositionActivity.this, PerfilActivity.class);
                intent.putExtra("usuario", usuario[1]);
                startActivity(intent);
            }
        } );

    }

    /**
     * Parseo de JSON  a Array de usuarios
     * @param response
     * @return
     */
    public ArrayList<String> obtDatosJson(String response){
        Log.d("response", response);
        ArrayList<String> listado = new ArrayList<String>(  );
        try{

            JSONArray jsonArray = new JSONArray( response );
            String texto="";

            for (int i = 0; i< jsonArray.length();i++){

                texto = jsonArray.getJSONObject( i ).getString( "nombre" ) + " "
                        + jsonArray.getJSONObject( i ).getString( "apellido" ) + "\n" +
                        jsonArray.getJSONObject( i ).getString( "mail" );

                listado.add( texto );

            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return listado;
    }


}
