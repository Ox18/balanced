package com.example.balanced.Entity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class VideoResume {
  private DatabaseReference mDatabase;
  public Integer minute = 10;

  public VideoResume(){
    mDatabase = FirebaseDatabase.getInstance().getReference();
  }

}
