package com.duarte.serviceapp.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.duarte.serviceapp.R;
import com.duarte.serviceapp.adapter.AdapterPrestador;
import com.duarte.serviceapp.helper.ConfiguracaoFirebase;
import com.duarte.serviceapp.helper.UsuarioFirebase;
import com.duarte.serviceapp.model.Prestador;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

public class SuporteActivityPrestador extends PrestadorActivityDrawer{

    private FirebaseAuth autenticacao;
    private MaterialSearchView searchView;
    private RecyclerView recyclerPrestador;
    private List<Prestador> prestadores = new ArrayList<>();
    private DatabaseReference firebaseRef;
    private AdapterPrestador adapterPrestador;
    private StorageReference storageReference;
    private FirebaseUser idat;

    private String idUsuarioLogado;
    private String urlImagem;
    private String nomeCliente;
    private String emailCliente;
    private Uri fotoCliente;

    //Drawer
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suporte_prestador);

        inicializarComponentes();

        //Toolbar
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("ServiceApp");
        setSupportActionBar(toolbar);


        //Drawer
        drawer = findViewById(R.id.drawerlayout3);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,toolbar,R.string.open_drawer,R.string.close_drawer);
        drawer.addDrawerListener(toggle);

        toggle.syncState();

        navigationView = findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(SuporteActivityPrestador.this);

        recuperaAuth();

        btOK();

    }


    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.menu_home:{
                Intent home = new Intent(SuporteActivityPrestador.this, PrestadorActivityDrawer.class);
                startActivity(home);
                break;
            }
            case R.id.menu_cont:{
                Toast.makeText(SuporteActivityPrestador.this, "Em breve..", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.menu_ordem:{
                Intent ordem = new Intent(SuporteActivityPrestador.this, OrdensServicoActivity.class);
                startActivity(ordem);
                break;
            }
            case R.id.menu_config:{
                Intent config = new Intent(SuporteActivityPrestador.this, ConfiguracoesClienteActivity.class);
                startActivity(config);
                break;
            }
            case R.id.menu_sup:{
                Intent sup = new Intent(SuporteActivityPrestador.this, SuporteActivityPrestador.class);
                startActivity(sup);
                break;
            }
            case R.id.menu_sair:{
                deslogarUsuario();
                break;
            }
        }

        drawer.closeDrawer(GravityCompat.START);


        return true;
    }

    private void recuperaAuth(){
        if (idat != null) {
            String nomeLogado = idat.getDisplayName();
            String emailLogado = idat.getEmail();
            Uri fotoURL = idat.getPhotoUrl();

            String userId = idat.getUid();
            nomeCliente = nomeLogado;
            emailCliente = emailLogado;
            fotoCliente = fotoURL;

            DatabaseReference clienteRef = firebaseRef
                    .child("clientes")
                    .child(idUsuarioLogado);
        }
    }

    private void inicializarComponentes(){
        searchView = findViewById(R.id.materialSearchView);
        recyclerPrestador = findViewById(R.id.recyclerPrestador);
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();
        idat = FirebaseAuth.getInstance().getCurrentUser();
    }

    private void deslogarUsuario(){
        try {
            autenticacao.signOut();
            finish();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void btOK(){
        Button bt = findViewById(R.id.bt_ok);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SuporteActivityPrestador.this, PrestadorActivityDrawer.class);
                startActivity(i);
            }
        });
    }

}