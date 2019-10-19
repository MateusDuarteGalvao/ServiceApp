package com.duarte.serviceapp.model;

import com.duarte.serviceapp.helper.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.List;

public class OrdemServico {

    private String idCliente;
    private String idPrestador;
    private String idOrdemServico;
    private String nome;
    private String endereco;
    private String telefone;
    private List<ItemOrdemServico> itens;
    private Double total;
    private String status = "pendente";
    private int metodoPagamento;
    private String observacao;

    public OrdemServico() {
    }

    public OrdemServico(String idCliente, String idPrestador) {

        setIdCliente( idCliente );
        setIdPrestador( idPrestador );

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference ordemServicoRef = firebaseRef
                .child("ordens_servico_cliente")
                .child( idPrestador )
                .child( idCliente );
        setIdOrdemServico( ordemServicoRef.push().getKey() );
    }

    public void salvar() {

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference ordemServicoRef = firebaseRef
                .child("ordens_servico_cliente")
                .child( getIdPrestador() )
                .child( getIdCliente() );
        ordemServicoRef.setValue( this );
    }

    public void remover() {

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference ordemServicoRef = firebaseRef
                .child("ordens_servico_cliente")
                .child( getIdPrestador() )
                .child( getIdCliente() );
        ordemServicoRef.removeValue();
    }

    public void confirmar() {

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference ordemServicoRef = firebaseRef
                .child("ordens_servico")
                .child( getIdPrestador() )
                .child( getIdOrdemServico() );
        ordemServicoRef.setValue( this );
    }

    public void atualizarStatus() {

        HashMap<String, Object> status = new HashMap<>();
        status.put("status", getStatus() );

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference ordemServicoRef = firebaseRef
                .child("ordens_servico")
                .child( getIdPrestador() )
                .child( getIdOrdemServico() );
        ordemServicoRef.updateChildren( status );
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public String getIdPrestador() {
        return idPrestador;
    }

    public void setIdPrestador(String idPrestador) {
        this.idPrestador = idPrestador;
    }

    public String getIdOrdemServico() {
        return idOrdemServico;
    }

    public void setIdOrdemServico(String idOrdemServico) {
        this.idOrdemServico = idOrdemServico;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public List<ItemOrdemServico> getItens() {
        return itens;
    }

    public void setItens(List<ItemOrdemServico> itens) {
        this.itens = itens;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getMetodoPagamento() {
        return metodoPagamento;
    }

    public void setMetodoPagamento(int metodoPagamento) {
        this.metodoPagamento = metodoPagamento;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

}
