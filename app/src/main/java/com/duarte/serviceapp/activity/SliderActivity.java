package com.duarte.serviceapp.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;

import com.duarte.serviceapp.R;
import com.duarte.serviceapp.helper.ConfiguracaoFirebase;
import com.github.paolorotolo.appintro.AppIntro;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SliderActivity extends AppIntro {

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Configurações iniciais
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        //Verificar usuario logado
        verificarUsuarioLogado();

        addSlide(AppIntroSampleSlider.newInstance(R.layout.slide01));
        addSlide(AppIntroSampleSlider.newInstance(R.layout.slide02));
        addSlide(AppIntroSampleSlider.newInstance(R.layout.slide03));
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
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
            Intent i = new Intent(getApplicationContext(), PrestadorActivityDrawer.class);
            startActivity(i);
            finish();
        }
        else {
            Intent i = new Intent(getApplicationContext(), HomeActivityDrawer.class);
            startActivity(i);
            finish();
        }
    }
}
