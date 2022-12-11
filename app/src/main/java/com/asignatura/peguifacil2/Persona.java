package com.asignatura.peguifacil2;

import java.util.Date;

public class Persona {
    // ATTRIBUTES
    private int id;
    private String rut;
    private String nombres;
    private String apellidos;
    private String correo;
    private String pass;
    private String telefono;
    private String tipo_educacion;
    private String fechaNacimiento;
    private String descripcion;
    private String tipo_usuario;

    // CONSTRUCTORS
    public Persona(String rut, String nombres, String apellidos, String correo, String pass, String telefono, String fechaNacimiento, String descripcion, String tipo_usuario) {
        this.rut = rut;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.correo = correo;
        this.pass = pass;
        this.telefono = telefono;
        this.fechaNacimiento = fechaNacimiento;
        this.descripcion = descripcion;
        this.tipo_usuario = tipo_usuario;
    }

    public Persona() {}

    // SETTERS
    public void setRut(String r){this.rut = r;}
    public void setNombres(String n){this.nombres = n;}
    public void setApellidos(String a){this.apellidos = a;}
    public void setCorreo(String c){this.correo = c;}
    public void setPass(String p){this.pass = p;}
    public void setTelefono(String t){this.telefono = t;}
    public void setFechaNacimiento(String f){this.fechaNacimiento = f;}
    public void setDescripcion(String d){this.descripcion = d;}
    public void setTipo_usuario(String t){this.tipo_usuario = t;}
    public void setTipo_educacion(String t){this.tipo_educacion = t;}

    // GETTERS
    public int getId() {return id;}
    public String getRut() {return rut;}
    public String getNombres() {return nombres;}
    public String getApellidos() {return apellidos;}
    public String getCorreo() {return correo;}
    public String getPass() {return pass;}
    public String getTelefono() {return telefono;}
    public String getFechaNacimiento() {return fechaNacimiento;}
    public String getDescripcion() {return descripcion;}
    public String getTipo_usuario() {return tipo_usuario;}
    public String getTipo_educacion(){return tipo_educacion;}

}
