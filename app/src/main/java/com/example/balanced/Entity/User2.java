package com.example.balanced.Entity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class User2{
    private DatabaseReference mDatabase;
    public boolean payment_active;
    public String phone;
    public String correo;
    public String name;
    public String dni;

    public User2(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }
}