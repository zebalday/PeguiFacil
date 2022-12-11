package com.asignatura.peguifacil2;

import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.location.Geocoder;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class AgregarUbicacionEmpresa extends AppCompatActivity
        implements OnMapReadyCallback, View.OnClickListener {


    // DATABASE
    private SQLiteDatabase DB;
    private DBHelper helper;
    double latitud, longitud;
    String userMail;

    // UI
    TextView latitudTextView, longitudTextView, direccion;
    Button saveLocation;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_ubicacion_empresa);
        getSupportActionBar().hide();

        // UI
        latitudTextView = findViewById(R.id.user_latitude);
        longitudTextView = findViewById(R.id.user_altitude);
        direccion = findViewById(R.id.marker_direccion);
        saveLocation = findViewById(R.id.btn_save_location);
        saveLocation.setOnClickListener(this);

        // DATABASE
        userMail = getIntent().getExtras().getString("user_mail");
        System.out.println(userMail);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLocation();
    }

    // OBTAIN CURRENT USER LOCATION
    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            if (location != null) {
                currentLocation = location;
                SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView);
                assert supportMapFragment != null;
                supportMapFragment.getMapAsync(AgregarUbicacionEmpresa.this);
            }
        });
    }

    // MAP READY EVENTS --> CONTAINS ON LONG CLICK EVENT
    @Override
    public void onMapReady(GoogleMap googleMap) {

        if (hasStoredLocation()){
            LatLng latLng = new LatLng(latitud, longitud);

            // SETTING COORDINATE VALUES
            latitudTextView.setText("Latitud: " + (latitud));
            longitudTextView.setText("Longitud: " + (longitud));

            // ADDING MARKER
            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Ubicación actual");
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
            googleMap.addMarker(markerOptions);

            // SHOWING SELECTED ADDRESS
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());

            try {
                addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                String address = addresses.get(0).getAddressLine(0);
                direccion.setText(address);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            LatLng latLng1 = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

            // SETTING COORDINATE VALUES
            latitudTextView.setText("Latitud: " + (currentLocation.getLatitude()));
            longitudTextView.setText("Longitud: " + (currentLocation.getLongitude()));
            latitud = currentLocation.getLatitude();
            longitud = currentLocation.getLongitude();

            // ADDING MARKER
            MarkerOptions markerOptions = new MarkerOptions().position(latLng1).title("Ubicación actual");
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng1));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng1, 18));
            googleMap.addMarker(markerOptions);

            // SHOWING SELECTED ADDRESS
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());

            try {
                addresses = geocoder.getFromLocation(latLng1.latitude, latLng1.longitude, 1);
                String address = addresses.get(0).getAddressLine(0);
                direccion.setText(address);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        // REPEAT PROCESS
        googleMap.setOnMapLongClickListener(latLng2 -> {

            googleMap.clear();

            latitudTextView.setText("Latitud: " + (latLng2.latitude));
            longitudTextView.setText("Longitud: " + (latLng2.longitude));
            latitud = latLng2.latitude;
            longitud = latLng2.longitude;

            MarkerOptions newMarker = new MarkerOptions().position(latLng2).title("Ubicación seleccionada");
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng2));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng2, 18));
            googleMap.addMarker(newMarker);

            Geocoder geocoder2;
            List<Address> addresses2;
            geocoder2 = new Geocoder(this, Locale.getDefault());

            try {
                addresses2 = geocoder2.getFromLocation(latLng2.latitude, latLng2.longitude, 1);
                String address = addresses2.get(0).getAddressLine(0);
                direccion.setText(address);

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    // REQUEST PERMISSIONS
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLocation();
            }
        }
    }

    // SAVE LOCATION TO DATABASE
    @Override
    public void onClick(View view) {
        // OBTAIN WRITABLE DATABASE
        helper = new DBHelper(this);
        DB = helper.getWritableDatabase();

        // SAVE SELECTED LOCATION
        ContentValues coordenadas = new ContentValues();
        coordenadas.put("latitud", latitud);
        coordenadas.put("longitud", longitud);
        DB.update("empresa", coordenadas, "correo = ?", new String[]{userMail});
        Toast.makeText(this, "Ubicación actualizada.", Toast.LENGTH_SHORT).show();
    }

    // CHECK ID ENTERPRISE HASE ALREADY A STORED LOCATION
    public boolean hasStoredLocation(){
        // OBTAIN WRITABLE DATABASE
        helper = new DBHelper(this);
        DB = helper.getWritableDatabase();

        // CHECK IF ENTERPRISE HAS STORED LOCATION
        Cursor coordenadas = DB.rawQuery("SELECT latitud, longitud FROM empresa WHERE correo = ?", new String[]{userMail});
        coordenadas.moveToFirst();
        double latitud_db = coordenadas.getDouble(0);
        double longitud_db = coordenadas.getDouble(1);

        if (latitud_db == 0 || longitud_db == 0){
            return false;
        }
        else{
            latitud = latitud_db;
            longitud = longitud_db;
            return true;
        }
    }
}