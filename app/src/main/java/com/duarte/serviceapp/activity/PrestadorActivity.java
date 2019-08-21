package com.duarte.serviceapp.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.support.v7.widget.Toolbar;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.duarte.serviceapp.R;
import com.duarte.serviceapp.helper.ConfiguracaoFirebase;
import com.google.firebase.auth.FirebaseAuth;

public class PrestadorActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prestador);

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        //Configurações ToolBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("ServiceApp - prestador");
        setSupportActionBar(toolbar);

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

        }

        return super.onOptionsItemSelected(item);
    }

    private void deslogarUsuario(){
        try {
            autenticacao.signOut();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void abrirConfiguracoes() {
        startActivity(new Intent(PrestadorActivity.this, ConfiguracaoFirebase.class));
    }

    private void abriNovoServico() {
        startActivity(new Intent(PrestadorActivity.this, NovoServicoPrestadorActivity.class));
    }



}
