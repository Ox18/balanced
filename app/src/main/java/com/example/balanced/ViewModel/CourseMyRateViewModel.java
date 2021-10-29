package com.example.balanced.ViewModel;

import android.provider.Telephony;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.balanced.Entity.Comment;
import com.example.balanced.Entity.Course;
import com.example.balanced.Entity.CourseMyRateEntity;
import com.example.balanced.Entity.CourseRate;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
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

        DatabaseReference myRateRef = mDatabase
                .child("Courses")
                .child(courseID)
                .child("Rates")
                .child(userID);

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    CourseMyRateEntity courseMyRateEntity = snapshot.getValue(CourseMyRateEntity.class);
                    resultado.setValue(courseMyRateEntity.getRatingInFloat());

                    updateRateOnListener(courseMyRateEntity.rating, courseID, userID);
                }else{
                    resultado.setValue(Float.parseFloat("0.0"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        myRateRef.addValueEventListener(eventListener);
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
                    }
                });

    }

    /**
     * Actualiza todos los comentarios que le pertenecen al usuario
     * con el rating actual
     * @param rating rating actual
     * @param courseID id del curso al que pertenece al rating
     * @param userID id del usuario al que pertenece el rating
     */
    private void updateRateOnListener(String rating, String courseID, String userID){
        Query query =  mDatabase
                .child("Courses")
                .child(courseID)
                .child("Comments")
                .orderByChild("userID")
                .equalTo(userID);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snap: snapshot.getChildren()){
                    Comment comment = snap.getValue(Comment.class);
                    comment.rating = rating;
                    snap.getRef().updateChildren(comment.getMapData());
                }
                fetchRatingOfCourse(courseID);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void fetchRatingOfCourse(String courseID){
        Query query =  mDatabase
                .child("Courses")
                .child(courseID)
                .child("Rates");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long count = snapshot.getChildrenCount();

                float sum = Float.parseFloat("0.0");

                for(DataSnapshot rate: snapshot.getChildren()){
                    CourseRate courseRate = rate.getValue(CourseRate.class);
                    courseRate.id = rate.getKey();

                    sum += courseRate.getRatingFloat();
                }
                float result = sum / count;
                mDatabase
                        .child("Courses")
                        .child(courseID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            Course course = snapshot.getValue(Course.class);
                            int rateTotal = (int)result;
                            if(rateTotal > 5){
                                rateTotal = 5;
                            }
                            if(rateTotal < 0){
                                rateTotal = 0;
                            }

                            course.rate = Integer.toString(rateTotal);

                            snapshot.getRef()
                                    .updateChildren(course.getMapData());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
