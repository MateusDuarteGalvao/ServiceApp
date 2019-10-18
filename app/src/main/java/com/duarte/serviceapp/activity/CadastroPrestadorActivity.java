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
import com.duarte.serviceapp.helper.UsuarioFirebase;
import com.duarte.serviceapp.model.Prestador;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class CadastroPrestadorActivity extends AppCompatActivity {

    private EditText campoNome, campoTelefone, campoEmail, campoSenha;
    private Button buttonCadastro, buttonCancelar;
    private Prestador prestador;
    private String idUsuario;

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_prestador);

        //Configurações
        inicializaComponentes();

        buttonCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nome = campoNome.getText().toString();
                String telefone = campoTelefone.getText().toString();
                String email = campoEmail.getText().toString();
                String senha = campoSenha.getText().toString();

                if ( !nome.isEmpty() ) {
                    if ( !telefone.isEmpty() ) {
                        if ( !email.isEmpty() ) {
                            if ( !senha.isEmpty() ) {

                                prestador = new Prestador();
                                prestador.setNome( nome );
                                prestador.setTelefone( telefone );
                                prestador.setEmail( email );
                                prestador.setSenha( senha );
                                cadastrarPrestador();

                            }
                            else {
                                Toast.makeText(CadastroPrestadorActivity.this,
                                        "Digite a senha",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            Toast.makeText(CadastroPrestadorActivity.this,
                                    "Digite seu e-mail",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Toast.makeText(CadastroPrestadorActivity.this,
                                "Digite seu telefone",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(CadastroPrestadorActivity.this,
                            "Digite seu nome",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });

        buttonCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void cadastrarPrestador() {

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(
                prestador.getEmail(), prestador.getSenha()
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    Toast.makeText(CadastroPrestadorActivity.this,
                            "Cadastro realizado com sucesso!",
                            Toast.LENGTH_SHORT).show();
                    String tipoUsuario = "prestador";
                    UsuarioFirebase.atualizarTipoUsuario(tipoUsuario);

                    idUsuario = UsuarioFirebase.getIdUsuario();
                    Prestador prestadorCadastro = new Prestador();
                    prestadorCadastro.setIdUsuario( idUsuario );
                    prestadorCadastro.setEmail( prestador.getEmail() );
                    prestadorCadastro.setNome( prestador.getNome() );
                    prestadorCadastro.setTelefone( prestador.getTelefone() );
                    prestadorCadastro.setCategoria("");
                    prestadorCadastro.setPrecoHora(0.00);
                    prestadorCadastro.setTempo("");
                    prestadorCadastro.salvar();

                    abrirTelaPrincipal();

                    finish();
                }
                else {
                    String erroExcecao = "";
                    try{
                        throw task.getException();
                    }catch (FirebaseAuthWeakPasswordException e) {
                        erroExcecao = "Digite uma senha mais forte!";
                    }catch (FirebaseAuthInvalidCredentialsException e) {
                        erroExcecao = "Por favor, digite um e-mail válido!";
                    }catch (FirebaseAuthUserCollisionException e) {
                        erroExcecao = "Essa conta já foi cadastrada!";
                    }catch (Exception e) {
                        erroExcecao = "ao cadastrar usuário: " + e.getMessage();
                        e.printStackTrace();
                    }
                    Toast.makeText( CadastroPrestadorActivity.this,
                            "Erro: " + erroExcecao ,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void abrirTelaPrincipal() {
        Intent i = new Intent(getApplicationContext(), PrestadorActivity.class);
        startActivity(i);
    }

    private void inicializaComponentes() {
        campoNome = findViewById(R.id.editPrestadorCadastroNome);
        campoTelefone = findViewById(R.id.editPrestadorCadastroTelefone);
        campoEmail = findViewById(R.id.editPrestadorCadastroEmail);
        campoSenha = findViewById(R.id.editPrestadorCadastroSenha);
        buttonCadastro = findViewById(R.id.buttonCadastro);
        buttonCancelar = findViewById(R.id.buttonCancelar);
    }
}
