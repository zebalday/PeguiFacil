package com.asignatura.peguifacil2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class EditarEmpleo extends AppCompatActivity implements View.OnClickListener {

    // DATABASE
    private SQLiteDatabase DB;
    private DBHelper helper;

    // UI
    private EditText titulo, descripcion, sueldo;
    private Spinner educacion, jornada, paga;
    private TextView vigencia;
    private Bundle user_info;
    private Button updateJob;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_empleo);
        getSupportActionBar().hide();

        // BUNDLE
        user_info = getIntent().getExtras();

        // INITIALIZE UI COMPONENTS
        titulo = findViewById(R.id.txtTituloTrabajo);
        descripcion = findViewById(R.id.txtDescripcionTrabajo);
        sueldo = findViewById(R.id.txtSueldoTrabajo);
        educacion = findViewById(R.id.SP_educacionMinima);
        jornada = findViewById(R.id.SP_jornadaTrabajo);
        paga = findViewById(R.id.SP_tipoPaga);
        vigencia = findViewById(R.id.txtVigenciaAviso);
        updateJob = findViewById(R.id.btn_save);
        updateJob.setText("Guardar cambios");
        updateJob.setOnClickListener(this);

        findViewById(R.id.textView8).setVisibility(View.GONE);
        findViewById(R.id.txtVigenciaAviso).setVisibility(View.GONE);
        findViewById(R.id.btn_date).setVisibility(View.GONE);

        // FILLING ALL INFO FROM DB
        fillSpinners();
        fillJobInfo();
    }

    // FILL JOB INFO
    public void fillJobInfo(){
        // OBTAIN WRITABLE DATABASE
        int job_id = user_info.getInt("job_id");
        System.out.println(job_id);
        helper = new DBHelper(this);
        DB = helper.getWritableDatabase();

        // OBTAIN INFO
        Cursor empleo = DB.rawQuery("SELECT titulo, descripcion, sueldo, tipo_educacion, tipo_jornada, tipo_paga " +
                "FROM empleo " +
                "WHERE id = ?",
                new String[]{String.valueOf(job_id)});
        empleo.moveToFirst();

        String titulo_bd = empleo.getString(0);
        String descripcion_bd = empleo.getString(1);
        int sueldo_bd = empleo.getInt(2);
        int tipo_educacion_bd = empleo.getInt(3);
        int tipo_jornada_bd = empleo.getInt(4);
        int tipo_paga_bd = empleo.getInt(5);

        // FILL INFO
        titulo.setText(titulo_bd);
        descripcion.setText(descripcion_bd);
        sueldo.setText(String.valueOf(sueldo_bd));
        educacion.setSelection(tipo_educacion_bd-1);
        jornada.setSelection(tipo_jornada_bd-1);
        paga.setSelection(tipo_paga_bd-1);

        empleo.close();

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
        List<String> eduArrAd = new ArrayList<>();
        while (educacion_cursor.moveToNext()) {
            eduArrAd.add(educacion_cursor.getString(0));
        }
        ArrayAdapter<String> edu_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, eduArrAd);
        edu_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        educacion.setAdapter(edu_adapter);

        // FILL WORKING HOURS SPINNER
        List<String> jorArrAd = new ArrayList<>();
        while (jornada_cursor.moveToNext()) {
            jorArrAd.add(jornada_cursor.getString(0));
        }
        ArrayAdapter<String> jor_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, jorArrAd);
        jor_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        jornada.setAdapter(jor_adapter);

        // FILL WAGE SPINNER
        List<String> pagaArrAd = new ArrayList<>();
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

    // UPDATE JOB BUTTON EVENT
    @Override
    public void onClick(View view) {
        updateJob();
    }

    // UPDATE JOB METHOD
    public void updateJob(){
        int job_id = user_info.getInt("job_id");
        helper = new DBHelper(EditarEmpleo.this);
        DB = helper.getWritableDatabase();

        // FK ID's
        int educacion_id = educacion.getSelectedItemPosition() + 1;
        int jornada_id = jornada.getSelectedItemPosition() + 1;
        int paga_id = paga.getSelectedItemPosition() + 1;

        // JOB ADVERTISEMENT INFO
        String job_title = titulo.getText().toString().trim();
        String job_desc = descripcion.getText().toString().trim();
        String salary= sueldo.getText().toString();

        // NEW REGISTER
        ContentValues registro = new ContentValues();
        registro.put("titulo", job_title);
        registro.put("descripcion", job_desc);
        int job_salary = Integer.parseInt(salary);
        registro.put("sueldo", job_salary);
        registro.put("tipo_educacion", educacion_id);
        registro.put("tipo_jornada", jornada_id);
        registro.put("tipo_paga", paga_id);

        // VALIDATE
        if (job_title.isEmpty() || job_desc.isEmpty() || salary.isEmpty()){
            Toast.makeText(this, "Rellene todos los campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        DB.update("empleo", registro, "id = ?", new String[]{String.valueOf(job_id)});

        Toast.makeText(this, "Aviso actualizado extiosamente", Toast.LENGTH_SHORT).show();
        DB.close();
    }
}