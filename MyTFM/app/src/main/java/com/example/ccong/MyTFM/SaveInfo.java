package com.example.ccong.MyTFM;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import java.util.ArrayList;

/**
 * Clase estática para leer y escribir en el fichero shared preference del dispositivo.
 * Gracias a ello la aplicación puede utilizar algunas funciones sin conexión a internet
 */
public class SaveInfo
{
    /**
     * Almacena o actualiza los datos del usuario
     * @param u Usuario
     * @param a Actividad desde la que se llama
     */
    public static void save(Usuario u, Activity a)
    {
        SharedPreferences myPreferences = PreferenceManager.getDefaultSharedPreferences(a);
        SharedPreferences.Editor myEditor = myPreferences.edit();

        myEditor.putString("NAME",u.getNombre());
        myEditor.putString("SURNAME",u.getApellidos());
        myEditor.putString("MAIL",u.getEmail());
        myEditor.putInt("TLF",u.getTlf());
        myEditor.putBoolean("WA",u.isWa());
        myEditor.putString("BIRTH",u.getFechaNac());
        myEditor.putString("CONOCIMIENTOS", u.getConocimientosString());
        myEditor.commit();
    }

    @Nullable
    /**
     * Lee la información almacenada en el dispositivo y devuelve los datos del usuario
     */
    public static Usuario load(Activity a)
    {
        SharedPreferences myPreferences = PreferenceManager.getDefaultSharedPreferences(a);
        SharedPreferences.Editor myEditor = myPreferences.edit();

        String nombre = myPreferences.getString("NAME", "");
        String ape = myPreferences.getString("SURNAME", "");
        String mail = myPreferences.getString("MAIL", "");
        int tlf = myPreferences.getInt("TLF", 0);
        boolean wa = myPreferences.getBoolean("WA", false);
        String fecha = myPreferences.getString("BIRTH", "");
        String cadena = myPreferences.getString("CONOCIMIENTOS", "");

        if (mail == "")
            return null;


        String[] con = cadena.split(",");
        ArrayList<String> conocimientos = new ArrayList<>();
        for (String s:con) {
            conocimientos.add(s);
        }

        return new Usuario(nombre, ape, mail, tlf, wa,fecha, conocimientos);
    }
}
