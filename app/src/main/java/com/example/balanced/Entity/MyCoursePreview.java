package com.example.balanced.Entity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MyCoursePreview {
    private DatabaseReference mDatabase;
    public String name;
    public String image;
    public String id;

    public MyCoursePreview(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }
}