package com.duarte.serviceapp.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;

import com.duarte.serviceapp.R;
import com.duarte.serviceapp.adapter.AdapterOrdemServico;
import com.duarte.serviceapp.helper.ConfiguracaoFirebase;
import com.duarte.serviceapp.helper.UsuarioFirebase;
import com.duarte.serviceapp.listener.RecyclerItemClickListener;
import com.duarte.serviceapp.model.OrdemServico;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

import android.widget.Toast;

public class OrdensServicoActivity extends AppCompatActivity {

    private RecyclerView recyclerOrdensServico;
    private AdapterOrdemServico adapterOrdemServico;
    private List<OrdemServico> ordensServico = new ArrayList<>();
    private AlertDialog dialog;
    private DatabaseReference firebaseRef;
    private String idPrestador;

    private FirebaseAuth autenticacao;
    private FirebaseUser idat;

    private String idUsuarioLogado;
    private String urlImagem;
    private String nomePrestador;
    private String emailPrestador;
    private Uri fotoPrestador;


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

        //Configuracoes Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Ordens de serviço");
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
                        startActivity(new Intent(OrdensServicoActivity.this, HomeActivity.class));
                        return false;
                    }
                }));

        result.addItem(new PrimaryDrawerItem().withName("Favoritos").withIcon(R.drawable.ic_fav).
                withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        Toast.makeText(OrdensServicoActivity.this, "Em breve", Toast.LENGTH_SHORT).show();
                        //startActivity(new Intent(HomeActivity.this, HomeActivity.class));
                        return false;
                    }
                }));

        result.addItem(new PrimaryDrawerItem().withName("Contratos").withIcon(R.drawable.ic_contratos).
                withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        startActivity(new Intent(OrdensServicoActivity.this, OrdensServicoActivity.class));

                        return false;
                    }
                }));

        result.addItem(new DividerDrawerItem());

        result.addItem(new PrimaryDrawerItem().withName("Configurações").withIcon(R.drawable.ic_config).
                withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        startActivity(new Intent(OrdensServicoActivity.this, ConfiguracoesClienteActivity.class));
                        return false;
                    }
                }));

        result.addItem(new PrimaryDrawerItem().withName("Suporte").withIcon(R.drawable.ic_ajuda).
                withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        //Toast.makeText(OrdensServicoActivity.this, "Em breve", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(OrdensServicoActivity.this, SuporteActivity.class));                        return false;
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

}
