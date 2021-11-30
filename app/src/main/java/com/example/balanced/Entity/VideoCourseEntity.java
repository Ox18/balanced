package com.example.balanced.Entity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class VideoCourseEntity {
    private DatabaseReference mDatabase;
    public String Title;
    public String number;
    public String time;
    public String url;
    public String id;
    public String description;

    public VideoCourseEntity(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

  public Map<String, Object> getMapData(){
    Map<String, Object> map = new HashMap<>();
    map.put("Title", Title);
    map.put("description", description);
    map.put("number", number);
    map.put("time", time);
    map.put("url", url);
    return map;
  }
}
