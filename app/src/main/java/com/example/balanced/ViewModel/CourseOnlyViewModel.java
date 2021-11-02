package com.example.balanced.ViewModel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.balanced.Entity.Course;
import com.example.balanced.Entity.CourseRate;
import com.example.balanced.Entity.VideoCourseEntity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CourseOnlyViewModel extends ViewModel {

    // Instances
    private DatabaseReference mDatabase;
    private MutableLiveData<Course> course;
    private MutableLiveData<Integer> countRates;
    private MutableLiveData<Boolean> haveCourse;
    private MutableLiveData<List<VideoCourseEntity>> listVideosCourse;

    public String courseID = "";
    public String uuid = "";
    private Course courseData = new Course();

    public CourseOnlyViewModel(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        course = new MutableLiveData<>();
        countRates = new MutableLiveData<>();
        haveCourse = new MutableLiveData<>();
        listVideosCourse = new MutableLiveData<>();
    }

    public LiveData<Course> getCourse(){
        return course;
    }

    public LiveData<Integer> getCountRates() { return countRates; }

    public LiveData<Boolean> getHaveCourse() { return haveCourse; }

    public LiveData<List<VideoCourseEntity>> getVideos() { return listVideosCourse; }

    public void LoadById(){
        courseRef().addValueEventListener(eventListenerCourse());
        LoadCourseRates();
    }

    public void LoadCourseRates(){
        ratesRef().addValueEventListener(eventListenerCountCourse());
    }

    public void LoadHaveCourse(){
        userCourse().addValueEventListener(eventListenerHaveCourse());
    }

    public void BuyCourse(){
        userCourse().setValue(courseData.getMapData());
    }

    public void LoadVideos(){
        videosRef().addValueEventListener(eventListenerLoadVideos());
    }

    public void UnsuscribeCourse(){
        userCourse().removeValue();
    }

    private DatabaseReference courseRef(){
        return mDatabase.child("Courses").child(courseID);
    }

    private DatabaseReference ratesRef(){
        return mDatabase.child("Courses").child(courseID).child("Rates");
    }

    private DatabaseReference videosRef(){
        return mDatabase.child("Courses").child(courseID).child("Videos");
    }

    private DatabaseReference userCourse(){
        return mDatabase.child("Users").child(uuid).child("Courses").child(courseID);
    }

    private ValueEventListener eventListenerCourse(){
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    course.setValue(snapshot.getValue(Course.class));
                    courseData = snapshot.getValue(Course.class);
                } else {
                    course.setValue(new Course());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                course.setValue(new Course());
            }
        };
    }

    private ValueEventListener eventListenerCountCourse(){
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int count = 0;
                    for (DataSnapshot postSnapshot:snapshot.getChildren()){
                        count++;
                    }
                    countRates.setValue(count);
                } else {
                    countRates.setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                countRates.setValue(0);
            }
        };
    }

    private ValueEventListener eventListenerHaveCourse(){
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                haveCourse.setValue(snapshot.exists());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                haveCourse.setValue(false);
            }
        };
    }

    private ValueEventListener eventListenerLoadVideos(){
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<VideoCourseEntity> courseEntities = new ArrayList<>();
                for(DataSnapshot video: snapshot.getChildren()){
                    VideoCourseEntity videoCourseEntity = video.getValue(VideoCourseEntity.class);
                    videoCourseEntity.id = video.getKey();
                    courseEntities.add(videoCourseEntity);
                }
                listVideosCourse.setValue(courseEntities);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                List<VideoCourseEntity> courseEntities = new ArrayList<>();
                listVideosCourse.setValue(courseEntities);
            }
        };
    }

}
