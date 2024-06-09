package com.example.bufrie;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.bufrie.databinding.ActivityEditBinding;

import java.io.ByteArrayOutputStream;

public class EditActivity extends AppCompatActivity {
    ActivityEditBinding  binding;
    private static final String CHANNEL_ID = "my_channel_id";
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final int REQUEST_CODE = 1;
    User user;
    String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getString("UserID", "");
        user = getUser(userId);
        binding.avaUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageSourceDialog();
            }
        });
        binding.userName.setText(user.userName);
        binding.phone.setText(user.phone);
        binding.password.setText(user.password);
        if (user.image != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(user.image, 0, user.image.length);
            binding.avaUser.setImageBitmap(bitmap);
        }
        Toast toast = Toast.makeText(this, "Профиль изменен", Toast.LENGTH_SHORT);
        binding.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToDB();
                toast.show();
            }
        });
        Intent mainActivity = new Intent(this, MainActivity.class);
        binding.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFromBD();
                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isLoggedIn", false);
                editor.apply();
                sendNotification();
                startActivity(mainActivity);
                finish();
            }
        });
        Intent back = new Intent(this, HomeActivity.class);
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(back);
                finish();
            }
        });
        createNotificationChannel();
    }
    private void sendNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle("До свидания!")
                .setContentText("Мы будем скучать(")
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
    private void deleteFromBD(){
        SQLiteDatabase database = new SQLiteHelper(this).getWritableDatabase();
        database.execSQL("DELETE FROM User WHERE ID="+userId);
    }
    private void saveToDB() {
        ImageView imageView = binding.avaUser;
        Bitmap bitmap = ((BitmapDrawable) binding.avaUser.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 800, 800, true);
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] imageInByte = baos.toByteArray();
        SQLiteDatabase database = new SQLiteHelper(this).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SQLiteTable.User.COLUMN_IMAGE, imageInByte);
        database.update(SQLiteTable.User.TABLE_NAME, values, SQLiteTable.User.COLUMN_ID + "=" + Integer.parseInt(String.valueOf(user.id)), null);

        values.put(SQLiteTable.User.COLUMN_NAME, binding.userName.getText().toString());
        values.put(SQLiteTable.User.COLUMN_PASSWORD, binding.password.getText().toString());
        values.put(SQLiteTable.User.COLUMN_PHONE, binding.phone.getText().toString());
        values.put(SQLiteTable.User.COLUMN_IMAGE, imageInByte);
        Cursor cursor = database.query(SQLiteTable.User.TABLE_NAME,
                null, null, null, null, null, null);
        String id = cursor.getCount()+1+"";
                database.update(SQLiteTable.User.TABLE_NAME, values, SQLiteTable.User.COLUMN_ID + "=" + Integer.parseInt(userId), null);}

    private void showImageSourceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Выберите источник изображения");
        builder.setItems(new CharSequence[]{"Сделать фото", "Выбрать из галереи"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    // Открыть камеру
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        // Запрос разрешения, если оно не было предоставлено
                        ActivityCompat.requestPermissions(EditActivity.this, new String[]{android.Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    } else {
                        // Разрешение уже предоставлено, можно открывать камеру
                        openCamera();
                    }
                } else {
                    // Открыть галерею
                    openGallery();
                }
            }
        });
        builder.create().show();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Разрешение предоставлено, можно открывать камеру
                openCamera();
            } else {
                // Разрешение не предоставлено, показать сообщение пользователю
                // ...
            }
        }
    }


    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, REQUEST_IMAGE_PICK);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                // Получаем сделанное фото из камеры
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                binding.avaUser.setImageBitmap(imageBitmap);
            } else if (requestCode == REQUEST_IMAGE_PICK) {
                // Получаем выбранное изображение из галереи
                Uri selectedImageUri = data.getData();
                binding.avaUser.setImageURI(selectedImageUri);
            }
        }
    }
    private User getUser(String userId) {
        SQLiteDatabase database = new SQLiteHelper(this).getReadableDatabase();
        String[] userProjection = {
                SQLiteTable.User.COLUMN_NAME,
                SQLiteTable.User.COLUMN_PHONE,
                SQLiteTable.User.COLUMN_ID,
                SQLiteTable.User.COLUMN_IMAGE,
                SQLiteTable.User.COLUMN_PASSWORD
        };
        String userSelection =
                SQLiteTable.User.COLUMN_ID + " = ?";
        String[] selectionArgs = {userId};

        Cursor userCursor = database.query(
                SQLiteTable.User.TABLE_NAME,     // Запрашиваемая таблица
                userProjection,                               // Возвращаемый столбец
                userSelection,                                // Столбец для условия WHERE
                selectionArgs,                            // Значение для условия WHERE
                null,                                     // не группировать строки
                null,                                     // не фильтровать по группам строк
                null                                      // не сортировать
        );
        userCursor.moveToFirst();
        String name = userCursor.getString(userCursor.getColumnIndexOrThrow(SQLiteTable.User.COLUMN_NAME));
        String phone = userCursor.getString(userCursor.getColumnIndexOrThrow(SQLiteTable.User.COLUMN_PHONE));
        String password = userCursor.getString(userCursor.getColumnIndexOrThrow(SQLiteTable.User.COLUMN_PASSWORD));
        int id = userCursor.getInt(userCursor.getColumnIndexOrThrow(SQLiteTable.User.COLUMN_ID));
        byte[] imageInByte = userCursor.getBlob(userCursor.getColumnIndexOrThrow(SQLiteTable.Ads.COLUMN_IMAGE));
        boolean seller = false;
        User user = new User(name, seller);
        user.id = id;
        user.phone = phone;
        user.password = password;
        user.setImage(imageInByte);
        return user;
    }
}