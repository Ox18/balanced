package com.example.balanced.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.balanced.Adapters.MyCoursesProfesionalAdapter;
import com.example.balanced.Entity.User2;
import com.example.balanced.R;
import com.example.balanced.ScreenCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class LobbyProfesionalActivity extends ScreenCompatActivity {

    private Button btnCrear;
    private TextView logoletter;
    private TextView txtWelcome;
    private LinearLayout circleProfile;
    private RecyclerView recyclerView;
    private MyCoursesProfesionalAdapter myCoursesProfesionalAdapter = new MyCoursesProfesionalAdapter();
    private User2 user = new User2();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby_profesional);

        btnCrear = findViewById(R.id.btnAgregar);
        logoletter = findViewById(R.id.logoLetter);
        txtWelcome = findViewById(R.id.txtWelcome);
        circleProfile = findViewById(R.id.profile_circle);

        circleProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadProfile();
            }
        });

        mDatabase.child("Users")
                .child(GetID())
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                user = snapshot.getValue(User2.class);
                                LoadComponents();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        }
                );
    }

    public void LoadComponents(){
        logoletter.setText(user.getFirstLetter());
        txtWelcome.setText("Bienvenido, " + user.getFirstName());
        LoadAdapater();

    }

    public void LoadAdapater(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        recyclerView = (RecyclerView)findViewById(R.id.recyclerPreviewCourses);
        recyclerView.setAdapter(myCoursesProfesionalAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        myCoursesProfesionalAdapter.Load(mDatabase, GetID());
    }
}