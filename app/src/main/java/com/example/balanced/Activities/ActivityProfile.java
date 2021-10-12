package com.example.balanced.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.balanced.Entity.User2;
import com.example.balanced.R;
import com.example.balanced.ScreenCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class ActivityProfile extends ScreenCompatActivity {

    private TextView txtVolver;
    private TextView txtGuardar;
    private TextView txtLogoLetter;
    private EditText edtNAME;
    private EditText edtCORREO;
    private EditText edtDNI;
    private EditText edtPHONE;
    private Button btnCerrarSesion;
    private Button btnDeleteAccount;
    private Button btnChangePassword;
    private User2 user = new User2();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        txtVolver = findViewById(R.id.txtVolver);
        txtGuardar = findViewById(R.id.txtGuardar);
        txtLogoLetter = findViewById(R.id.logoLetter);
        edtNAME = findViewById(R.id.edtNombre);
        edtCORREO = findViewById(R.id.edtCorreo);
        edtDNI = findViewById(R.id.edtDNI);
        edtPHONE = findViewById(R.id.edtPhone);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        btnDeleteAccount = findViewById(R.id.btnDeleteAccount);
        btnChangePassword = findViewById(R.id.btnChangePassword);

        loadComponents();

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ResetPassword();
            }
        });

        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

        btnDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteMeAccount();
            }
        });

        txtGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User2 userNewData = user;
                userNewData.name = edtNAME.getText().toString();
                userNewData.phone = edtPHONE.getText().toString();
                userNewData.dni = edtDNI.getText().toString();
                updateMeAccount(userNewData);
            }
        });

    }

    public void loadComponents(){
        txtVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user.isProfessional()){
                    LoadLobbyProfesional();
                }
                if(user.isUser()){
                    LoadLobby();
                }
            }
        });
        mDatabase.child("Users")
                .child(GetID())
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                User2 user2 = snapshot.getValue(User2.class);
                                user = user2;
                                txtLogoLetter.setText(user2.getFirstLetter());
                                edtNAME.setText(user2.name);
                                edtCORREO.setText(user2.correo);
                                edtPHONE.setText(user2.phone);
                                edtDNI.setText(user2.dni);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        }
                );
    }
}