package com.example.bufrie;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import com.example.bufrie.databinding.ActivityRegistrBinding;

import java.sql.ResultSet;

public class RegistrActivity extends AppCompatActivity {
    ActivityRegistrBinding binding;
    String userId;
    SharedPreferences saveEnter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistrBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent home = new Intent(this, HomeActivity.class);
        Intent back = new Intent(this, EnterActivity.class);
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(back);
                finish();
            }
        });
        binding.enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean all = true;
                if (binding.userName.getText().toString().equals("")) {
                    binding.userName.setTextColor(Color.rgb(200,0,0));
                    binding.mistake.setText("Не все поля заполнены!");
                    binding.mistake.setVisibility(View.VISIBLE);
                    all = false;
                }
                if (binding.phone.getText().toString().equals("")){
                    binding.phone.setTextColor(Color.rgb(200,0,0));
                    binding.mistake.setText("Не все поля заполнены!");
                    binding.mistake.setVisibility(View.VISIBLE);
                    all = false;
                }
                if (binding.password.getText().toString().equals("")){
                    binding.password.setTextColor(Color.rgb(200,0,0));
                    binding.mistake.setText("Не все поля заполнены!");
                    binding.mistake.setVisibility(View.VISIBLE);
                    all = false;
                }
                if (binding.checkPassword.getText().toString().equals("")){
                    binding.checkPassword.setTextColor(Color.rgb(200,0,0));
                    binding.mistake.setText("Не все поля заполнены!");
                    binding.mistake.setVisibility(View.VISIBLE);
                    all = false;
                }
                if (!all) return;
                binding.mistake.setVisibility(View.INVISIBLE);
                binding.userName.setTextColor(Color.rgb(103, 49, 0));
                binding.phone.setTextColor(Color.rgb(103, 49, 0));
                if (!binding.password.getText().toString().equals(binding.checkPassword.getText().toString())){
                    binding.password.setTextColor(Color.rgb(200,0,0));
                    binding.checkPassword.setTextColor(Color.rgb(200,0,0));
                    binding.mistake.setText("Пароли не совпадают!");
                    binding.mistake.setVisibility(View.VISIBLE);
                    return;
                }
                binding.password.setTextColor(Color.rgb(103, 49, 0));
                binding.checkPassword.setTextColor(Color.rgb(103, 49, 0));
                if (setUser()){
                    saveEnter(true);
                    startActivity(home);
                    finish();
                }
                else{
                    binding.userName.setTextColor(Color.rgb(200,0,0));
                    binding.mistake.setText("Такой аккаунт уже существует");
                    binding.mistake.setVisibility(View.VISIBLE);
                }
            }
        });
    }
    public void saveEnter(boolean ent){
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.putString("UserID", userId);
        editor.apply();
    }

    public boolean setUser(){
        if (checkUser()){ return false;}
        String name = binding.userName.getText().toString();
        String phone = binding.phone.getText().toString();
        String password = binding.password.getText().toString();
        String seller;
        if (binding.seller.isChecked()) seller = "1";
        else seller = "0";
        SQLiteDatabase database = new SQLiteHelper(this).getWritableDatabase();
        Cursor cursor = database.query(SQLiteTable.User.TABLE_NAME,
                null, null, null, null, null, null);
        int id_int = cursor.getCount();
        String id = id_int+1+"";
        userId = id;
        cursor.getColumnIndexOrThrow(SQLiteTable.User.COLUMN_ID);
        database.execSQL("INSERT INTO "+SQLiteTable.User.TABLE_NAME+" VALUES ("+id+ ", \""+name+"\", NULL, "+seller+", \""+phone+"\", \""+password+"\")");
        return true;
    }
    private boolean checkUser() {
        String name = binding.userName.getText().toString();
        String phone = binding.phone.getText().toString();
        SQLiteDatabase database = new SQLiteHelper(this).getReadableDatabase();
        String[] projection = {
                SQLiteTable.User.COLUMN_ID
        };
        String selection =
                SQLiteTable.User.COLUMN_NAME + " = ? and " +
                        SQLiteTable.User.COLUMN_PHONE + " = ?";
        String[] selectionArgs = {name, phone};

        Cursor cursor = database.query(
                SQLiteTable.User.TABLE_NAME,     // Запрашиваемая таблица
                projection,                               // Возвращаемый столбец
                selection,                                // Столбец для условия WHERE
                selectionArgs,                            // Значение для условия WHERE
                null,                                     // не группировать строки
                null,                                     // не фильтровать по группам строк
                null                                      // не сортировать
        );
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            userId = cursor.getString(0);
            cursor.close();
            return true;
        }

        cursor.close();
        return false;
    }
}