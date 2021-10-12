package com.example.balanced.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.balanced.Adapters.MyCoursesProfesionalAdapter;
import com.example.balanced.Entity.Course;
import com.example.balanced.Entity.User2;
import com.example.balanced.R;
import com.example.balanced.ScreenCompatActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LobbyProfesionalActivity extends ScreenCompatActivity {

    private Button btnCrear;
    private TextView logoletter;
    private TextView txtWelcome;
    private LinearLayout circleProfile;
    private RecyclerView recyclerView;
    private MyCoursesProfesionalAdapter myCoursesProfesionalAdapter = new MyCoursesProfesionalAdapter();
    private User2 user = new User2();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby_profesional);

        btnCrear = findViewById(R.id.btnAgregar);
        logoletter = findViewById(R.id.logoLetter);
        txtWelcome = findViewById(R.id.txtWelcome);
        circleProfile = findViewById(R.id.profile_circle);

        circleProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadProfile();
            }
        });

        mDatabase.child("Users")
                .child(GetID())
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                user = snapshot.getValue(User2.class);
                                LoadComponents();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        }
                );
    }

    public void LoadComponents(){
        logoletter.setText(user.getFirstLetter());
        txtWelcome.setText("Bienvenido, " + user.getFirstName());
        LoadAdapater();
        btnCrear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MostrarDialogo();
            }
        });
    }

    public void MostrarDialogo(){
       AlertDialog.Builder builder = new AlertDialog.Builder(LobbyProfesionalActivity.this);

       LayoutInflater inflater = getLayoutInflater();

       View view = inflater.inflate(R.layout.dialog_personalizado, null);

       builder.setView(view);

       AlertDialog dialog = builder.create();
       dialog.show();

       Button btnCancel = view.findViewById(R.id.btnCancel);
       EditText edtNombre = view.findViewById(R.id.edtNombre);
       EditText edtDescription = view.findViewById(R.id.edtDescription);
       Button btnConfirm = view.findViewById(R.id.btnConfirm);
       Spinner spinnerCategory = view.findViewById(R.id.spinnerCategory);
       EditText edtTiempo = view.findViewById(R.id.edtTiempo);
       EditText edtPrecioAdicional = view.findViewById(R.id.edtPrecioAdicional);
       EditText edtURLPhoto = view.findViewById(R.id.edtURLPhoto);

       ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.items_category, android.R.layout.simple_spinner_item);
       adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
       spinnerCategory.setAdapter(adapter);

       Course course = new Course();

       btnConfirm.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               course.category = spinnerCategory.getSelectedItem().toString();
               course.description = edtDescription.getText().toString();
               course.image = edtURLPhoto.getText().toString();
               course.name = edtNombre.getText().toString();
               course.priceAditional = edtPrecioAdicional.getText().toString();
               course.profesionalID = GetID();
               course.profesionalName = user.name;
               course.rate = "0.0";
               course.request = 0;
               course.state = "SUPER";
               course.time = edtTiempo.getText().toString();
               String key = mDatabase.child("Courses").push().getKey();

               mDatabase
                       .child("Courses")
                       .child(key)
                       .setValue(course.getMapData())
                       .addOnSuccessListener(new OnSuccessListener<Void>() {
                           @Override
                           public void onSuccess(Void unused) {
                               Toast.makeText(getBaseContext(), "Se creo su servicio con exito", Toast.LENGTH_SHORT).show();
                                LoadCourses();
                                dialog.dismiss();
                           }
                       })
                       .addOnFailureListener(new OnFailureListener() {
                           @Override
                           public void onFailure(@NonNull Exception e) {
                               Toast.makeText(getBaseContext(), "Falla", Toast.LENGTH_SHORT).show();
                           }
                       });
           }
       });

       btnCancel.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               dialog.dismiss();
           }
       });
    }

    public void LoadAdapater(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        recyclerView = (RecyclerView)findViewById(R.id.recyclerPreviewCourses);
        recyclerView.setAdapter(myCoursesProfesionalAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        LoadCourses();
    }

    public void LoadCourses(){
        myCoursesProfesionalAdapter.Load(mDatabase, GetID());
    }
}