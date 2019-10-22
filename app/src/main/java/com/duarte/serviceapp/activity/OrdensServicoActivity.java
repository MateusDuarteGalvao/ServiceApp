package com.duarte.serviceapp.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.duarte.serviceapp.R;
import com.duarte.serviceapp.adapter.AdapterOrdemServico;
import com.duarte.serviceapp.helper.ConfiguracaoFirebase;
import com.duarte.serviceapp.helper.UsuarioFirebase;
import com.duarte.serviceapp.listener.RecyclerItemClickListener;
import com.duarte.serviceapp.model.OrdemServico;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class OrdensServicoActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    private RecyclerView recyclerOrdensServico;
    private AdapterOrdemServico adapterOrdemServico;
    private List<OrdemServico> ordensServico = new ArrayList<>();
    private AlertDialog dialog;
    private DatabaseReference firebaseRef;
    private String idPrestador;

    private FirebaseAuth autenticacao;

    private BottomNavigationView btView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordens_servico);

        //Configurações iniciais
        inicializarComponentes();
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        idPrestador = UsuarioFirebase.getIdUsuario();

        //Puxando os dados autenticados


        btView =  findViewById(R.id.bt_nav);
        btView.setOnNavigationItemSelectedListener(this);


        //Configuracoes Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Ordens de serviço");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //Configurações recyclerView
        recyclerOrdensServico.setLayoutManager(new LinearLayoutManager(this));
        recyclerOrdensServico.setHasFixedSize(true);
        adapterOrdemServico = new AdapterOrdemServico(ordensServico);
        recyclerOrdensServico.setAdapter( adapterOrdemServico );

        recuperarOrdensServico();

        //Adiciona evento de clique no recyclerView
        recyclerOrdensServico.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        this,
                        recyclerOrdensServico,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                            }

                            @Override
                            public void onLongItemClick(View view, int position) {
                                OrdemServico ordemServico = ordensServico.get( position );
                                ordemServico.setStatus("finalizado");
                                ordemServico.atualizarStatus();

                                                           }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }
                )
        );

    }

    private void recuperarOrdensServico() {

        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Carregando dados")
                .setCancelable(false)
                .build();
        dialog.show();

        DatabaseReference ordemServicoRef = firebaseRef
                .child("ordens_servico")
                .child( idPrestador );

        Query ordemServicoPesquisa = ordemServicoRef.orderByChild("status")
                .equalTo("confirmado");

        ordemServicoPesquisa.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ordensServico.clear();
                if ( dataSnapshot.getValue() != null ){
                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                        OrdemServico ordemServico = ds.getValue(OrdemServico.class);
                        ordensServico.add( ordemServico );
                    }
                    adapterOrdemServico.notifyDataSetChanged();
                }
                dialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void deslogarUsuario(){
        try {
           autenticacao.signOut();
            finish();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void inicializarComponentes() {
        recyclerOrdensServico = findViewById(R.id.recyclerOrdensServico);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.bt_home:{
                Intent i = new Intent(OrdensServicoActivity.this, HomeActivityDrawer.class);
                startActivity(i);
                break;

            }
            case R.id.bt_fav:{
                Toast.makeText(this, "Em breve..", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.bt_chat:{
                Toast.makeText(this, "Em breve...", Toast.LENGTH_SHORT).show();
                break;
            }
        }

        return true;
    }
}
