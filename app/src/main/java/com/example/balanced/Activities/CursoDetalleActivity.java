package com.example.balanced.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class CursoDetalleActivity extends ScreenCompatActivity {

    String cursoID = "";
    private ImageView imageCourse;
    private TextView txtDescription;

    private TextView txtNombreDelCurso;
    private TextView txtNameProfesional;
    private TextView txtRate;
    private TextView txtRequests;
    private TextView txtCategory;
    private TextView txtState;
    private Button btnAction;
    private TextView txtVolver;
    private TextView txtPriceAditional;
    private boolean adquired = false;
    private Course courseMetadata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_curso_detalle);

        imageCourse = findViewById(R.id.imageCourse);
        txtDescription = findViewById(R.id.descriptionCourse);

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
        loadComponents();
        LoadButton();
        LoadHeaderBar();
        LoadEventToButtonAction();
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
                            btnAction.setText("Adquirir");
                            Toast.makeText(getBaseContext(), "El curso fue eliminado", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void LoadHeaderBar(){
        String uid = GetID();
        mDatabase
                .child("Users")
                .child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            User2 user = snapshot.getValue(User2.class);
                            String roleUser = user.role;
                            String roleUserValidate = "user";
                            boolean validateUser = roleUserValidate.equals(roleUser);
                            txtVolver.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if(validateUser){
                                        LoadLobby();
                                    }else{
                                        // es profesional
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

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
                        btnAction.setVisibility(View.VISIBLE);
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