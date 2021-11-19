package com.example.balanced.Service;

import com.example.balanced.ServiceImpl.LeccionServiceImpl;
import com.google.android.gms.tasks.Task;

public interface LeccionService {
  public Task<Void> DeleteById(String id, String courseID);
}
