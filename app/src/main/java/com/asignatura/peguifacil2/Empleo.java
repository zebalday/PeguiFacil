package com.asignatura.peguifacil2;

import java.sql.Date;

public class Empleo {

    //ATTRIBUTES
    private int id;
    private String titulo;
    private String descripcion;
    private int sueldo;
    private String created_at;
    private String empresa;
    private String jornada;
    private String paga;
    private String educacion;

    // CONSTRUCTORS
    public Empleo(String titulo, String descripcion, int sueldo, String created_at, String empresa, String jornada, String paga, String educacion) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.sueldo = sueldo;
        this.created_at = created_at;
        this.empresa = empresa;
        this.jornada = jornada;
        this.paga = paga;
        this.educacion = educacion;
    }

    public Empleo(String titulo, int sueldo, String empresa, String jornada) {
        this.titulo = titulo;
        this.sueldo = sueldo;
        this.empresa = empresa;
        this.jornada = jornada;
    }

    public Empleo() {}

    // SETTERS

    public void setId(int id) {this.id = id;}
    public void setTitulo(String titulo) {this.titulo = titulo;}
    public void setDescripcion(String descripcion) {this.descripcion = descripcion;}
    public void setSueldo(int sueldo) {this.sueldo = sueldo;}
    public void setCreated_at(String created_at) {this.created_at = created_at;}
    public void setEmpresa(String empresa) {this.empresa = empresa;}
    public void setJornada(String jornada) {this.jornada = jornada;}
    public void setPaga(String paga) {this.paga = paga;}
    public void setCategoria(String educacion) {this.educacion = educacion;}

    // GETTERS
    public int getId() {return id;}
    public String getTitulo() {return titulo;}
    public String getDescripcion() {return descripcion;}
    public int getSueldo() {return sueldo;}
    public String getCreated_at() {return created_at;}
    public String getEmpresa() {return empresa;}
    public String getJornada() {return jornada;}
    public String getPaga() {return paga;}
    public String getEducacion() {return educacion;}
}
