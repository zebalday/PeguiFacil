package com.asignatura.peguifacil2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EmpleosAdapter extends RecyclerView.Adapter<EmpleosAdapter.ViewHolder> {

    // NECESSARY ATTRIBUTES
    private List<Empleo> data;
    private LayoutInflater inflater;
    private Context context;

    // CONSTRUCTOR
    public EmpleosAdapter(List<Empleo> data, Context context){
        this.data = data;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    // RETURNS DATA SIZE
    @Override
    public int getItemCount() {return data.size();}

    // VIEW-HOLDER CLASS
    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView titulo, empresa, jornada, salario;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.txtJobTitle);
            empresa = itemView.findViewById(R.id.txtEnterpriseName);
            jornada = itemView.findViewById(R.id.txtJobHours);
            salario = itemView.findViewById(R.id.txtJobSalary);
        }
        public void bindData(final Empleo empleo){
            titulo.setText(empleo.getTitulo());
            empresa.setText(empleo.getEmpresa());
            jornada.setText(empleo.getJornada());
            salario.setText(String.valueOf(empleo.getSueldo()));
        }
    }


    // VIEW-HOLDER METHODS TO INFLATE LAYOUT
    @NonNull
    @Override
    public EmpleosAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.empleo_viewholder, null);
        return new EmpleosAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmpleosAdapter.ViewHolder holder, int position) {
        holder.bindData(data.get(position));
    }

}
