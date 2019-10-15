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

        addSlide(AppIntroSampleSlider.newInstance(R.layout.slide01));
        addSlide(AppIntroSampleSlider.newInstance(R.layout.slide02));
        addSlide(AppIntroSampleSlider.newInstance(R.layout.slide03));

    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        Intent intent = new Intent(getApplicationContext(), SelecaoUsuarioActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        Intent intent = new Intent(getApplicationContext(), SelecaoUsuarioActivity.class);
        startActivity(intent);
        finish();
    }
}
