package com.example.bufrie;
import android.provider.BaseColumns;
public final class SQLiteTable {
    private SQLiteTable(){}
    public static class User implements BaseColumns{
        public static final String TABLE_NAME = "User";
        public static final String COLUMN_ID = "ID";
        public static final String COLUMN_NAME = "Name";
        public static final String COLUMN_SELLER = "Seller";
        public static final String COLUMN_PHONE = "Phone";
        public static final String COLUMN_PASSWORD = "Password";
        public static final String COLUMN_IMAGE = "Image";
        public static final String CREATE_TABLE ="CREATE TABLE IF NOT EXISTS \""+TABLE_NAME+"\" (" +
                "\""+COLUMN_ID+"\" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +COLUMN_NAME+" TEXT" +
                ", "+COLUMN_IMAGE+" BLOB, "+COLUMN_SELLER+" INTEGER DEFAULT (0) NOT NULL, "+COLUMN_PHONE+
                " DEFAULT (89999999999) NOT NULL, "+COLUMN_PASSWORD+" TEXT DEFAULT (123) NOT NULL);";
        public static final String[] FILL_DATA = {"INSERT INTO User VALUES(1,'Максим',NULL,0,'89999999999','123');\n",
                "INSERT INTO User VALUES(2,'Анна',NULL,1,'89999999991','123');\n",
                "INSERT INTO User VALUES(3,'Иван',NULL,1,'89999999992','123');\n",
                "INSERT INTO User VALUES(4,'Валентина',NULL,1,'89999999993','123');\n",
                "INSERT INTO User VALUES(5,'Павел',NULL,1,'89999999994','123');"};    }
    public static class Ads implements BaseColumns{
        public static final String TABLE_NAME = "Ads";
        public static final String COLUMN_ID = "ID";
        public static final String COLUMN_NAME = "Name";
        public static final String COLUMN_USER = "User_ID";
        public static final String COLUMN_DESCRIPTIONS = "Descriptions";
        public static final String COLUMN_PRICE = "Price";
        public static final String COLUMN_IMAGE = "Image";
        public static final String COLUMN_RELEVANT = "Relevant";
        public static final String CREATE_TABLE ="CREATE TABLE IF NOT EXISTS \""+TABLE_NAME+"\" (\n" +
                "\""+COLUMN_ID+"\" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                "\t\""+COLUMN_USER+"\" INTEGER, "+COLUMN_RELEVANT+" INTEGER DEFAULT (1) NOT NULL, "+COLUMN_NAME+
                " TEXT, "+COLUMN_PRICE+" INTEGER, "+COLUMN_IMAGE+" BLOB, "+COLUMN_DESCRIPTIONS+" TEXT,\n" +
                "\tFOREIGN KEY (\""+COLUMN_USER+"\") REFERENCES \"User\"("+COLUMN_ID+") ON DELETE CASCADE\n" +
                ");";
        public static final String[] FILL_DATA ={"INSERT INTO Ads VALUES(1,2,1,'Буська',3000,NULL,'В добрые руки');\n",
                "INSERT INTO Ads VALUES(2,2,1,'Помет 04.02',6500,NULL,'3 девочки, 1 мальчик');\n",
                "INSERT INTO Ads VALUES(3,3,1,'Кошечка',10000,NULL,'3 месяца малышке');\n",
                "INSERT INTO Ads VALUES(4,4,1,'Котята 05.03',4000,NULL,'4 девчонки');\n",
                "INSERT INTO Ads VALUES(5,5,1,'Шпиц',3000,NULL,'Последний мальчишка остался');\n",
                "INSERT INTO Ads VALUES(6,5,1,'3 щенка',30000,NULL,'Цена за одного');\n",
                "INSERT INTO Ads VALUES(7,2,1,'Помет 02.03',45000,NULL,'Корги');\n",
                "INSERT INTO Ads VALUES(8,2,1,'Крыса дамбо',1000,NULL,'Окрас белый');"}; }
    public static class Favourites implements BaseColumns{
        public static final String TABLE_NAME = "Favourites";
        public static final String COLUMN_AD = "Ad_ID";
        public static final String COLUMN_USER = "User_ID";
        public static final String CREATE_TABLE ="CREATE TABLE IF NOT EXISTS \""+TABLE_NAME+"\" (\n" +
                "  \""+COLUMN_USER+"\" INTEGER,\n" +
                "  \""+COLUMN_AD+"\" INTEGER,\n" +
                "   FOREIGN KEY (\""+COLUMN_USER+"\") REFERENCES \"User\"(id) ON DELETE CASCADE,\n" +
                "   FOREIGN KEY (\""+COLUMN_AD+"\") REFERENCES \"Ads\"(id) ON DELETE CASCADE\n" +
                ");";
        public static final String[] FILL_DATA ={"INSERT INTO Favourites VALUES(2,3);\n",
                "INSERT INTO Favourites VALUES(2,4);\n",
                "INSERT INTO Favourites VALUES(2,5);"};}}
