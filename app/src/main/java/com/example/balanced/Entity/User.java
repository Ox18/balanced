package com.example.balanced.Entity;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String correo;
    private String dni;
    private String name;
    private String phone;
    private Boolean payment_active;


    public User(String correo, String dni, String name, String phone, Boolean payment_active) {
        this.correo = correo;
        this.dni = dni;
        this.name = name;
        this.phone = phone;
        this.payment_active = payment_active;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getName() {
        return name.replaceAll("-", " ");
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Boolean getPayment_active() {
        return payment_active;
    }

    public void setPayment_active(Boolean payment_active) {
        this.payment_active = payment_active;
    }

    public Map<String, Object> getMapData(){
        Map<String, Object> map = new HashMap<>();
        map.put("name", getName());
        map.put("phone", getPhone());
        map.put("dni", getDni());
        map.put("correo", getCorreo());
        map.put("payment_active", getPayment_active());
        return map;
    }
}