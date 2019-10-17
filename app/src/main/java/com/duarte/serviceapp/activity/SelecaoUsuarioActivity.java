package com.duarte.serviceapp.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.duarte.serviceapp.R;

public class SelecaoUsuarioActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selecao_usuario);


    }

    public void autenticarCliente(View view) {
        Intent i = new Intent(getApplicationContext(), AutenticacaoClienteActivity.class);
        startActivity(i);
    }

    public void autenticarPrestador(View view) {
        Intent i = new Intent(getApplicationContext(), AutenticacaoPrestadorActivity.class);
        startActivity(i);
    }
}
