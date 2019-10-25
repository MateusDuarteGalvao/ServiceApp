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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.duarte.serviceapp.R;
import com.duarte.serviceapp.adapter.AdapterPrestador;
import com.duarte.serviceapp.helper.ConfiguracaoFirebase;
import com.duarte.serviceapp.helper.UsuarioFirebase;
import com.duarte.serviceapp.model.Cliente;
import com.duarte.serviceapp.model.Prestador;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

public class SuporteActivity extends HomeActivityDrawer {

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
    private TextView nomeAtual;
    private TextView emailAtual;
    private ImageView fotoAtual;

    private DrawerLayout drawer;
    private NavigationView navigationView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suporte);

        searchView = findViewById(R.id.materialSearchView);
        recyclerPrestador = findViewById(R.id.recyclerPrestador);
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();
        idat = FirebaseAuth.getInstance().getCurrentUser();

        drawer = findViewById(R.id.drawerlayout4);
        navigationView = findViewById(R.id.navView);

        View viewUser = navigationView.getHeaderView(0);
        View viewEmail = navigationView.getHeaderView(0);
        View viewFoto = navigationView.getHeaderView(0);

        emailAtual = viewEmail.findViewById(R.id.emailuser);
        nomeAtual = viewUser.findViewById(R.id.nomeuser);
        fotoAtual = viewFoto.findViewById(R.id.fotouser);

        DatabaseReference clienteRef = firebaseRef
                .child("clientes")
                .child(idUsuarioLogado);
        if (idat != null) {
            String nomeLogado = idat.getDisplayName();
            String emailLogado = idat.getEmail();
            Uri fotoURL = idat.getPhotoUrl();

            nomeCliente = nomeLogado;
            emailCliente = emailLogado;
            fotoCliente = fotoURL;
            emailAtual.setText(emailCliente);
            fotoAtual.setImageURI(fotoCliente);
        }

        clienteRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if( dataSnapshot.getValue() != null ){
                    Cliente cliente = dataSnapshot.getValue(Cliente.class);
                    nomeCliente = cliente.getNome();
                    nomeAtual.setText(nomeCliente);


                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        //Toolbar
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("ServiceApp");
        setSupportActionBar(toolbar);

        //Drawer


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,toolbar,R.string.open_drawer,R.string.close_drawer);
        drawer.addDrawerListener(toggle);

        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(SuporteActivity.this);
        viewFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent config = new Intent(SuporteActivity.this, PerfilClienteActivity.class);
                startActivity(config);

            }
        });


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
                Intent home = new Intent(SuporteActivity.this, HomeActivityDrawer.class);
                startActivity(home);
                break;
            }
            case R.id.menu_fav:{
                Toast.makeText(this, "Em breve..", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.menu_ordem:{
                Intent ordem = new Intent(SuporteActivity.this, OrdensServicoActivity.class);
                startActivity(ordem);
                break;
            }
            case R.id.menu_config:{
                Intent config = new Intent(SuporteActivity.this, PerfilClienteActivity.class);
                startActivity(config);
                break;
            }
            case R.id.menu_sup:{
                Intent sup = new Intent(SuporteActivity.this, SuporteActivity.class);
                startActivity(sup);
                break;
            }
            case R.id.menu_sair:{
                deslogarUsuario();
                break;
            }
        }

        return true;
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
                Intent i = new Intent(SuporteActivity.this, HomeActivityDrawer.class);
                startActivity(i);
            }
        });
    }

}