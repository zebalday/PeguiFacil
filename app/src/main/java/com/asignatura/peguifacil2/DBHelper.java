package com.asignatura.peguifacil2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "peguifacil.sqlite";
    private static final int DB_SCHEME_VERSION = 1;


    // Constructor
    public DBHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_SCHEME_VERSION);
    }

    // On create method. "IF NOT EXISTS" is used as SQL constraint.
    @Override
    public void onCreate(SQLiteDatabase DB) {

        // User Type table.
        DB.execSQL("CREATE TABLE IF NOT EXISTS\"tipo_usuario\" (\n" +
                "\t\"id\"\tINTEGER,\n" +
                "\t\"tipo\"\tTEXT(50) NOT NULL,\n" +
                "\tPRIMARY KEY(\"id\")\n" +
                ");");

        // Salary Type table.
        DB.execSQL("CREATE TABLE IF NOT EXISTS \"tipo_paga\" (\n" +
                "\t\"id\"\tINTEGER,\n" +
                "\t\"tipo\"\tTEXT(50) NOT NULL,\n" +
                "\tPRIMARY KEY(\"id\")\n" +
                ");");

        // Working day hours type.
        DB.execSQL("CREATE TABLE IF NOT EXISTS \"tipo_jornada\" (\n" +
                "\t\"id\"\tINTEGER,\n" +
                "\t\"tipo\"\tTEXT(50) NOT NULL,\n" +
                "\tPRIMARY KEY(\"id\")\n" +
                ");");

        // Job category table.
        DB.execSQL("CREATE TABLE IF NOT EXISTS \"tipo_educacion\" (\n" +
                "\t\"id\"\tINTEGER,\n" +
                "\t\"tipo\"\tTEXT(100) NOT NULL,\n" +
                "\tPRIMARY KEY(\"id\")\n" +
                ");");

        // Person table.
        DB.execSQL("CREATE TABLE IF NOT EXISTS \"persona\" (\n" +
                "\t\"id\"\tINTEGER,\n" +
                "\t\"rut\"\tTEXT(12) NOT NULL UNIQUE,\n" +
                "\t\"nombres\"\tTEXT(100) NOT NULL,\n" +
                "\t\"apellidos\"\tTEXT(100) NOT NULL,\n" +
                "\t\"correo\"\tTEXT(100) NOT NULL UNIQUE,\n" +
                "\t\"pass\"\tNUMERIC NOT NULL,\n" +
                "\t\"telefono\"\tTEXT(12) NOT NULL,\n" +
                "\t\"fec_nac\"\tTEXT NOT NULL,\n" +
                "\t\"niv_edu\"\tTEXT(100) NOT NULL,\n" +
                "\t\"descripcion\" TEXT(200),\n" +
                "\t\"tipo_usuario\"\tINTEGER NOT NULL,\n" +
                "\tFOREIGN KEY(\"tipo_usuario\") REFERENCES \"tipo_usuario\"(\"id\") ON DELETE CASCADE,\n" +
                "\tPRIMARY KEY(\"id\")\n" +
                ");");

        // Enterprise table.
        DB.execSQL("CREATE TABLE IF NOT EXISTS \"empresa\" (\n" +
                "\t\"id\"\tINTEGER,\n" +
                "\t\"rut_empresa\"\tTEXT(13) NOT NULL UNIQUE,\n" +
                "\t\"nombre_empresa\"\tTEXT(100) NOT NULL,\n" +
                "\t\"nombre_repre\"\tTEXT(150) NOT NULL,\n" +
                "\t\"correo\"\tTEXT(150) NOT NULL UNIQUE,\n" +
                "\t\"pass\"\tTEXT(50) NOT NULL,\n" +
                "\t\"telefono\"\tTEXT(12) NOT NULL,\n" +
                "\t\"region\"\tTEXT(50) NOT NULL,\n" +
                "\t\"direccion\"\tTEXT(100) NOT NULL,\n" +
                "\t\"latitud\"\tREAL,\n" +
                "\t\"longitud\"\tREAL,\n" +
                "\t\"tipo_usuario\"\tINTEGER NOT NULL,\n" +
                "\tPRIMARY KEY(\"id\")\n" +
                ");");

        // Job advertisement table.
        DB.execSQL("CREATE TABLE IF NOT EXISTS \"empleo\" (\n" +
                "\t\"id\"\tINTEGER,\n" +
                "\t\"titulo\"\tTEXT(50) NOT NULL,\n" +
                "\t\"descripcion\"\tTEXT NOT NULL,\n" +
                "\t\"sueldo\"\tINTEGER NOT NULL,\n" +
                "\t\"created_at\"\tTEXT NOT NULL,\n" +
                "\t\"empresa\"\tINTEGER NOT NULL,\n" +
                "\t\"tipo_jornada\"\tINTEGER NOT NULL,\n" +
                "\t\"tipo_paga\"\tINTEGER NOT NULL,\n" +
                "\t\"tipo_educacion\"\tINTEGER NOT NULL,\n" +
                "\t\"visible\"\tINTEGER NOT NULL DEFAULT(1),\n" +
                "\tFOREIGN KEY(\"tipo_jornada\") REFERENCES \"tipo_jornada\"(\"id\") ON DELETE CASCADE,\n" +
                "\tFOREIGN KEY(\"empresa\") REFERENCES \"empresa\"(\"id\") ON DELETE CASCADE,\n" +
                "\tFOREIGN KEY(\"tipo_paga\") REFERENCES \"tipo_paga\"(\"id\") ON DELETE CASCADE,\n" +
                "\tFOREIGN KEY(\"tipo_educacion\") REFERENCES \"tipo_educacion\"(\"id\") ON DELETE CASCADE,\n" +
                "\tPRIMARY KEY(\"id\")\n" +
                ");");

        // Job applications table.
        DB.execSQL("CREATE TABLE IF NOT EXISTS \"persona_empleo\" (\n" +
                "\t\"persona\"\tINTEGER NOT NULL,\n" +
                "\t\"empleo\"\tINTEGER NOT NULL,\n" +
                "\t\"fec_postulacion\"\tTEXT NOT NULL,\n" +
                "\tFOREIGN KEY(\"persona\") REFERENCES \"persona\"(\"id\") ON DELETE CASCADE,\n" +
                "\tFOREIGN KEY(\"empleo\") REFERENCES \"empleo\"(\"id\") ON DELETE CASCADE\n" +
                ");");


        // ADDING SOME IMPORTANT DATA.
        DB.execSQL("INSERT INTO tipo_usuario VALUES (1, \"Persona\");");
        DB.execSQL("INSERT INTO tipo_usuario VALUES (2, \"Empresa\");");


        // ADDING SOME IMPORTANT DATA.
        DB.execSQL("INSERT INTO tipo_educacion VALUES (1, \"Media incompleta\");");
        DB.execSQL("INSERT INTO tipo_educacion VALUES (2, \"Media completa\");");
        DB.execSQL("INSERT INTO tipo_educacion VALUES (3, \"Superior incompleta\");");
        DB.execSQL("INSERT INTO tipo_educacion VALUES (4, \"Superior completa\");");

        // ADDING SOME IMPORTANT DATA.
        DB.execSQL("INSERT INTO tipo_paga VALUES (1, \"Diaria\");");
        DB.execSQL("INSERT INTO tipo_paga VALUES (2, \"Semanal\");");
        DB.execSQL("INSERT INTO tipo_paga VALUES (3, \"Mensual\");");

        // ADDING SOME IMPORTANT DATA.
        DB.execSQL("INSERT INTO tipo_jornada VALUES (1, \"Full time - 45 hrs.\");");
        DB.execSQL("INSERT INTO tipo_jornada VALUES (2, \"Part time - 30 hrs.\");");
        DB.execSQL("INSERT INTO tipo_jornada VALUES (3, \"Part time - 20 hrs.\");");
    }

    // On upgrade method. Only executes when database is modified.
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }
}
