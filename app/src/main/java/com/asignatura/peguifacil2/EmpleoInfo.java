package com.asignatura.peguifacil2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDate;

public class EmpleoInfo extends AppCompatActivity implements View.OnClickListener {

    // DATABASE
    private SQLiteDatabase DB;
    private DBHelper helper;

    // BUNDLE
    String userMail;
    int userType;
    int userID;
    int jobID;

    // UI COMPONENTS
    TextView titulo, descripcion, empresa, sueldo, tipo_educacion, tipo_jornada, tipo_paga;
    Button postular;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empleo_info);

        getSupportActionBar().hide();

        // INIT BUNDLE
        userMail = String.valueOf(getIntent().getExtras().getString("user_mail"));
        userType = (int) getIntent().getExtras().getInt("user_type");
        userID = userID(userType);
        jobID = (int) getIntent().getExtras().getInt("job_id");

        // INIT UI
        titulo = findViewById(R.id.txtTituloTrabajo);
        descripcion = findViewById(R.id.txtDescripcionTrabajo);
        empresa = findViewById(R.id.txtNombreEmpresa);
        sueldo = findViewById(R.id.txtSueldo);
        tipo_educacion = findViewById(R.id.txtTipoEducacion);
        tipo_jornada = findViewById(R.id.txtTipoJornada);
        tipo_paga = findViewById(R.id.txtTipoPaga);
        postular = findViewById(R.id.btn_postular);

        postular.setOnClickListener(this);

        if (getIntent().getExtras().getString("origin") == "Postulaciones"){
            postular.setVisibility(View.GONE);
        }
        else{
            postular.setVisibility(View.VISIBLE);
        }

        if (userType == 2){
            postular.setVisibility(View.GONE);
        }

        // FILL JOB INFO
        fillInfo();
    }

    private void fillInfo() {
        // OBTAIN WRITABLE DATABASE
        helper = new DBHelper(this);
        DB = helper.getWritableDatabase();

        Cursor empleo = DB.rawQuery("SELECT titulo, descripcion, empresa, sueldo, tipo_educacion, tipo_jornada, tipo_paga" +
                " FROM empleo WHERE id = ?", new String[]{String.valueOf(jobID)});

        empleo.moveToFirst();

        titulo.setText(empleo.getString(0));
        titulo.setEnabled(false);

        descripcion.setText(empleo.getString(1));
        descripcion.setEnabled(false);;

        empresa.setText("Empresa: " + nombreEmpresa(empleo.getInt(2)));
        sueldo.setText("Sueldo: " + empleo.getInt(3));
        tipo_educacion.setText("Requiere: "+tipoEducacion(empleo.getInt(4)));
        tipo_jornada.setText("Horario: "+ tipoJornada(empleo.getInt(5)));
        tipo_paga.setText("Paga: "+ tipoPaga(empleo.getInt(6)));

    }

    private String nombreEmpresa(int ID){
        // SQL TRANSLATE BY ID
        helper = new DBHelper(this);
        DB = helper.getWritableDatabase();
        Cursor cursor = DB.rawQuery("SELECT nombre_empresa FROM empresa WHERE id = ?", new String[]{String.valueOf(ID)});
        cursor.moveToFirst();
        String nombre = cursor.getString(0);
        cursor.close();
        return nombre;
    }

    private String tipoJornada(int ID){
        // SQL TRANSLATE BY ID
        helper = new DBHelper(this);
        DB = helper.getWritableDatabase();
        Cursor cursor = DB.rawQuery("SELECT tipo FROM tipo_jornada WHERE id = ?", new String[]{String.valueOf(ID)});
        cursor.moveToFirst();
        String tipo = cursor.getString(0);
        cursor.close();
        return tipo;
    }

    private String tipoPaga(int ID){
        // SQL TRANSLATE BY ID
        helper = new DBHelper(this);
        DB = helper.getWritableDatabase();
        Cursor cursor = DB.rawQuery("SELECT tipo FROM tipo_paga WHERE id = ?", new String[]{String.valueOf(ID)});
        cursor.moveToFirst();
        String tipo = cursor.getString(0);
        cursor.close();
        return tipo;
    }

    private String tipoEducacion(int ID){
        // SQL TRANSLATE BY ID
        helper = new DBHelper(this);
        DB = helper.getWritableDatabase();
        Cursor cursor = DB.rawQuery("SELECT tipo FROM tipo_educacion WHERE id = ?", new String[]{String.valueOf(ID)});
        cursor.moveToFirst();
        String tipo = cursor.getString(0);
        cursor.close();
        return tipo;
    }

    private int userID(int type){
        // SQL TRANSLATE BY ID
        helper = new DBHelper(this);
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

    // APPLY TO JOB BUTTON EVENT
    @Override
    public void onClick(View view) {
        // SQL TRANSLATE BY ID
        helper = new DBHelper(this);
        DB = helper.getWritableDatabase();

        Cursor prevApply = DB.rawQuery("SELECT * FROM persona_empleo " +
                "WHERE persona = ? " +
                "AND empleo = ?",
                new String[]{String.valueOf(userID), String.valueOf(jobID)});

        if (prevApply.moveToFirst()){
            Toast.makeText(this, "Ya has postulado a este empleo.", Toast.LENGTH_SHORT).show();
        }
        else{
            ContentValues registro = new ContentValues();
            registro.put("persona", userID);
            registro.put("empleo", jobID);
            String fecha_postulacion = LocalDate.now().toString();
            registro.put("fec_postulacion", fecha_postulacion);
            DB.insert("persona_empleo", null, registro);
            Toast.makeText(this, "Postulación exitosa!!", Toast.LENGTH_SHORT).show();
        }
    }
}
