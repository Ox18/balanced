package com.example.balanced.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.balanced.Entity.Course;
import com.example.balanced.Entity.VideoResume;
import com.example.balanced.R;
import com.example.balanced.ViewModel.CourseOnlyViewModel;
import com.example.balanced.ViewModel.VideoResumeViewModel;
import com.universalvideoview.UniversalMediaController;
import com.universalvideoview.UniversalVideoView;

public class VideoCourseActivity extends AppCompatActivity {

    private String videoURL;
    private String videoID;
    private String userID;
    private String courseID;
    private VideoResumeViewModel videoResumeViewModel;
    private UniversalVideoView mVideoView;
    private UniversalMediaController mMediaController;
    private ProgressBar progressBar;
    private TextView txtTitle;
    private TextView txtDescription;
    private TextView txtDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_course);
      txtDuration = findViewById(R.id.txtDuracion);
        initParams();
    }

    private void initParams(){
      txtTitle = findViewById(R.id.title);
      txtDescription = findViewById(R.id.description);

      Bundle extras = getIntent().getExtras();
      if (extras != null) {
        videoID = extras.getString("videoID");
        videoURL = extras.getString("videoURL");
        courseID = extras.getString("courseID");
        userID = extras.getString("userID");
        txtTitle.setText(extras.getString("title"));
        txtDescription.setText(extras.getString("description"));
      }

      configView();
    }

    private void configView(){
      videoResumeViewModel = ViewModelProviders.of(this).get(VideoResumeViewModel.class);
      videoResumeViewModel.course(userID, courseID, videoID);
      configObservers();
    }

    private void configObservers(){
      progressBar = (ProgressBar)findViewById(R.id.progressBar);
      mVideoView = (UniversalVideoView) findViewById(R.id.videoView);
      mMediaController = (UniversalMediaController) findViewById(R.id.media_controller);
      mVideoView.setMediaController(mMediaController);

      mVideoView.setVideoPath(videoURL);

      mVideoView.setVideoViewCallback(new UniversalVideoView.VideoViewCallback() {
        @Override
        public void onScaleChange(boolean isFullscreen) {

        }

        @Override
        public void onPause(MediaPlayer mediaPlayer) { // Video pause
          videoResumeViewModel.save(mVideoView.getCurrentPosition(), userID, courseID, videoID);
        }

        @Override
        public void onStart(MediaPlayer mediaPlayer) { // Video start/resume to play
          videoResumeViewModel.save(mVideoView.getCurrentPosition(), userID, courseID, videoID);
        }

        @Override
        public void onBufferingStart(MediaPlayer mediaPlayer) {// steam start loading
        }

        @Override
        public void onBufferingEnd(MediaPlayer mediaPlayer) {// steam end loading

        }

      });

      mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
          progressBar.setVisibility(View.GONE);
          mVideoView.setVisibility(View.VISIBLE);
          mMediaController.setVisibility(View.VISIBLE);
          String durationInString = secondsToString(mVideoView.getDuration());
          txtDuration.setText(durationInString + " min");
        }
      });

      final Observer<VideoResume> observerCourseOnly = new Observer<VideoResume>() {
        @Override
        public void onChanged(VideoResume videoResume) {
          mVideoView.seekTo(videoResume.minute);
        }
      };

      videoResumeViewModel.getVideoResume().observe(this, observerCourseOnly);
    }

    @Override
    public void finish(){
      videoResumeViewModel.save(mVideoView.getCurrentPosition(), userID, courseID, videoID);
      super.finish();
    }

    private String secondsToString(int pTime) {
      int seconds = pTime % 60000;
      String secondInString = Integer.toString(seconds).substring(0, 2);
      return pTime / 60000 + ":" + secondInString;
    }
}
