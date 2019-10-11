package com.duarte.serviceapp.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import android.os.Bundle;

import com.duarte.serviceapp.R;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

public class SliderActivity extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_slider);

        /*addSlide(AppIntroFragment.newInstance("Resolva seus problemas!" , "Rápido e fácil.",
                R.drawable.intro01, ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary)));
        addSlide(AppIntroFragment.newInstance("Escolha seu tipo de perfil.." , "..e faça o cadastro",
                R.drawable.intro02, ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary)));
        addSlide(AppIntroFragment.newInstance("", "",
                R.drawable.intro03, ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark)));*/
        addSlide(AppIntroSampleSlider.newInstance(R.layout.slide01));
        addSlide(AppIntroSampleSlider.newInstance(R.layout.slide02));
        addSlide(AppIntroSampleSlider.newInstance(R.layout.slide03));

    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        Intent intent = new Intent(getApplicationContext(),AutenticacaoActivity.class);
        startActivity(intent);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        Intent intent = new Intent(getApplicationContext(), AutenticacaoActivity.class);
        startActivity(intent);
    }

    private void abrirAutenticacao() {
        Intent i = new Intent(SliderActivity.this, AutenticacaoActivity.class);
        startActivity(i);
        finish();
    }
}