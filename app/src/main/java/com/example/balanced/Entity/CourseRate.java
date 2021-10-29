package com.example.balanced.Entity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CourseRate {
    private DatabaseReference mDatabase;
    public String id;
    public String rating;
    public CourseRate(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }


    public Float getRatingFloat(){
        return Float.parseFloat(rating);
    }
}
