package com.asignatura.peguifacil2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends AppCompatActivity {

    private SQLiteDatabase DB;
    private DBHelper helper;
    private Intent login, register;
    private EditText mail, pass;
    private Button btn_login;
    private Bundle unique_user_mail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // INITIALIZE DECLARED EDIT-TEXT
        mail = (EditText) findViewById(R.id.txtCorreo);
        pass = (EditText) findViewById(R.id.txtPassword);

        // INITIALIZE DECLARED BUTTON
        btn_login = (Button) findViewById(R.id.btn_login);

        getSupportActionBar().hide();
    }

    // REGISTER BUTTON EVENT
    public void choiceUserType(View view) {
        register = new Intent(this, User_choice.class);
        startActivity(register);
    }

    // LOGIN BUTTON EVENT
    public void loginValidate(View view){
        // OBTAIN WRITABLE DATABASE
        helper = new DBHelper(this);
        DB = helper.getWritableDatabase();

        // OBTAIN VALUES FROM THE UI
        String m = mail.getText().toString().trim();
        String p = pass.getText().toString().trim();

        // Validate if data is empty.
        if (m.isEmpty() || p.isEmpty() ){
            Toast.makeText(this, "Ingrese credenciales", Toast.LENGTH_SHORT).show();
            return;
        }

        // ACCESSING SYSTEM:

        // First validation: Existing user mail.
        int type = userTypeValidation(m);

        // NO TYPE
        if (type == 0) {
            Toast.makeText(this, "Este correo no está registrado.", Toast.LENGTH_SHORT).show();
        }

        // TYPE PERSON
        if (type == 1){
            // Second validation: Correct password.
            Cursor fila_1 = DB.rawQuery("SELECT pass FROM persona WHERE correo = ? ", new String[] {m});
            fila_1.moveToFirst();
            String password = fila_1.getString(0);
            if (password.equals(p)){
                // ACCESS GRANTED
                login(m, 1);
            }
            else{
                Toast.makeText(this, "Contraseña incorrecta.", Toast.LENGTH_SHORT).show();
                pass.setText("");
            }
        }

        // TYPE ENTERPRISE
        if (type == 2){
            // Second validation: Correct password.
            Cursor fila_2 = DB.rawQuery("SELECT pass FROM empresa WHERE correo = ? ", new String[] {m});
            fila_2.moveToFirst();
            String password_e = fila_2.getString(0);
            if (password_e.equals(p)){
                // ACCESS GRANTED
                login(m, 2);
            }
            else{
                Toast.makeText(this, "Contraseña incorrecta.", Toast.LENGTH_SHORT).show();
                pass.setText("");
            }
        }
    }

    // ACCESS TO THE APP
    public void login(String mail, int type){
        login = new Intent(this, MenuLateral.class);
        login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        login.putExtra("user_mail", mail);
        login.putExtra("user_type", type);
        startActivity(login);
        finish();
    }

    // RETURNS USER TYPE
    public int userTypeValidation(String mail){
        // OBTAIN WRITABLE DATABASE
        helper = new DBHelper(this);
        DB = helper.getWritableDatabase();

        // CHECK IF EMAIL IS IN PERSON TABLE
        Cursor fila = DB.rawQuery("SELECT * FROM persona WHERE correo = ?", new String[]{mail});
        if (fila.moveToFirst()){return 1;}

        // CHECK IF EMAIL IS IN ENTERPRISE TABLE
        fila = DB.rawQuery("SELECT * FROM empresa WHERE correo = ?", new String[]{mail});
        if (fila.moveToFirst()){return 2;}

        // REGISTER DOESN´T EXISTS
        return 0;
    }

}