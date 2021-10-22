package com.example.balanced.Entity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Comment {
    private DatabaseReference mDatabase;
    public String rating;
    public String author;
    public String userID;
    public String comment;
    public String id;

    public Comment(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public float getRatingToFloat(){
        return Float.parseFloat(rating);
    }

    public Map<String, Object> getMapData(){
        Map<String, Object> map = new HashMap<>();
        map.put("rating", rating);
        map.put("author", author);
        map.put("userID", userID);
        map.put("comment", comment);
        return map;
    }
}
