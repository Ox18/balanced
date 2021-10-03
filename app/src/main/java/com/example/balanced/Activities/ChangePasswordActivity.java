package com.example.balanced.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.balanced.R;
import com.example.balanced.ScreenCompatActivity;

public class ChangePasswordActivity extends ScreenCompatActivity {

    private TextView btnVolver;
    private EditText newPassword;
    private EditText newRePassword;
    private Button btnChangePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        btnVolver = findViewById(R.id.txtVolver);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        newPassword = findViewById(R.id.edtPassword);
        newRePassword = findViewById(R.id.edtRePassword);

        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadProfile();
            }
        });
        LoadComponents();
    }

    public void LoadComponents(){
        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean passwordValid = false;

                if(newPassword.getText().toString().isEmpty()){
                    newPassword.setError(getString(R.string.TEXT_ERROR_EMPTY_PASSWORD));
                }

                if(newRePassword.getText().toString().isEmpty()){
                    newRePassword.setError(getString(R.string.TEXT_ERROR_EMPTY_PASSWORD));
                }

                if(newPassword.getText().toString().length() < 7){
                    newPassword.setError("La contraseña no es segura. Introduzca una contraseña que tenga 7 digitos");
                }

                if(newRePassword.getText().toString().length() < 7){
                    newPassword.setError("La contraseña no es segura. Introduzca una contraseña que tenga 7 digitos");
                }

                if(newPassword.getText().toString().equals(newRePassword.getText().toString()) && newPassword.getText().toString().length() >= 7){
                    ChangePassword(newPassword.getText().toString());
                }else{
                    Toast.makeText(getBaseContext(), "Las contraseñas ingresadas no son iguales", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}