package com.duarte.serviceapp.activity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.internal.NavigationMenu;
import android.support.design.widget.NavigationView;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.duarte.serviceapp.R;
import com.duarte.serviceapp.adapter.AdapterPrestador;
import com.duarte.serviceapp.helper.ConfiguracaoFirebase;
import com.duarte.serviceapp.helper.UsuarioFirebase;
import com.duarte.serviceapp.listener.RecyclerItemClickListener;
import com.duarte.serviceapp.model.Cliente;
import com.duarte.serviceapp.model.Prestador;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;
import io.github.yavski.fabspeeddial.FabSpeedDial;

public class HomeActivityDrawer extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth autenticacao;
    private MaterialSearchView searchView;
    private RecyclerView recyclerPrestador;
    private List<Prestador> prestadores = new ArrayList<>();
    private DatabaseReference firebaseRef;
    private AdapterPrestador adapterPrestador;
    private StorageReference storageReference;
    private FirebaseUser idat;

    //Recuperação de dados
    private String idUsuarioLogado;
    private String urlImagem;
    private String nomeCliente;
    private String emailCliente;
    private Uri fotoCliente;
    private TextView nomeAtual;
    private TextView emailAtual;
    private ImageView fotoAtual;
    private String numeroTel;
    private android.app.AlertDialog dialog;


    //Drawer
    private DrawerLayout drawer;
    private NavigationView navigationView;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_drawer);

        //Inicia componentes

        firebaseRef = FirebaseDatabase.getInstance().getReference();
        recyclerPrestador = findViewById(R.id.recyclerPrestador);
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();
        idat = FirebaseAuth.getInstance().getCurrentUser();

        //Configura recyclerView
        recyclerPrestador.setLayoutManager(new LinearLayoutManager(this));
        recyclerPrestador.setHasFixedSize(true);
        adapterPrestador = new AdapterPrestador(prestadores);
        recyclerPrestador.setAdapter( adapterPrestador );

        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Carregando dados")
                .setCancelable(false)
                .build();
        dialog.show();

        //Recupera prestadores para o cliente
        DatabaseReference prestadorRef = firebaseRef.child("prestadores");
        prestadorRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                prestadores.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    prestadores.add( ds.getValue(Prestador.class) );
                }

                dialog.dismiss();
                adapterPrestador.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        navigationView = findViewById(R.id.navView);
        drawer = findViewById(R.id.drawerlayout);
        View viewUser = navigationView.getHeaderView(0);
        View viewEmail = navigationView.getHeaderView(0);
        View viewFoto = navigationView.getHeaderView(0);

        emailAtual = viewEmail.findViewById(R.id.emailuser);
        nomeAtual = viewUser.findViewById(R.id.nomeuser);
        fotoAtual = viewFoto.findViewById(R.id.fotouser);

        Toolbar toolbar = findViewById(R.id.toolbar);
        searchView = findViewById(R.id.materialSearchView);

        DatabaseReference clienteRef = firebaseRef
                .child("clientes")
                .child(idUsuarioLogado);
        if (idat != null) {
            String nomeLogado = idat.getDisplayName();
            String emailLogado = idat.getEmail();
            //Uri fotoURL = idat.getPhotoUrl();
            numeroTel = idat.getPhoneNumber();

            nomeCliente = nomeLogado;
            emailCliente = emailLogado;
            //fotoCliente = fotoURL;
            emailAtual.setText(emailCliente);
            //fotoAtual.setImageURI(fotoCliente);
        }

        //Toolbar
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);

        //Configuração do search view
        configSearchView();

        clienteRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if( dataSnapshot.getValue() != null ){
                    Cliente cliente = dataSnapshot.getValue(Cliente.class);
                    nomeCliente = cliente.getNome();
                    nomeAtual.setText(nomeCliente);
                    String fURL = cliente.getUrlImagem();
                    Picasso.get().load(fURL)
                            .resize(300, 300)
                            .centerCrop()
                            .into(fotoAtual, new Callback() {
                                @Override
                                public void onSuccess() {
                                    Bitmap imageBitmap = ((BitmapDrawable) fotoAtual.getDrawable()).getBitmap();
                                    RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory.create(getResources(), imageBitmap);
                                    imageDrawable.setCircular(true);
                                    imageDrawable.setCornerRadius(Math.max(imageBitmap.getWidth(), imageBitmap.getHeight()) / 2.0f);
                                    fotoAtual.setImageDrawable(imageDrawable);
                                }

                                @Override
                                public void onError(Exception e) {

                                }

                            });
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        //Botão flutuante
        botaoFlutuante();

        //Drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,toolbar,R.string.open_drawer,R.string.close_drawer);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(HomeActivityDrawer.this);
        viewFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent config = new Intent(HomeActivityDrawer.this, PerfilClienteActivity.class);
                startActivity(config);

            }
        });


        //Configurar evento de clique
        configEventClique();

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
                Intent home = new Intent(HomeActivityDrawer.this, HomeActivityDrawer.class);
                startActivity(home);
                break;
            }
            case R.id.menu_fav:{
                Toast.makeText(this, "Em breve..", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.menu_ordem:{
                Intent ordem = new Intent(HomeActivityDrawer.this, OrdensServicoClienteActivity.class);
                startActivity(ordem);
                break;
            }
            case R.id.menu_config:{
                Intent config = new Intent(HomeActivityDrawer.this, PerfilClienteActivity.class);
                startActivity(config);
                break;
            }
            case R.id.menu_sup:{
                Intent sup = new Intent(HomeActivityDrawer.this, SuporteActivity.class);
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


    /*------------------------------------------------------------*/

    private void configSearchView(){
        searchView.setHint("Pesquisar...");
        searchView.setVoiceSearch(true);
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


    }

    private void configEventClique(){
        recyclerPrestador.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        this,
                        recyclerPrestador,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                                Prestador prestadorSelecionado = prestadores.get(position);
                                Intent i = new Intent(HomeActivityDrawer.this, ServicosActivity.class);
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

    private void deslogarUsuario(){
        try {
            autenticacao.signOut();
            finish();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void abrirConfiguracoes() {
        startActivity(new Intent(HomeActivityDrawer.this, PerfilClienteActivity.class));
    }

    private  void botaoFlutuante(){
        FabSpeedDial fabSpeedDial = findViewById(R.id.fabSpeedDial);
        fabSpeedDial.setMenuListener(new FabSpeedDial.MenuListener() {
            @Override
            public boolean onPrepareMenu(NavigationMenu navigationMenu) {
                return true;
            }

            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();

                if (id == R.id.menuConfiguracoes) {
                    startActivity(new Intent(getBaseContext(), PerfilClienteActivity.class));
                    return true;
                }
                if (id == R.id.serviços) {
                    startActivity(new Intent(getBaseContext(), OrdensServicoClienteActivity.class));
                    return true;
                }
                if (id == R.id.fav) {
                    Toast.makeText(getBaseContext(), "Em breve!", Toast.LENGTH_SHORT).show();

                    return true;
                }
                return true;
            }

            @Override
            public void onMenuClosed() {

            }

        });
    }

    public void dialogContato(String numero){

        String contato = numero.replace(" ", "")
                .replace("-", "")
                .replace("(", "")
                .replace(")", "");

        final StringBuffer numeroContato = new StringBuffer(contato);

        AlertDialog.Builder builder = new AlertDialog.Builder(getBaseContext())
                .setTitle("Entrar em Contato")
                .setMessage("O que deseja fazer?")
                .setPositiveButton("whatsApp", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        contatoZap(numeroContato);
                    }
                })
                .setNegativeButton("Ligar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        contatoLigar();
                    }
                });


    }

    public void contatoZap(StringBuffer numeroContato){
        numeroContato.deleteCharAt(2);

        Intent intent = new Intent("android.intent.action.MAIN");
        intent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
        intent.putExtra("jid",
                PhoneNumberUtils.stripSeparators("55"+numeroContato) + "@s.whatsapp.net");

        startActivity(intent);

    }

    public void contatoLigar(){


    }



}