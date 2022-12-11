package com.asignatura.peguifacil2;

import android.app.Person;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.service.controls.actions.FloatAction;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;


public class Profile extends Fragment implements View.OnClickListener {

    // DATABASE
    ArrayList<Empleo> empleos = new ArrayList<>();
    private SQLiteDatabase DB;
    private DBHelper helper;
    private int userType;
    private String userMail;

    // UI - ELEMENTS
    TextView rut, full_name, mail, phone, birth_address, descripcion;
    ImageButton edit_profile;
    Button edit_description;
    FloatingActionButton add_job;
    RecyclerView empleosRecyclerView;


    public Profile() {
        // Required empty public constructor
    }

    public static Profile newInstance(String param1, String param2) {
        Profile fragment = new Profile();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        // OBTAIN USER BUNDLE
        MenuLateral activity = (MenuLateral) getActivity();
        Bundle user_bundle = activity.getUserData();
        userType = user_bundle.getInt("user_type");
        userMail = user_bundle.getString("user_mail");

        if (userType == 1){
            // OBTAINING VIEW
            View view = inflater.inflate(R.layout.fragment_profile, container, false);

            // INITIALIZING UI COMPONENTS
            rut = (TextView) view.findViewById(R.id.rut);
            full_name = (TextView) view.findViewById(R.id.nombre);
            mail = (TextView) view.findViewById(R.id.correo);
            phone = (TextView) view.findViewById(R.id.telefono);
            birth_address = (TextView) view.findViewById(R.id.birth_address);
            descripcion = (TextView) view.findViewById(R.id.descripcion);
            edit_description = (Button) view.findViewById(R.id.btn_save);
            edit_profile = (ImageButton) view.findViewById(R.id.btn_edit);

            // UPDATE BUTTON EVENTS
            edit_description.setOnClickListener(this);
            edit_profile.setOnClickListener(this);

            // OBTAIN WRITABLE DATABASE
            helper = new DBHelper(getActivity());
            DB = helper.getWritableDatabase();
            Cursor person = DB.rawQuery("SELECT rut, nombres, apellidos, correo, telefono, fec_nac, descripcion FROM persona WHERE correo = ?", new String[]{userMail});
            person.moveToFirst();

            // FILL USER DATA
            rut.setText(person.getString(0));
            String nombre_completo = person.getString(1) + " " + person.getString(2);
            full_name.setText(nombre_completo);
            mail.setText(person.getString(3));
            phone.setText(person.getString(4));
            birth_address.setText(person.getString(5));
            descripcion.setText(person.getString(6));

            // RETURN VIEW
            return view;
        }
        else{
            // OBTAINING VIEW
            View view = inflater.inflate(R.layout.fragment_profile_enterprise, container, false);

            // INITIALIZING UI COMPONENTS
            rut = view.findViewById(R.id.rut);
            full_name = view.findViewById(R.id.nombre);
            mail = view.findViewById(R.id.correo);
            phone = view.findViewById(R.id.telefono);
            birth_address = view.findViewById(R.id.birth_address);
            edit_profile = view.findViewById(R.id.btn_edit_e);
            add_job = view.findViewById(R.id.addJob);



            // BUTTON EVENTS
            edit_profile.setOnClickListener(this);
            add_job.setOnClickListener(this);

            // OBTAIN WRITABLE DATABASE
            helper = new DBHelper(getActivity());
            DB = helper.getWritableDatabase();
            Cursor enterprise = DB.rawQuery("SELECT rut_empresa, nombre_empresa, correo, telefono, direccion FROM empresa WHERE correo = ?", new String[]{userMail});
            enterprise.moveToFirst();

            // FILL USER DATA
            rut.setText(enterprise.getString(0));
            full_name.setText(enterprise.getString(1));
            mail.setText(enterprise.getString(2));
            phone.setText(enterprise.getString(3));
            birth_address.setText(enterprise.getString(4));

            // FILL ENTERPRISE JOB ADVERTISEMENTS
            initJobs(view);

            // RETURN VIEW
            return view;
        }
    }


    // UPDATE USER DESCRIPTION
    public void updateDescription(String mail){
        // DATABASE
        helper = new DBHelper(getActivity());
        DB = helper.getWritableDatabase();
        String new_desc = descripcion.getText().toString().trim();
        // SAVE DATA
        Cursor person = DB.rawQuery("UPDATE persona SET descripcion = ? WHERE correo = ? ", new String[]{new_desc, mail});
        person.moveToFirst();
        Toast.makeText(getActivity(), "Cambios guardados.", Toast.LENGTH_SHORT).show();
    }

    // BUTTON EVENTS
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_save:
                updateDescription(this.mail.getText().toString());
                break;
            case R.id.btn_edit:
                Intent edit = new Intent(getActivity(), PersonaEditProfile.class);
                edit.putExtra("user_mail", mail.getText().toString());
                startActivity(edit);
                break;
            case R.id.btn_edit_e:
                Intent edit_e = new Intent(getActivity(), EmpresaEditProfile.class);
                edit_e.putExtra("user_mail", mail.getText().toString());
                startActivity(edit_e);
                break;
            case R.id.addJob:
                Intent addJob = new Intent(getActivity(), AgregarEmpleo.class);
                addJob.putExtra("user_mail", mail.getText().toString());
                startActivity(addJob);
                break;
        }
    }

    // LOAD RECYCLERVIEW ADAPTER
    public void initJobs(View view) {
        // BUNDLE
        MenuLateral activity = (MenuLateral) getActivity();
        Bundle user_bundle = activity.getUserData();
        String user_mail = user_bundle.getString("user_mail");
        empleos = listEmpleo(user_mail);

        // JOBS ADAPTER OBJECT
        EmpleosAdapterEnterprise adapter = new EmpleosAdapterEnterprise(empleos, getActivity());

        // INITIALIZE RECYCLERVIEW AND LAYOUT MANAGER SETTING
        empleosRecyclerView = view.findViewById(R.id.jobs_recycler);
        empleosRecyclerView.setHasFixedSize(true);
        empleosRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // SETTING THE RECYCLERVIEW ADAPTER
        empleosRecyclerView.setAdapter(adapter);
    }

    // RETURN ARRAYLIST OF ELEMENTS FROM THE DATABASE
    public ArrayList<Empleo> listEmpleo(String mail){
        // OBTAIN WRITABLE DATABASE
        helper = new DBHelper(getActivity());
        DB = helper.getWritableDatabase();

        // ENTERPRISE INFO
        Cursor enterprise_info = DB.rawQuery("SELECT id, nombre_empresa FROM empresa WHERE correo = ?", new String[]{mail});
        enterprise_info.moveToFirst();
        int id = enterprise_info.getInt(0);
        String nombre_empresa = enterprise_info.getString(1);

        // ADD JOBS TO THE LIST
        Cursor empleos_db = DB.rawQuery("SELECT titulo, sueldo, tipo_jornada, created_at, id " +
                "FROM empleo " +
                "WHERE visible = ? " +
                "AND empresa = ? ",
                new String[]{String.valueOf(1), String.valueOf(id)});
        empleos.clear();
        while(empleos_db.moveToNext()){
            Empleo empleo = new Empleo();
            empleo.setTitulo(empleos_db.getString(0));
            empleo.setEmpresa(nombre_empresa);
            empleo.setSueldo(empleos_db.getInt(1));
            empleo.setJornada(tipoJornada(empleos_db.getInt(2)));
            empleo.setCreated_at(String.valueOf(vigency(empleos_db.getString(3))));
            empleo.setId(empleos_db.getInt(4));
            empleos.add(empleo);
        }

        // RETURN ALUMNOS LIST
        return empleos;
    }

    // SQL TRANSLATE
    private String tipoJornada(int tipo){
        // SQL TRANSLATE BY ID
        helper = new DBHelper(getActivity());
        DB = helper.getWritableDatabase();
        Cursor tipo_BD =  DB.rawQuery("SELECT tipo FROM tipo_jornada WHERE id = ?", new String[]{String.valueOf(tipo)});
        tipo_BD.moveToFirst();
        String nombre_tipo = tipo_BD.getString(0);
        return nombre_tipo;
    }

    public long vigency(String created_at) {
        DateTimeFormatter format = DateTimeFormatter.ISO_LOCAL_DATE;
        LocalDate startdate = LocalDate.parse(created_at, format);
        LocalDate finaldate = startdate.plusMonths(3);

        return ChronoUnit.DAYS.between(startdate, finaldate);
    }
}