package com.example.balanced.ViewModel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.balanced.Entity.Course;
import com.example.balanced.Entity.CourseMyRateEntity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class CourseMyRateViewModel extends ViewModel {
    private DatabaseReference mDatabase;
    private MutableLiveData<Float> resultado;
    private FirebaseAuth mAuth;

    public CourseMyRateViewModel(){
        mAuth = FirebaseAuth.getInstance();
        resultado = new MutableLiveData<>();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public LiveData<Float> resultado(){ return resultado; }

    public void load(String courseID){
        String userID = mAuth.getUid();

        mDatabase
                .child("Courses")
                .child(courseID)
                .child("Rates")
                .child(userID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            CourseMyRateEntity courseMyRateEntity = snapshot.getValue(CourseMyRateEntity.class);
                            resultado.setValue(courseMyRateEntity.getRatingInFloat());
                        }else{
                            resultado.setValue(Float.parseFloat("0.0"));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        resultado.setValue(Float.parseFloat("0.0"));
                    }
                });
    }

    public void changeRate(float rating, String courseID) {
        String rate = Float.toString(rating);
        String userID = mAuth.getUid();
        Map<String, Object> map = new HashMap<>();
        map.put("rating", rate);
        mDatabase
                .child("Courses")
                .child(courseID)
                .child("Rates")
                .child(userID)
                .setValue(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        load(courseID);
                    }
                });

    }
}
