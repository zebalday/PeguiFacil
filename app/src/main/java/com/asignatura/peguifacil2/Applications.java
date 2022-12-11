package com.asignatura.peguifacil2;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;


public class Applications extends Fragment {

    // DATABASE
    private SQLiteDatabase DB;
    private DBHelper helper;
    private String userMail;
    private int userType;
    private int userID;
    Cursor empleos_DB;


    // ADAPTER
    private EmpleosAdapterApplications adapter;
    private ArrayList<Empleo> empleosList;

    // UI
    private RecyclerView empleosRecyclerView;


    public Applications() {}


    public static Applications newInstance(String param1, String param2) {
        Applications fragment = new Applications();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // INIT VIEW
        View view = inflater.inflate(R.layout.fragment_applications, container, false);

        // INIT RECYCLERVIEW
        buildRecyclerView(view);
        return view;
    }

    private void buildRecyclerView(View view) {
        // GETTING JOBS FROM THE BATABASE
        helper = new DBHelper(getActivity());
        DB = helper.getWritableDatabase();

        // USER DATA
        userMail = getActivity().getIntent().getExtras().getString("user_mail");
        userType = getActivity().getIntent().getExtras().getInt("user_type");
        userID = userID(userType);

        if (userType == 1) {
            // DATABASE PARTITION
            Cursor persona_empleo = DB.rawQuery("SELECT empleo FROM persona_empleo WHERE persona = ?", new String[]{String.valueOf(userID)});
            ArrayList<Integer> id_empleos = new ArrayList<>();
            while (persona_empleo.moveToNext()) {
                id_empleos.add(persona_empleo.getInt(0));
            }

            // SELECT JOBS FROM PARTITION
            empleosList = new ArrayList<Empleo>();
            for (int id : id_empleos) {
                empleos_DB = DB.rawQuery("SELECT titulo, empresa, sueldo, tipo_jornada, created_at, id FROM empleo WHERE id = ?",
                        new String[]{String.valueOf(id)});

                // FILLING JOBS(CLASS) ARRAYLIST
                while (empleos_DB.moveToNext()) {
                    Empleo newEmpleo = new Empleo();
                    newEmpleo.setTitulo(empleos_DB.getString(0));
                    newEmpleo.setEmpresa(nombreEmpresa(empleos_DB.getInt(1)));
                    newEmpleo.setSueldo(empleos_DB.getInt(2));
                    newEmpleo.setJornada(tipoJornada(empleos_DB.getInt(3)));
                    newEmpleo.setCreated_at(empleos_DB.getString(4));
                    newEmpleo.setId(empleos_DB.getInt(5));
                    empleosList.add(newEmpleo);
                }
            }

            persona_empleo.close();

            // SETTING THE ADAPTER
            adapter = new EmpleosAdapterApplications(empleosList, getContext());
            adapter.setUserMail(userMail);
            adapter.setUserType(userType);

            //  ADAPTER --> RECYCLER
            empleosRecyclerView = view.findViewById(R.id.jobs_recycler);
            empleosRecyclerView.setAdapter(adapter);
            empleosRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            empleosRecyclerView.setHasFixedSize(true);
        }

    }

    private String tipoJornada(int tipo) {
        // SQL TRANSLATE BY ID
        helper = new DBHelper(getActivity());
        DB = helper.getWritableDatabase();
        Cursor tipo_BD = DB.rawQuery("SELECT tipo FROM tipo_jornada WHERE id = ?", new String[]{String.valueOf(tipo)});
        tipo_BD.moveToFirst();
        String nombre_tipo = tipo_BD.getString(0);
        return nombre_tipo;
    }

    private String nombreEmpresa(int id) {
        // SQL TRANSLATE BY ID
        helper = new DBHelper(getActivity());
        DB = helper.getWritableDatabase();
        Cursor empresa_BD = DB.rawQuery("SELECT nombre_empresa FROM empresa WHERE id = ?", new String[]{String.valueOf(id)});
        empresa_BD.moveToFirst();
        String nombre_empresa = empresa_BD.getString(0);
        return nombre_empresa;
    }

    private int userID(int type){
        // SQL TRANSLATE BY ID
        helper = new DBHelper(getContext());
        DB = helper.getWritableDatabase();

        if(type == 1){
            Cursor cursor = DB.rawQuery("SELECT id FROM persona WHERE correo = ?", new String[]{userMail});
            cursor.moveToFirst();
            int id = cursor.getInt(0);
            return id;
        }
        else{
            Cursor cursor = DB.rawQuery("SELECT id FROM empresa WHERE correo = ?", new String[]{userMail});
            cursor.moveToFirst();
            int id = cursor.getInt(0);
            return id;
        }
    }
}