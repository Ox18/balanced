package com.example.balanced.ServiceImpl;

import com.example.balanced.Service.CursoService;
import com.example.balanced.database.DataBase;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;

public class CursoServiceImpl implements CursoService {
  @Override
  public Task<Void> DeleteById(String id) {
    DatabaseReference connection = DataBase.getConnection();
    return connection.child("Courses").child(id).removeValue();
  }
}
