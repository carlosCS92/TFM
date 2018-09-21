package com.example.ccong.MyTFM;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Listado de usuarios en una posición dada
 */
public class UsersByPositionActivity extends AppCompatActivity {

    String usuario; //El usuario que usa la app
    String position; //posición sobre la qque se va a buscar

    ListView usuarios;//Lista de usuarios


    //Conexion con la BBDD
    AsyncHttpClient client = new AsyncHttpClient( );
    String url = "http://adminweb.ddns.net/connect.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_users_by_position );

        usuario = SaveInfo.load( this ).getEmail();//email del usuario
        position = getIntent().getExtras().getString( "ubicacion" );//nombre de la ubicación

        usuarios = (ListView) findViewById( R.id.usersPos_list );

        obtUsers();
    }

    /**
     * Obtencion de los usuarios segun la posicion
     */
    public void obtUsers(){

        RequestParams parametros = new RequestParams(  );


        parametros.put("operation", "buscarUserS");//
        parametros.put( "ma", usuario );
        parametros.put("nom", position);



        client.post( url, parametros, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode == 200)
                {

                    CargaLista( obtDatosJson( new String( responseBody ) ) );
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        } );
    }//obtPos

    /**
     * Actualiza la lista de usuarios encontrados
     * @param datos
     */
    public void CargaLista(final ArrayList<String> datos) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>( this, android.R.layout.simple_list_item_1, datos );

        usuarios.setAdapter( adapter );
        usuarios.setOnItemClickListener( new AdapterView.OnItemClickListener() {
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
