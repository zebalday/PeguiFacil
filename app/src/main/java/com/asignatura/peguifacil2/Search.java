package com.asignatura.peguifacil2;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;


public class Search extends Fragment {

    // DATABASE
    private SQLiteDatabase DB;
    private DBHelper helper;
    private String userMail;
    private int userType;

    // ADAPTER
    private EmpleosAdapter adapter;
    private ArrayList<Empleo> empleosList;

    // UI
    private RecyclerView empleosRecyclerView;


    public Search() {}
    public static Search newInstance(String param1, String param2) {
        Search fragment = new Search();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // VIEW
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        userMail = getActivity().getIntent().getExtras().getString("user_mail");
        userType = getActivity().getIntent().getExtras().getInt("user_type");

        // INIT RECYCLERVIEW
        buildRecyclerView(view);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {

        menuInflater.inflate(R.menu.search_menu, menu);
        super.onCreateOptionsMenu(menu, menuInflater);

        // MenuInflater inflater = requireActivity().getMenuInflater();

        MenuItem searchItem = menu.findItem(R.id.searchBar);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return false;
            }
        });
    }


    private void filter(String text) {

        // NEW JOBS ARRAYLIST --> AFTER FILTER
        ArrayList<Empleo> filteredlist = new ArrayList<Empleo>();

        // COMPARE INITIAL ELEMENTS WITH THE USER QUERY
        for (Empleo emepleo : empleosList) {
            if (emepleo.getTitulo().toLowerCase().contains(text.toLowerCase())) {
                filteredlist.add(emepleo);
            }
        }
        // NOTHING FOUND
        if (filteredlist.isEmpty()) {
            Toast.makeText(getContext(), "Sin resultados...", Toast.LENGTH_SHORT).show();
        }
        // SHOW RESULTS
        else {
            adapter.filterList(filteredlist);
        }
    }


    private void buildRecyclerView(View view) {
        // GETTING JOBS FROM THE BATABASE
        helper = new DBHelper(getActivity());
        DB = helper.getWritableDatabase();
        Cursor empleos_DB = DB.rawQuery("SELECT titulo, empresa, sueldo, tipo_jornada, created_at, id " +
                        "FROM empleo " +
                        "WHERE visible = ?",
                new String[]{String.valueOf(1)});

        // FILLING JOBS(CLASS) ARRAYLIST
        empleosList = new ArrayList<>();
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

        empleos_DB.close();

        // SETTING THE ADAPTER
        adapter = new EmpleosAdapter(empleosList, getContext());
        adapter.setUserMail(userMail);
        adapter.setUserType(userType);

        //  ADAPTER --> RECYCLER
        empleosRecyclerView = view.findViewById(R.id.jobs_recycler);
        empleosRecyclerView.setAdapter(adapter);
        empleosRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        empleosRecyclerView.setHasFixedSize(true);
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
}