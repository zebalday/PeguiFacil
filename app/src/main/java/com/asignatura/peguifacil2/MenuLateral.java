package com.asignatura.peguifacil2;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.asignatura.peguifacil2.databinding.ActivityMenuLateralBinding;
import com.google.android.material.navigation.NavigationView;

public class MenuLateral extends AppCompatActivity {

    private Bundle user_bundle;
    private SQLiteDatabase DB;
    private DBHelper helper;
    private AppBarConfiguration mAppBarConfiguration;
    // Important!!
    private ActivityMenuLateralBinding binding;
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private AlertDialog.Builder logoutBuilder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMenuLateralBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // UI - Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); // Setup toolbar.


        // UI - Navigation view
        navigationView = binding.navView;
        updateNavHeader(navigationView);
        navigationView.setItemIconTintList(null);


        // UI - Drawer menu:
        //      Passing each menu ID as a set of Ids because each
        drawer = binding.drawerLayout;
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_profile,
                R.id.nav_map,
                R.id.nav_search,
                R.id.nav_applications,
                R.id.nav_logout
        )
                .setOpenableLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_menu_lateral);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        setTitle("PeguiFácil");

        // For logout.
         logoutBuilder = new AlertDialog.Builder(this);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lateral, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_menu_lateral);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }



    /*
    * THIS METHOD SETS THE USER DATA TO THE NAVIGATION DRAWER HEADER
    * It is called when after the NavigationView is initialized.
    * */
    public void updateNavHeader(NavigationView nav){
        // OBTAIN WRITABLE DATABASE
        helper = new DBHelper(this);
        DB = helper.getWritableDatabase();

        // OBTAIN NAVIGATION DRAWER HEADER
        View headerView = nav.getHeaderView(0);

        // OBTAIN HEADER ELEMENTS
        TextView user_name = (TextView) headerView.findViewById(R.id.nav_user_name);
        TextView user_mail = (TextView) headerView.findViewById(R.id.nav_user_mail);
        ImageView user_photo = (ImageView) headerView.findViewById(R.id.nav_user_photo);

        // OBTAIN USER DATA BY ACTIVITY BUNDLE
        user_bundle = getIntent().getExtras();
        String mail = user_bundle.getString("user_mail");
        int type = user_bundle.getInt("user_type");

        if (type == 1){
            Cursor fila = DB.rawQuery("SELECT nombres, apellidos FROM persona WHERE correo = ? ", new String[] {mail});
            fila.moveToFirst();
            String nombres = fila.getString(0);
            String apellidos = fila.getString(1);
            String full_name = nombres + " " + apellidos;

            // SETTING USER INFO INTO HEADER
            user_name.setText(full_name);
            user_mail.setText(mail);
            user_photo.setImageResource(R.drawable.person);

        }
        if (type ==2) {
            Cursor fila = DB.rawQuery("SELECT nombre_empresa FROM empresa WHERE correo = ? ", new String[] {mail});
            fila.moveToFirst();
            String nombre = fila.getString(0);

            // SETTING USER INFO INTO HEADER
            user_name.setText(nombre);
            user_mail.setText(mail);
            user_photo.setImageResource(R.drawable.enterprise);
        }
    }

    /*
    * Returns user Bundle to fill the profile user info from the Profile Fragment.
    * */
    public Bundle getUserData(){
        user_bundle = getIntent().getExtras();
        return user_bundle;
    }

    public void logout(MenuItem item){

        // CONFIRM
        logoutBuilder.setTitle("Confirme");
        logoutBuilder.setMessage("¿Está seguro que desea salir?");

        logoutBuilder.setPositiveButton("SÍ", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent login = new Intent(getBaseContext(), Login.class);
                login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(login);
                finish();
                dialog.dismiss();
            }
        });
        logoutBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
                dialog.dismiss();
            }
        });

        AlertDialog alert = logoutBuilder.create();
        alert.show();

    }

}