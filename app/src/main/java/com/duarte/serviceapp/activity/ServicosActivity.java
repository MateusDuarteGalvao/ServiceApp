package com.duarte.serviceapp.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.NumberFormat;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.squareup.picasso.Picasso;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

import android.widget.RatingBar;

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

    private FirebaseUser idat;


    private String urlImagem;
    private String nomePrestador;
    private String emailPrestador;
    private Uri fotoPrestador;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servicos);

        //Configurações iniciais
        inicializarComponentes();
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();

        //Puxando os dados autenticados
        idat = FirebaseAuth.getInstance().getCurrentUser();

        if (idat != null){
            String nomeLogado = idat.getDisplayName();
            String emailLogado = idat.getEmail();
            Uri fotoURL = idat.getPhotoUrl();

            String userId = idat.getUid();
            nomePrestador = nomeLogado;
            emailPrestador = emailLogado;
            fotoPrestador = fotoURL;
        }

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

        //Drawer

        AccountHeader conta = new AccountHeaderBuilder()
                .withActivity(this)
                .withCompactStyle(false)
                .withSavedInstance(savedInstanceState)
                .withThreeSmallProfileImages(false)
                .withHeaderBackground(R.drawable.bh)
                .addProfiles(
                        new ProfileDrawerItem()
                                .withName(nomePrestador)
                                .withEmail(emailPrestador)
                                .withIcon(fotoPrestador)
                )

                .build();

        new DrawerBuilder().withActivity(this).build();

        Drawer result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .withDisplayBelowStatusBar(true)
                .withTranslucentStatusBar(false)
                .withActionBarDrawerToggleAnimated(true)
                .withDrawerGravity(Gravity.LEFT)
                .withSavedInstance(savedInstanceState)
                .withSelectedItem(0)
                .withHeaderPadding(true)
                .withAccountHeader(conta)
                .withHeaderPadding(true)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        return false;
                    }
                })
                .withOnDrawerItemLongClickListener(new Drawer.OnDrawerItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(View view, int position, IDrawerItem drawerItem) {
                        //Toast.makeText(HomeActivity.this, "onItemLongClick" + position, Toast.LENGTH_SHORT).show();
                        return false;
                    }
                })


                .build();


        result.addItem(new PrimaryDrawerItem().withName("Home").withIcon(R.drawable.bt_home).
                withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        startActivity(new Intent(ServicosActivity.this, HomeActivity.class));
                        return false;
                    }
                }));

        result.addItem(new PrimaryDrawerItem().withName("Favoritos").withIcon(R.drawable.ic_fav).
                withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        Toast.makeText(ServicosActivity.this, "Em breve", Toast.LENGTH_SHORT).show();
                        //startActivity(new Intent(HomeActivity.this, HomeActivity.class));
                        return false;
                    }
                }));

        result.addItem(new PrimaryDrawerItem().withName("Contratos").withIcon(R.drawable.ic_contratos).
                withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        startActivity(new Intent(ServicosActivity.this, OrdensServicoActivity.class));

                        return false;
                    }
                }));

        result.addItem(new DividerDrawerItem());

        result.addItem(new PrimaryDrawerItem().withName("Configurações").withIcon(R.drawable.ic_config).
                withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        startActivity(new Intent(ServicosActivity.this, ConfiguracoesClienteActivity.class));
                        return false;
                    }
                }));

        result.addItem(new PrimaryDrawerItem().withName("Suporte").withIcon(R.drawable.ic_suggestion).
                withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        Toast.makeText(ServicosActivity.this, "Em breve", Toast.LENGTH_SHORT).show();
                        //startActivity(new Intent(HomeActivity.this, HomeActivity.class));
                        return false;
                    }
                }));

        result.addItem(new PrimaryDrawerItem().withName("Sair").withIcon(R.drawable.bt_sair).
                withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        deslogarUsuario();
                        return false;
                    }
                }));

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

                Toast.makeText(
                        ServicosActivity.this,
                        "Serviço confirmado com sucesso.",
                        Toast.LENGTH_SHORT).show();

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

    private void deslogarUsuario(){
        try {
            autenticacao.signOut();
            finish();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void inicializarComponentes(){
        recyclerServicosPrestador = findViewById(R.id.recyclerServicosPrestador);
        imagePrestadorServico = findViewById(R.id.imagePrestadorServico);
        textNomePrestadorServico = findViewById(R.id.textNomePrestadorServico);

        textServicosQtde = findViewById(R.id.textOrcamentoQtd);
        textServicosTotal = findViewById(R.id.textOrcamentoTotal);
    }




}
