package com.duarte.serviceapp.activity;

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
import com.duarte.serviceapp.model.Cliente;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class CadastroClienteActivity extends AppCompatActivity {

    private EditText campoNome, campoEndereco, campoTelefone, campoEmail, campoSenha;
    private Button buttonCadastro, buttonCancelar;
    private Cliente cliente;
    private String idUsuario;

    private FirebaseAuth autenticacao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_cliente);

        //Configurações
        inicializaComponentes();

        buttonCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nome = campoNome.getText().toString();
                String endereco = campoEndereco.getText().toString();
                String telefone = campoTelefone.getText().toString();
                String email = campoEmail.getText().toString();
                String senha = campoSenha.getText().toString();


                //Validação de campos
                if ( !nome.isEmpty() ) {
                    if ( !endereco.isEmpty() ) {
                        if ( !telefone.isEmpty() ) {
                            if ( !email.isEmpty() ) {
                                if ( !senha.isEmpty() ) {

                                    cliente = new Cliente();
                                    cliente.setNome( nome );
                                    cliente.setEndereco( endereco );
                                    cliente.setTelefone( telefone );
                                    cliente.setEmail( email );
                                    cliente.setSenha( senha );
                                    cadastrarCliente();

                                }
                                else {
                                    Toast.makeText(CadastroClienteActivity.this,
                                            "Digite sua senha",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                            else {
                                Toast.makeText(CadastroClienteActivity.this,
                                        "Digite seu e-mail",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            Toast.makeText(CadastroClienteActivity.this,
                                    "Digite seu telefone",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Toast.makeText(CadastroClienteActivity.this,
                                "Digite seu endereco",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(CadastroClienteActivity.this,
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

    private void cadastrarCliente() {

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(
                cliente.getEmail(), cliente.getSenha()
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    Toast.makeText(CadastroClienteActivity.this,
                            "Cadastro realizado com sucesso!",
                            Toast.LENGTH_SHORT).show();
                    String tipoUsuario = "cliente";
                    UsuarioFirebase.atualizarTipoUsuario(tipoUsuario);

                    idUsuario = UsuarioFirebase.getIdUsuario();
                    Cliente clienteCadastro = new Cliente();
                    clienteCadastro.setIdUsuario( idUsuario );
                    clienteCadastro.setNome( cliente.getNome() );
                    clienteCadastro.setEndereco( cliente.getEndereco() );
                    clienteCadastro.setTelefone( cliente.getTelefone() );
                    clienteCadastro.salvar();
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
                    Toast.makeText( CadastroClienteActivity.this,
                            "Erro: " + erroExcecao ,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void inicializaComponentes() {
        campoNome = findViewById(R.id.editClienteCadastroNome);
        campoEndereco = findViewById(R.id.editClienteCadastroEndereco);
        campoTelefone = findViewById(R.id.editClienteCadastroTelefone);
        campoEmail = findViewById(R.id.editClienteCadastroEmail);
        campoSenha = findViewById(R.id.editClienteCadastroSenha);
        buttonCadastro = findViewById(R.id.buttonCadastro);
        buttonCancelar = findViewById(R.id.buttonCancelar);
    }
}
