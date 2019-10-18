package com.duarte.serviceapp.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.duarte.serviceapp.R;

public class SelecaoUsuarioActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selecao_usuario);


    }

    public void abrirCadastroCliente(View view) {
        Intent i = new Intent(getApplicationContext(), CadastroClienteActivity.class);
        startActivity(i);
        finish();
    }

    public void abrirCadastroPrestador(View view) {
        Intent i = new Intent(getApplicationContext(), CadastroPrestadorActivity.class);
        startActivity(i);
        finish();
    }
}
