package com.example.balanced.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.balanced.Entity.Course;
import com.example.balanced.Entity.User2;
import com.example.balanced.R;
import com.example.balanced.Recyclers.ListMyCoursesAdapter;
import com.example.balanced.ScreenCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LobbyActivity extends ScreenCompatActivity {

    private EditText edtSearchMyCourse;
    private RecyclerView recyclerView;
    private ListMyCoursesAdapter listMyCoursesAdapter;
    private Button btnSearchByName;
    ArrayList<Course> courses;
    ArrayList<Course> coursesInit;
    LinearLayout profileCircle;
    TextView txtWelcome;
    TextView txtLogoLetter;

    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        btnSearchByName = (Button)findViewById(R.id.btnSearchByName);
        courses = new ArrayList<>();
        coursesInit = new ArrayList<>();
        edtSearchMyCourse = (EditText)findViewById(R.id.edtSearchCourses);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerViewMisCursos);
        listMyCoursesAdapter = new ListMyCoursesAdapter();
        recyclerView.setAdapter(listMyCoursesAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);


        profileCircle = (LinearLayout)findViewById(R.id.profile_circle);
        profileCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignOut();
            }
        });

        txtWelcome = (TextView)findViewById(R.id.txtWelcome);
        txtLogoLetter = (TextView)findViewById(R.id.logoLetter);

        userID = mAuth.getCurrentUser().getUid();

        LoadProfile();
        LoadMyCourses();

        btnSearchByName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orderMyCoursesByName();
            }
        });
    }

    public void LoadProfile(){
        mDatabase.child("Users")
                .child(userID)
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                User2 user = snapshot.getValue(User2.class);
                                String firstWord = user.name.split(" ")[0];
                                txtWelcome.setText("Bienvenido, " + firstWord);
                                txtLogoLetter.setText(firstWord.substring(0, 1));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        }
                );
    }

    public void LoadMyCourses(){
        mDatabase.child("Users")
                .child(userID)
                .child("Courses")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        fetchMyCourses(snapshot);
                        listMyCoursesAdapter.adicionarListaCourses(courses);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void fetchMyCourses(DataSnapshot snapshot){
        for(DataSnapshot postSnapshot:snapshot.getChildren()){
            Course course = postSnapshot.getValue(Course.class);
            courses.add(course);
            coursesInit.add(course);
        }
    }

    private void orderMyCoursesByName(){
        String searchName = edtSearchMyCourse.getText().toString();

        if(searchName.length() == 0){
            listMyCoursesAdapter.adicionarListaCourses(coursesInit);
        }else{
            ArrayList<Course> filtroCursos = new ArrayList<>();
            for(Course courseSeleccionado: coursesInit){
                String nameSeleccionado = courseSeleccionado.getName().toLowerCase();
                String nameSearchLower = searchName.toLowerCase();
                if(nameSeleccionado.startsWith(nameSearchLower)){
                    filtroCursos.add(courseSeleccionado);
                }
            }
            listMyCoursesAdapter.adicionarListaCourses(filtroCursos);
        }

    }
}
