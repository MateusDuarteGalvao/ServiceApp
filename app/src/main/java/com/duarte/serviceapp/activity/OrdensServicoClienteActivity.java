package com.duarte.serviceapp.activity;

import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.duarte.serviceapp.R;
import com.duarte.serviceapp.adapter.AdapterOrdemServico;
import com.duarte.serviceapp.helper.ConfiguracaoFirebase;
import com.duarte.serviceapp.helper.UsuarioFirebase;
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

public class OrdensServicoClienteActivity extends AppCompatActivity {

    private RecyclerView recyclerOrdensServico;
    private AdapterOrdemServico adapterOrdemServico;
    private List<OrdemServico> ordensServico = new ArrayList<>();
    private AlertDialog dialog;
    private DatabaseReference firebaseRef;
    private String idCliente;

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordens_servico_cliente);

        //Configurações iniciais
        recyclerOrdensServico = findViewById(R.id.recyclerOrdensServico);
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        idCliente = UsuarioFirebase.getIdUsuario();

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
                .child( idCliente );

        ordemServicoRef.addValueEventListener(new ValueEventListener() {
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

}
