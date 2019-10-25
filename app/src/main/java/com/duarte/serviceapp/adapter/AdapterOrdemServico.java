package com.duarte.serviceapp.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.duarte.serviceapp.R;
import com.duarte.serviceapp.model.ItemOrdemServico;
import com.duarte.serviceapp.model.OrdemServico;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class AdapterOrdemServico extends RecyclerView.Adapter<AdapterOrdemServico.MyViewHolder> {

    private List<OrdemServico> ordensServico;
    private NumberFormat nf = NumberFormat.getCurrencyInstance();

    public AdapterOrdemServico(List<OrdemServico> ordensServico) {
        this.ordensServico = ordensServico;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_ordens_servico, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {

        OrdemServico ordemServico = ordensServico.get(i);
        holder.nome.setText( ordemServico.getNome());
        holder.endereco.setText( "Endereço: "+ordemServico.getEndereco() );
        holder.telefone.setText( "Telefone: "+ordemServico.getTelefone() );
        holder.observacao.setText( "Obs: "+ ordemServico.getObservacao() );

        List<ItemOrdemServico> itens = new ArrayList<>();
        itens = ordemServico.getItens();
        String descricaoItens = "";

        int numeroItem = 1;
        Double total = 0.0;
        for( ItemOrdemServico itemOrdemServico : itens ){

            int qtde = itemOrdemServico.getQuantidade();
            Float preco = itemOrdemServico.getPreco();
            total += (qtde * preco);


            String nome = itemOrdemServico.getNomeServico();
            descricaoItens += numeroItem + ") " + nome + " / (" + qtde + " x " + nf.format(preco) + ") \n";
            numeroItem++;
        }
        descricaoItens += "Total: " + nf.format(total);
        holder.itens.setText(descricaoItens);

        int metodoPagamento = ordemServico.getMetodoPagamento();
        String pagamento = metodoPagamento == 0 ? "Dinheiro" : "Máquina cartão" ;
        holder.pgto.setText( "Pagamento: " + pagamento );

    }

    @Override
    public int getItemCount() {
        return ordensServico.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView nome;
        TextView endereco;
        TextView telefone;
        TextView pgto;
        TextView observacao;
        TextView itens;

        public MyViewHolder(View itemView) {
            super(itemView);

            nome        = itemView.findViewById(R.id.textOrdemServicoNome);
            endereco    = itemView.findViewById(R.id.textOrdemServicoEndereco);
            telefone    = itemView.findViewById(R.id.textOrdemServicoTelefone);
            pgto        = itemView.findViewById(R.id.textOrdemServicoPgto);
            observacao  = itemView.findViewById(R.id.textOrdemServicoObs);
            itens       = itemView.findViewById(R.id.textOrdemServicoItens);
        }
    }

}
