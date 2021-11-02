package com.example.balanced.Entity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class VideoCourseEntity {
    private DatabaseReference mDatabase;
    public String Title;
    public String number;
    public String time;
    public String url;
    public String id;

    public VideoCourseEntity(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }
}
