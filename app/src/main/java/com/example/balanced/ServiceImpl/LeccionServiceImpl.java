package com.example.balanced.ServiceImpl;

import com.example.balanced.Service.LeccionService;
import com.example.balanced.database.DataBase;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;

public class LeccionServiceImpl implements LeccionService {
  @Override
  public Task<Void> DeleteById(String id, String courseID) {
    DatabaseReference connection = DataBase.getConnection();
    return connection.child("Courses").child(courseID).child("Videos").child(id).removeValue();
  }
}
