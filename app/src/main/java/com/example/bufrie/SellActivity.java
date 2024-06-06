package com.example.bufrie;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.ContentValues;
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
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.bufrie.databinding.ActivitySellBinding;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

public class SellActivity extends AppCompatActivity {
    ActivitySellBinding binding;
    User u, me;
    boolean newAd = true;
    boolean dislike = true;
    String itemId;
    private static final int PICK_IMAGE_REQUEST = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySellBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", this.MODE_PRIVATE);
        String userId = sharedPreferences.getString("UserID", "");
        me = getUser(userId);
        Intent intent = getIntent();
        String itemNew = intent.getStringExtra("new");
        itemId = intent.getStringExtra("itemId");
        String itemEdit = intent.getStringExtra("edit");
        if (isDisliked()){
            binding.like.setText("Понравилось!");}
        else{binding.like.setText("Уже не нравится!");}
        if (Objects.equals(itemEdit, "1")){
            newAd = false;
            Add current = getAd(itemId);
            binding.priceAd.setText(current.price+"");
            binding.titleAd.setText(current.title);
            binding.descrAd.setText(current.descriptions);
            binding.sellerAd.setText(u.userName);
            binding.contactAd.setText(u.phone+"");
            binding.act.setChecked(current.relevant);
            if (current.image != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(current.image, 0, current.image.length);
                binding.selAni.setImageBitmap(bitmap);
            }
            if (me.sell && u.id == me.id){
                binding.edit.setVisibility(View.VISIBLE);
                binding.like.setVisibility(View.INVISIBLE);
            }
            else{
                binding.edit.setVisibility(View.INVISIBLE);
                binding.like.setVisibility(View.VISIBLE);
            }
        }
        else {
            binding.edit.setVisibility(View.VISIBLE);
            binding.like.setVisibility(View.INVISIBLE);
            binding.sellerAd.setText(me.userName);
            binding.contactAd.setText(me.phone+"");}


        Intent back = new Intent(this, HomeActivity.class);
        binding.exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(back);
                finish();
            }
        });
        SQLiteDatabase database = new SQLiteHelper(this).getWritableDatabase();
        binding.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDisliked()){
                    binding.like.setText("Уже не нравится");
                    dislike = false;
                    ContentValues values = new ContentValues();
                    values.put(SQLiteTable.Favourites.COLUMN_AD, itemId);
                    values.put(SQLiteTable.Favourites.COLUMN_USER, me.id);
                    database.insert(SQLiteTable.Favourites.TABLE_NAME, null, values);
                    return;
                }
                binding.like.setText("Понравилось!");
                dislike = true;
                database.execSQL("DELETE FROM "+SQLiteTable.Favourites.TABLE_NAME + " WHERE " + SQLiteTable.Favourites.COLUMN_AD +" = "+ itemId +" and "+ SQLiteTable.Favourites.COLUMN_USER + " = " + me.id);
            }
        });

        binding.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.saveButton.setVisibility(View.VISIBLE);
                binding.act.setVisibility(View.VISIBLE);
                binding.titleAd.setEnabled(true);
                binding.descrAd.setEnabled(true);
                binding.priceAd.setEnabled(true);
                binding.selAni.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showImageSourceDialog();
                    }
                });
            }
        });
        binding.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.saveButton.setVisibility(View.INVISIBLE);
                binding.act.setVisibility(View.INVISIBLE);
                binding.titleAd.setEnabled(false);
                binding.descrAd.setEnabled(false);
                binding.priceAd.setEnabled(false);
                saveToDB();
            }
        });

    }
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;
    private static final int REQUEST_CAMERA_PERMISSION = 100;
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
                        ActivityCompat.requestPermissions(SellActivity.this, new String[]{android.Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
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
    private void saveToDB() {
        ImageView imageView = binding.selAni;
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 600, 800, true);
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] imageInByte = baos.toByteArray();

        SQLiteDatabase database = new SQLiteHelper(this).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SQLiteTable.Ads.COLUMN_NAME, binding.titleAd.getText().toString());
        values.put(SQLiteTable.Ads.COLUMN_DESCRIPTIONS, binding.descrAd.getText().toString());
        values.put(SQLiteTable.Ads.COLUMN_PRICE, Integer.parseInt(binding.priceAd.getText().toString()));
        values.put(SQLiteTable.Ads.COLUMN_IMAGE, imageInByte);
        Cursor cursor = database.query(SQLiteTable.Ads.TABLE_NAME,
                null, null, null, null, null, null);
        String id = cursor.getCount()+1+"";
        int rel = 0;
        if (binding.act.isChecked()) rel = 1;
        values.put(SQLiteTable.Ads.COLUMN_RELEVANT, rel);
        if (newAd){
            values.put(SQLiteTable.Ads.COLUMN_ID, id);
            values.put(SQLiteTable.Ads.COLUMN_USER, me.id);
            long newRowId = database.insert(SQLiteTable.Ads.TABLE_NAME, null, values);}

        else{database.update(SQLiteTable.Ads.TABLE_NAME, values, SQLiteTable.Ads.COLUMN_ID + "=" + Integer.parseInt(itemId), null);}
    }
    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, REQUEST_IMAGE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                // Получаем сделанное фото из камеры
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                binding.selAni.setImageBitmap(imageBitmap);
            } else if (requestCode == REQUEST_IMAGE_PICK) {
                // Получаем выбранное изображение из галереи
                Uri selectedImageUri = data.getData();
                binding.selAni.setImageURI(selectedImageUri);
            }
        }
    }
    private Add getAd(String adId) {
        SQLiteDatabase database = new SQLiteHelper(this).getReadableDatabase();
        String[] adsProjection = {
                SQLiteTable.Ads.COLUMN_NAME,
                SQLiteTable.Ads.COLUMN_DESCRIPTIONS,
                SQLiteTable.Ads.COLUMN_PRICE,
                SQLiteTable.Ads.COLUMN_USER,
                SQLiteTable.Ads.COLUMN_RELEVANT,
                SQLiteTable.Ads.COLUMN_IMAGE
        };
        String adsSelection =
                SQLiteTable.Ads.COLUMN_ID + " = ?";
        String[] selectionArgs = {adId};

        Cursor adCursor = database.query(
                SQLiteTable.Ads.TABLE_NAME,     // Запрашиваемая таблица
                adsProjection,                               // Возвращаемый столбец
                adsSelection,                                // Столбец для условия WHERE
                selectionArgs,                            // Значение для условия WHERE
                null,                                     // не группировать строки
                null,                                     // не фильтровать по группам строк
                null                                      // не сортировать
        );
        adCursor.moveToFirst();
        String name = adCursor.getString(adCursor.getColumnIndexOrThrow(SQLiteTable.Ads.COLUMN_NAME));
        int price = adCursor.getInt(adCursor.getColumnIndexOrThrow(SQLiteTable.Ads.COLUMN_PRICE));
        String descriptions = adCursor.getString(adCursor.getColumnIndexOrThrow(SQLiteTable.Ads.COLUMN_DESCRIPTIONS));
        String userId = adCursor.getString(adCursor.getColumnIndexOrThrow(SQLiteTable.Ads.COLUMN_USER));
        u = getUser(userId);
        int relevant = adCursor.getInt(adCursor.getColumnIndexOrThrow(SQLiteTable.Ads.COLUMN_RELEVANT));
        byte[] imageInByte = adCursor.getBlob(adCursor.getColumnIndexOrThrow(SQLiteTable.Ads.COLUMN_IMAGE));

        Add current = new Add(Integer.parseInt(adId), price, imageInByte, name, u.userName);
        current.descriptions = descriptions;
        if (relevant == 1) current.relevant = true;
        else current.relevant = false;
        return current;
    }
    private User getUser(String userId) {
        SQLiteDatabase database = new SQLiteHelper(this).getReadableDatabase();
        String[] userProjection = {
                SQLiteTable.User.COLUMN_NAME,
                SQLiteTable.User.COLUMN_SELLER,
                SQLiteTable.User.COLUMN_PHONE,
                SQLiteTable.User.COLUMN_ID
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
        int sellerIs = userCursor.getInt(userCursor.getColumnIndexOrThrow(SQLiteTable.User.COLUMN_SELLER));
        int gettingID = userCursor.getInt(userCursor.getColumnIndexOrThrow(SQLiteTable.User.COLUMN_ID));
        boolean seller = false;
        if (sellerIs == 1){
            seller = true;
        }
        User user = new User(name, seller);
        user.phone = phone;
        user.id = gettingID;
        userCursor.close();
        return user;
    }

    public boolean isDisliked(){
        SQLiteDatabase database = new SQLiteHelper(this).getReadableDatabase();
        String[] userProjection = {
                SQLiteTable.Favourites.COLUMN_USER,
                SQLiteTable.Favourites.COLUMN_AD
        };
        String userSelection =
                SQLiteTable.Favourites.COLUMN_USER + " = ? and " + SQLiteTable.Favourites.COLUMN_AD +" = ?";
        String[] selectionArgs = {me.id+"", itemId};

        Cursor userCursor = database.query(
                SQLiteTable.Favourites.TABLE_NAME,     // Запрашиваемая таблица
                userProjection,                               // Возвращаемый столбец
                userSelection,                                // Столбец для условия WHERE
                selectionArgs,                            // Значение для условия WHERE
                null,                                     // не группировать строки
                null,                                     // не фильтровать по группам строк
                null                                      // не сортировать
        );
        if (userCursor.getCount()!=0) return false;
        return true;
    }
}