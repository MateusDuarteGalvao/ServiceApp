package com.duarte.serviceapp.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class AutenticacaoClienteActivity extends AppCompatActivity {

    private Button buttonLoginCliente, buttonCadastroCliente;
    private EditText editClienteEmail, editClienteSenha;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autenticacao_cliente);

        //Configurações iniciais
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        inicializaComponentes();

        //Verificar usuario logado
        verificarUsuarioLogado();

        /* Setando clique para o botão de login */
        buttonLoginCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editClienteEmail.getText().toString();
                String senha = editClienteSenha.getText().toString();

                if ( !email.isEmpty() ) {
                    if ( !senha.isEmpty() ) {
                        loginCliente(email, senha);
                    }
                    else {
                        Toast.makeText(AutenticacaoClienteActivity.this,
                                "Preencha a senha!",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(AutenticacaoClienteActivity.this,
                            "Preencha o E-mail!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        /* Setando clique para o botão de cadastro */
        buttonCadastroCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), CadastroClienteActivity.class);
                startActivity(i);
            }
        });


    }

    private void loginCliente(String email, String senha) {
        //Autenticando com email e senha
        autenticacao.signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    Toast.makeText(AutenticacaoClienteActivity.this,
                            "Logado com sucesso",
                            Toast.LENGTH_SHORT).show();
                    abrirTelaPrincipal();

                }
                else {
                    Toast.makeText(AutenticacaoClienteActivity.this,
                            "Erro ao fazer login",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void verificarUsuarioLogado() {
        FirebaseUser usuarioAtual = autenticacao.getCurrentUser();
        if ( usuarioAtual != null ){
            abrirTelaPrincipal();
        }

    }



    private void abrirTelaPrincipal() {
        Intent i = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(i);
    }

    private void inicializaComponentes() {
        buttonLoginCliente = findViewById(R.id.buttonLoginCliente);
        buttonCadastroCliente = findViewById(R.id.buttonCadastroCliente);
        editClienteEmail = findViewById(R.id.editClienteLoginEmail);
        editClienteSenha = findViewById(R.id.editClienteLoginSenha);
    }
}
