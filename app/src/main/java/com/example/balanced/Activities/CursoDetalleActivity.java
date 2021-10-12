package com.example.balanced.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.balanced.Entity.Course;
import com.example.balanced.Entity.User2;
import com.example.balanced.R;
import com.example.balanced.ScreenCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class CursoDetalleActivity extends ScreenCompatActivity {

    String cursoID = "";
    private ImageView imageCourse;
    private TextView txtDescription;
    private LinearLayout contentRate;
    private TextView txtNombreDelCurso;
    private TextView txtNameProfesional;
    private TextView txtRate;
    private TextView txtRequests;
    private TextView txtCategory;
    private TextView txtState;
    private Button btnAction;
    private Button btnVerCapacitacion;
    private Button btnEliminarCurso;
    private Button btnEditarCurso;
    private TextView txtVolver;
    private TextView txtPriceAditional;
    private TextView txtTime;
    private boolean adquired = false;
    private Course courseMetadata;
    private User2 user = new User2();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_curso_detalle);
        contentRate = findViewById(R.id.contentRate);
        btnEliminarCurso = findViewById(R.id.btnEliminarCurso);
        btnEditarCurso = findViewById(R.id.btnEditar);
        btnVerCapacitacion = findViewById(R.id.buttonVerCapacitacion);
        imageCourse = findViewById(R.id.imageCourse);
        txtDescription = findViewById(R.id.descriptionCourse);
        txtTime = findViewById(R.id.txtTime);
        txtNombreDelCurso = findViewById(R.id.txtNombreDelCurso);
        txtNameProfesional = findViewById(R.id.txtNameProfesional);
        txtRate = findViewById(R.id.txtRate);
        txtRequests = findViewById(R.id.txtRequests);
        txtCategory = findViewById(R.id.txtCategory);
        txtState = findViewById(R.id.txtState);
        btnAction = findViewById(R.id.buttonAction);
        txtVolver = findViewById(R.id.txtVolver);
        txtPriceAditional = findViewById(R.id.txtPrecioAditional);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            cursoID = extras.getString("id");
        }

        contentRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CursoDetalleActivity.this, RateActivity.class);
                intent.putExtra("id", cursoID);
                startActivity(intent);
                finish();
            }
        });

        mDatabase.child("Users")
                .child(GetID())
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                user = snapshot.getValue(User2.class);
                                loadComponents();
                                LoadButton();
                                LoadHeaderBar();
                                LoadEventToButtonAction();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        }
                );
    }

    public void LoadEventToButtonAction() {
        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (adquired) {
                    DeleteForMeCourse();
                }else{
                    AddForMeCourse();
                }
            }
        });

        btnEliminarCurso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase.child("Courses")
                        .child(cursoID)
                        .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getBaseContext(), "Se elimino satisfactoriamente", Toast.LENGTH_SHORT).show();
                        if(user.isUser()){
                            LoadLobby();
                        }
                        if(user.isProfessional()){
                            LoadLobbyProfesional();
                        }
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getBaseContext(), "No se pudo eliminar el curso", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        btnEditarCurso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MostrarDialogEditar();
            }
        });
    }

    public void MostrarDialogEditar(){
        AlertDialog.Builder builder = new AlertDialog.Builder(CursoDetalleActivity.this);

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

        Course course = courseMetadata;
        btnConfirm.setText("Guardar");
        edtNombre.setText(course.name);
        edtDescription.setText(course.description);
        edtTiempo.setText(course.time);
        edtPrecioAdicional.setText(course.priceAditional);
        edtURLPhoto.setText(course.image);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.items_category, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                course.description = edtDescription.getText().toString();
                course.image = edtURLPhoto.getText().toString();
                course.name = edtNombre.getText().toString();
                course.priceAditional = edtPrecioAdicional.getText().toString();
                course.time = edtTiempo.getText().toString();

                mDatabase
                        .child("Courses")
                        .child(cursoID)
                        .updateChildren(course.getMapData())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getBaseContext(), "Se actualizo el curso", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                                RefreshActivity();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getBaseContext(), "No se pudo actualizar el curso", Toast.LENGTH_SHORT).show();
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

    public void AddForMeCourse(){
        AddCourseForMe(cursoID, courseMetadata);
    }

    public void DeleteForMeCourse(){
        mDatabase
                .child("Users")
                .child(GetID())
                .child("Courses")
                .child(cursoID)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            adquired = false;
                            btnVerCapacitacion.setVisibility(View.GONE);
                            btnAction.setText("Adquirir");
                            Toast.makeText(getBaseContext(), "El curso fue eliminado", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void LoadHeaderBar(){
        txtVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user.isUser()){
                    LoadLobby();
                }

                if(user.isProfessional()){
                    LoadLobbyProfesional();
                }
            }
        });
    }

    public void loadComponents(){
        mDatabase
                .child("Courses")
                .child(cursoID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                Course course = snapshot.getValue(Course.class);
                                LoadImage(course.image);
                                txtDescription.setText(course.description);
                                txtState.setText(course.state);
                                txtRate.setText(course.rate);
                                txtNameProfesional.setText(course.profesionalName);
                                txtNombreDelCurso.setText(course.name);
                                txtRequests.setText(course.getRequest());
                                txtCategory.setText(course.category);
                                txtPriceAditional.setText(course.priceAditional);
                                txtTime.setText(course.time);
                                courseMetadata = course;
                            }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void LoadButton(){
        String uid = GetID();
        mDatabase
                .child("Users")
                .child(uid)
                .child("Courses")
                .child(cursoID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            adquired = true;
                            btnAction.setText("Eliminar");
                        }else{
                            adquired = false;
                            btnAction.setText("Adquirir");
                        }
                        if(user.isProfessional()){
                            btnEditarCurso.setVisibility(View.VISIBLE);
                            btnEliminarCurso.setVisibility(View.VISIBLE);
                        }
                        if(user.isUser()){
                            if(adquired){
                                btnVerCapacitacion.setVisibility(View.VISIBLE);
                            }
                            btnAction.setVisibility(View.VISIBLE);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void LoadImage(String uri){
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.placeholder(R.drawable.ic_launcher_background);
        Glide.with(imageCourse.getContext())
                .load(uri)
                .centerCrop()
                .apply(requestOptions)
                .into(imageCourse);


    }

}