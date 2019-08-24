package com.duarte.serviceapp.model;

import com.duarte.serviceapp.helper.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

public class Servico {

    private String idUsuario;
    private String nome;
    private String descricao;
    private Double preco;

    public Servico() {
    }

    public void salvar(){

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference servicoRef = firebaseRef
                .child("servicos")
                .child( getIdUsuario() )
                .push();
        servicoRef.setValue(this);

    }


    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }
}
