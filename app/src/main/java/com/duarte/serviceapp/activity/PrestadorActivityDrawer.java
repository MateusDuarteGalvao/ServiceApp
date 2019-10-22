package com.duarte.serviceapp.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.internal.NavigationMenu;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
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

import java.util.ArrayList;
import java.util.List;

import io.github.yavski.fabspeeddial.FabSpeedDial;

public class PrestadorActivityDrawer extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

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

    //Drawer
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prestador_drawer);

        botaoFlutuante();

        //Toolbar
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("ServiceApp");
        setSupportActionBar(toolbar);

        //Drawer
        drawer = findViewById(R.id.drawerlayout2);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,toolbar,R.string.open_drawer,R.string.close_drawer);
        drawer.addDrawerListener(toggle);

        toggle.syncState();

        navigationView = findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(PrestadorActivityDrawer.this);

        //Inicia os componentes
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();
        recyclerServicos = findViewById(R.id.recyclerServicos);

        //Configura recyclerView

        recyclerServicos.setLayoutManager(new LinearLayoutManager(this));
        recyclerServicos.setHasFixedSize(true);
        adapterServico = new AdapterServico(servicos, this);
        recyclerServicos.setAdapter(adapterServico);


        //recupera dados autenticados
        recuperaDAuth();

        //Recupera serviços do prestador
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

        //Adiciona o evento de clique no recyclerView
        clicRecicler();

    }

    //Drawer------------------------------------------------------------------

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
                Intent home = new Intent(PrestadorActivityDrawer.this, PrestadorActivityDrawer.class);
                startActivity(home);
                break;
            }
            case R.id.menu_cont:{
                Toast.makeText(PrestadorActivityDrawer.this, "Em breve..", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.menu_ordem:{
                Intent ordem = new Intent(PrestadorActivityDrawer.this, OrdensServicoActivity.class);
                startActivity(ordem);
                break;
            }
            case R.id.menu_config:{
                Intent config = new Intent(PrestadorActivityDrawer.this, ConfiguracoesClienteActivity.class);
                startActivity(config);
                break;
            }
            case R.id.menu_sup:{
                Intent sup = new Intent(PrestadorActivityDrawer.this, SuporteActivityPrestador.class);
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


    //-------------------------------------------------------------------

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

    private void clicRecicler(){
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
                                Toast.makeText(PrestadorActivityDrawer.this,
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

    private void abrirConfiguracoes() {
        startActivity(new Intent(PrestadorActivityDrawer.this, ConfiguracoesPrestadorActivity.class));
    }

    private void abriNovoServico() {
        startActivity(new Intent(PrestadorActivityDrawer.this, NovoServicoPrestadorActivity.class));
    }

    private void abrirOrdensServico() {
        startActivity(new Intent(PrestadorActivityDrawer.this, OrdensServicoActivity.class));
    }

    private void recuperaDAuth(){
        idat = FirebaseAuth.getInstance().getCurrentUser();

        if (idat != null){
            String nomeLogado = idat.getDisplayName();
            String emailLogado = idat.getEmail();
            Uri fotoURL = idat.getPhotoUrl();

            nomePrestador = nomeLogado;
            emailPrestador = emailLogado;
            fotoPrestador = fotoURL;
        }
    }

    protected void botaoFlutuante(){
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
                    startActivity(new Intent(PrestadorActivityDrawer.this, ConfiguracoesPrestadorActivity.class));
                    return true;
                }
                if (id == R.id.ordens) {
                    startActivity(new Intent(PrestadorActivityDrawer.this, OrdensServicoActivity.class));
                    return true;
                }
                if (id == R.id.chat) {
                    Toast.makeText(PrestadorActivityDrawer.this, "Em breve", Toast.LENGTH_SHORT).show();
                    //startActivity(new Intent(PrestadorActivity.this, ConfiguracoesPrestadorActivity.class));
                    return true;
                }
                return true;
            }

            @Override
            public void onMenuClosed() {

            }


        });
    }

}