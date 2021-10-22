package com.example.balanced;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.balanced.Activities.ActivityProfile;
import com.example.balanced.Activities.ChangePasswordActivity;
import com.example.balanced.Activities.CursoDetalleActivity;
import com.example.balanced.Activities.LobbyActivity;
import com.example.balanced.Activities.LobbyProfesionalActivity;
import com.example.balanced.Activities.MainActivity;
import com.example.balanced.Activities.PaymentActivity;
import com.example.balanced.Activities.RegisterActivity;
import com.example.balanced.Entity.Course;
import com.example.balanced.Entity.User2;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class ScreenCompatActivity extends AppCompatActivity {

    public FirebaseAuth mAuth;
    public DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void LoadLobbyProfesional(){
        startActivity(new Intent(this, LobbyProfesionalActivity.class));
        finish();
    }

    public void LoadCursoDetaller(String id){
        Intent intent = new Intent(this, CursoDetalleActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
        finish();
    }

    public void LoadChangePassword(){
        startActivity(new Intent(this, ChangePasswordActivity.class));
        finish();
    }

    public void LoadProfile(){
        startActivity(new Intent(this, ActivityProfile.class));
        finish();
    }

    public void LoadLobby(){
        startActivity(new Intent(this, LobbyActivity.class));
        finish();
    }

    public void LoadLogin(){
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    public void LoadRegister(){
        startActivity(new Intent(this, RegisterActivity.class));
        finish();
    }

    public void LoadPayment(){
        startActivity(new Intent(this, PaymentActivity.class));
        finish();
    }

    // Firebase Functions
    public void SignOut(){
        try{
            mAuth.signOut();
            LoadLogin();
        }catch (Exception ex) {
            Toast.makeText(this, "Hubo un error al salir de la sesión", Toast.LENGTH_SHORT).show();
        }
    }

    public void Login(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            LoadThePayment();
                        }else{
                            Toast.makeText(ScreenCompatActivity.this, "No se pudo completar el inicio de sesión", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public String GetID(){
        return mAuth.getCurrentUser().getUid();
    }

    public void LoadThePayment(){
        String id = mAuth.getCurrentUser().getUid();

        mDatabase.child("Users")
                .child(id)
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                User2 user2 = snapshot.getValue(User2.class);

                                SharedPreferences preferences = getSharedPreferences("auth", Context.MODE_PRIVATE);

                                String name =  user2.name;
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("name", name);
                                editor.apply();

                                if(user2.payment_active){
                                    if(user2.isProfessional()){
                                        LoadLobbyProfesional();
                                    }
                                    if(user2.isUser()){
                                        LoadLobby();
                                    }
                                }else{
                                    LoadPayment();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        }
                );
    }

    public void VerifyExistAuth(){
        if(mAuth.getCurrentUser() != null){
            LoadThePayment();
        }else{
            LoadLogin();
        }
    }

    public void RegisterAccount(String email, String password, User2 userData){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            task.addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    String id = mAuth.getCurrentUser().getUid();
                                    AddUser(id, userData.getMapData());
                                }
                            });
                        }
                    }
                });
    }

    public void AddUser(String uid, Map<String, Object> userData){
        userData.put("role", "user");
        mDatabase.child("Users").child(uid)
                .setValue(userData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        LoadPayment();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ScreenCompatActivity.this, "No se pudo completar el registro", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void AddCourseForMe(String courseID, Course course){
        String uid = GetID();
        mDatabase.child("Users").child(uid)
                .child("Courses")
                .child(courseID)
                .setValue(course.getMapData())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(ScreenCompatActivity.this, "Se agrego el servicio satisfactoriamente", Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(getIntent());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ScreenCompatActivity.this, "No se pudo agregar el servicio", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void logout(){
        mAuth.signOut();
        LoadLogin();
    }

    public void deleteMeAccount(){
        String uid = GetID();
        mDatabase.child("Users")
                .child(uid)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            logout();
                        }
                    }
                });
    }

    public void updateMeAccount(User2 user){
        String uid = GetID();
        mDatabase.child("Users")
                .child(uid)
                .setValue(user.getMapData()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(ScreenCompatActivity.this, "Se actualizaron tus datos", Toast.LENGTH_SHORT).show();
                        RefreshActivity();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ScreenCompatActivity.this, "No se pudo actualizar tus datos", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public void RefreshActivity(){
        finish();
        startActivity(getIntent());
    }

    public void ResetPassword(){
        sendResetPassword(mAuth.getCurrentUser().getEmail());
    }

    public void sendResetPassword(String email){
        mAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getBaseContext(), "Revise su correo para realizar el cambio de contraseña", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void ChangePassword(String password){
        mAuth.getCurrentUser()
                .updatePassword(password)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getBaseContext(), "La contraseña se cambio correctamente", Toast.LENGTH_SHORT).show();
                            LoadProfile();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
