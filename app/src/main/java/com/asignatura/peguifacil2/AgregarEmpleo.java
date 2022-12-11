package com.asignatura.peguifacil2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

public class AgregarEmpleo extends AppCompatActivity {

    // DATABASE
    private SQLiteDatabase DB;
    private DBHelper helper;
    // UI
    private EditText titulo, descripcion, sueldo;
    private Spinner educacion, jornada, paga;
    private TextView vigencia;
    private Bundle user_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_empleo);
        getSupportActionBar().hide();

        // INITIALIZE UI COMPONENTS
        titulo = findViewById(R.id.txtTituloTrabajo);
        descripcion = findViewById(R.id.txtDescripcionTrabajo);
        sueldo = findViewById(R.id.txtSueldoTrabajo);
        educacion = findViewById(R.id.SP_educacionMinima);
        jornada = findViewById(R.id.SP_jornadaTrabajo);
        paga = findViewById(R.id.SP_tipoPaga);
        vigencia = findViewById(R.id.txtVigenciaAviso);

        // FILLING SPINNERS FROM DB
        fillSpinners();
    }

    // ADD NEW REGISTER
    public void addJob(View view){
        user_info = getIntent().getExtras();
        String mail = user_info.getString("user_mail");
        helper = new DBHelper(AgregarEmpleo.this);
        DB = helper.getWritableDatabase();
        Cursor enterprise = DB.rawQuery("SELECT id FROM empresa WHERE correo = ?", new String[]{mail});
        enterprise.moveToFirst();

        // FK ID's
        int en_id = enterprise.getInt(0);
        int j_id = jornada.getSelectedItemPosition() + 1;
        int p_id = paga.getSelectedItemPosition() + 1;
        int e_id = educacion.getSelectedItemPosition() + 1;

        // JOB ADVERTISEMENT INFO
        String job_title = titulo.getText().toString().trim();
        String job_desc = descripcion.getText().toString().trim();
        String salary= sueldo.getText().toString();
        String created_at = LocalDate.now().toString();
        String final_date = vigencia.getText().toString();

        // NEW REGISTER
        ContentValues registro = new ContentValues();
        registro.put("titulo", job_title);
        registro.put("descripcion", job_desc);
        registro.put("created_at", created_at);
        registro.put("empresa", en_id);
        registro.put("tipo_jornada", j_id);
        registro.put("tipo_paga", p_id);
        registro.put("tipo_educacion", e_id);

        if (job_title.isEmpty() || job_desc.isEmpty() || salary.isEmpty() || final_date.isEmpty()){
            Toast.makeText(this, "Rellene todos los campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        int job_salary = Integer.parseInt(salary);
        registro.put("sueldo", job_salary);

        DB.insert("empleo", null, registro);
        titulo.setText("");
        descripcion.setText("");
        sueldo.setText("");
        educacion.setSelection(0);
        jornada.setSelection(0);
        paga.setSelection(0);
        vigencia.setText("");
        Toast.makeText(this, "Aviso publicado exitosamente.", Toast.LENGTH_SHORT).show();
        DB.close();
    }

    // FILL SPINNERS OPTIONS FROM BD
    public void fillSpinners() {
        // OBTAIN WRITABLE DATABASE
        helper = new DBHelper(this);
        DB = helper.getWritableDatabase();

        // OBTAIN INFO
        Cursor educacion_cursor = DB.rawQuery("SELECT tipo FROM tipo_educacion", new String[]{});
        Cursor jornada_cursor = DB.rawQuery("SELECT tipo FROM tipo_jornada", new String[]{});
        Cursor paga_cursor = DB.rawQuery("SELECT tipo FROM tipo_paga", new String[]{});

        // FILL EDUCATION LEVEL SPINNER
        List<String> eduArrAd = new ArrayList<String>();
        while (educacion_cursor.moveToNext()) {
            eduArrAd.add(educacion_cursor.getString(0));
        }
        ArrayAdapter<String> edu_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, eduArrAd);
        edu_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        educacion.setAdapter(edu_adapter);

        // FILL WORKING HOURS SPINNER
        List<String> jorArrAd = new ArrayList<String>();
        while (jornada_cursor.moveToNext()) {
            jorArrAd.add(jornada_cursor.getString(0));
        }
        ArrayAdapter<String> jor_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, jorArrAd);
        jor_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        jornada.setAdapter(jor_adapter);

        // FILL WAGE SPINNER
        List<String> pagaArrAd = new ArrayList<String>();
        while (paga_cursor.moveToNext()) {
            pagaArrAd.add(paga_cursor.getString(0));
        }
        ArrayAdapter<String> paga_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, pagaArrAd);
        paga_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        paga.setAdapter(paga_adapter);

        educacion_cursor.close();
        jornada_cursor.close();
        paga_cursor.close();
    }

    // PICK DATE
    public void showDatePickerDialog(View v) {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                final String selectedDate = year + "-" + twoDigits(month + 1) + "-" + twoDigits(day);
                // JOB ADVERTISEMENT LIFE: 3 MONTHS MAX
                if ((0<getDays(year, month, day)) && (getMonths(year, month, day) <= 3)) {
                    vigencia.setText(selectedDate);
                } else {
                    Toast.makeText(AgregarEmpleo.this, "Vigencia máxima: 3 meses.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    // DATE FORMATTER
    private String twoDigits(int n) {
        return (n <= 9) ? ("0" + n) : String.valueOf(n);
    }

    // LEGAL AGE VERIFIERS
    public long getMonths(int year, int month, int day) {
        // MONTHS START AT ZERO
        month++;
        return Period.between(
                LocalDate.now(),
                LocalDate.of(year, month, day)
        ).getMonths();
    }
    public long getDays(int year, int month, int day) {
        // MONTHS START AT ZERO
        month++;
        return Period.between(
                LocalDate.now(),
                LocalDate.of(year, month, day)
        ).getDays();
    }


}