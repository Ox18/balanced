package com.example.balanced.ViewModel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.balanced.Entity.Course;
import com.example.balanced.Entity.VideoResume;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class VideoResumeViewModel extends ViewModel {

  private DatabaseReference mDatabase;
  private MutableLiveData<VideoResume> videoResume;

  public VideoResumeViewModel(){
    videoResume = new MutableLiveData<>();
    mDatabase = FirebaseDatabase.getInstance().getReference();
  }

  public LiveData<VideoResume> getVideoResume(){
    return videoResume;
  }


  public void course(String userID, String courseID, String videoID){
    DatabaseReference courseRef = mDatabase
      .child("Users")
      .child(userID)
      .child("Courses")
      .child(courseID)
      .child("ResumeVideo")
      .child(videoID);

    ValueEventListener eventListener = new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot snapshot) {
        if(snapshot.exists()){
          VideoResume vResume = snapshot.getValue(VideoResume.class);
          videoResume.setValue(vResume);
        }else{
          videoResume.setValue(new VideoResume());
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        videoResume.setValue(new VideoResume());
      }
    };

    courseRef.addValueEventListener(eventListener);
  }
}
