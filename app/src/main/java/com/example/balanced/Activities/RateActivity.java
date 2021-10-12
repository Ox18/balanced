package com.example.balanced.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.balanced.R;

public class RateActivity extends AppCompatActivity {

    private String cursoID = "";
    private TextView txtVolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);

        txtVolver = findViewById(R.id.txtVolver);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            cursoID = extras.getString("id");
        }

        txtVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RateActivity.this, CursoDetalleActivity.class);
                intent.putExtra("id", cursoID);
                startActivity(intent);
                finish();
            }
        });
    }
}