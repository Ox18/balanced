package com.example.balanced.Activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.balanced.R;
import com.example.balanced.ScreenCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class MainActivity extends ScreenCompatActivity {

    private TextView txtResetearPassword;
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

        txtResetearPassword = findViewById(R.id.txtResetearPassword);
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

        txtResetearPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edtEmail.getText().toString().isEmpty()) {
                    edtEmail.setError(getString(R.string.TEXT_ERROR_EMPTY_EMAIL));
                }else {
                    if (edtEmail.getText().toString().trim().matches(getString(R.string.TEXT_PATTERN_EMAIL))) {
                        sendResetPassword(edtEmail.getText().toString());
                    } else {
                        edtEmail.setError(getString(R.string.TEXT_ERROR_INVALID_EMAIL));
                    }
                }
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = (String)edtEmail.getText().toString();
                password = (String)edtPassword.getText().toString();

                boolean validEmail = false;

                if(edtEmail.getText().toString().isEmpty()) {
                    validEmail = false;
                    edtEmail.setError(getString(R.string.TEXT_ERROR_EMPTY_EMAIL));
                }else {
                    if (edtEmail.getText().toString().trim().matches(getString(R.string.TEXT_PATTERN_EMAIL))) {
                        validEmail = true;
                    } else {
                        validEmail = false;
                        edtEmail.setError(getString(R.string.TEXT_ERROR_INVALID_EMAIL));
                    }
                }
                if(edtPassword.getText().toString().isEmpty()){
                    edtPassword.setError(getString(R.string.TEXT_ERROR_EMPTY_PASSWORD));
                }

                if(validEmail && password.length() > 0){
                    Login(email, password);
                }
            }
        });
    }
}
