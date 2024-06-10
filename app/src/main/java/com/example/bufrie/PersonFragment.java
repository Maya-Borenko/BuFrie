package com.example.bufrie;

import static androidx.core.provider.FontsContractCompat.FontRequestCallback.RESULT_OK;

import android.app.Activity;
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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.bufrie.databinding.FragmentPersonBinding;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;


public class PersonFragment extends Fragment {
    User user;
    MyListAdapter adapter;
    private static final String CHANNEL_ID = "my_channel_id";
    ArrayList<Add> adds = new ArrayList<Add>();
    FragmentPersonBinding binding;


    PersonFragment(User currentUser){
        this.user = currentUser;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPersonBinding.inflate(getLayoutInflater());
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", getActivity().MODE_PRIVATE);
        String userId = sharedPreferences.getString("UserID", "");
        user = getUser(userId);
        Intent exit = new Intent(getActivity(), MainActivity.class);
        binding.exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNotification();
                SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isLoggedIn", false);
                editor.apply();
                startActivity(exit);
                getActivity().finish();
            }
        });
        Intent editProf = new Intent(getActivity(), EditActivity.class);
        binding.editProf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(editProf);
                getActivity().finish();
            }
        });
        binding.myName.setText(user.userName);
        if (!user.sell){
            binding.myTitle.setVisibility(View.INVISIBLE);
            binding.newAdd.setVisibility(View.INVISIBLE);
            binding.myAdds.setVisibility(View.INVISIBLE);
            binding.userStatus.setText("Покупатель");
        }
        binding.myName.setText(user.userName);
        if (user.image != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(user.image, 0, user.image.length);
            binding.ava.setImageBitmap(bitmap);
        }
        else{
            binding.ava.setImageResource(R.drawable.user);
        }
        fillData();
        adapter = new MyListAdapter(getActivity(), adds);
        ListView list = binding.myAdds;
        list.setAdapter(adapter);
        Intent newAd = new Intent(getActivity(), SellActivity.class);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                newAd.putExtra("new", "2");
                newAd.putExtra("edit", "1");
                String addId = ((TextView) view.findViewById(R.id.adId)).getText().toString();
                newAd.putExtra("itemId", addId);
                startActivity(newAd);
                getActivity().finish();
            }
        });
        binding.newAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newAd.putExtra("new", "2");
                newAd.putExtra("edit", "0");
                newAd.putExtra("itemId", "");
                startActivity(newAd);
            }
        });

        createNotificationChannel();
        return binding.getRoot();
    }
    private void sendNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity(), CHANNEL_ID)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle("До свидания!")
                .setContentText("Мы будем скучать(")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
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
            NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void fillData() {
        String[] projection = {
                SQLiteTable.Ads.COLUMN_ID,
                SQLiteTable.Ads.COLUMN_NAME,
                SQLiteTable.Ads.COLUMN_PRICE,
                SQLiteTable.Ads.COLUMN_USER,
                SQLiteTable.Ads.COLUMN_IMAGE
        };
        SQLiteDatabase database = new SQLiteHelper(getActivity()).getReadableDatabase();
        Cursor cursor = database.query(
                SQLiteTable.Ads.TABLE_NAME,     // Запрашиваемая таблица
                projection,                               // Возвращаемый столбец
                null,                                // Столбец для условия WHERE
                null,                            // Значение для условия WHERE
                null,                                     // не группировать строки
                null,                                     // не фильтровать по группам строк
                null                                      // не сортировать
        );
        cursor.moveToFirst();
        for (int i=0; i<cursor.getCount();i++){

            int id = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(SQLiteTable.Ads.COLUMN_ID)));
            int price = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(SQLiteTable.Ads.COLUMN_PRICE)));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(SQLiteTable.Ads.COLUMN_NAME));
            String userId = cursor.getString(cursor.getColumnIndexOrThrow(SQLiteTable.Ads.COLUMN_USER));
            byte[] imageInByte = cursor.getBlob(cursor.getColumnIndexOrThrow(SQLiteTable.Ads.COLUMN_IMAGE));
            String[] userProjection = {
                    SQLiteTable.User.COLUMN_NAME
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
            String userName = userCursor.getString(0);
            if (user.id == Integer.parseInt(userId))
                adds.add(new Add(id, price, imageInByte, name, userName));
            userCursor.close();
            cursor.moveToNext();
        }

        cursor.close();
    }
//    private void fillData(){
//        int image = R.drawable.icon;
//        for (int i=0; i < 10; i++){
//            User u = new User("User "+i, true);
//            adds.add(new Add(i, i, image, "Add "+i, u.userName));
//        }
//    }
    private User getUser(String userId) {
        SQLiteDatabase database = new SQLiteHelper(getActivity()).getReadableDatabase();
        String[] userProjection = {
                SQLiteTable.User.COLUMN_NAME,
                SQLiteTable.User.COLUMN_SELLER,
                SQLiteTable.User.COLUMN_ID,
                SQLiteTable.User.COLUMN_IMAGE
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
        int sellerIs = userCursor.getInt(userCursor.getColumnIndexOrThrow(SQLiteTable.User.COLUMN_SELLER));
        int id = userCursor.getInt(userCursor.getColumnIndexOrThrow(SQLiteTable.User.COLUMN_ID));
        byte[] imageInByte = userCursor.getBlob(userCursor.getColumnIndexOrThrow(SQLiteTable.Ads.COLUMN_IMAGE));
        boolean seller = false;
        if (sellerIs == 1){
            seller = true;
        }

        User user = new User(name, seller);
        user.id = id;
        user.setImage(imageInByte);
        return user;
    }
}