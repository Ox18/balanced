package com.example.balanced.Entity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class User2{
    private DatabaseReference mDatabase;
    public boolean payment_active;
    public String phone;
    public String correo;
    public String name;
    public String dni;
    public String role;

    public User2(){

        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public String getFirstLetter(){
        char s = this.name.charAt(0);
        String letter = Character.toString(s);
        return letter.toUpperCase();
    }

    public String getFirstName(){
        return this.name.split(" ")[0];
    }

    public Map<String, Object> getMapData(){
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("payment_active", payment_active);
        map.put("phone", phone);
        map.put("correo", correo);
        map.put("dni", dni);
        map.put("role", role);
        return map;
    }


}