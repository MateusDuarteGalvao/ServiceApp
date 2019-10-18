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

public class LoginActivity extends AppCompatActivity {

    private Button buttonLogin, buttonCadastro;
    private EditText editEmail, editSenha;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Configurações iniciais
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        inicializaComponentes();

        //Verificar usuario logado
        verificarUsuarioLogado();

        /* Setando clique para o botão de login */
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editEmail.getText().toString();
                String senha = editSenha.getText().toString();

                if ( !email.isEmpty() ) {
                    if ( !senha.isEmpty() ) {
                        login(email, senha);
                    }
                    else {
                        Toast.makeText(LoginActivity.this,
                                "Preencha a senha!",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(LoginActivity.this,
                            "Preencha o E-mail!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        /* Setando clique para o botão de cadastro */
        buttonCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent i = new Intent(getApplicationContext(), SelecaoUsuarioActivity.class);
            startActivity(i);
            }
        });

    }

    private void login(String email, String senha) {
        //Autenticando com email e senha
        autenticacao.signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    Toast.makeText(LoginActivity.this,
                            "Logado com sucesso",
                            Toast.LENGTH_SHORT).show();
                    String tipoUsuario = task.getResult().getUser().getDisplayName();
                    abrirTelaPrincipal(tipoUsuario);

                }
                else {
                    Toast.makeText(LoginActivity.this,
                            "Erro ao fazer login",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void verificarUsuarioLogado() {
        FirebaseUser usuarioAtual = autenticacao.getCurrentUser();
        if ( usuarioAtual != null ) {
            String tipoUsuario = usuarioAtual.getDisplayName();
            abrirTelaPrincipal(tipoUsuario);
        }
    }

    private void abrirTelaPrincipal(String tipoUsuario) {
        if (tipoUsuario.equals("prestador")) {
            Intent i = new Intent(getApplicationContext(), PrestadorActivity.class);
            startActivity(i);
            finish();
        }
        else {
            Intent i = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(i);
            finish();
        }
    }

    private void inicializaComponentes() {
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonCadastro = findViewById(R.id.buttonCadastro);
        editEmail = findViewById(R.id.editLoginEmail);
        editSenha = findViewById(R.id.editLoginSenha);
    }
}
