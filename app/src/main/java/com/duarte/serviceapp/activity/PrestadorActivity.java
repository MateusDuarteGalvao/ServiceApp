package com.duarte.serviceapp.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.design.internal.NavigationMenu;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.Menu;
import android.support.v7.widget.Toolbar;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.duarte.serviceapp.R;
import com.duarte.serviceapp.adapter.AdapterServico;
import com.duarte.serviceapp.helper.ConfiguracaoFirebase;
import com.duarte.serviceapp.helper.UsuarioFirebase;
import com.duarte.serviceapp.listener.RecyclerItemClickListener;
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

import java.util.ArrayList;
import java.util.List;

import io.github.yavski.fabspeeddial.FabSpeedDial;


public class PrestadorActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private RecyclerView recyclerServicos;
    private AdapterServico adapterServico;
    private List<Servico> servicos = new ArrayList<>();
    private DatabaseReference firebaseRef;
    private String idUsuarioLogado;

    private FirebaseUser idat;


    private String urlImagem;
    private String nomePrestador;
    private String emailPrestador;
    private Uri fotoPrestador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prestador);


        //Botão flutuante

        FabSpeedDial fabSpeedDial = (FabSpeedDial) findViewById(R.id.fabSpeedDial);
        fabSpeedDial.setMenuListener(new FabSpeedDial.MenuListener() {
            @Override
            public boolean onPrepareMenu(NavigationMenu navigationMenu) {
                return true;
            }

            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {
                //Toast.makeText(PrestadorActivity.this, "" + menuItem.getTitle(), Toast.LENGTH_SHORT).show();
                int id = menuItem.getItemId();

                if (id == R.id.menuConfiguracoes) {
                    startActivity(new Intent(PrestadorActivity.this, PerfilPrestadorActivity.class));
                    return true;
                }
                if (id == R.id.ordens) {
                    startActivity(new Intent(PrestadorActivity.this, OrdensServicoActivity.class));
                    return true;
                }
                if (id == R.id.chat) {
                    Toast.makeText(PrestadorActivity.this, "Em breve", Toast.LENGTH_SHORT).show();
                    //startActivity(new Intent(PrestadorActivity.this, PerfilPrestadorActivity.class));
                    return true;
                }
                return true;
            }

            @Override
            public void onMenuClosed() {

            }


        });


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

            nomePrestador = nomeLogado;
            emailPrestador = emailLogado;
            fotoPrestador = fotoURL;
        }

        //Configurações ToolBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("ServiceApp - prestador");
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
                        startActivity(new Intent(PrestadorActivity.this, HomeActivity.class));
                        return false;
                    }
                }));

        result.addItem(new PrimaryDrawerItem().withName("Favoritos").withIcon(R.drawable.ic_fav).
                withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        Toast.makeText(PrestadorActivity.this, "Em breve", Toast.LENGTH_SHORT).show();
                        //startActivity(new Intent(HomeActivity.this, HomeActivity.class));
                        return false;
                    }
                }));

        result.addItem(new PrimaryDrawerItem().withName("Contratos").withIcon(R.drawable.ic_contratos).
                withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        startActivity(new Intent(PrestadorActivity.this, OrdensServicoActivity.class));

                        return false;
                    }
                }));

        result.addItem(new DividerDrawerItem());

        result.addItem(new PrimaryDrawerItem().withName("Configurações").withIcon(R.drawable.ic_config).
                withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        startActivity(new Intent(PrestadorActivity.this, PerfilPrestadorActivity.class));
                        return false;
                    }
                }));

        result.addItem(new PrimaryDrawerItem().withName("Suporte").withIcon(R.drawable.ic_ajuda).
                withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        //Toast.makeText(PrestadorActivity.this, "Em breve", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(PrestadorActivity.this, SuporteActivity.class));
                        return false;
                    }
                }));

        result.addItem(new PrimaryDrawerItem().withName("Sair").withIcon(R.drawable.bt_sair).
                withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        deslogarUsuario();
                        return true;
                    }
                }));


        //Configura recyclerView
        recyclerServicos.setLayoutManager(new LinearLayoutManager(this));
        recyclerServicos.setHasFixedSize(true);
        adapterServico = new AdapterServico(servicos, this);
        recyclerServicos.setAdapter( adapterServico );

        //Recupera serviços para o prestador
        recuperarServicos();

        //Adiciona o evento de clique no recyclerView
        recyclerServicos.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        this,
                        recyclerServicos,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                            }

                            @Override
                            public void onLongItemClick(View view, int position) {
                                Servico servicoSelecionado = servicos.get(position);




                                servicoSelecionado.remover();
                                Toast.makeText(PrestadorActivity.this,
                                        "Servico excluído com sucesso",
                                        Toast.LENGTH_SHORT)
                                        .show();
                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }
                )
        );
    }

    private void recuperarServicos(){

        DatabaseReference servicosRef = firebaseRef
                .child("servicos")
                .child( idUsuarioLogado );

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
        recyclerServicos = findViewById(R.id.recyclerServicos);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_prestador, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.menuSair :
                deslogarUsuario();
                break;
            case R.id.menuConfiguracoes :
                abrirConfiguracoes();
                break;
            case R.id.menuNovoServico :
                abriNovoServico();
                break;
            case R.id.menuOrdensServico :
                abrirOrdensServico();
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    private void deslogarUsuario(){
        try {
            autenticacao.signOut();
            finish();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void abrirConfiguracoes() {
        Intent i = new Intent(PrestadorActivity.this, PerfilPrestadorActivity.class);
        startActivity(i);
    }

    private void abriNovoServico() {
        Intent i = new Intent(PrestadorActivity.this, NovoServicoPrestadorActivity.class);
        startActivity(i);
    }

    private void abrirOrdensServico() {
        Intent i = new Intent(PrestadorActivity.this, OrdensServicoActivity.class);
        startActivity(i);
    }


}
