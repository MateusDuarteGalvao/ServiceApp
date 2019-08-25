package com.duarte.serviceapp.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.duarte.serviceapp.R;
import com.duarte.serviceapp.adapter.AdapterServico;
import com.duarte.serviceapp.helper.ConfiguracaoFirebase;
import com.duarte.serviceapp.model.Prestador;
import com.duarte.serviceapp.model.Servico;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ServicosActivity extends AppCompatActivity {

    private RecyclerView recyclerServicosPrestador;
    private ImageView imagePrestadorServico;
    private TextView textNomePrestadorServico;
    private Prestador prestadorSelecionado;

    private AdapterServico adapterServico;
    private List<Servico> servicos = new ArrayList<>();
    private DatabaseReference firebaseRef;
    private String idPrestador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servicos);

        //Configurações iniciais
        inicializarComponentes();
        firebaseRef = ConfiguracaoFirebase.getFirebase();

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

    private void inicializarComponentes(){
        recyclerServicosPrestador = findViewById(R.id.recyclerServicosPrestador);
        imagePrestadorServico = findViewById(R.id.imagePrestadorServico);
        textNomePrestadorServico = findViewById(R.id.textNomePrestadorServico);
    }




}
