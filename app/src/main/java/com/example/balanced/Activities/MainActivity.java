package com.example.balanced.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.balanced.R;
import com.example.balanced.ScreenCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class MainActivity extends ScreenCompatActivity {

    private EditText edtEmail;
    private EditText edtPassword;
    private Button btnLogin;
    private TextView txtRegister;

    private String email = "";
    private String password = "";

    private FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtEmail = (EditText)findViewById(R.id.edtEmail);
        edtPassword = (EditText)findViewById(R.id.edtPassword);
        btnLogin = (Button)findViewById(R.id.btnLogin);
        txtRegister = (TextView)findViewById(R.id.txtRegistrarse);

        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadRegister();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = (String)edtEmail.getText().toString();
                password = (String)edtPassword.getText().toString();
                Login(email, password);
            }
        });
    }
}
