package com.asignatura.peguifacil2;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.content.Context;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class EmpleosAdapterEnterprise extends RecyclerView.Adapter<EmpleosAdapterEnterprise.ViewHolder> {

    // DATABASE
    private SQLiteDatabase DB;
    private DBHelper helper;

    // NECESSARY ATTRIBUTES
    private ArrayList<Empleo> empleosList; // DATA TO BE DISPLAYED
    private LayoutInflater inflater;
    private Context context;


    public EmpleosAdapterEnterprise(ArrayList<Empleo> empleosList, Context context) {
        this.empleosList = empleosList;
    }

    // RETURNS DATA SIZE
    @Override
    public int getItemCount() {return empleosList.size();}

    // INFLATING LAYOUT
    @NonNull
    @Override
    public EmpleosAdapterEnterprise.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.empleoviewholder_enterprise, null);
        return new EmpleosAdapterEnterprise.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmpleosAdapterEnterprise.ViewHolder holder, int position) {
        holder.bindData(empleosList.get(position));
    }

    // VIEW-HOLDER CLASS
    public static class ViewHolder
            extends RecyclerView.ViewHolder implements View.OnClickListener {

        // DATABASE
        private SQLiteDatabase DB;
        private DBHelper helper;

        // UI COMPONENTS
        TextView titulo, empresa, jornada, salario, created_at;
        Button editJob, deleteJob;
        CardView cardView;
        int empleoID;

        // INITIALIZE ATTRIBUTES
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.job_cardview_enterprise);
            titulo = itemView.findViewById(R.id.txtJobTitle);
            empresa = itemView.findViewById(R.id.txtEnterpriseName);
            jornada = itemView.findViewById(R.id.txtJobHours);
            salario = itemView.findViewById(R.id.txtJobSalary);
            created_at = itemView.findViewById(R.id.txtCreatedAt);
            editJob = itemView.findViewById(R.id.btn_edit_job);
            deleteJob = itemView.findViewById(R.id.btn_delete_job);

            editJob.setOnClickListener(this);
            deleteJob.setOnClickListener(this);
            cardView.setOnClickListener(this);
        }

        // SET IT´S VALUES
        public void bindData(final Empleo empleo){
            empleoID = empleo.getId();
            titulo.setText(empleo.getTitulo());
            empresa.setText(empleo.getEmpresa());
            jornada.setText(empleo.getJornada());
            salario.setText(String.valueOf(empleo.getSueldo()));
            created_at.setText("Vigente por: "+empleo.getCreated_at() + " días.");
        }

        // EDIT - DELETE BUTTON EVENTS
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btn_delete_job:
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(itemView.getContext());
                    alertDialog.setTitle("Confirme");
                    alertDialog.setMessage("¿Seguro que desea eliminar el aviso?");

                    alertDialog.setPositiveButton("SÍ", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // DATABASE
                            helper = new DBHelper(itemView.getContext());
                            DB = helper.getWritableDatabase();
                            System.out.println(empleoID);

                            ContentValues update = new ContentValues();
                            update.put("visible", 0);
                            DB.update("empleo", update, "id = ?", new String[]{String.valueOf(empleoID)} );

                            Toast.makeText(itemView.getContext(), "Aviso de empleo eliminado",Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });
                    alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Do nothing
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alert = alertDialog.create();
                    alert.show();
                    break;

                case R.id.btn_edit_job:
                    Intent intent = new Intent(view.getContext(), EditarEmpleo.class);
                    intent.putExtra("job_id", empleoID);
                    view.getContext().startActivity(intent);
                    break;

                case R.id.job_cardview_enterprise:
                    intent = new Intent(view.getContext(), ApplicationsEnterprise.class);
                    intent.putExtra("job_id", empleoID);
                    view.getContext().startActivity(intent);
                    break;

            }
        }
    }
}
