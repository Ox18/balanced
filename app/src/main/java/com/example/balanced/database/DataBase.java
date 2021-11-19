package com.example.balanced.database;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DataBase {
  private DatabaseReference connection;

  public DataBase(){
    this.connection =  FirebaseDatabase.getInstance().getReference();
  }

  private static DataBase getInstance(){
    return new DataBase();
  }

  public static DatabaseReference getConnection(){
    DataBase db = DataBase.getInstance();
    return db.connection;
  }
}
