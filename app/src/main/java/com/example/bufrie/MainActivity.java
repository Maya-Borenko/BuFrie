package com.example.bufrie;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;

import com.example.bufrie.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    SharedPreferences saveEnter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SQLiteDatabase database = new SQLiteHelper(this).getWritableDatabase();
        ContentValues values = new ContentValues();
        Intent home = new Intent(this, HomeActivity.class);
        try {
            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", this.MODE_PRIVATE);
            boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
            if (isLoggedIn) {
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        }catch (Exception error){
        }
        Intent enterActivity = new Intent(this, EnterActivity.class);
        binding.enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(enterActivity);
                finish();
            }
        });
        Intent registrActivity = new Intent(this, RegistrActivity.class);
        binding.registr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(registrActivity);
                finish();
            }
        });
    }
}