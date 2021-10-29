package com.example.balanced.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
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
import com.example.balanced.Adapters.ListVideosCourseAdapter;
import com.example.balanced.Entity.Course;
import com.example.balanced.Entity.User2;
import com.example.balanced.Entity.VideoCourseEntity;
import com.example.balanced.R;
import com.example.balanced.ScreenCompatActivity;
import com.example.balanced.ViewModel.CourseOnlyViewModel;
import com.example.balanced.ViewModel.CourseViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CursoDetalleActivity extends ScreenCompatActivity {

    private String COURSE_ID = "";
    private CourseOnlyViewModel courseOnlyViewModel;
    private TextView txtCourseName;
    private TextView txtCourseNameProfesional;
    private TextView txtRate;
    private TextView txtCourseCountRates;
    private TextView txtCourseDescription;
    private ImageView imageCourse;
    private SharedPreferences preferences;
    private Button btnCourseAdd;
    private Button btnCourseDelete;
    private TextView txtVolver;
    private LinearLayout contentRate;
    private ListVideosCourseAdapter listVideosCourseAdapter;
    private RecyclerView recyclerViewVideos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_curso_detalle);
        preferences = getSharedPreferences("auth", Context.MODE_PRIVATE);
        initCourseLoad();
        configVew();
    }

    private void initCourseLoad(){
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            COURSE_ID = extras.getString("id");
        }
    }

    private void configVew(){
        listVideosCourseAdapter = new ListVideosCourseAdapter();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewVideos = (RecyclerView)findViewById(R.id.recyclerViewVideos);
        recyclerViewVideos.setAdapter(listVideosCourseAdapter);
        recyclerViewVideos.setLayoutManager(linearLayoutManager);
        recyclerViewVideos.setHasFixedSize(true);

        courseOnlyViewModel = ViewModelProviders.of(this).get(CourseOnlyViewModel.class);
        courseOnlyViewModel.courseID = COURSE_ID;
        courseOnlyViewModel.uuid = preferences.getString("uuid", "____");

        txtVolver = findViewById(R.id.txtVolver);
        txtCourseName = findViewById(R.id.txtCourseName);
        txtCourseNameProfesional = findViewById(R.id.txtCourseNameProfesional);
        txtRate = findViewById(R.id.txtRate);
        txtCourseCountRates = findViewById(R.id.txtCourseCountRates);
        txtCourseDescription = findViewById(R.id.txtCourseDescription);
        imageCourse = findViewById(R.id.imageCourse);
        btnCourseAdd = findViewById(R.id.btnCourseAdd);
        btnCourseDelete = findViewById(R.id.btnCourseDelete);
        contentRate = findViewById(R.id.contentRate);

        loadEventClick();
        configLoad();
    }

    private void loadEventClick(){
        View.OnClickListener eventClickBack = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(preferences.getString("role", "____").equals("user")){
                    LoadLobby();
                }else{
                    LoadLobbyProfesional();
                }
            }
        };

        View.OnClickListener eventClickContainerRate = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CursoDetalleActivity.this, RateActivity.class);
                intent.putExtra("id", COURSE_ID);
                startActivity(intent);
                finish();
            }
        };

        txtVolver.setOnClickListener(eventClickBack);
        contentRate.setOnClickListener(eventClickContainerRate);
    }

    private void configLoad(){
        courseOnlyViewModel.LoadById();
        courseOnlyViewModel.LoadHaveCourse();
        courseOnlyViewModel.LoadVideos();

        configObservers();
    }

    private void configObservers(){
        final Observer<Course> observerCourseOnly = new Observer<Course>() {
            @Override
            public void onChanged(Course course) {
                LoadCourseDetails(course);
            }
        };

        final Observer<Integer> observerCountRates = new Observer<Integer>() {
            @Override
            public void onChanged(Integer count) {
                txtCourseCountRates.setText("(" + count + " Puntuaciones)");
            }
        };

        final Observer<Boolean> observerHaveCourse = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean haveCourse) {
                if(preferences.getString("role", "____").equals("user")){
                    loadViewButtonsByUser(haveCourse);
                    listVideosCourseAdapter.active = haveCourse;
                }
            }
        };

        final Observer<List<VideoCourseEntity>> observerVideoCoursesEntity = new Observer<List<VideoCourseEntity>>() {
            @Override
            public void onChanged(List<VideoCourseEntity> videoCourseEntities) {
                listVideosCourseAdapter.adicionarLista(videoCourseEntities);
            }
        };

        courseOnlyViewModel.getCourse().observe(this, observerCourseOnly);
        courseOnlyViewModel.getCountRates().observe(this, observerCountRates);
        courseOnlyViewModel.getHaveCourse().observe(this, observerHaveCourse);
        courseOnlyViewModel.getVideos().observe(this, observerVideoCoursesEntity);

        LoadEventClickBeforeStartViewModel();
    }

    private void LoadCourseDetails(Course course){
        txtCourseName.setText(course.name);
        txtCourseNameProfesional.setText(course.profesionalName);
        txtRate.setText(course.rate);
        txtCourseDescription.setText(Html.fromHtml(course.description));

        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.placeholder(R.drawable.ic_launcher_background);
        Glide.with(imageCourse.getContext())
                .load(course.image)
                .centerCrop()
                .apply(requestOptions)
                .into(imageCourse);
    }

    private void loadViewButtonsByUser(Boolean haveCourse){
            if(haveCourse){
                btnCourseAdd.setVisibility(View.GONE);
                btnCourseDelete.setVisibility(View.VISIBLE);
            }else{
                btnCourseAdd.setVisibility(View.VISIBLE);
                btnCourseDelete.setVisibility(View.GONE);
            }
    }

    private void LoadEventClickBeforeStartViewModel(){
        View.OnClickListener onClickAddCourse = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                courseOnlyViewModel.BuyCourse();
                showMessageNotify("Se adquirio el curso");
            }
        };

        View.OnClickListener onClickUnsuscribeCourse = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                courseOnlyViewModel.UnsuscribeCourse();
                showMessageNotify("Se removio el curso");
            }
        };

        btnCourseAdd.setOnClickListener(onClickAddCourse);
        btnCourseDelete.setOnClickListener(onClickUnsuscribeCourse);
    }

}