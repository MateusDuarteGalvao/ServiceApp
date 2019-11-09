package com.duarte.serviceapp.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.duarte.serviceapp.R;
import com.duarte.serviceapp.model.Prestador;
import com.squareup.picasso.Picasso;

import java.util.List;


public class AdapterPrestador extends RecyclerView.Adapter<AdapterPrestador.MyViewHolder> {

    private List<Prestador> prestadores;


    public AdapterPrestador(List<Prestador> prestadores) {
        this.prestadores = prestadores;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_prestador, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        Prestador prestador = prestadores.get(i);
        holder.nomePrestador.setText(prestador.getNome());

        holder.categoria.setText(prestador.getCategoria());
        holder.cidade.setText(prestador.getCidade());
        holder.ava.setText(prestador.getAvalia());

        //Carregar imagem
        String urlImagem = prestador.getUrlImagem();
        Picasso.get().load( urlImagem ).into( holder.imagemPrestador );

    }

    @Override
    public int getItemCount() {
        return prestadores.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imagemPrestador;
        TextView nomePrestador;
        TextView categoria;
        TextView cidade;
        TextView ava;

        public MyViewHolder(View itemView) {
            super(itemView);

            nomePrestador = itemView.findViewById(R.id.textNomePrestador);
            categoria = itemView.findViewById(R.id.textCategoriaPrestador);
            cidade = itemView.findViewById(R.id.textCidadePrestador);
            imagemPrestador = itemView.findViewById(R.id.imagePrestador);
            ava = itemView.findViewById(R.id.rtTeste);
//


        }
    }




}
