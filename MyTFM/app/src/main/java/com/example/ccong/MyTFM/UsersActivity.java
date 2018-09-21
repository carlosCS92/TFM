package com.example.ccong.MyTFM;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
 * Listado de usuarios (contactos)
 */
public class UsersActivity extends Activity {

    ListView contactos; //lista de contactos
    String user;//usuario
    public static ArrayAdapter<String> adapter;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_users );
        contactos = (ListView) findViewById( R.id.users_list );

        this.user = SaveInfo.load( this ).getEmail();




        obtDatos();


    }

    /**
     * Llamada a la BBDD
     */
    public void obtDatos(){
        AsyncHttpClient client = new AsyncHttpClient( );
        String url = "http://adminweb.ddns.net/connect.php";
        RequestParams parametros = new RequestParams(  );


            parametros.put ("operation", "contactos");
            parametros.put ("ma", user);


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
    }


    public void CargaLista(final ArrayList<String> datos) {
        adapter = new ArrayAdapter<String>( this, android.R.layout.simple_list_item_1, datos );

        contactos.setAdapter( adapter );
        contactos.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String s = datos.get( position ); //Obtencion del texto del item de la lista
                String [] usuario = s.split( "\n" ); //Separo por el salto de línea
                Intent intent = new Intent(UsersActivity.this, PerfilActivity.class);
                intent.putExtra("usuario", usuario[1]); //Mando a la siguiente vista el email del contacto
                startActivity(intent);
            }
        } );

    }

    /**
     * Parseo de JSON a Array de nombres
     * @param response
     * @return
     */
    public ArrayList<String> obtDatosJson(String response){
        ArrayList<String> listado = new ArrayList<String>(  );
        try{

            JSONArray jsonArray = new JSONArray( response );
            String texto="";

            for (int i = 0; i< jsonArray.length();i++){

                    texto = jsonArray.getJSONObject( i ).getString( "nombre" ) + "\n" + //Nombre y apellidos del contacto
                            jsonArray.getJSONObject( i ).getString( "mail" );//email del contacto

                    listado.add( texto );

            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return listado;
    }

}
