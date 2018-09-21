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
 * Listado de las ubicaciones almacenadas
 */
public class UbicationActivity extends Activity {

    ListView ubications; //Listado
    String user;//Email del usuario


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_ubication );
        ubications = (ListView) findViewById( R.id.ubications_list );

        this.user = SaveInfo.load( this ).getEmail();

        obtDatos();

    }

    /**
     * Se obtienen los datos de la base de datos
     */
    public void obtDatos(){
        AsyncHttpClient client = new AsyncHttpClient( );
        String url = "http://adminweb.ddns.net/connect.php/";
        RequestParams parametros = new RequestParams(  );


            parametros.put("operation", "buscarUbi");
            parametros.put( "ma", user );



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


    /**
     * Actualizacion de la vista para mostrar las ubicaciones
     * @param datos listado de nombres de ubicación
     */
    public void CargaLista(final ArrayList<String> datos) {
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>( this, android.R.layout.simple_list_item_1, datos );
        ubications.setAdapter( adapter );

        ubications.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Se llama a la lista de usuarios por posicion
                Intent intent = new Intent(UbicationActivity.this, UsersByPositionActivity.class);
                intent.putExtra("ubicacion", datos.get( position )); //Se comunica a la siguiente vista el nombre de la ubicacion
                startActivity(intent);
            }
        } );


    }

    /**
     * Parseo del fichero JSON que devuelve la BBDD a array de Strings
     * @param response
     * @return
     */
    public ArrayList<String> obtDatosJson(String response){
        ArrayList<String> listado = new ArrayList<String>(  );
        try{

            JSONArray jsonArray = new JSONArray( response );
            String texto="";

            for (int i = 0; i< jsonArray.length();i++){

                    texto = jsonArray.getJSONObject( i ).getString( "nombre" );

                    listado.add( texto );

            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return listado;
    }

}
