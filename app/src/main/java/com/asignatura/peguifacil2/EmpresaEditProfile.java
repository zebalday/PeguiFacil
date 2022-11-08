package com.asignatura.peguifacil2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class EmpresaEditProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa_edit_profile);
        getSupportActionBar().hide();
    }
}