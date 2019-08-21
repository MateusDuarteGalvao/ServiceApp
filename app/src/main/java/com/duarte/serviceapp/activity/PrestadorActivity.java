package com.duarte.serviceapp.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.support.v7.widget.Toolbar;
import android.view.MenuInflater;

import com.duarte.serviceapp.R;

public class PrestadorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prestador);

        //Configurações ToolBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("ServiceApp - prestador");
        setSupportActionBar(toolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_prestador, menu);

        return super.onCreateOptionsMenu(menu);
    }
}
