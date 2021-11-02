package com.example.balanced.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.widget.Toast;

import com.example.balanced.Entity.Course;
import com.example.balanced.Entity.VideoResume;
import com.example.balanced.R;
import com.example.balanced.ViewModel.CourseOnlyViewModel;
import com.example.balanced.ViewModel.VideoResumeViewModel;

public class VideoCourseActivity extends AppCompatActivity {

    private String videoURL;
    private String videoID;
    private String userID;
    private String courseID;
    private VideoResumeViewModel videoResumeViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_course);

        initParams();
    }

    private void initParams(){
      Bundle extras = getIntent().getExtras();
      if (extras != null) {
        videoID = extras.getString("videoID");
        videoURL = extras.getString("videoURL");
        courseID = extras.getString("courseID");
        userID = extras.getString("userID");
      }

      configView();
    }

    private void configView(){
      videoResumeViewModel = ViewModelProviders.of(this).get(VideoResumeViewModel.class);

      videoResumeViewModel.course(userID, courseID, videoID);


      configObservers();
    }

    private void configObservers(){


      final Observer<VideoResume> observerCourseOnly = new Observer<VideoResume>() {
        @Override
        public void onChanged(VideoResume videoResume) {
          Toast.makeText(VideoCourseActivity.this, "minute"+ videoResume.minute, Toast.LENGTH_SHORT).show();
        }
      };

      videoResumeViewModel.getVideoResume().observe(this, observerCourseOnly);
    }
}
