package com.example.ccong.MyTFM;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;


public class MainActivity extends AppCompatActivity
{
    private EditText nameField;//Nombre del usuario
    private EditText surnameField;//Apellidos del usuario
    private EditText mailField;//Mail del usuario
    private EditText phoneField;//Teléfono del usuario
    private EditText birthField;//Fecha de nacimiento del usuario
    private Switch waButton; //Check de whatsapp
    private Button guardar;//Boton de guardar


    private Usuario user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Compruebo si existen datos
        Usuario u = SaveInfo.load(this);
        if (u!=null){ //Si existe se muestra la ventana principal de la app
            Intent intent = new Intent(this, MenuActivity.class);
            intent.putExtra("usuario", u.getNombre()+" "+u.getApellidos());
            startActivity(intent);
        }
        setContentView(R.layout.activity_main);


        nameField = (EditText) findViewById(R.id.editName);
        surnameField = (EditText) findViewById(R.id.editSurname);
        mailField = (EditText) findViewById(R.id.editMail);
        phoneField = (EditText) findViewById(R.id.editPhone);
        birthField = (EditText) findViewById(R.id.editBirth);
        waButton = (Switch) findViewById(R.id.radioButtonWa);
        guardar = (Button) findViewById(R.id.buttonSave);


        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUser(v);
            }
        });
    }

    //Recogida de datos cuando se pulsa el botón
    public void sendUser(View v)
    {
        String name = nameField.getText().toString();
        String surname = surnameField.getText().toString();
        String mail = mailField.getText().toString();
        Integer phone = Integer.parseInt(phoneField.getText().toString());
        String birth = birthField.getText().toString();
        Boolean wa = waButton.isChecked();



            user = new Usuario(name, surname, mail, phone, wa, birth);
            SaveInfo.save(user, this); //Se almacenan los datos en shared preferences


            guardarDatos();//Se crea el usuario en la BBDD


    }

    /**
     * Establece la conexion con el script php
     */
    public void guardarDatos(){
        AsyncHttpClient client = new AsyncHttpClient( );
        String url = "http://adminweb.ddns.net/connect.php";

        //Parámetros del script
        RequestParams parametros = new RequestParams(  );
        parametros.put("operation", "nuevo");
        parametros.put("nom", this.user.getNombre());
        parametros.put("ap", this.user.getApellidos());
        parametros.put("ma", this.user.getEmail());
        parametros.put("wa", this.user.isWa());
        parametros.put("te", this.user.getTlf());
        parametros.put("fn", this.user.getFechaNac());

        client.post( url, parametros, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode == 200)
                {
                    String ret = new String (responseBody);
                    Toast.makeText(getApplicationContext(), ret , Toast.LENGTH_LONG).show();

                    nextActivity( statusCode );
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                nextActivity( statusCode );
            }
        } );
    }

    /**
     * Se comprueba que todo ha ido bien en función del código.
     * Si 200 se muestra el formulario 2
     * Si no, Se muestra mensaje de error
     * @param code
     */
    public void nextActivity (int code){
        if (code == 200)
        {
            Intent intent = new Intent(this, ActitudesActivity.class);
            intent.putExtra("usuario", true);
            startActivity(intent);
        }
        else
            Toast.makeText(getApplicationContext(), "Fallo al registrarse. Inténtelo más tarde", Toast.LENGTH_LONG).show();
    }




}
