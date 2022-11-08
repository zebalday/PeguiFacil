package com.asignatura.peguifacil2;

import android.app.Person;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.service.controls.actions.FloatAction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Profile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Profile extends Fragment implements View.OnClickListener {

    // DATABASE
    ArrayList<Empleo> empleos = new ArrayList<>();
    private SQLiteDatabase DB;
    private DBHelper helper;

    // UI - ELEMENTS
    TextView rut, full_name, mail, phone, birth_address, descripcion;
    ImageButton edit_profile;
    Button edit_description;
    FloatingActionButton add_job;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Profile() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Profile.
     */
    // TODO: Rename and change types and number of parameters
    public static Profile newInstance(String param1, String param2) {
        Profile fragment = new Profile();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // OBTAIN USER BUNDLE
        MenuLateral activity = (MenuLateral) getActivity();
        Bundle user_bundle = activity.getUserData();
        String user_mail = user_bundle.getString("user_mail");
        int user_type = user_bundle.getInt("user_type");

        if (user_type == 1){
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
            Cursor person = DB.rawQuery("SELECT rut, nombres, apellidos, correo, telefono, fec_nac, descripcion FROM persona WHERE correo = ?", new String[]{user_mail});
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
            Cursor person = DB.rawQuery("SELECT rut_empresa, nombre_empresa, correo, telefono, direccion FROM empresa WHERE correo = ?", new String[]{user_mail});
            person.moveToFirst();

            // FILL USER DATA
            rut.setText(person.getString(0));
            full_name.setText(person.getString(1));
            mail.setText(person.getString(2));
            phone.setText(person.getString(3));
            birth_address.setText(person.getString(4));

            // FILL ENTERPRISE JOB ADVERTISEMENTS
            init(view);

            // RETURN VIEW
            return view;
        }
    }


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



    // METHOD TO RETURN ARRAYLIST OF ELEMENTS FROM THE DATABASE
    public ArrayList<Empleo> listEmpleo(String mail){
        // OBTAIN WRITABLE DATABASE
        helper = new DBHelper(getActivity());
        DB = helper.getWritableDatabase();

        // ENTERPRISE ID
        Cursor fila = DB.rawQuery("SELECT id, nombre_empresa FROM empresa WHERE correo = ?", new String[]{mail});
        fila.moveToFirst();
        int id = fila.getInt(0);
        String nombre_empresa = fila.getString(1);

        Cursor empleos_db = DB.rawQuery("SELECT titulo, tipo_jornada, sueldo FROM empleo WHERE empresa = ?", new String[]{String.valueOf(id)});

        // ADD ALUMOS TO THE LIST
        while(empleos_db.moveToNext()){
            String titulo = empleos_db.getString(0);
            String tipo_jornada = empleos_db.getString(1);
            int sueldo = empleos_db.getInt(2);
            Empleo empleo = new Empleo(titulo, sueldo, nombre_empresa, tipo_jornada);
            empleos.add(empleo);
        }

        // RETURN ALUMNOS LIST
        return empleos;
    }

    // METHODS TO LOAD RECYCLERVIEW ADAPTER
    public void init(View view) {
        // BUNDLE
        MenuLateral activity = (MenuLateral) getActivity();
        Bundle user_bundle = activity.getUserData();
        String user_mail = user_bundle.getString("user_mail");
        empleos = listEmpleo(user_mail);

        // ALUMNOADAPTER OBJECT
        EmpleosAdapter itemsAdapter = new EmpleosAdapter(empleos, getActivity());

        // INITIALIZE RECYCLERVIEW AND LAYOUT MANAGER SETTING
        androidx.recyclerview.widget.RecyclerView recyclerView = view.findViewById(R.id.jobs_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // SETTING THE RECYCLERVIEW ADAPTER
        recyclerView.setAdapter(itemsAdapter);
    }
}