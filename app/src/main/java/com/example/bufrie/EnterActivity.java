package com.example.bufrie;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.bufrie.databinding.ActivityEnterBinding;

import java.sql.ResultSet;

public class EnterActivity extends AppCompatActivity {
    String userId;
    private static final String CHANNEL_ID = "my_channel_id";
    ActivityEnterBinding binding;
    SharedPreferences saveEnter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEnterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent home = new Intent(this, HomeActivity.class);
        Intent registrActivity = new Intent(this, RegistrActivity.class);
        binding.reg.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               startActivity(registrActivity);
               finish();
           }
        });
        binding.enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean all = true;
                if (binding.login.getText().toString().equals("")) {
                    binding.login.setTextColor(Color.rgb(200,0,0));
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

                if (!all) return;
                binding.mistake.setVisibility(View.INVISIBLE);
                binding.login.setTextColor(Color.rgb(103, 49, 0));
                binding.password.setTextColor(Color.rgb(103, 49, 0));

                if (checkUser()){
                    binding.mistake.setVisibility(View.INVISIBLE);
                    binding.login.setTextColor(Color.rgb(103, 49, 0));
                    binding.password.setTextColor(Color.rgb(103, 49, 0));

                    saveEnter(true);
                    sendNotification();
                    startActivity(home);
                    finish();
                }else{
                    binding.login.setTextColor(Color.rgb(200,0,0));
                    binding.password.setTextColor(Color.rgb(200,0,0));
                    binding.mistake.setText("Неверные логин или пароль!");
                    binding.mistake.setVisibility(View.VISIBLE);
                }
                    }
                });
        createNotificationChannel();
    }

    private void sendNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle("Здравствуйте!")
                .setContentText("Рады снова вас видеть!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "My Channel";
            String description = "This is my channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    public void saveEnter(boolean ent){
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", true);
        Log.d("MyLogs", "user_ID_save = "+userId);
        editor.putString("UserID", userId);
        editor.apply();
    }
    private boolean checkUser() {
        String name = binding.login.getText().toString();
        String password = binding.password.getText().toString();
        SQLiteDatabase database = new SQLiteHelper(this).getReadableDatabase();
        String[] projection = {
                SQLiteTable.User.COLUMN_NAME,
                SQLiteTable.User.COLUMN_ID,
                SQLiteTable.User.COLUMN_PASSWORD
        };
        String selection =
                SQLiteTable.User.COLUMN_NAME + " = ? and " +
                        SQLiteTable.User.COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = {name, password};

        Cursor cursor = database.query(
                SQLiteTable.User.TABLE_NAME,     // Запрашиваемая таблица
                projection,                               // Возвращаемый столбец
                selection,                                // Столбец для условия WHERE
                selectionArgs,                            // Значение для условия WHERE
                null,                                     // не группировать строки
                null,                                     // не фильтровать по группам строк
                null                                      // не сортировать
        );
        cursor.moveToFirst();
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            userId = cursor.getString(cursor.getColumnIndexOrThrow(SQLiteTable.User.COLUMN_ID));
            cursor.close();

            return true;
        }

        cursor.close();
        return false;
    }
}