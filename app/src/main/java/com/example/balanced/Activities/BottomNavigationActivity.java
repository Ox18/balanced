package com.example.balanced.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.balanced.Fragments.DescargasFragment;
import com.example.balanced.Fragments.EstudiarFragment;
import com.example.balanced.Fragments.ExplorarFragment;
import com.example.balanced.Fragments.PerfilFragment;
import com.example.balanced.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BottomNavigationActivity extends AppCompatActivity {

  EstudiarFragment estudiarFragment = new EstudiarFragment();
  ExplorarFragment explorarFragment = new ExplorarFragment();
  DescargasFragment descargasFragment = new DescargasFragment();
  PerfilFragment perfilFragment = new PerfilFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation_view);

      BottomNavigationView navigation = findViewById(R.id.bottom_navigation);
      navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
      @Override
      public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
          case R.id.estudiarFragment:
            loadFragment(estudiarFragment);
            return true;
          case R.id.explorarFragment:
            loadFragment(explorarFragment);
            return true;
          case R.id.descargasFragment:
            loadFragment(descargasFragment);
            return true;
          case R.id.perfilFragment:
            loadFragment(perfilFragment);
            return true;
        }
        return false;
      }
    };

    public void loadFragment(Fragment fragment){
      FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
      transaction.replace(R.id.frame_container, fragment);
      transaction.commit();
    }
}
