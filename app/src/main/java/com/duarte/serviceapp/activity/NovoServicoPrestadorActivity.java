package com.duarte.serviceapp.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.duarte.serviceapp.R;
import com.duarte.serviceapp.helper.UsuarioFirebase;
import com.duarte.serviceapp.model.Servico;

public class NovoServicoPrestadorActivity extends AppCompatActivity {

    //Inicializando atributos
    private EditText editServicoNome, editServicoDescricao, editServicoPreco;
    private String idUsuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_servico_prestador);

        //Configurações iniciais
        inicializarComponentes();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();

        //Configurações Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Novo serviço");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    public void validarDadosServico(View view){

        //Valida se os campos foram preenchidos
        String nome     = editServicoNome.getText().toString();
        String descricao = editServicoDescricao.getText().toString();
        String preco    = editServicoPreco.getText().toString();

        if ( !nome.isEmpty() ){
            if ( !descricao.isEmpty() ){
                if ( !preco.isEmpty() ){

                        Servico servico = new Servico();
                        servico.setIdUsuario( idUsuarioLogado );
                        servico.setNome( nome );
                        servico.setDescricao( descricao );
                        servico.setPreco( Double.parseDouble(preco) );

                        servico.salvar();
                        finish();
                        exibirMensagem("Serviço salvo com sucesso!");

                }else{
                    exibirMensagem("Digite o preço do serviço");
                }

            }else{
                exibirMensagem("Digite a descricao do serviço");
            }

        }else{
            exibirMensagem("Digite o nome do serviço");
        }
    }


    private void exibirMensagem(String texto){
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
    }

    private void inicializarComponentes(){
        editServicoNome = findViewById(R.id.editServicoNome);
        editServicoDescricao = findViewById(R.id.editServicoDescricao);
        editServicoPreco = findViewById(R.id.editServicoPreco);
    }

}
