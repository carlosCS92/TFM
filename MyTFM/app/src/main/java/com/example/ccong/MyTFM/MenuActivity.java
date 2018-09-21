package com.example.ccong.MyTFM;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;


/**
 * Vista principal de la aplicación
 */
public class MenuActivity extends AppCompatActivity {
    private TextView user; //Nombre y apellidos del usuario
    private Button search; //boton de buscar usuarios al momento
    private Button lugares;// Boton para ver las ultimas posiciones
    private Button setting;// ajustes
    private TextView posText; //Texto que muestra dónde te encuentras
    private Button actualizar;// Botón que almacena la ubicación
    private EditText ubicationName; //Texto para nombrar la ubicación a almacenar
    private Button usuarios;// Mostrar el listado de usuarios favoritos

    //Elementos para la posición
    private LocationListener listener;
    private LocationManager locman;
    private Location loc;
    private double latitude = 360;
    private double longitude = 360;

    Intent intent;
    Usuario us; //El usuario

    boolean act;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        this.user = (TextView) findViewById(R.id.textUser);
        this.search = (Button) findViewById(R.id.buttonSearch);
        this.actualizar = (Button) findViewById(R.id.buttonSave);
        this.lugares = (Button) findViewById(R.id.buttonCheck);
        this.setting = (Button) findViewById(R.id.buttonSetting);
        this.posText = (TextView) findViewById(R.id.textUbication);
        this.ubicationName = (EditText) findViewById( R.id.locid);
        this.usuarios = (Button) findViewById( R.id.usersSaved );

        this.us = SaveInfo.load(this);



        //En la parte superior de la pantalla aparece el nombre y apellidos del usuario
        this.user.setText(us.getNombre()+" "+ us.getApellidos());

        this.setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ajustes(v);
            }
        });


        this.search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, UsersMoment.class);
                intent.putExtra("tipo", "usuarios");
                startActivity(intent);
            }
        });

        this.actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardar(v);
            }
        });

        this.lugares.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                getPosiciones(v);
            }
        });

        this.usuarios.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                getUsuarios(v);
            }
        });


    }

    private void ajustes(View v){
        intent = new Intent(this, SettingActivity.class);
        intent.putExtra("id",  true);
        startActivity(intent);
    }

    private void getPosiciones(View v) {
        Intent intent = new Intent(this, UbicationActivity.class);
        intent.putExtra("tipo", "position");
        startActivity(intent);

    }

    private void getUsuarios(View v) {
        Intent intent = new Intent(this, UsersActivity.class);
        intent.putExtra("tipo", "usuarios");
        startActivity(intent);

    }

    //Obtiene la posición del dispositivo
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

                locman.requestLocationUpdates( locman.GPS_PROVIDER, 0, 0, listener );

                loc = locman.getLastKnownLocation( locman.GPS_PROVIDER );
                if (loc != null) {
                    this.latitude = loc.getLatitude();
                    this.longitude = loc.getLongitude();
                }
                else {
                    Log.d ("loc", "nullo");
                }

            }//if activo
            else {
                Log.d( "activo", "No estoy activo" );
            }
        }//permisos
        else
            Log.d("permisos", "No hay");
    }



    //Almacena la posición del usuario en la BBDD
    private void guardar(View v) {

       actualizarPosicion(); //Llamada a la función de actualización de coordenadas
        //Se comprueba que existen posiciones correctas
        if (this.latitude != 360 && this.longitude != 360) {

           //Se actualiza el texto de la ubicación
           posText.setText( "Ahora estas en \n"+this.ubicationName.getText() );

            AsyncHttpClient client = new AsyncHttpClient( );
            String url = "http://adminweb.ddns.net/connect.php";
            RequestParams parametros = new RequestParams(  );
            parametros.put("operation", "ubicacion");
            parametros.put("ma", this.us.getEmail());
            parametros.put("lat", this.latitude);
            parametros.put("lon", this.longitude);
            parametros.put("nom", this.ubicationName.getText());

            client.post( url, parametros, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    if(statusCode == 200)
                    {
                        Toast.makeText(getApplicationContext(), "Ubicación almacenada", Toast.LENGTH_LONG).show();
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
            //Mensaje de error que aparece si falla la ubicación
            posText.setText( "Fallo de ubicación" );
        }


    }

}
