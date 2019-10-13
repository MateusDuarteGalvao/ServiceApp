package com.duarte.serviceapp.activity;

import android.content.Intent;
import android.support.design.internal.NavigationMenu;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.duarte.serviceapp.R;
import com.duarte.serviceapp.adapter.AdapterPrestador;
import com.duarte.serviceapp.helper.ConfiguracaoFirebase;
import com.duarte.serviceapp.listener.RecyclerItemClickListener;
import com.duarte.serviceapp.model.Prestador;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.ArrayList;
import java.util.List;

import io.github.yavski.fabspeeddial.FabSpeedDial;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private MaterialSearchView searchView;
    private RecyclerView recyclerPrestador;
    private List<Prestador> prestadores = new ArrayList<>();
    private DatabaseReference firebaseRef;
    private AdapterPrestador adapterPrestador;


    //Drawer presente nas activitys: HomeActivity, PrestadorActivity, ServicosActivity, OrdensServcoActivity.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Botão flutuante

        FabSpeedDial fabSpeedDial = (FabSpeedDial) findViewById(R.id.fabSpeedDial2);
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
                    startActivity(new Intent(HomeActivity.this, ConfiguracoesClienteActivity.class));
                    return true;
                }
               /* if (id == R.id.serviços) {
                    startActivity(new Intent(HomeActivity.this, ServicosActivity.class));
                    return true;
                }*/
                if (id == R.id.chat) {
                    Toast.makeText(HomeActivity.this, "Em breve", Toast.LENGTH_SHORT).show();
                    //startActivity(new Intent(PrestadorActivity.this, ConfiguracoesPrestadorActivity.class));
                    return true;
                }
                return true;
            }

            @Override
            public void onMenuClosed() {

            }


        });


        inicializarComponentes();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

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
                        new ProfileDrawerItem().withName("Lucas").withEmail("Email").withIcon(R.drawable.perfil)
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
                startActivity(new Intent(HomeActivity.this, HomeActivity.class));
                return false;
            }
        }));

        result.addItem(new PrimaryDrawerItem().withName("Favoritos").withIcon(R.drawable.ic_fav).
                withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        Toast.makeText(HomeActivity.this, "Em breve", Toast.LENGTH_SHORT).show();
                        //startActivity(new Intent(HomeActivity.this, HomeActivity.class));
                        return false;
                    }
                }));

        result.addItem(new PrimaryDrawerItem().withName("Contratos").withIcon(R.drawable.ic_contratos).
                withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        startActivity(new Intent(HomeActivity.this, OrdensServicoActivity.class));

                        return false;
                    }
                }));

        result.addItem(new DividerDrawerItem());

        result.addItem(new PrimaryDrawerItem().withName("Configurações").withIcon(R.drawable.ic_config).
                withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        startActivity(new Intent(HomeActivity.this, ConfiguracoesClienteActivity.class));
                        return false;
                    }
                }));

        result.addItem(new PrimaryDrawerItem().withName("Suporte").withIcon(R.drawable.ic_suggestion).
                withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        Toast.makeText(HomeActivity.this, "Em breve", Toast.LENGTH_SHORT).show();
                        //startActivity(new Intent(HomeActivity.this, HomeActivity.class));
                        return false;
                    }
                }));



        //Configura recyclerView
        recyclerPrestador.setLayoutManager(new LinearLayoutManager(this));
        recyclerPrestador.setHasFixedSize(true);
        adapterPrestador = new AdapterPrestador(prestadores);
        recyclerPrestador.setAdapter( adapterPrestador );

        //Recupera prestadores para o cliente
        recuperarPrestadores();

        //Configuração do search view
        searchView.setHint("Pesquisar...");
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                pesquisarPrestadores( newText );

                return true;
            }
        });

        //Configurar evento de clique
        recyclerPrestador.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        this,
                        recyclerPrestador,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                                Prestador prestadorSelecionado = prestadores.get(position);
                                Intent i = new Intent(HomeActivity.this, ServicosActivity.class);
                                i.putExtra("prestador", prestadorSelecionado);
                                startActivity(i);

                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }
                )
        );

    }

    private void pesquisarPrestadores(String pesquisa){
        final DatabaseReference prestadoresRef = firebaseRef
                .child("prestadores");
        Query query = prestadoresRef.orderByChild("nome")
                .startAt(pesquisa)
                .endAt(pesquisa + "\uf8ff");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                prestadores.clear();

                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    prestadores.add( ds.getValue(Prestador.class) );
                }

                adapterPrestador.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void recuperarPrestadores(){

        DatabaseReference prestadorRef = firebaseRef.child("prestadores");
        prestadorRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                prestadores.clear();

                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    prestadores.add( ds.getValue(Prestador.class) );
                }

                adapterPrestador.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_cliente, menu);

        //Configurar botão de pesquisa
        MenuItem item = menu.findItem(R.id.menuPesquisa);
        searchView.setMenuItem(item);


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
        }

        return super.onOptionsItemSelected(item);
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

    private void abrirConfiguracoes() {
        startActivity(new Intent(HomeActivity.this, ConfiguracoesClienteActivity.class));
    }
}
