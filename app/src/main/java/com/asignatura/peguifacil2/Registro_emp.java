package com.asignatura.peguifacil2;

import androidx.appcompat.app.AppCompatActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import java.util.concurrent.TimeUnit;

public class Registro_emp extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private SQLiteDatabase DB;
    private DBHelper helper;
    private Spinner regiones;
    private EditText rut_e, nombre_e, nombre_r, correo, pass, telefono, direccion;
    private Intent login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_emp);
        getSupportActionBar().hide();


        // INITIALIZE DECLARED EDIT-TEXT
        rut_e = (EditText) findViewById(R.id.txtRutEmpresa);
        nombre_e = (EditText) findViewById(R.id.txtNombreEmpresa);
        nombre_r = (EditText) findViewById(R.id.txtNombreRepre);
        correo = (EditText) findViewById(R.id.txtCorreo);
        pass = (EditText) findViewById(R.id.txtPassword);
        telefono = (EditText) findViewById(R.id.txtTelefono);
        direccion = (EditText) findViewById(R.id.txtDireccion);

        // INITIALIZE DECLARED SPINNER
        regiones = (Spinner) findViewById(R.id.SP_regiones);

        // SET SPINNER ADAPTER
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.regiones, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        regiones.setAdapter(adapter);

        // SPECIFY INTERFACE CONNECTION
        regiones.setOnItemSelectedListener(this);
    }


    public void register(View view){
        // OBTAIN WRITABLE DATABASE
        helper = new DBHelper(this);
        DB = helper.getWritableDatabase();

        // OBTAIN VALUES FROM THE UI
        String r_e = rut_e.getText().toString().trim();
        String n_e = nombre_e.getText().toString().trim();
        String n_r = nombre_r.getText().toString().trim();
        String c = correo.getText().toString().trim();
        String p = pass.getText().toString().trim();
        String t = telefono.getText().toString().trim();
        String r = regiones.getSelectedItem().toString().trim();
        String d = direccion.getText().toString().trim();
        int ut = 2;

        // Validate if data is empty.
        if (r_e.isEmpty() || n_e.isEmpty() || n_r.isEmpty() || c.isEmpty() || p.isEmpty() || t.isEmpty() || r.isEmpty() || d.isEmpty()){
            Toast.makeText(this, "Rellene todos los campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        // CREATING NEW REGISTER
        ContentValues registro = new ContentValues();
        registro.put("rut_empresa", r_e);
        registro.put("nombre_empresa", n_e);
        registro.put("nombre_repre", n_r);
        registro.put("correo", c);
        registro.put("pass", p);
        registro.put("telefono", t);
        registro.put("region", r);
        registro.put("direccion", d);
        registro.put("tipo_usuario", ut);

        // INSERT THE NEW REGISTER TO THE DATABASE

        // First validation: Unique RUT.
        Cursor fila1 = DB.rawQuery("SELECT * FROM empresa WHERE rut_empresa = ? ", new String[] {r_e});
        if (fila1.moveToFirst()){
            Toast.makeText(this, "La Empresa ya existe en la base de datos.", Toast.LENGTH_SHORT).show();
        }
        else{
            // Second validation: Unique mail.
            if (validateUniqueEmail(DB, c)){
                Toast.makeText(this, "Este correo electrónico ya está en uso.", Toast.LENGTH_SHORT).show();
                correo.setText("");
            }
            else {
                DB.insert("empresa", null, registro);
                rut_e.setText("");
                nombre_e.setText("");
                nombre_r.setText("");
                correo.setText("");
                pass.setText("");
                telefono.setText("");
                direccion.setText("");
                regiones.setSelection(0);
                Toast.makeText(this, "Empresa registrada exitosamente.", Toast.LENGTH_SHORT).show();
                DB.close();
                redirectLogin();
            }
        }
        DB.close();
    }

    // REDIRECTS USER AFTER REGISTER SUCCEEDED
    public void redirectLogin(){
        Toast.makeText(this, "Serás redirigido a iniciar sesión.", Toast.LENGTH_SHORT).show();
        try {
            TimeUnit.SECONDS.sleep(5);
            login = new Intent(this, Login.class);
            startActivity(login);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    // CHECKS WHETHER THE EMAIL IS USED BY PERSON OR ENTERPRISE -- FALSE IF DOESN'T
    public boolean validateUniqueEmail(SQLiteDatabase DB, String mail){
        Cursor person = DB.rawQuery("SELECT * FROM persona WHERE correo = ? ", new String[]{mail});
        Cursor enterprise = DB.rawQuery("SELECT * FROM empresa WHERE correo = ?", new String[]{mail});
        if ((person.moveToFirst()) || (enterprise.moveToFirst())){
            return true;
        }
        else{
            return false;
        }
    }


    // SPINNER INTERFACE METHODS
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}