package com.duarte.serviceapp.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.duarte.serviceapp.R;
import com.duarte.serviceapp.model.Prestador;
import com.squareup.picasso.Picasso;

public class ServicosActivity extends AppCompatActivity {

    private RecyclerView recyclerServicosPrestador;
    private ImageView imagePrestadorServico;
    private TextView textNomePrestadorServico;
    private Prestador prestadorSelecionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servicos);

        //Configurações iniciais
        inicializarComponentes();

        //Recupera prestador selecionado
        Bundle bundle = getIntent().getExtras();
        if( bundle != null ){
            prestadorSelecionado = (Prestador) bundle.getSerializable("prestador");

            textNomePrestadorServico.setText( prestadorSelecionado.getNome() );

            String url = prestadorSelecionado.getUrlImagem();
            Picasso.get().load(url).into(imagePrestadorServico);
        }

        //Configurações Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Serviços");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void inicializarComponentes(){
        recyclerServicosPrestador = findViewById(R.id.recyclerServicosPrestador);
        imagePrestadorServico = findViewById(R.id.imagePrestadorServico);
        textNomePrestadorServico = findViewById(R.id.textNomePrestadorServico);
    }




}
