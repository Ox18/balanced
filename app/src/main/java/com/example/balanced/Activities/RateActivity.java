package com.example.balanced.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.balanced.Adapters.CommentsAdapter;
import com.example.balanced.Entity.Comment;
import com.example.balanced.Entity.Course;
import com.example.balanced.R;
import com.example.balanced.ScreenCompatActivity;
import com.example.balanced.ViewModel.CourseCommentViewModel;
import com.example.balanced.ViewModel.CourseMyRateViewModel;
import com.example.balanced.ViewModel.CourseViewModel;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;

public class RateActivity extends ScreenCompatActivity {

    private TextView txtVolver;
    private ShimmerFrameLayout shimmer_view_ratin_stars;
    private String cursoID = "";
    private CourseViewModel courseViewModel;
    private CourseMyRateViewModel courseMyRateViewModel;
    private CourseCommentViewModel courseCommentViewModel;

    private CommentsAdapter commentsAdapter = new CommentsAdapter();
    private RecyclerView recyclerPreviewMyCourses;

    private LinearLayout contentEmptyComments;
    private RatingBar ratingBar;
    private EditText editComentario;
    private Button btnComentario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            cursoID = extras.getString("id");
        }
        configView();
    }

    private void configView(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerPreviewMyCourses = (RecyclerView)findViewById(R.id.recyclerPreviewMyCourses);
        recyclerPreviewMyCourses.setAdapter(commentsAdapter);
        recyclerPreviewMyCourses.setLayoutManager(linearLayoutManager);
        recyclerPreviewMyCourses.setHasFixedSize(true);

        contentEmptyComments = findViewById(R.id.contentEmptyComments);
        courseViewModel = ViewModelProviders.of(this).get(CourseViewModel.class);
        courseMyRateViewModel = ViewModelProviders.of(this).get(CourseMyRateViewModel.class);
        courseCommentViewModel = ViewModelProviders.of(this).get(CourseCommentViewModel.class);

        txtVolver = findViewById(R.id.txtVolver);
        ratingBar = (RatingBar)findViewById(R.id.ratingBar);
        shimmer_view_ratin_stars = (ShimmerFrameLayout)findViewById(R.id.shimmer_view_ratin_stars);
        editComentario = (EditText)findViewById(R.id.editComentario);
        btnComentario = (Button)findViewById(R.id.btnComentario);

        courseViewModel.course(cursoID);
        courseMyRateViewModel.load(cursoID);
        courseCommentViewModel.Load(cursoID);

        final Observer<Course> observer = new Observer<Course>() {
            @Override
            public void onChanged(Course course) {

            }
        };

        final Observer<Float> observer1 = new Observer<Float>() {
            @Override
            public void onChanged(Float aFloat) {
                shimmer_view_ratin_stars.setVisibility(View.GONE);
                ratingBar.setVisibility(View.VISIBLE);
                ratingBar.setRating(aFloat);
            }
        };

        String uid = mAuth.getCurrentUser().getUid();
        commentsAdapter.addUID(uid);
        commentsAdapter.addCourseID(cursoID);

        final Observer<ArrayList<Comment>> observerComments = new Observer<ArrayList<Comment>>() {
            @Override
            public void onChanged(ArrayList<Comment> comments) {
                commentsAdapter.adicionarLista(comments);
                if(comments.size() == 0){
                    contentEmptyComments.setVisibility(View.VISIBLE);
                    recyclerPreviewMyCourses.setVisibility(View.GONE);
                }else{
                    contentEmptyComments.setVisibility(View.GONE);
                    recyclerPreviewMyCourses.setVisibility(View.VISIBLE);
                }
            }
        };


        courseViewModel.getResultado().observe(this, observer);
        courseMyRateViewModel.resultado().observe(this, observer1);
        courseCommentViewModel.getResultado().observe(this, observerComments);

        SharedPreferences preferences = getSharedPreferences("auth", Context.MODE_PRIVATE);
        String name = preferences.getString("name", "No existe la información");

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                courseMyRateViewModel.changeRate(v, cursoID);
            }
        });

        btnComentario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comentario = editComentario.getText().toString();
                courseCommentViewModel.PushComment(cursoID, ratingBar.getRating(), comentario, name);
                editComentario.setText("");
            }
        });
        
        txtVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadCursoDetaller(cursoID);
            }
        });
    }
}