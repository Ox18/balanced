package com.example.balanced.Activities;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.balanced.Adapters.PreviewCourseAdapter;
import com.example.balanced.Adapters.PreviewMyCoursesAdapter;
import com.example.balanced.Entity.Course;
import com.example.balanced.Entity.ListMyCoursesPreview;
import com.example.balanced.Entity.MyCoursePreview;
import com.example.balanced.Entity.User2;
import com.example.balanced.R;
import com.example.balanced.ScreenCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.security.Key;
import java.util.ArrayList;

public class LobbyActivity extends ScreenCompatActivity {
    private LinearLayout circleProfile;
    private RecyclerView recyclerView;
    private PreviewCourseAdapter previewCourseAdapter = new PreviewCourseAdapter();
    private ArrayList<Course> courseArrayList = new ArrayList<>();
    private ArrayList<MyCoursePreview> myCoursePreviewArrayList = new ArrayList<>();

    private RecyclerView recyclerViewMyCourses;
    private PreviewMyCoursesAdapter previewMyCoursesAdapter = new PreviewMyCoursesAdapter();

    private ListMyCoursesPreview listMyCoursesPreview = new ListMyCoursesPreview();
    private TextView logoletter;
    private TextView txtWelcome;
    private TextView txtAlertNotServices;
    private EditText edtSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        LoadAdapter();
        edtSearch = findViewById(R.id.editTextSearchByName);
        logoletter = findViewById(R.id.logoLetter);
        circleProfile = findViewById(R.id.profile_circle);
        txtWelcome = findViewById(R.id.txtWelcome);
        txtAlertNotServices = findViewById(R.id.txtAlertNotServices);
        circleProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadProfile();
            }
        });


        /**
         * @Description Actión asignada al @EditText para buscar mis cursos por sus nombre
         */
        edtSearch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_ENTER:
                        String searchText = edtSearch.getText().toString();
                        previewMyCoursesAdapter.SortByName(searchText);
                        return true;
                    default:
                        break;
                }
                return false;
            }
        });

        mDatabase.child("Users")
                .child(GetID())
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                User2 user2 = snapshot.getValue(User2.class);
                                logoletter.setText(user2.getFirstLetter());
                                txtWelcome.setText("Bienvenido, " + user2.getFirstName());
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        }
                );
    }


    private void LoadAdapter(){
       LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        recyclerView = (RecyclerView)findViewById(R.id.recyclerPreviewCourses);
        recyclerView.setAdapter(previewCourseAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        recyclerViewMyCourses = (RecyclerView)findViewById(R.id.recyclerPreviewMyCourses);
        recyclerViewMyCourses.setAdapter(previewMyCoursesAdapter);
        recyclerViewMyCourses.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setHasFixedSize(true);

        LoadCourses();
        LoadMyCoursesPreview();



    }

    private void LoadCourses(){
        mDatabase
                .child("Courses")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        FetchCourses(snapshot);
                        previewCourseAdapter.adicionarLista(courseArrayList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void LoadMyCoursesPreview(){
        mDatabase
                .child("Users")
                .child(GetID())
                .child("Courses")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        FetchMyCoursesPreview(snapshot);
                        previewMyCoursesAdapter.adicionarLista(myCoursePreviewArrayList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void FetchMyCoursesPreview(DataSnapshot snapshot){
        int count = 0;
            for (DataSnapshot postSnapshot:snapshot.getChildren()){
                MyCoursePreview myCoursePreview = postSnapshot.getValue(MyCoursePreview.class);
                myCoursePreview.id = postSnapshot.getKey();
                myCoursePreviewArrayList.add(myCoursePreview);
                count++;
            }
            if(count == 0){
                txtAlertNotServices.setVisibility(View.VISIBLE);
            }
            listMyCoursesPreview.setList(myCoursePreviewArrayList);
    }

    private void FetchCourses(DataSnapshot snapshot){
            for (DataSnapshot postSnapshot:snapshot.getChildren()){
                Course course = postSnapshot.getValue(Course.class);
                course.id = postSnapshot.getKey();
                courseArrayList.add(course);
            }
    }

}
