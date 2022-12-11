package com.asignatura.peguifacil2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class Map extends Fragment
        implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    // DATABASE
    private SQLiteDatabase DB;
    private DBHelper helper;
    private String user_mail;
    private int user_type;

    // ADAPTER
    private EmpleosAdapter adapter;
    private ArrayList<Empleo> empleosList;

    // UI
    RecyclerView empleosRecyclerView;
    SupportMapFragment supportMapFragment;
    GoogleMap googlemap;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;

    // MAPS
    LocationManager locationManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapView);

        // INIT LOCATION CLIENT AND GETS CURRENT LOCATION
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        fetchLocation();
        user_mail = getActivity().getIntent().getExtras().getString("user_mail");
        user_type = getActivity().getIntent().getExtras().getInt("user_type");
        empleosList = new ArrayList<>();

        // INIT UI ELEMENTS
        empleosRecyclerView = view.findViewById(R.id.jobs_recycler);

        // Return view
        return view;
    }

    // GETS CURRENT USER LOCATION
    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            if (location != null) {
                currentLocation = location;
                SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapView);
                assert supportMapFragment != null;
                supportMapFragment.getMapAsync(this);
            }
        });
    }

    // SETS USER LOCATION MARKER
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        LatLng latLng1 = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

        // ADDING MARKER
        MarkerOptions markerOptions = new MarkerOptions().position(latLng1).title("Ubicación actual");
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng1));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng1, 12));
        googleMap.addMarker(markerOptions);

        // SET ENTERPRISE MARKERS
        fillEnterpriseMarkers(googleMap);

        // SET ON MARKER CLICK LISTENER
        googleMap.setOnMarkerClickListener(this);
    }

    // REQUESTS PERMISSIONS
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLocation();
            }
        }
    }

    // FILL MAP WITH ENTERPRISE MARKERS
    public void fillEnterpriseMarkers(GoogleMap googlemap){
        // OBTAIN WRITABLE DATABASE
        helper = new DBHelper(getContext());
        DB = helper.getWritableDatabase();

        // GET ENTERPRISES THAT HAS LOCATION STORED
        Cursor coordenadas = DB.rawQuery("SELECT latitud, longitud, nombre_empresa, id FROM empresa " +
                "WHERE latitud != 0 AND longitud != 0", new String[]{});


        while (coordenadas.moveToNext()){
            double latitud = coordenadas.getDouble(0);
            double longitud = coordenadas.getDouble(1);
            String nombre = coordenadas.getString(2);

            System.out.println(latitud +" "+ longitud+" "+nombre);

            LatLng location = new LatLng(latitud, longitud);

            int height = 100;
            int width = 100;
            BitmapDrawable bitmapdraw = (BitmapDrawable)getResources().getDrawable(R.drawable.enterprise);
            Bitmap b = bitmapdraw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

            MarkerOptions markerOptions = new MarkerOptions().position(location).title(nombre)
                    .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)) ;
            googlemap.addMarker(markerOptions);
        }
    }


    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {

        String nombre_empresa = marker.getTitle();

        if (nombre_empresa.equals("Ubicación actual")){
            return false;
        }
        else{
            // GETTING JOBS FROM THE BATABASE
            helper = new DBHelper(getActivity());
            DB = helper.getWritableDatabase();

            // ENTERPRISE ID
            Cursor id = DB.rawQuery("SELECT id FROM empresa WHERE nombre_empresa = ?", new String[]{nombre_empresa});
            id.moveToFirst();
            int id_empresa = id.getInt(0);

            // ENTERPRISE JOB ADVERTISEMENTS
            Cursor empleos_DB = DB.rawQuery("SELECT titulo, sueldo, tipo_jornada, created_at, id " +
                            "FROM empleo " +
                            "WHERE visible = ?" +
                            "AND empresa = ?",
                    new String[]{String.valueOf(1), String.valueOf(id_empresa)});

            // FILLING JOBS(CLASS) ARRAYLIST
            empleosList.clear();
            while (empleos_DB.moveToNext()) {
                Empleo newEmpleo = new Empleo();
                newEmpleo.setTitulo(empleos_DB.getString(0));
                newEmpleo.setEmpresa(nombreEmpresa(id_empresa));
                newEmpleo.setSueldo(empleos_DB.getInt(1));
                newEmpleo.setJornada(tipoJornada(empleos_DB.getInt(2)));
                newEmpleo.setCreated_at(empleos_DB.getString(3));
                newEmpleo.setId(empleos_DB.getInt(4));
                empleosList.add(newEmpleo);
            }

            empleos_DB.close();

            // SETTING THE ADAPTER
            adapter = new EmpleosAdapter(empleosList, getContext());
            adapter.setUserMail(user_mail);
            adapter.setUserType(user_type);

            //  ADAPTER --> RECYCLER
            empleosRecyclerView.setAdapter(adapter);
            empleosRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            empleosRecyclerView.setHasFixedSize(true);

        }

        return false;
    }

    private String tipoJornada(int tipo) {
        // SQL TRANSLATE BY ID
        helper = new DBHelper(getActivity());
        DB = helper.getWritableDatabase();
        Cursor tipo_BD = DB.rawQuery("SELECT tipo FROM tipo_jornada WHERE id = ?", new String[]{String.valueOf(tipo)});
        tipo_BD.moveToFirst();
        String nombre_tipo = tipo_BD.getString(0);
        return nombre_tipo;
    }

    private String nombreEmpresa(int id) {
        // SQL TRANSLATE BY ID
        helper = new DBHelper(getActivity());
        DB = helper.getWritableDatabase();
        Cursor empresa_BD = DB.rawQuery("SELECT nombre_empresa FROM empresa WHERE id = ?", new String[]{String.valueOf(id)});
        empresa_BD.moveToFirst();
        String nombre_empresa = empresa_BD.getString(0);
        return nombre_empresa;
    }
}