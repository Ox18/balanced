package com.example.balanced.Entity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CourseMyRateEntity {
    private DatabaseReference mDatabase;
    public String rating;

    public CourseMyRateEntity(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public Float getRatingInFloat(){
        return Float.parseFloat(this.rating);
    }
}
