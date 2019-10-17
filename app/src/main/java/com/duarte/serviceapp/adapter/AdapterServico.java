package com.duarte.serviceapp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.duarte.serviceapp.R;
import com.duarte.serviceapp.model.Servico;

import java.util.List;



public class AdapterServico extends RecyclerView.Adapter<AdapterServico.MyViewHolder>{

    private List<Servico> servicos;
    private Context context;

    public AdapterServico(List<Servico> servicos, Context context) {
        this.servicos = servicos;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_servico, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        Servico servico = servicos.get(i);
        holder.nome.setText(servico.getNome());
        holder.descricao.setText(servico.getDescricao());
        holder.valor.setText(servico.getPreco());
    }

    @Override
    public int getItemCount() {
        return servicos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView nome;
        TextView descricao;
        TextView valor;

        public MyViewHolder(View itemView) {
            super(itemView);

            nome = itemView.findViewById(R.id.textNomeServico);
            descricao = itemView.findViewById(R.id.textDescricaoServico);
            valor = itemView.findViewById(R.id.textPreco);
        }
    }
}
