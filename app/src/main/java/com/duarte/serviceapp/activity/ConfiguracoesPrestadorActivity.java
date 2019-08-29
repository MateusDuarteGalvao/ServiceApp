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


public class ConfiguracoesPrestadorActivity extends AppCompatActivity {

    //Inicializando atributos
    private EditText editPrestadorNome, editPrestadorCategoria, editPrestadorTempo,
            editPrestadorValorHora;
    private ImageView imagePerfilPrestador;
    private AlertDialog dialog;

    private static final int SELECAO_GALERIA = 200;
    private StorageReference storageReference;
    private DatabaseReference firebaseRef;
    private String idUsuarioLogado;
    private String urlImagemSelecionada = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes_prestador);

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
        imagePerfilPrestador.setOnClickListener(new View.OnClickListener() {
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

        //Recuperar dados do prestador
        recuperarDadosPrestador();

    }

   private void recuperarDadosPrestador(){

       dialog = new SpotsDialog.Builder()
               .setContext(this)
               .setMessage("Carregando dados")
               .setCancelable(false)
               .build();
       dialog.show();

        DatabaseReference prestadorRef = firebaseRef
                .child("prestadores")
                .child( idUsuarioLogado );
        prestadorRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if( dataSnapshot.getValue() != null ){
                    Prestador prestador = dataSnapshot.getValue(Prestador.class);
                    editPrestadorNome.setText(prestador.getNome());
                    editPrestadorCategoria.setText(prestador.getCategoria());
                    editPrestadorTempo.setText(prestador.getTempo());
                    editPrestadorValorHora.setText(prestador.getPrecoHora().toString());

                    urlImagemSelecionada = prestador.getUrlImagem();
                    if ( urlImagemSelecionada != "" ) {
                        Picasso.get()
                                .load(urlImagemSelecionada)
                                .into(imagePerfilPrestador);
                    }

                    dialog.dismiss();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void validarDadosPrestador(View view){

        //Valida se os campos foram preenchidos
        String nome = editPrestadorNome.getText().toString();
        String valor = editPrestadorValorHora.getText().toString();
        String categoria = editPrestadorCategoria.getText().toString();
        String tempo = editPrestadorTempo.getText().toString();

        if ( !nome.isEmpty() ){
            if ( !valor.isEmpty() ){
                if ( !categoria.isEmpty() ){
                    if ( !tempo.isEmpty() ){

                        Prestador prestador = new Prestador();
                        prestador.setIdUsuario( idUsuarioLogado );
                        prestador.setNome( nome );
                        prestador.setPrecoHora( Double.parseDouble(valor) );
                        prestador.setCategoria(categoria);
                        prestador.setTempo( tempo );
                        prestador.setUrlImagem( urlImagemSelecionada );
                        prestador.salvar();
                        finish();

                    }else{
                        exibirMensagem("Digite o tempo de espera");
                    }

                }else{
                    exibirMensagem("Digite uma categoria");
                }

            }else{
                exibirMensagem("Digite um valor por hora");
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
                    imagePerfilPrestador.setImageBitmap( imagem );

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    StorageReference imageRef = storageReference
                            .child("Imagens")
                            .child("prestadores")
                            .child(idUsuarioLogado + "jpeg");

                    UploadTask uploadTask = imageRef.putBytes( dadosImagem );
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ConfiguracoesPrestadorActivity.this,
                                    "Erro ao fazer upload da imagem",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            urlImagemSelecionada = taskSnapshot.getDownloadUrl().toString();
                            Toast.makeText(ConfiguracoesPrestadorActivity.this,
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
        editPrestadorNome = findViewById(R.id.editPrestadorNome);
        editPrestadorCategoria = findViewById(R.id.editPrestadorCategoria);
        editPrestadorTempo = findViewById(R.id.editPrestadorTempo);
        editPrestadorValorHora = findViewById(R.id.editPrestadorValorHora);
        imagePerfilPrestador = findViewById(R.id.imagePerfilPrestador);
    }


}
