package com.example.balanced.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.balanced.Entity.VideoResume;
import com.example.balanced.R;
import com.example.balanced.ScreenCompatActivity;
import com.example.balanced.ServiceImpl.LeccionServiceImpl;
import com.example.balanced.ViewModel.VideoResumeViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.universalvideoview.UniversalMediaController;
import com.universalvideoview.UniversalVideoView;

public class VideoCourseActivity extends ScreenCompatActivity {

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
    private Button btnVideoEliminar;
    private LinearLayout llSectionManage;
    private SharedPreferences preferences;
    private String role;

  @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_course);
        txtDuration = findViewById(R.id.txtDuracion);
        initParams();
    }

    private void initParams(){
      preferences = getSharedPreferences("auth", Context.MODE_PRIVATE);
      role = preferences.getString("role", "nada");
      txtTitle = findViewById(R.id.txtLeccionTitle);
      txtDescription = findViewById(R.id.description);
      btnVideoEliminar = findViewById(R.id.btnVideoEliminar);

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

      btnVideoEliminar.setOnClickListener(onCLickBtnVideoEliminar());
    }

    private View.OnClickListener onCLickBtnVideoEliminar(){
      return new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          LeccionServiceImpl leccionService = new LeccionServiceImpl();
          leccionService.DeleteById(videoID, courseID)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
              @Override
              public void onSuccess(Void unused) {
                Toast.makeText(VideoCourseActivity.this, "La lección se elimino satisfactoriamente.", Toast.LENGTH_SHORT).show();
                finish();
              }
            })
            .addOnFailureListener(new OnFailureListener() {
              @Override
              public void onFailure(@NonNull Exception e) {
                Toast.makeText(VideoCourseActivity.this, "No se pudo eliminar esta lección.", Toast.LENGTH_SHORT).show();
              }
            });
        }
      };
    }

    private void configView(){
      llSectionManage = findViewById(R.id.llSectionManage);

      if(role.equals("professional")){
        llSectionManage.setVisibility(View.VISIBLE);
      }


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
