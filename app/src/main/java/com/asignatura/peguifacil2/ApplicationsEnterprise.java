package com.asignatura.peguifacil2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;

import java.util.ArrayList;

public class ApplicationsEnterprise extends AppCompatActivity {

    // DATABASE
    private SQLiteDatabase DB;
    private DBHelper helper;
    private int job_id;

    // NECESSARY ATTRIBUTES
    private ArrayList<Persona> personasList = new ArrayList<>(); // DATA TO BE DISPLAYED
    private RecyclerView recyclerView;
    private PersonasAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applications_enterprise);
        getSupportActionBar().hide();
        initRecyclerView();
    }

    public void initRecyclerView(){
        // OBTAIN WRITABLE DATABASE
        helper = new DBHelper(this);
        DB = helper.getWritableDatabase();

        // OBTAIN JOB ID
        job_id = getIntent().getExtras().getInt("job_id");
        Cursor postulantes_id = DB.rawQuery("SELECT persona FROM persona_empleo WHERE empleo = ?", new String[]{String.valueOf(job_id)});

        while(postulantes_id.moveToNext()){
            int persona_id = postulantes_id.getInt(0);
            Cursor persona_info = DB.rawQuery("SELECT nombres, apellidos, correo, telefono, fec_nac, niv_edu " +
                    "FROM persona " +
                    "WHERE id = ?",
                    new String[]{String.valueOf(persona_id)});

            while (persona_info.moveToNext()){
                System.out.println(persona_info);
                Persona new_persona = new Persona();
                new_persona.setNombres(persona_info.getString(0));
                new_persona.setApellidos(persona_info.getString(1));
                new_persona.setCorreo(persona_info.getString(2));
                new_persona.setTelefono(persona_info.getString(3));
                new_persona.setFechaNacimiento(persona_info.getString(4));
                new_persona.setTipo_educacion(persona_info.getString(5));
                personasList.add(new_persona);
            }
        }

        // SETTING THE ADAPTER
        adapter = new PersonasAdapter(personasList, this);

        //  ADAPTER --> RECYCLER
        recyclerView = findViewById(R.id.personas_recycler);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
    }
}