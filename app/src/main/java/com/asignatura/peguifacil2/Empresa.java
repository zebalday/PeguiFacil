package com.asignatura.peguifacil2;

public class Empresa {

    // ATTRIBUTES
    private int id;
    private String rut;
    private String nombre;
    private String nombre_representante;
    private String correo;
    private String pass;
    private String telefono;
    private String region;
    private String direccion;
    private String altitud;
    private String latitud;
    private int tipo_usuario;

    // CONSTRUCTORS
    public Empresa(String rut, String nombre, String nombre_representante, String correo, String pass, String telefono, String region, String direccion, String altitud, String latitud, int tipo_usuario) {
        this.rut = rut;
        this.nombre = nombre;
        this.nombre_representante = nombre_representante;
        this.correo = correo;
        this.pass = pass;
        this.telefono = telefono;
        this.region = region;
        this.direccion = direccion;
        this.altitud = altitud;
        this.latitud = latitud;
        this.tipo_usuario = tipo_usuario;
    }

    public Empresa() {}

    // SETTERS
    public void setRut(String rut) {this.rut = rut;}
    public void setNombre(String nombre) {this.nombre = nombre;}
    public void setNombre_representante(String nombre_representante) {this.nombre_representante = nombre_representante;}
    public void setCorreo(String correo) {this.correo = correo;}
    public void setPass(String pass) {this.pass = pass;}
    public void setTelefono(String telefono) {this.telefono = telefono;}
    public void setRegion(String region) {this.region = region;}
    public void setDireccion(String direccion) {this.direccion = direccion;}
    public void setAltitud(String altitud) {this.altitud = altitud;}
    public void setLatitud(String latitud) {this.latitud = latitud;}
    public void setTipo_usuario(int tipo_usuario) {this.tipo_usuario = tipo_usuario;}

    // GETTERS
    public int getId() {return id;}
    public String getRut() {return rut;}
    public String getNombre() {return nombre;}
    public String getNombreRepresentante() {return nombre_representante;}
    public String getCorreo() {return correo;}
    public String getPass() {return pass;}
    public String getTelefono() {return telefono;}
    public String getRegion() {return region;}
    public String getDireccion() {return direccion;}
    public String getAltitud() {return altitud;}
    public String getLatitud() {return latitud;}
    public int getTipo_usuario() {return tipo_usuario;}
}
