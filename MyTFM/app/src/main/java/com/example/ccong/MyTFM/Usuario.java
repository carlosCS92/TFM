package com.example.ccong.MyTFM;

import java.util.ArrayList;

/**
 * Created by Carlos Congosto
 *
 * Clase con los campos del usuario activo de la aplicacion
 */

public class Usuario
{
    private String nombre; //Nombre del usuario
    private String apellidos; //Apellidos del usuario
    private String email; //Correo electronico del usuario
    private int tlf; //Telefono del usuario
    private boolean wa; //permite whatsapp o no
    private String fechaNac; //Fecha de nacimiento
    private ArrayList <String> conocimientos; //lista de conocimientos


    /**
     * Constructor sin array de conocimientos
     * @param nombre
     * @param apellidos
     * @param email
     * @param tlf
     * @param wa
     * @param fecha
     */
    public Usuario (String nombre, String apellidos, String email, int tlf, boolean wa, String fecha)
    {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.tlf = tlf;
        this.wa = wa;
        this.fechaNac = fecha;
        this.conocimientos = new ArrayList<>();
    }

    /**
     * Constructor con todos los campos
     * @param nombre
     * @param apellidos
     * @param email
     * @param tlf
     * @param wa
     * @param fecha
     * @param con
     */
    public Usuario (String nombre, String apellidos, String email, int tlf, boolean wa, String fecha, ArrayList<String> con )
    {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.tlf = tlf;
        this.wa = wa;
        this.fechaNac = fecha;
        this.conocimientos = con;
        //Log.d("usuario", "User created");
    }

    /**
     * Devuelve el nombre
     * @return nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Modifica el nombre
     * @param nombre nuevo nombre
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Devuelve los apellidos
     * @return apellidos
     */
    public String getApellidos() {
        return apellidos;
    }

    /**
     * Modifica los apellidos
     * @param apellidos nuevos apellidos
     */
    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    /**
     * Devuelve el correo electronico
     * @return email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Modifica el email
     * @param email nuevo email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Devuelve el telefono
     * @return telefono
     */
    public int getTlf() {
        return tlf;
    }

    /**
     * Modifica el telefono
     * @param tlf nuevo telefono
     */
    public void setTlf(int tlf) {
        this.tlf = tlf;
    }

    /**
     * Devuelve si se utiliza whatsapp o no
     * @return true / false
     */
    public boolean isWa() {
        return wa;
    }

    /**
     * Modifica el booleano
     * @param wa
     */
    public void setWa(boolean wa) {
        this.wa = wa;
    }

    /**
     * Devuelve la fecha de nacimiento
     * @return Fecha de nacimiento
     */
    public String getFechaNac() {
        return fechaNac;
    }

    /**
     * Modifica la fecha de nacimiento
     * @param fechaNac Nuen¡va fecha
     */
    public void setFechaNac(String fechaNac) {
        this.fechaNac = fechaNac;
    }

    /**
     * Devuelve el array de conocimientos
     * @return conocimientos
     */
    public ArrayList<String> getConocimientos() {
        return conocimientos;
    }

    /**
     * Modifica el array de conocimientos
     * @param conocimientos Nuevos conocimientos
     */
    public void setConocimientos(ArrayList<String> conocimientos) {
        this.conocimientos = conocimientos;
    }

    /**
     * Devuelve los conocimientos en formato de cadena, separados por comas
     * @return conocimientos
     */
    public String getConocimientosString(){
        String aux = "";
        int c = 0;
        while (c < conocimientos.size())
        {
            if (c>0)
                aux +=",";
            aux+=conocimientos.get(c);
            c++;
        }
        return aux;
    }

}
