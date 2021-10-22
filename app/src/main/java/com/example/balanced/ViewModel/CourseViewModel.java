package com.example.balanced.ViewModel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.balanced.Entity.Course;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CourseViewModel extends ViewModel {

    public DatabaseReference mDatabase;
    private MutableLiveData<Course> resultado;

    public CourseViewModel(){
        resultado = new MutableLiveData<>();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public LiveData<Course> getResultado(){
        return resultado;
    }

    public void course(String courseID){
        mDatabase
                .child("Courses")
                .child(courseID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            Course course = snapshot.getValue(Course.class);
                            resultado.setValue(course);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                            resultado.setValue(new Course());
                    }
                });
    }

}
