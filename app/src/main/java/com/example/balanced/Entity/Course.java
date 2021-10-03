package com.example.balanced.Entity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Course {
    private DatabaseReference mDatabase;
    public String name;
    public String image;
    public String rate;
    public String priceAditional;
    public String time;
    public String profesionalName;
    public String state;
    public String id;
    public String description;
    public int request;
    public String category;

    public Course(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public String getRequest(){
        return Integer.toString(request);
    }

    public Map<String, Object> getMapData(){
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("image", image);
        map.put("rate", rate);
        map.put("priceAditional", priceAditional);
        map.put("time", time);
        map.put("profesionalName", profesionalName);
        map.put("state", state);
        map.put("description", description);
        map.put("request", request);
        map.put("category", category);
        return map;
    }
}