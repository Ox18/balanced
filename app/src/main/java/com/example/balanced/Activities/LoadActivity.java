package com.example.balanced.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.example.balanced.R;
import com.example.balanced.ScreenCompatActivity;

public class LoadActivity extends ScreenCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);



    }

    @Override
    protected  void onStart(){
        super.onStart();
        VerifyExistAuth();
    }
}
