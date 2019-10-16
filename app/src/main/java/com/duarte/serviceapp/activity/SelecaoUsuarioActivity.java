package com.duarte.serviceapp.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.duarte.serviceapp.R;
import com.duarte.serviceapp.helper.ConfiguracaoFirebase;
import com.duarte.serviceapp.helper.UsuarioFirebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SelecaoUsuarioActivity extends AppCompatActivity {

    private Button botaoAutenticaoPrestador, botaoAutenticacaoCliente;
    private FirebaseAuth autenticacao;
    private String idUsuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selecao_usuario);

        //Configurações inicais
        inicializaComponentes();
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        //Verificar usuario logado
        verificarUsuarioLogado();

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

    private void verificarUsuarioLogado() {
        FirebaseUser usuarioAtual = autenticacao.getCurrentUser();
        if ( usuarioAtual != null ){
            String tipoUsuario = usuarioAtual.getDisplayName();
            abrirTelaPrincipal(tipoUsuario);
        }
    }

    private void abrirTelaPrincipal(String tipoUsuario) {
        if(tipoUsuario.equals("prestador")) {//prestador
            startActivity(new Intent(getApplicationContext(),
                    PrestadorActivity.class));
        }
        else {//cliente
            startActivity(new Intent(getApplicationContext(),
                    HomeActivity.class));
        }
    }
}
