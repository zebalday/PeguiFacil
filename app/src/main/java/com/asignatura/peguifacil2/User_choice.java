package com.asignatura.peguifacil2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class User_choice extends AppCompatActivity {

    Intent person, enterprise;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_choice);
        getSupportActionBar().hide();

    }

    public void personRegister(View view){
        person = new Intent(this, Registro_usu.class);
        startActivity(person);
    }
    public void enterpriseRegister(View view){
        enterprise = new Intent(this, Registro_emp.class);
        startActivity(enterprise);
    }

}