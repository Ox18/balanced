package com.example.balanced.Service;

import com.google.android.gms.tasks.Task;

public interface CursoService {
  public Task<Void> DeleteById(String id);
}
