package com.example.bufrie;

import android.app.Activity;
import android.content.Context;
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
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.bufrie.databinding.FragmentAddBinding;

import java.sql.ResultSet;
import java.util.ArrayList;

public class AddFragment extends Fragment {

    FragmentAddBinding binding;
    String LOG_TAG="MyLogs";
    String LOG_ERROR = "package:mine";
    MyListAdapter adapter;
    ArrayList<Add> adds = new ArrayList<Add>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddBinding.inflate(getLayoutInflater());
        fillData();
        adapter = new MyListAdapter(getActivity(), adds);
        ListView list = binding.allAdds;
        list.setAdapter(adapter);
        Intent newAd = new Intent(getActivity(), SellActivity.class);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                newAd.putExtra("new", "0");
                String addId = ((TextView) view.findViewById(R.id.adId)).getText().toString();
                newAd.putExtra("itemId", addId);
                newAd.putExtra("edit", "1");
                startActivity(newAd);
                getActivity().finish();
            }
        });
        return binding.getRoot();
    }
    private void fillData() {
        String[] projection = {
                SQLiteTable.Ads.COLUMN_ID,
                SQLiteTable.Ads.COLUMN_NAME,
                SQLiteTable.Ads.COLUMN_PRICE,
                SQLiteTable.Ads.COLUMN_USER,
                SQLiteTable.Ads.COLUMN_RELEVANT,
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
            int relevant = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(SQLiteTable.Ads.COLUMN_RELEVANT)));
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
            String user = userCursor.getString(0);
            if (relevant == 1) adds.add(new Add(id, price, imageInByte, name, user));
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
}