package com.example.bufrie;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
public class SQLiteHelper extends android.database.sqlite.SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "sample_database";
    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQLiteTable.User.CREATE_TABLE);
        for (int i=0; i< SQLiteTable.User.FILL_DATA.length;i++){
            db.execSQL(SQLiteTable.User.FILL_DATA[i]);}
        db.execSQL(SQLiteTable.Ads.CREATE_TABLE);
        for (int i=0; i< SQLiteTable.Ads.FILL_DATA.length;i++){
            db.execSQL(SQLiteTable.Ads.FILL_DATA[i]);}
        db.execSQL(SQLiteTable.Favourites.CREATE_TABLE);
        for (int i=0; i< SQLiteTable.Favourites.FILL_DATA.length;i++){
            db.execSQL(SQLiteTable.Favourites.FILL_DATA[i]);}
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + SQLiteTable.User.TABLE_NAME);
        onCreate(db);
    }
}
