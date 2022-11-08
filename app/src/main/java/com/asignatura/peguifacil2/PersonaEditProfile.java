package com.asignatura.peguifacil2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import java.time.LocalDate;
import java.time.Period;

public class PersonaEditProfile extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    // DATABASE
    private SQLiteDatabase DB;
    private DBHelper helper;
    // UI
    private EditText nombres, apellidos, correo, pass, telefono;
    private TextView fec_nac;
    private Spinner educacion;
    private Bundle user_info = new Bundle();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_persona_edit_profile);
        getSupportActionBar().hide();

        // BUNDLE
        user_info = getIntent().getExtras();

        // INITIALIZE UI COMPONENTS
        nombres = findViewById(R.id.txtNombres);
        apellidos = findViewById(R.id.txtApellidos);
        correo = findViewById(R.id.txtCorreo);
        correo.setEnabled(false);
        pass = findViewById(R.id.txtPassword);
        telefono = findViewById(R.id.txtTelefono);
        fec_nac = findViewById(R.id.txtFecha);
        educacion = findViewById(R.id.SP_educacion);

        // SET SPINNER ADAPTER
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.nivel_educativo, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        educacion.setAdapter(adapter);

        // SPECIFY INTERFACE CONNECTION
        educacion.setOnItemSelectedListener(this);

        // FILLING USER INFO
        fillInfo();

    }


    // FILL USER INFO METHOD
    public void fillInfo() {
        // OBTAINING INFO FROM DATABASE
        String mail = user_info.getString("user_mail");
        helper = new DBHelper(PersonaEditProfile.this);
        DB = helper.getWritableDatabase();

        Cursor person = DB.rawQuery("SELECT nombres, apellidos, correo, pass, telefono, fec_nac, niv_edu FROM persona WHERE correo = ?", new String[]{mail});

        // FILLING USER INFO
        person.moveToFirst();
        nombres.setText(person.getString(0));
        apellidos.setText(person.getString(1));
        correo.setText(person.getString(2));
        pass.setText(person.getString(3));
        telefono.setText(person.getString(4));
        fec_nac.setText(person.getString(5));

        // EDUCATIONAL LEVEL SWITCH SELECTION
        switch (person.getString(6)) {
            case "Cursando media":
                educacion.setSelection(0);
                break;
            case "Media completa":
                educacion.setSelection(1);
                break;
            case "Cursando superior":
                educacion.setSelection(2);
                break;
            case "Superior completa":
                educacion.setSelection(3);
                break;
        }

        person.close();
    }

    public void saveChanges(View view) {
        // OBTAIN WRITABLE DATABASE
        helper = new DBHelper(this);
        DB = helper.getWritableDatabase();

        // OBTAIN VALUES FROM THE UI
        String n = nombres.getText().toString().trim();
        String a = apellidos.getText().toString().trim();
        String c = correo.getText().toString().trim();
        String p = pass.getText().toString().trim();
        String t = telefono.getText().toString().trim();
        String f = fec_nac.getText().toString().trim();
        String e = educacion.getSelectedItem().toString();

        // Validate if data is empty.
        if (n.isEmpty() || a.isEmpty() || c.isEmpty() || p.isEmpty() || t.isEmpty() || f.isEmpty() || e.isEmpty()) {
            Toast.makeText(this, "Rellene todos los campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        // MODIFYING REGISTER
        ContentValues registro = new ContentValues();
        registro.put("nombres", n);
        registro.put("apellidos", a);
        registro.put("correo", c);
        registro.put("pass", p);
        registro.put("telefono", t);
        registro.put("fec_nac", f);
        registro.put("niv_edu", e);

        DB.update("persona", registro, "correo = ?", new String[]{correo.getText().toString()});
        Toast.makeText(this, "Datos actualizados.", Toast.LENGTH_SHORT).show();
        DB.close();
    }


    /*
    Show date-picker dialog method, associated to image-button onclick event.
    Implements it´s own listener on it´s class: DatePickerFragment
    */
    public void showDatePickerDialog(View v) {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                final String selectedDate = year + "-" + twoDigits(month + 1) + "-" + twoDigits(day);

                // LEGAL AGE TO GET A JOB VERIFICATION
                if (getAge(year, month, day) >= 15) {
                    fec_nac.setText(selectedDate);
                } else {
                    Toast.makeText(PersonaEditProfile.this, "Debes ser mayor de 15 años.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }


    /* Date formatter method */
    private String twoDigits(int n) {
        return (n <= 9) ? ("0" + n) : String.valueOf(n);
    }


    /* Legal age verifier */
    public int getAge(int year, int month, int dayOfMonth) {
        month++;
        return Period.between(
                LocalDate.of(year, month, dayOfMonth),
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