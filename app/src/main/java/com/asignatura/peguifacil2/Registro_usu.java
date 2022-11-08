package com.asignatura.peguifacil2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.biometrics.BiometricManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.type.DateTime;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.concurrent.TimeUnit;

public class Registro_usu extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private SQLiteDatabase DB;
    private DBHelper helper;
    private TextView fec_nac;
    private EditText rut, nombres, apellidos, correo, pass, telefono;
    private Spinner educacion;
    private Button register;
    private Intent login;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        getSupportActionBar().hide();


        // INITIALIZE REGISTER FORM COMPONENTS
        rut = findViewById(R.id.txtRut);
        nombres = findViewById(R.id.txtNombres);
        apellidos = findViewById(R.id.txtApellidos);
        correo = findViewById(R.id.txtCorreo);
        pass = findViewById(R.id.txtPassword);
        telefono = findViewById(R.id.txtTelefono);
        fec_nac = findViewById(R.id.txtFecha);

        // INITIALIZE REGISTER BUTTON
        register = findViewById(R.id.btn_register);

        // INITIALIZE DECLARED SPINNER
        educacion = findViewById(R.id.SP_educacion);

        // SET SPINNER ADAPTER
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.nivel_educativo, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        educacion.setAdapter(adapter);

        // SPECIFY INTERFACE CONNECTION
        educacion.setOnItemSelectedListener(this);

    }


    public void register(View view){
        // OBTAIN WRITABLE DATABASE
        helper = new DBHelper(this);
        DB = helper.getWritableDatabase();

        // OBTAIN VALUES FROM THE UI
        String r = rut.getText().toString().trim();
        String n = nombres.getText().toString().trim();
        String a = apellidos.getText().toString().trim();
        String c = correo.getText().toString().trim();
        String p = pass.getText().toString().trim();
        String t = telefono.getText().toString().trim();
        String f = fec_nac.getText().toString().trim();
        String e = educacion.getSelectedItem().toString();
        int ut = 1;

        // Validate if data is empty.
        if (r.isEmpty() || n.isEmpty() || a.isEmpty() || c.isEmpty() || p.isEmpty() || t.isEmpty() || f.isEmpty() || e.isEmpty() ){
            Toast.makeText(this, "Rellene todos los campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        // CREATING NEW REGISTER
        ContentValues registro = new ContentValues();
        registro.put("rut", r);
        registro.put("nombres", n);
        registro.put("apellidos", a);
        registro.put("correo", c);
        registro.put("pass", p);
        registro.put("telefono", t);
        registro.put("fec_nac", f);
        registro.put("niv_edu", e);
        registro.put("tipo_usuario", ut);

        // INSERT THE NEW REGISTER TO THE DATABASE

        // First validation: Unique RUT.
        Cursor fila1 = DB.rawQuery("SELECT * FROM persona WHERE rut = ? ", new String[] {r});
        if (fila1.moveToFirst()){
            Toast.makeText(this, "El usuario ya existe en la base de datos.", Toast.LENGTH_SHORT).show();
        }
        else{
            // Second validation: Unique mail.
            if (validateUniqueEmail(DB, c)){
                Toast.makeText(this, "Este correo electrónico ya está en uso.", Toast.LENGTH_SHORT).show();
                correo.setText("");
            }
            else {
                DB.insert("persona", null, registro);
                rut.setText("");
                nombres.setText("");
                apellidos.setText("");
                correo.setText("");
                pass.setText("");
                telefono.setText("");
                fec_nac.setText("");
                Toast.makeText(this, "Usuario registrado exitosamente.", Toast.LENGTH_SHORT).show();
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

    /*
        Show date-picker dialog method, associated to image-button onclick event.
        Implements it´s own listener on it´s class: DatePickerFragment
    */
    public void showDatePickerDialog(View v) {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                final String selectedDate = year + "-" + twoDigits(month+1) + "-" + twoDigits(day);

                // LEGAL AGE TO GET A JOB VERIFICATION
                if (getAge(year, month, day) >= 15){
                    fec_nac.setText(selectedDate);
                }
                else{
                    Toast.makeText(Registro_usu.this, "Debes ser mayor de 15 años.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }


    /* Date formatter method */
    private String twoDigits(int n) {
        return (n<=9) ? ("0"+n) : String.valueOf(n);
    }


    /* Legal age verifier */
    public long getAge(int year, int month, int day) {
        // MONTHS START AT ZERO
        month++;
        return Period.between(
                LocalDate.of(year, month, day),
                LocalDate.now()
        ).getYears();
    }


    // SPINNER INTERFACE METHODS
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}






