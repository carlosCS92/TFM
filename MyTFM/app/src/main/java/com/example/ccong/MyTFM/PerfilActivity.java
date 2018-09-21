package com.example.ccong.MyTFM;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;

import cz.msebera.android.httpclient.Header;

public class PerfilActivity extends AppCompatActivity {

    //Campos de informacion
    TextView nombre; //nombre
    TextView tlf; //Telefono
    TextView wa;//Indicador si se permite whatsapp o no
    TextView mail;// Email

    //Botones
    Button save; //Guardar contacto
    Button del; //Borrar contacto

    //Datos auxiliares
    String user; //email del usuario
    String candidato; //email del candidato

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_perfil );

        this.nombre = (TextView) findViewById( R.id.nameVisit );
        this.tlf = (TextView) findViewById( R.id.phoneVisit );
        this.mail = (TextView) findViewById( R.id.emailVisit );
        this.wa = (TextView) findViewById( R.id.waVisit );

        this.save = (Button)findViewById( R.id.saveVisit );
        this.del = (Button)findViewById( R.id.delVisit );

        user = SaveInfo.load( this ).getEmail();

        this.candidato = getIntent().getExtras().getString( "usuario" );


        cargaDatos();//Actualización de los campos



        this.save.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarContacto();
            }
        } );

        this.del.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                borrarContacto();
            }
        } );

    }

    /**
     * Se hace la llamada a la BBDD para borrar al contacto de la lista
     */
    private void borrarContacto() {
        AsyncHttpClient client = new AsyncHttpClient( );
        String url = "http://adminweb.ddns.net/connect.php";
        RequestParams parametros = new RequestParams(  );
        parametros.put("operation", "delContact");
        parametros.put("ma", user);
        parametros.put("favorito", candidato);

        client.post( url, parametros, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode == 200)
                {
                    Toast.makeText(getApplicationContext(), "Borrado con exito", Toast.LENGTH_LONG).show();
                    back();


                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getApplicationContext(), "No se ha podido borrar. Intentelo más tarde", Toast.LENGTH_LONG).show();

            }
        } );
    }

    /**
     * Se vuelve a mostrar el listado de nuevo
     */
    private void back() {
        Intent intent = new Intent(this, UsersActivity.class);
        intent.putExtra("tipo", "usuarios");
        startActivity(intent);
    }

    /**
     * Se almacena como contacto del usuario
     */
    private void guardarContacto() {
        AsyncHttpClient client = new AsyncHttpClient( );
        String url = "http://adminweb.ddns.net/connect.php";
        RequestParams parametros = new RequestParams(  );
        parametros.put("operation", "newContact");
        parametros.put("ma", user);
        parametros.put("favorito", candidato);
        parametros.put("nombre", this.nombre.getText().toString());

        client.post( url, parametros, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode == 200)
                {
                    Toast.makeText(getApplicationContext(), "Contacto guardado", Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG).show();

            }
        } );
    }

    /**
     * Funcion que recoge los datos del contacto y actualiza los campos de la vista
     */
    private void cargaDatos(){
        AsyncHttpClient client = new AsyncHttpClient( );
        String url = "http://adminweb.ddns.net/connect.php";
        RequestParams parametros = new RequestParams(  );
        parametros.put("operation", "usuario");
        parametros.put("user", candidato);//mail del usuario

        client.post( url, parametros, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d("codigo", Integer.toString( statusCode));
                if(statusCode == 200)
                {
                   obtDatosJson( new String( responseBody ));

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getApplicationContext(), "Error al cargar información", Toast.LENGTH_LONG).show();


            }
        } );
    }



    public void obtDatosJson(String response){

        try{

            JSONArray jsonArray = new JSONArray( response );


            for (int i = 0; i< jsonArray.length();i++){
                this.nombre.setText( jsonArray.getJSONObject( i ).getString( "nombre" ) +
                        "\n"+ jsonArray.getJSONObject( i ).getString( "apellidos" ) );

                this.mail.setText(jsonArray.getJSONObject( i ).getString( "mail" ));
                String watext = jsonArray.getJSONObject( i ).getString( "wa" );
                if (watext == "false"){
                    this.wa.setText( "" );
                }

                this.tlf.setText(jsonArray.getJSONObject( i ).getString( "tlf" ));

            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
