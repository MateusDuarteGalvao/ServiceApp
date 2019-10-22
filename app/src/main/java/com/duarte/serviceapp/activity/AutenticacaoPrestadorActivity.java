package com.duarte.serviceapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.duarte.serviceapp.R;
import com.duarte.serviceapp.helper.ConfiguracaoFirebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AutenticacaoPrestadorActivity extends AppCompatActivity {

    private Button buttonLoginPrestador, buttonCadastroPrestador;
    private EditText editPrestadorEmail, editPrestadorSenha;

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autenticacao_prestador);

        //Configurações iniciais
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        inicializaComponentes();

        //Verificar usuario logado
        verificarUsuarioLogado();

        /* Setando clique para o botão de login */
        buttonLoginPrestador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = editPrestadorEmail.getText().toString();
                String senha = editPrestadorSenha.getText().toString();

                if ( !email.isEmpty() ) {
                    if ( !senha.isEmpty() ) {
                        loginPrestador(email, senha);
                    }
                    else {
                        Toast.makeText(AutenticacaoPrestadorActivity.this,
                                "Preencha a senha!",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(AutenticacaoPrestadorActivity.this,
                            "Preencha o E-mail!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        /* Setando clique para o botão de cadastro */
        buttonCadastroPrestador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), CadastroPrestadorActivity.class);
                startActivity(i);
            }
        });
    }

    private void loginPrestador(String email, String senha) {
        //Autenticando com email e senha
        autenticacao.signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Toast.makeText(AutenticacaoPrestadorActivity.this,
                                    "Logado com sucesso",
                                    Toast.LENGTH_SHORT).show();
                            abrirTelaPrincipal();

                        }
                        else {
                            Toast.makeText(AutenticacaoPrestadorActivity.this,
                                    "Erro ao fazer login",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void verificarUsuarioLogado() {
        FirebaseUser usuarioAtual = autenticacao.getCurrentUser();
        if ( usuarioAtual != null ){
            abrirTelaPrincipal();
        }
    }

    private void abrirTelaPrincipal() {
        Intent i = new Intent(getApplicationContext(), PrestadorActivityDrawer.class);
        startActivity(i);
    }

    private void inicializaComponentes() {
        editPrestadorEmail = findViewById(R.id.editPrestadorLoginEmail);
        editPrestadorSenha = findViewById(R.id.editPrestadorLoginSenha);
        buttonCadastroPrestador = findViewById(R.id.buttonCadastroPrestador);
        buttonLoginPrestador = findViewById(R.id.buttonLoginPrestador);
    }
}
