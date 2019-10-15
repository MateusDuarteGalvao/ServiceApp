package com.duarte.serviceapp.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
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

public class SuporteActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suporte);


        inicializarComponentes();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();
        idat = FirebaseAuth.getInstance().getCurrentUser();

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
        //Configurações ToolBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("ServiceApp");
        setSupportActionBar(toolbar);





        //Drawer
        AccountHeader conta = new AccountHeaderBuilder()
                .withActivity(this)
                .withCompactStyle(false)
                .withSavedInstance(savedInstanceState)
                .withThreeSmallProfileImages(false)
                .withHeaderBackground(R.drawable.bh)
                .addProfiles(
                        new ProfileDrawerItem()
                                .withName(nomeCliente)
                                .withEmail(emailCliente)
                                .withIcon(fotoCliente)
                ).build();

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
                        startActivity(new Intent(SuporteActivity.this, HomeActivity.class));
                        return false;
                    }
                }));

        result.addItem(new PrimaryDrawerItem().withName("Favoritos").withIcon(R.drawable.ic_fav).
                withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        Toast.makeText(SuporteActivity.this, "Em breve", Toast.LENGTH_SHORT).show();
                        //startActivity(new Intent(HomeActivity.this, HomeActivity.class));
                        return false;
                    }
                }));

        result.addItem(new PrimaryDrawerItem().withName("Contratos").withIcon(R.drawable.ic_contratos).
                withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        startActivity(new Intent(SuporteActivity.this, OrdensServicoActivity.class));

                        return false;
                    }
                }));

        result.addItem(new DividerDrawerItem());

        result.addItem(new PrimaryDrawerItem().withName("Configurações").withIcon(R.drawable.ic_config).
                withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        startActivity(new Intent(SuporteActivity.this, ConfiguracoesClienteActivity.class));
                        return false;
                    }
                }));

        result.addItem(new PrimaryDrawerItem().withName("Suporte").withIcon(R.drawable.ic_ajuda).
                withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        Toast.makeText(SuporteActivity.this, "Em breve", Toast.LENGTH_SHORT).show();
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


    }


    private void inicializarComponentes(){
        searchView = findViewById(R.id.materialSearchView);
        recyclerPrestador = findViewById(R.id.recyclerPrestador);
    }

    private void deslogarUsuario(){
        try {
            autenticacao.signOut();
            finish();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}