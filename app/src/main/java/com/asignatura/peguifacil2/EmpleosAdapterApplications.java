package com.asignatura.peguifacil2;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class EmpleosAdapterApplications extends RecyclerView.Adapter<EmpleosAdapter.ViewHolder> {

    // DATABASE
    private SQLiteDatabase DB;
    private DBHelper helper;
    private String userMail;
    private int userType;

    // NECESSARY ATTRIBUTES
    private ArrayList<Empleo> empleosList; // DATA TO BE DISPLAYED
    private LayoutInflater inflater;
    private Context context;

    // CONSTRUCTOR
    public EmpleosAdapterApplications(ArrayList<Empleo> empleosList, Context context){
        this.empleosList = empleosList;
        this.context = context;
    }

    // RETURNS DATA SIZE
    @Override
    public int getItemCount() {return empleosList.size();}

    // FILTERING OUR RECYLERVIEW ITEMS
    public void filterList(ArrayList<Empleo> filteredList){
        empleosList = filteredList;
        notifyDataSetChanged();
    }

    // INFLATING LAYOUT
    @NonNull
    @Override
    public EmpleosAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.empleo_viewholder, null);
        return new EmpleosAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmpleosAdapter.ViewHolder holder, int position) {
        holder.setUserMail(userMail);
        holder.setUserType(userType);
        holder.bindData(empleosList.get(position));
    }

    public void setUserMail(String mail){this.userMail = mail;}
    public void setUserType(int type){this.userType = type;}


    // VIEW-HOLDER CLASS
    public static class ViewHolder extends RecyclerView.ViewHolder {

        // USER INTERFACE ATTRIBUTES
        TextView titulo, empresa, jornada, salario, created_at;
        CardView jobCardView;
        Intent jobInfo;
        String userMail;
        int userType;
        int jobID;

        // INITIALIZE ATTRIBUTES
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            jobCardView = itemView.findViewById(R.id.job_cardview);
            titulo = itemView.findViewById(R.id.txtJobTitle);
            empresa = itemView.findViewById(R.id.txtEnterpriseName);
            jornada = itemView.findViewById(R.id.txtJobHours);
            salario = itemView.findViewById(R.id.txtJobSalary);
            created_at = itemView.findViewById(R.id.txtCreatedAt);
        }

        // SET IT´S VALUES
        public void bindData(final Empleo empleo){
            jobID = empleo.getId();
            titulo.setText(empleo.getTitulo());
            empresa.setText(empleo.getEmpresa());
            jornada.setText(empleo.getJornada());
            salario.setText(String.valueOf(empleo.getSueldo()));
            if (empleo.getCreated_at() != null){
                created_at.setText("Publicado el: "+empleo.getCreated_at());
            }

            jobCardView.setOnClickListener(view -> {
                jobInfo = new Intent(view.getContext(), EmpleoInfo.class);
                jobInfo.putExtra("user_mail", this.userMail);
                jobInfo.putExtra("user_type", this.userType);
                jobInfo.putExtra("job_id", this.jobID);
                jobInfo.putExtra("origin", "Postulaciones");
                itemView.getContext().startActivity(jobInfo);
            });
        }

        public void setUserMail(String mail){this.userMail = mail;}
        public void setUserType(int type){this.userType = type;}
    }
}
