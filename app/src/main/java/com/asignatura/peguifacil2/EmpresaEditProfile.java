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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class EmpresaEditProfile extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    // DATABASE
    private SQLiteDatabase DB;
    private DBHelper helper;
    // UI
    private EditText nombre_empresa, representante, correo, pass, telefono, direccion;
    private Spinner region;
    private ArrayAdapter adapter;
    private Button saveChanges, addLocation;
    private Bundle user_info = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa_edit_profile);
        getSupportActionBar().hide();

        // BUNDLE
        user_info = getIntent().getExtras();

        // INITIALIZE UI COMPONENTS
        nombre_empresa = findViewById(R.id.txtNombreEmpresa);
        representante = findViewById(R.id.txtNombreRepre);
        correo = findViewById(R.id.txtCorreo);
        correo.setEnabled(false);
        pass = findViewById(R.id.txtPassword);
        telefono = findViewById(R.id.txtTelefono);
        region = findViewById(R.id.SP_regiones);
        direccion = findViewById(R.id.txtDireccion);
        saveChanges = findViewById(R.id.btn_save);
        addLocation = findViewById(R.id.btn_edit_location);

        // BUTTON EVENTS
        saveChanges.setOnClickListener(this);
        addLocation.setOnClickListener(this);

        // SET SPINNER ADAPTER
        adapter = ArrayAdapter.createFromResource(this, R.array.regiones, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        region.setAdapter(adapter);

        // SPECIFY INTERFACE CONNECTION
        region.setOnItemSelectedListener(this);

        // FILLING USER INFO
        fillInfo();

    }

    // FILL USER INFO METHOD
    public void fillInfo() {
        // OBTAINING INFO FROM DATABASE
        String mail = user_info.getString("user_mail");
        helper = new DBHelper(EmpresaEditProfile.this);
        DB = helper.getWritableDatabase();

        Cursor enterprise = DB.rawQuery("SELECT nombre_empresa, nombre_repre, correo, pass, telefono, region, direccion, latitud, longitud FROM empresa WHERE correo = ?", new String[]{mail});

        // FILLING USER INFO
        enterprise.moveToFirst();
        nombre_empresa.setText(enterprise.getString(0));
        representante.setText(enterprise.getString(1));
        correo.setText(enterprise.getString(2));
        pass.setText(enterprise.getString(3));
        telefono.setText(enterprise.getString(4));

        String regionSelection = enterprise.getString(5);
        int spinnerPosition = adapter.getPosition(regionSelection);
        region.setSelection(spinnerPosition);

        direccion.setText(enterprise.getString(6));
        enterprise.close();
    }

    // SAVE USER CHANGES
    public void saveChanges() {
        // OBTAIN WRITABLE DATABASE
        helper = new DBHelper(this);
        DB = helper.getWritableDatabase();

        // OBTAIN VALUES FROM THE UI
        String ne = nombre_empresa.getText().toString();
        String nr = representante.getText().toString();
        String c = correo.getText().toString();
        String p = pass.getText().toString();
        String t = telefono.getText().toString();
        String r = region.getSelectedItem().toString();
        String d = direccion.getText().toString();

        // Validate if data is empty.
        if (ne.isEmpty() || nr.isEmpty() || c.isEmpty() || p.isEmpty() || t.isEmpty() || r.isEmpty() || d.isEmpty()) {
            Toast.makeText(this, "Rellene todos los campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        // MODIFYING REGISTER
        ContentValues registro = new ContentValues();
        registro.put("nombre_empresa", ne);
        registro.put("nombre_repre", nr);
        registro.put("correo", c);
        registro.put("pass", p);
        registro.put("telefono", t);
        registro.put("region", r);
        registro.put("direccion", d);

        DB.update("empresa", registro, "correo = ?", new String[]{correo.getText().toString()});
        Toast.makeText(this, "Datos actualizados.", Toast.LENGTH_SHORT).show();
        fillInfo();
        DB.close();
    }

    // REDIRECT TO ADD ENTERPRISE LOCATION ACTIVITY

    // SPINNER INTERFACE METHODS
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    // BUTTON EVENTS
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_save:
                saveChanges();
                break;
            case R.id.btn_edit_location:
                Intent intent = new Intent(this, AgregarUbicacionEmpresa.class);
                intent.putExtra("user_mail", this.correo.getText().toString());
                startActivity(intent);
                break;
        }
    }
}