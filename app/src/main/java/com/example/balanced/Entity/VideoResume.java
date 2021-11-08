package com.example.balanced.Entity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class VideoResume {
  private DatabaseReference mDatabase;
  public Integer minute = 0;

  public Map<String, Object> getMapData(){
    Map<String, Object> map = new HashMap<>();
    map.put("minute", minute);
    return map;
  }

  public VideoResume(){
    mDatabase = FirebaseDatabase.getInstance().getReference();
  }

}
