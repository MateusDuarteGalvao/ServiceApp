package com.duarte.serviceapp.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.duarte.serviceapp.R;
import com.duarte.serviceapp.helper.ConfiguracaoFirebase;
import com.duarte.serviceapp.helper.UsuarioFirebase;
import com.duarte.serviceapp.model.Cliente;
import com.duarte.serviceapp.model.Prestador;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import dmax.dialog.SpotsDialog;

public class ConfiguracoesClienteActivity extends AppCompatActivity {

    //Inicializando atributos
    private EditText editClienteNome, editClienteEndereco, editClienteTelefone;
    private ImageView imagePerfilCliente;
    private AlertDialog dialog;

    private static final int SELECAO_GALERIA = 200;
    private StorageReference storageReference;
    private DatabaseReference firebaseRef;
    private String idUsuarioLogado;
    private String urlImagemSelecionada = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes_cliente);

        //Configurações iniciais
        inicializarComponentes();
        storageReference = ConfiguracaoFirebase.getFirebaseStorage();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();

        //Configurações Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Configuração");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Adicionando evento de click na imagem
        imagePerfilCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                );
                if( i.resolveActivity(getPackageManager()) != null ){
                    startActivityForResult(i, SELECAO_GALERIA);
                }
            }
        });

        //Recuperar dados do cliente
        recuperarDadosCliente();

    }

    private void recuperarDadosCliente(){

        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Carregando dados")
                .setCancelable(false)
                .build();
        dialog.show();

        DatabaseReference clienteRef = firebaseRef
                .child("clientes")
                .child( idUsuarioLogado );
        clienteRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if( dataSnapshot.getValue() != null ){
                    Cliente cliente = dataSnapshot.getValue(Cliente.class);
                    editClienteNome.setText(cliente.getNome());
                    editClienteEndereco.setText(cliente.getEndereco());
                    editClienteTelefone.setText(cliente.getTelefone());

                    urlImagemSelecionada = cliente.getUrlImagem();
                    if ( urlImagemSelecionada != "" ) {
                        Picasso.get()
                                .load(urlImagemSelecionada)
                                .into(imagePerfilCliente);
                    }

                    dialog.dismiss();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void validarDadosCliente(View view){

        //Valida se os campos foram preenchidos
        String nome = editClienteNome.getText().toString();
        String endereco = editClienteEndereco.getText().toString();
        String telefone = editClienteTelefone.getText().toString();

        if ( !nome.isEmpty() ){
            if ( !endereco.isEmpty() ){
                if ( !telefone.isEmpty() ){

                    Cliente cliente = new Cliente();
                    cliente.setIdUsuario( idUsuarioLogado );
                    cliente.setNome( nome );
                    cliente.setEndereco( endereco );
                    cliente.setTelefone( telefone );
                    cliente.setUrlImagem( urlImagemSelecionada );
                    cliente.salvar();

                    exibirMensagem("Dados atualizados com sucesso!");
                    finish();

                }else{
                    exibirMensagem("Digite um telefone!");
                }

            }else{
                exibirMensagem("Digite seu endereço");
            }

        }else{
            exibirMensagem("Digite seu nome");
        }
    }

    private void exibirMensagem(String texto){
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if( resultCode == RESULT_OK ){
            Bitmap imagem = null;

            try {


                switch (requestCode){
                    case SELECAO_GALERIA:
                        Uri localImagem = data.getData();
                        imagem = MediaStore.Images
                                .Media
                                .getBitmap(
                                        getContentResolver(),
                                        localImagem
                                );
                        break;
                }

                if( imagem != null){
                    imagePerfilCliente.setImageBitmap( imagem );

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    StorageReference imageRef = storageReference
                            .child("Imagens")
                            .child("clientes")
                            .child(idUsuarioLogado + "jpeg");

                    UploadTask uploadTask = imageRef.putBytes( dadosImagem );
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ConfiguracoesClienteActivity.this,
                                    "Erro ao fazer upload da imagem",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            urlImagemSelecionada = taskSnapshot.getDownloadUrl().toString();
                            Toast.makeText(ConfiguracoesClienteActivity.this,
                                    "Sucesso ao fazer upload da imagem",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });

                }

            }catch (Exception e){
                e.printStackTrace();
            }

        }

    }

    private void inicializarComponentes(){
        editClienteNome = findViewById(R.id.editClienteNome);
        editClienteEndereco = findViewById(R.id.editClienteEndereco);
        editClienteTelefone = findViewById(R.id.editClienteTelefone);
        imagePerfilCliente = findViewById(R.id.imagePerfilCliente);
    }

}
