package com.duarte.serviceapp.activity;

import android.content.Intent;
import android.support.design.internal.NavigationMenu;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.support.v7.widget.Toolbar;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import com.duarte.serviceapp.R;
import com.duarte.serviceapp.adapter.AdapterServico;
import com.duarte.serviceapp.helper.ConfiguracaoFirebase;
import com.duarte.serviceapp.helper.UsuarioFirebase;
import com.duarte.serviceapp.listener.RecyclerItemClickListener;
import com.duarte.serviceapp.model.Servico;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.FabSpeedDialBehaviour;


public class PrestadorActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private RecyclerView recyclerServicos;
    private AdapterServico adapterServico;
    private List<Servico> servicos = new ArrayList<>();
    private DatabaseReference firebaseRef;
    private String idUsuarioLogado;

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
                    startActivity(new Intent(PrestadorActivity.this, ConfiguracoesPrestadorActivity.class));
                    return true;
                }
                if (id == R.id.ordens) {
                    startActivity(new Intent(PrestadorActivity.this, OrdensServicoActivity.class));
                    return true;
                }
                if (id == R.id.chat) {
                    Toast.makeText(PrestadorActivity.this, "Em breve", Toast.LENGTH_SHORT).show();
                    //startActivity(new Intent(PrestadorActivity.this, ConfiguracoesPrestadorActivity.class));
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

        //Configurações ToolBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("ServiceApp - prestador");
        setSupportActionBar(toolbar);

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
        startActivity(new Intent(PrestadorActivity.this, ConfiguracoesPrestadorActivity.class));
    }

    private void abriNovoServico() {
        startActivity(new Intent(PrestadorActivity.this, NovoServicoPrestadorActivity.class));
    }

    private void abrirOrdensServico() {
        startActivity(new Intent(PrestadorActivity.this, OrdensServicoActivity.class));
    }


}
