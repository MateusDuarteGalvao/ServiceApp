package com.duarte.serviceapp.activity;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberUtils;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
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
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class ServicosActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

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

    private BottomNavigationView btView;

    private NumberFormat nf = NumberFormat.getCurrencyInstance();

    private String urlImagem;
    private String nomePrestador;
    private String emailPrestador;
    private Uri fotoPrestador;
    private FirebaseAuth autenticacao;

    private AlertDialog avalia;
    private AlertDialog comunica;
    private RatingBar rating01;
    private RatingBar ratingTeste;
    private TextView txtValor;
    private Button btAvaliar;
    private Float rate;

    private String numeroTel;

    private Button zap;
    private Button liga;

    private Prestador prest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servicos);

        //Configurações iniciais
        recyclerServicosPrestador = findViewById(R.id.recyclerServicosPrestador);
        imagePrestadorServico = findViewById(R.id.imagePrestadorServico);
        textNomePrestadorServico = findViewById(R.id.textNomePrestadorServico);
        textServicosQtde = findViewById(R.id.textOrcamentoQtd);
        textServicosTotal = findViewById(R.id.textOrcamentoTotal);
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
        if( bundle != null ) {
            prestadorSelecionado = (Prestador) bundle.getSerializable("prestador");

            textNomePrestadorServico.setText( prestadorSelecionado.getNome() );
            idPrestador = prestadorSelecionado.getIdUsuario();
            numeroTel = prestadorSelecionado.getTelefone();
            String url = prestadorSelecionado.getUrlImagem();
            Picasso.get().load(url).into(imagePrestadorServico);
        }

        //Configurações Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Serviços");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Recupera serviços para o prestador
        recuperarServicos();
        recuperarDadosCliente();

        btView =  findViewById(R.id.bt_nav);
        btView.setOnNavigationItemSelectedListener(this);


        //Configura recyclerView
        recyclerServicosPrestador.setLayoutManager(new LinearLayoutManager(this));
        recyclerServicosPrestador.setHasFixedSize(true);
        adapterServico = new AdapterServico(servicos, this);
        recyclerServicosPrestador.setAdapter( adapterServico );

//        DatabaseReference prestRef = firebaseRef
//                .child("prestadores")
//                .child(idUsuarioLogado);
//        prestRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if( dataSnapshot.getValue() != null ){
//                    Prestador prest = dataSnapshot.getValue(Prestador.class);
//                    numeroTel = prest.getTelefone();
//
//
//
//                }
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//            }
//        });


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

    }

    private void confirmarQuantidade(final int posicao) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Quantidade");
        builder.setMessage("Digite a quantidade");

        final EditText editQunatidade = new EditText(this);
        editQunatidade.setText("1");
        editQunatidade.setInputType(InputType.TYPE_CLASS_NUMBER);

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
                ordemServicoRecuperada.setTelefone( cliente.getTelefone() );
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
                        Float preco = itemOrdemServico.getPreco();



                        totalServicos += (qtde * preco);
                        qtdeItensServico += qtde;
                    }

                }

                textServicosQtde.setText( "Pedidos : " + String.valueOf(qtdeItensServico) );
                textServicosTotal.setText( nf.format(totalServicos));

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

                finish();
                Toast.makeText(
                        getApplicationContext(),
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

    private void deslogarUsuario() {
        try {
            autenticacao.signOut();
            finish();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.bt_home:{
                Intent i = new Intent(ServicosActivity.this, HomeActivityDrawer.class);
                startActivity(i);
                break;

            }
            case R.id.bt_fav:{
                //Toast.makeText(this, "Em breve..", Toast.LENGTH_SHORT).show();

               dialogAvaliacao();

                break;
            }
            case R.id.bt_chat:{

                String numero = numeroTel;
                if (!numero.isEmpty()){

                    dialogContato(numero);

                }
                else{
                    Toast.makeText(getBaseContext(), "Numero Indisponivel!", Toast.LENGTH_SHORT).show();
                }

                //Toast.makeText(this, "Em breve...", Toast.LENGTH_SHORT).show();
                break;
            }
        }

        return true;
    }



    private void dialogAvaliacao() {

        txtValor = findViewById(R.id.rtTeste);
        btAvaliar = findViewById(R.id.btAvaliar);

        LayoutInflater li = getLayoutInflater();
        final View image = li.inflate(R.layout.avaliacao, null);

        image.findViewById(R.id.btAvaliar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                rating01 = image.findViewById(R.id.ratingDialog);
                txtValor.setText(String.valueOf(rating01.getRating()));
                Toast.makeText(getBaseContext(), "" + rating01.getRating(), Toast.LENGTH_SHORT).show();
                Toast.makeText(getBaseContext(), "Obrigado por avaliar!", Toast.LENGTH_SHORT).show();

                avalia.dismiss();
            }

        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(image);
        avalia = builder.create();
        avalia.getWindow().setBackgroundDrawableResource(R.drawable.borda_redonda);
        avalia.show();


    }

    public void dialogContato(String numero){

        String contato = numero.replace(" ", "")
                .replace("-", "")
                .replace("(", "")
                .replace(")", "");

        final StringBuffer numeroContato = new StringBuffer(contato);

        zap = findViewById(R.id.btZap);
        liga = findViewById(R.id.btLiga);

        LayoutInflater lc = getLayoutInflater();
        View vcont = lc.inflate(R.layout.chat, null);

        vcont.findViewById(R.id.btZap).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contatoZap(numeroContato);
            }
        });

        vcont.findViewById(R.id.btLiga).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contatoLigar(numeroContato);
            }
        });



        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(vcont);
        comunica = builder.create();
        comunica.getWindow().setBackgroundDrawableResource(R.drawable.borda_redonda);
        comunica.show();

//
    }

    public void contatoZap(StringBuffer numeroContato){
        numeroContato.deleteCharAt(2);

        Intent intent = new Intent("android.intent.action.MAIN");
        intent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
        intent.putExtra("jid",
                PhoneNumberUtils.stripSeparators("55"+numeroContato) + "@s.whatsapp.net");

        startActivity(intent);

    }

    public void contatoLigar(StringBuffer numeroContato){
        Uri uri = Uri.parse("tel:" + numeroContato);
        Intent intent = new Intent(Intent.ACTION_DIAL, uri);
        startActivity(intent);

    }



}