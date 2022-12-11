package com.asignatura.peguifacil2;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class PersonasAdapter extends RecyclerView.Adapter<PersonasAdapter.ViewHolder> {

    // DATABASE
    private SQLiteDatabase DB;
    private DBHelper helper;
    private String userMail;
    private int userType;


    // NECESSARY ATTRIBUTES
    private ArrayList<Persona> personasList; // DATA TO BE DISPLAYED
    private LayoutInflater inflater;
    private Context context;

    // CONSTRUCTOR
    public PersonasAdapter(ArrayList<Persona> personasList, Context context){
        this.personasList = personasList;
        this.context = context;
    }

    // INFLATING LAYOUT
    @NonNull
    @Override
    public PersonasAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.persona_viewholder, null);
        return new PersonasAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindData(personasList.get(position));
    }

    @Override
    public int getItemCount() {return personasList.size();}

    // VIEW-HOLDER CLASS
    public static class ViewHolder extends RecyclerView.ViewHolder {

        // USER INTERFACE ATTRIBUTES
        TextView nombre, correo, telefono, edad, educacion;
        MaterialCardView personaCardView;

        // INITIALIZE ATTRIBUTES
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.txtNombrePersona);
            nombre = itemView.findViewById(R.id.txtNombrePersona);
            correo = itemView.findViewById(R.id.txtCorreo);
            telefono = itemView.findViewById(R.id.txtTelefono);
            edad = itemView.findViewById(R.id.txtEdad);
            educacion = itemView.findViewById(R.id.txtEducacion);
        }

        // SET IT´S VALUES
        public void bindData(final Persona persona){
            nombre.setText(persona.getNombres()+" "+persona.getApellidos());
            correo.setText(persona.getCorreo());
            telefono.setText(persona.getTelefono());
            edad.setText((CharSequence) persona.getFechaNacimiento());
            educacion.setText(persona.getTipo_educacion());
        }

    }

}
