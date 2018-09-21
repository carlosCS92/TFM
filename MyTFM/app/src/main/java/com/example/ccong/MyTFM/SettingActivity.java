package com.example.ccong.MyTFM;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;

/**
 * Vista de ajustes de la informacion del usuario
 */
public class SettingActivity extends AppCompatActivity {
    //Campos de texto
    private TextView mail; //email
    private EditText nombre; //Nombre
    private EditText apellidos; //Apellidos
    private EditText tlf; //Teléfono
    private EditText nac; //Fecha de nacimiento
    private Switch wa; //Whatsapp

    //Botones
    private Button know; //Guarda los cambios y muestra la lista de conocimientos
    private Button save; //Guarda los cambios y muestra el menú principal

    private Usuario u; //Usuario

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        this.mail = (TextView) findViewById(R.id.IDUser);
        this.nombre = (EditText) findViewById(R.id.editSettingName);
        this.apellidos = (EditText) findViewById(R.id.editSettingSurname);
        this.tlf = (EditText) findViewById(R.id.editSettingTlf);
        this.nac = (EditText) findViewById(R.id.editSettingBirth);
        this.wa = (Switch) findViewById(R.id.settingWa);
        this.save = (Button) findViewById(R.id.settingSave);
        this.know = (Button) findViewById(R.id.settingKnow);

        //Cargar datos de usuario
        u = SaveInfo.load(this);



        //Modificar textos
        this.mail.setText(u.getEmail());
        this.nombre.setText(u.getNombre());
        this.apellidos.setText(u.getApellidos());
        this.tlf.setText(Integer.toString(u.getTlf()));
        this.nac.setText(u.getFechaNac());
        this.wa.setChecked(u.isWa());
        this.know.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modificar(v, 1);
            }
        });
        this.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modificar(v, 2);
            }
        });

    }

    /**
     * Actualiza la información del dispositivo
     * @param v
     * @param boton El botón que se ha pulsado
     */
    public void modificar(View v, int boton){
        this.u.setNombre(this.nombre.getText().toString());
        this.u.setApellidos(this.apellidos.getText().toString());
        this.u.setTlf(Integer.parseInt(this.tlf.getText().toString()));
        this.u.setFechaNac(this.nac.getText().toString());
        this.u.setWa(this.wa.isChecked());



        SaveInfo.save(this.u, this);

        guardarDatos(boton);

    }

    /**
     * Actualiza la base de datos
     * @param boton //Boton que se ha pulsado
     */
    public void guardarDatos(final int boton){
        AsyncHttpClient client = new AsyncHttpClient( );
        String url = "http://adminweb.ddns.net/connect.php";
        RequestParams parametros = new RequestParams(  );
        parametros.put("operation", "change");
        parametros.put("nom", this.u.getNombre());
        parametros.put("ap", this.u.getApellidos());
        parametros.put("ma", this.u.getEmail());
        parametros.put("wa", this.u.isWa());
        parametros.put("te", this.u.getTlf());
        parametros.put("fn", this.u.getFechaNac());
        parametros.put("con", this.u.getConocimientosString());

        client.post( url, parametros, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode == 200)
                {
                    String ret = new String (responseBody);
                    Log.d("fallo", ret);
                    Toast.makeText(getApplicationContext(), ret , Toast.LENGTH_LONG).show();

                    nextActivity( boton );
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                nextActivity( boton );
            }
        } );
    }

    /**
     * Muestra la vista corresppondiente
     * @param code //Boton que se ha pulsado. 1, conocimientos; 2, guardar
     */
    public void nextActivity (int code){
        if (code == 1)
        {
            Intent intent = new Intent(this, ActitudesActivity.class);
            intent.putExtra("usuario", true);
            startActivity(intent);
        }
        else
        {
            Intent intent = new Intent(this, MenuActivity.class);
            intent.putExtra("usuario", true);
            startActivity(intent);
        }

    }

}
