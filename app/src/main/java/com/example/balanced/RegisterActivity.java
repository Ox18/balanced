package com.example.balanced;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends ScreenCompatActivity {

    private EditText edtCorreo;
    private EditText edtPassword;
    private EditText edtNombre;
    private EditText edtPhone;
    private EditText edtDNI;
    private Button btnRegistar;
    private TextView txtBackToLogin;

    // Datos
    private String correo = "";
    private String password = "";
    private String nombre = "";
    private String phone = "";
    private String dni = "";

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtCorreo = (EditText)findViewById(R.id.edtEmail);
        edtPassword = (EditText)findViewById(R.id.edtPassword);
        edtNombre = (EditText)findViewById(R.id.edtNombre);
        edtPhone = (EditText)findViewById(R.id.edtPhone);
        edtDNI = (EditText)findViewById(R.id.edtDNI);
        btnRegistar = (Button)findViewById(R.id.btnRegistrar);
        txtBackToLogin = (TextView)findViewById(R.id.txtLoguearse);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        txtBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadLogin();
            }
        });

        btnRegistar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                correo = edtCorreo.getText().toString();
                password = edtPassword.getText().toString();
                nombre = edtNombre.getText().toString().trim().replaceAll(" ", "-");
                phone = edtPhone.getText().toString();
                dni = edtDNI.getText().toString();

                Map<String, Object> map = new HashMap<>();

                map.put("name", nombre.trim().replaceAll(" ", "-"));
                map.put("phone", phone);
                map.put("dni", dni);
                map.put("correo", correo);
                map.put("payment_active", false);
                btnRegistar.setEnabled(false);
                RegisterAccount(correo, password, map);
            }
        });
    }
}
