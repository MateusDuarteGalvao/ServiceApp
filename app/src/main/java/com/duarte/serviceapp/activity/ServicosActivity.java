package com.duarte.serviceapp.activity;

import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.duarte.serviceapp.R;
import com.duarte.serviceapp.adapter.AdapterServico;
import com.duarte.serviceapp.helper.ConfiguracaoFirebase;
import com.duarte.serviceapp.helper.UsuarioFirebase;
import com.duarte.serviceapp.model.Cliente;
import com.duarte.serviceapp.model.Prestador;
import com.duarte.serviceapp.model.Servico;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class ServicosActivity extends AppCompatActivity {

    private RecyclerView recyclerServicosPrestador;
    private ImageView imagePrestadorServico;
    private TextView textNomePrestadorServico;
    private Prestador prestadorSelecionado;
    private AlertDialog dialog;


    private AdapterServico adapterServico;
    private List<Servico> servicos = new ArrayList<>();
    private DatabaseReference firebaseRef;
    private String idPrestador;
    private String idUsuarioLogado;
    private Cliente cliente;

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

        //Recupera serviços para o prestador
        recuperarServicos();
        recuperarDadosCliente();

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

        dialog.dismiss();
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
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    private void inicializarComponentes(){
        recyclerServicosPrestador = findViewById(R.id.recyclerServicosPrestador);
        imagePrestadorServico = findViewById(R.id.imagePrestadorServico);
        textNomePrestadorServico = findViewById(R.id.textNomePrestadorServico);
    }




}
