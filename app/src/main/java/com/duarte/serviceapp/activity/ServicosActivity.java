package com.duarte.serviceapp.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.duarte.serviceapp.R;
import com.duarte.serviceapp.adapter.AdapterServico;
import com.duarte.serviceapp.helper.ConfiguracaoFirebase;
import com.duarte.serviceapp.helper.UsuarioFirebase;
import com.duarte.serviceapp.listener.RecyclerItemClickListener;
import com.duarte.serviceapp.model.Cliente;
import com.duarte.serviceapp.model.ItemOrdemServico;
import com.duarte.serviceapp.model.OrdemServico;
import com.duarte.serviceapp.model.Prestador;
import com.duarte.serviceapp.model.Servico;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class ServicosActivity extends AppCompatActivity {

    private RecyclerView recyclerServicosPrestador;
    private ImageView imagePrestadorServico;
    private TextView textNomePrestadorServico;
    private Prestador prestadorSelecionado;
    private AlertDialog dialog;
    private TextView textServicosQtde, textServicosTotal;


    private AdapterServico adapterServico;
    private List<Servico> servicos = new ArrayList<>();
    private List<ItemOrdemServico> itensServicos = new ArrayList<>();
    private DatabaseReference firebaseRef;
    private String idPrestador;
    private String idUsuarioLogado;
    private Cliente cliente;
    private OrdemServico ordemServicoRecuperada;
    private int qtdeItensServico;
    private Double totalServicos;
    private int metodoPagamento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servicos);

        //Configurações iniciais
        inicializarComponentes();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();

        //Recupera prestador selecionado
        Bundle bundle = getIntent().getExtras();
        if( bundle != null ){
            prestadorSelecionado = (Prestador) bundle.getSerializable("prestador");

            textNomePrestadorServico.setText( prestadorSelecionado.getNome() );
            idPrestador = prestadorSelecionado.getIdUsuario();

            String url = prestadorSelecionado.getUrlImagem();
            Picasso.get().load(url).into(imagePrestadorServico);
        }

        //Configurações Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Serviços");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Configura recyclerView
        recyclerServicosPrestador.setLayoutManager(new LinearLayoutManager(this));
        recyclerServicosPrestador.setHasFixedSize(true);
        adapterServico = new AdapterServico(servicos, this);
        recyclerServicosPrestador.setAdapter( adapterServico );

        //Configurar evento de clique
        recyclerServicosPrestador.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        this,
                        recyclerServicosPrestador,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                confirmarQuantidade(position);
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }
                )
        );

        //Recupera serviços para o prestador
        recuperarServicos();
        recuperarDadosCliente();

    }

    private void confirmarQuantidade(final int posicao) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Quantidade");
        builder.setMessage("Digite a quantidade");

        final EditText editQunatidade = new EditText(this);
        editQunatidade.setText("1");

        builder.setView( editQunatidade );

        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String quantidade = editQunatidade.getText().toString();

                Servico servicoSelecionado = servicos.get(posicao);

                ItemOrdemServico itemOrdemServico = new ItemOrdemServico();
                itemOrdemServico.setIdServico( servicoSelecionado.getIdServico() );
                itemOrdemServico.setDescricaoServico( servicoSelecionado.getDescricao() );
                itemOrdemServico.setNomeServico( servicoSelecionado.getNome() );
                itemOrdemServico.setPreco( servicoSelecionado.getPreco() );
                itemOrdemServico.setQuantidade( Integer.parseInt(quantidade) );

                itensServicos.add( itemOrdemServico );

                if( ordemServicoRecuperada == null ){
                    ordemServicoRecuperada = new OrdemServico(idUsuarioLogado, idPrestador);
                }

                ordemServicoRecuperada.setNome( cliente.getNome() );
                ordemServicoRecuperada.setEndereco( cliente.getEndereco() );
                ordemServicoRecuperada.setTelefone( cliente.getEndereco() );
                ordemServicoRecuperada.setItens( itensServicos );
                ordemServicoRecuperada.salvar();

            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void recuperarDadosCliente() {

        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Carregando dados")
                .setCancelable(false)
                .build();
        dialog.show();

        DatabaseReference clientesRef = firebaseRef
                .child( "clientes" )
                .child( idUsuarioLogado );

        clientesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if( dataSnapshot.getValue() != null ){
                    cliente = dataSnapshot.getValue(Cliente.class);
                }

                recuperarOrdemServico();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void recuperarOrdemServico() {

        final DatabaseReference ordemServicoRef = firebaseRef
                .child("ordens_servico_cliente")
                .child( idPrestador )
                .child( idUsuarioLogado );

        ordemServicoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                qtdeItensServico = 0;
                totalServicos = 0.0;
                itensServicos = new ArrayList<>();

                if(dataSnapshot.getValue() != null) {

                    ordemServicoRecuperada = dataSnapshot.getValue(OrdemServico.class);
                    itensServicos = ordemServicoRecuperada.getItens();

                    for(ItemOrdemServico itemOrdemServico: itensServicos) {

                        int qtde = itemOrdemServico.getQuantidade();
                        Double preco = itemOrdemServico.getPreco();

                        totalServicos += (qtde * preco);
                        qtdeItensServico += qtde;
                    }

                }

                DecimalFormat df = new DecimalFormat( "0.00" );

                textServicosQtde.setText( "qtd: " + String.valueOf(qtdeItensServico) );
                textServicosTotal.setText( "R$ " +  df.format( totalServicos ));

                dialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void recuperarServicos(){

        DatabaseReference servicosRef = firebaseRef
                .child("servicos")
                .child( idPrestador );

        servicosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                servicos.clear();

                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    servicos.add( ds.getValue(Servico.class) );
                }

                adapterServico.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_servicos, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.menuOrdemServico :
                confirmarOrdemServico();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void confirmarOrdemServico() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecione um método de pagamento");

        CharSequence[] itens = new CharSequence[] {
          "Dinheiro", "Máquina cartão"
        };
        builder.setSingleChoiceItems(itens, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                metodoPagamento = which;
            }
        });

        final EditText editObservacao = new EditText(this);
        editObservacao.setHint("Digite uma observação");
        builder.setView( editObservacao );


        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String observacao = editObservacao.getText().toString();
                ordemServicoRecuperada.setMetodoPagamento( metodoPagamento );
                ordemServicoRecuperada.setObservacao( observacao );
                ordemServicoRecuperada.setStatus( "confirmado" );
                ordemServicoRecuperada.confirmar();
                ordemServicoRecuperada.remover();
                ordemServicoRecuperada = null;

            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }


    private void inicializarComponentes(){
        recyclerServicosPrestador = findViewById(R.id.recyclerServicosPrestador);
        imagePrestadorServico = findViewById(R.id.imagePrestadorServico);
        textNomePrestadorServico = findViewById(R.id.textNomePrestadorServico);

        textServicosQtde = findViewById(R.id.textOrcamentoQtd);
        textServicosTotal = findViewById(R.id.textOrcamentoTotal);
    }




}
