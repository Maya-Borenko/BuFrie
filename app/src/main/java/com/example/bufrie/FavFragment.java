package com.example.bufrie;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.bufrie.databinding.FragmentFavBinding;

import java.util.ArrayList;

public class FavFragment extends Fragment {
    FragmentFavBinding binding;
    User user;
    MyListAdapter adapter;
    ArrayList<Add> adds = new ArrayList<Add>();
    private static final int YOUR_REQUEST_CODE = 1;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == YOUR_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .detach(this)
                    .attach(this)
                    .commit();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFavBinding.inflate(getLayoutInflater());
        adapter = new MyListAdapter(getActivity(), adds);
        ListView list = binding.myFavs;
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", getActivity().MODE_PRIVATE);
        String userId = sharedPreferences.getString("UserID", "");
        user = getUser(userId);
        if (fillData()) {
            list.setAdapter(adapter);
            list.setVisibility(View.VISIBLE);
            binding.sadSmile.setVisibility(View.INVISIBLE);
            binding.sadText.setVisibility(View.INVISIBLE);
            Intent newAd = new Intent(getActivity(), SellActivity.class);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    newAd.putExtra("new", "1");
                    newAd.putExtra("edit", "1");
                    String addId = ((TextView) view.findViewById(R.id.adId)).getText().toString();
                    newAd.putExtra("itemId", addId);
                    startActivity(newAd);
                    getActivity().finish();
                }
            });
        }
        else{
            list.setVisibility(View.INVISIBLE);
            binding.sadSmile.setVisibility(View.VISIBLE);
            binding.sadText.setVisibility(View.VISIBLE);
            ImageView imageView = binding.sadSmile;
            Animation shakeAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_shake);
            imageView.startAnimation(shakeAnimation);

        }
        return binding.getRoot();
    }
    private boolean fillData() {
        SQLiteDatabase database = new SQLiteHelper(getActivity()).getReadableDatabase();
        String[] adProjection = {
                SQLiteTable.Favourites.COLUMN_AD,
                SQLiteTable.Favourites.COLUMN_USER
        };
        String adSelection =
                SQLiteTable.Favourites.COLUMN_USER + " = ?";
        String[] selectionArgs = {String.valueOf(user.id)};

        Cursor adCursor = database.query(
                SQLiteTable.Favourites.TABLE_NAME,     // Запрашиваемая таблица
                adProjection,                               // Возвращаемый столбец
                adSelection,                                // Столбец для условия WHERE
                selectionArgs,                            // Значение для условия WHERE
                null,                                     // не группировать строки
                null,                                     // не фильтровать по группам строк
                null                                      // не сортировать
        );
        adCursor.moveToFirst();
        String adId, userId;
        for (int i=0; i<adCursor.getCount();i++){
            adId = adCursor.getString(adCursor.getColumnIndexOrThrow(SQLiteTable.Favourites.COLUMN_AD));
            userId = adCursor.getString(adCursor.getColumnIndexOrThrow(SQLiteTable.Favourites.COLUMN_USER));
            adds.add(getAd(adId));
            adCursor.moveToNext();
        }
        if (adds.size()==0){return false;}
        return true;
    }

    private Add getAd(String adId) {
        SQLiteDatabase database = new SQLiteHelper(getActivity()).getReadableDatabase();
        String[] adsProjection = {
                SQLiteTable.Ads.COLUMN_NAME,
                SQLiteTable.Ads.COLUMN_PRICE,
                SQLiteTable.Ads.COLUMN_USER,
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
        String userId = adCursor.getString(adCursor.getColumnIndexOrThrow(SQLiteTable.Ads.COLUMN_USER));
        byte[] imageInByte = adCursor.getBlob(adCursor.getColumnIndexOrThrow(SQLiteTable.Ads.COLUMN_IMAGE));
        User u = getUser(userId);
        return new Add(Integer.parseInt(adId), price, imageInByte, name, u.userName);
    }
    private User getUser(String userId) {
        SQLiteDatabase database = new SQLiteHelper(getActivity()).getReadableDatabase();
        String[] userProjection = {
                SQLiteTable.User.COLUMN_NAME,
                SQLiteTable.User.COLUMN_SELLER,
                SQLiteTable.User.COLUMN_ID
        };
        Log.d("MyLogs", "userId = " + userId);
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
        boolean seller = false;
        if (sellerIs == 1){
            seller = true;
        }

        User user = new User(name, seller);
        user.id = id;
        return user;
    }
//    private boolean fillData(){
//        int image = R.drawable.icon;
//        for (int i=0; i < 10; i++){
//            User u = new User("User "+i, true);
//            adds.add(new Add(i, i, image, "Add "+i, u.userName));
//        }
//        return false;
//    }
}