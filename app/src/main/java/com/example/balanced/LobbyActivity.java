package com.example.balanced;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.balanced.Entity.User2;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class LobbyActivity extends ScreenCompatActivity{

    LinearLayout profileCircle;
    TextView txtWelcome;
    TextView txtLogoLetter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        profileCircle = (LinearLayout)findViewById(R.id.profile_circle);

        profileCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignOut();
            }
        });

        txtWelcome = (TextView)findViewById(R.id.txtWelcome);
        txtLogoLetter = (TextView)findViewById(R.id.logoLetter);

        String id = mAuth.getCurrentUser().getUid();
        mDatabase.child("Users")
                .child(id)
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                User2 user = snapshot.getValue(User2.class);
                                String firstWord = user.name.split(" ")[0];
                                txtWelcome.setText("Bienvenido, " + firstWord);
                                txtLogoLetter.setText(firstWord.substring(0, 1));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        }
                );
    }

}
