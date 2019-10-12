package com.duarte.serviceapp.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.duarte.serviceapp.R;

public class SelecaoUsuarioActivity extends AppCompatActivity {

    private Button botaoAutenticaoPrestador, botaoAutenticacaoCliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selecao_usuario);

        //Configurações inicais
        inicializaComponentes();

        botaoAutenticacaoCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),
                        AutenticacaoClienteActivity.class));
            }
        });

        botaoAutenticaoPrestador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),
                        AutenticacaoPrestadorActivity.class));
            }
        });
    }


    private void inicializaComponentes() {
        botaoAutenticacaoCliente = findViewById(R.id.botaoAutenticaoCliente);
        botaoAutenticaoPrestador = findViewById(R.id.botaoAutenticaoPrestador);
    }
}
