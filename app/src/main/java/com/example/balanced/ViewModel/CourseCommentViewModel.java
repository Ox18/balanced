package com.example.balanced.ViewModel;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.balanced.Entity.Comment;
import com.example.balanced.Entity.Course;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CourseCommentViewModel extends ViewModel {
    private DatabaseReference mDatabase;
    private MutableLiveData<ArrayList<Comment>> resultado;
    private FirebaseAuth mAuth;

    public CourseCommentViewModel(){
        mAuth = FirebaseAuth.getInstance();
        resultado = new MutableLiveData<>();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public LiveData<ArrayList<Comment>> getResultado(){ return resultado; }

    public void Load(String courseID){
        mDatabase
                .child("Courses")
                .child(courseID)
                .child("Comments")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            ArrayList<Comment> listCommentClean = new ArrayList<>();
                            for (DataSnapshot commentSnapShot:snapshot.getChildren()){
                                Comment comment = commentSnapShot.getValue(Comment.class);
                                listCommentClean.add(comment);
                            }
                            resultado.setValue(listCommentClean);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void PushComment(String courseID, float rating, String comentario){
        String userID = mAuth.getUid();

        Comment comment = new Comment();
        comment.rating = Float.toString(rating);
        comment.author = "Wilmer Delgado";
        comment.userID = userID;
        comment.comment = comentario;
        String key = mDatabase.child("Courses").child(courseID).child("Comments").push().getKey();
        mDatabase
                .child("Courses")
                .child(courseID)
                .child("Comments")
                .child(key)
                .setValue(comment.getMapData())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Load(courseID);
                    }
                });
    }



}
