package my.edu.utar.individualpractical;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Random;

public class SQLiteAdapter {
    public static final String MYDATABASE_NAME = "ReactGame";
    public static final String MYDATABASE_TABLE = "Winner";
    public static final int MYDATABASE_VERSION = 1;
    public static final String KEY_CONTENT = "Name";
    public static final String KEY_CONTENT2 = "Score";
    final int top25 = 25;

    private static final String SCRIPT_CREATE_DATABASE = "create table "
            + MYDATABASE_TABLE + " (id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_CONTENT + " TEXT NOT NULL, "
            + KEY_CONTENT2 + " TEXT NOT NULL);";

    private static final String SCRIPT_UPDATE_DATABASE = "create table "
            + MYDATABASE_TABLE + " (id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_CONTENT + " TEXT NOT NULL, "
            + KEY_CONTENT2 + " TEXT NOT NULL);";

    private SQLiteHelper sqLiteHelper;
    private SQLiteDatabase sqLiteDatabase;
    private Context context;

    public SQLiteAdapter(Context c) {
        context = c;
    }

    public SQLiteAdapter openToRead() throws android.database.SQLException {
        sqLiteHelper = new SQLiteHelper(context, MYDATABASE_NAME, null, MYDATABASE_VERSION);
        sqLiteDatabase = sqLiteHelper.getReadableDatabase();
        return this;
    }

    public SQLiteAdapter openToWrite() throws android.database.SQLException {
        sqLiteHelper = new SQLiteHelper(context, MYDATABASE_NAME, null, MYDATABASE_VERSION);
        sqLiteDatabase = sqLiteHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        sqLiteHelper.close();
    }

    public long insert(String content, int content2) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_CONTENT, content);
        contentValues.put(KEY_CONTENT2, content2);

        return sqLiteDatabase.insert(MYDATABASE_TABLE, null, contentValues);
    }

    public int deleteAll() {
        return sqLiteDatabase.delete(MYDATABASE_TABLE, null, null);
    }

    public String queueAll() {
        String[] columns = new String[] { "id", KEY_CONTENT, KEY_CONTENT2 };
        String sortOrder = "CAST("+KEY_CONTENT2+" AS INTEGER)" + " DESC";
        Cursor cursor = sqLiteDatabase.query(MYDATABASE_TABLE, columns, null, null, null, null, sortOrder,"25");
        String result = "";
        int index_ID = cursor.getColumnIndex("id");
        int index_CONTENT = cursor.getColumnIndex(KEY_CONTENT);
        int index_CONTENT2 = cursor.getColumnIndex(KEY_CONTENT2);

        for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor.moveToNext()) {
            result = result + cursor.getInt(index_ID) + "\n" + cursor.getString(index_CONTENT) + "\n" + cursor.getString(index_CONTENT2) + "\n";
        }

        return result;
    }

    public int queueByLowest() {
        String[] columns = new String[] { KEY_CONTENT, KEY_CONTENT2 };
        String sortOrder = "CAST("+KEY_CONTENT2+" AS INTEGER)" + " DESC";
        Cursor cursor = sqLiteDatabase.query(MYDATABASE_TABLE, columns, null, null, null,null ,sortOrder,"25");
        int index_CONTENT2 = cursor.getColumnIndex(KEY_CONTENT2);
        int result = 0;

        if (cursor.moveToLast()) {
            result = cursor.getInt(index_CONTENT2);
        }

        return result;
    }

    public int retrieveLastWinnerID() {
        String[] columns = new String[] { KEY_CONTENT, KEY_CONTENT2 };
        Cursor cursor = sqLiteDatabase.query(MYDATABASE_TABLE, columns, null, null, null, null, null);
        int result = 0, i = 0;

        for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor.moveToNext()) {
            i++;
            result = i;
        }

        return result;
    }

    public class SQLiteHelper extends SQLiteOpenHelper {
        public SQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SCRIPT_CREATE_DATABASE);

            //create 25 default winner records
            for(int i=0; i<top25; i++){
                db.execSQL("INSERT INTO " + MYDATABASE_TABLE + " ("+KEY_CONTENT+", "+KEY_CONTENT2+") VALUES ('" + randomNameGenerator() + "', "+ randomScoreGenerator() +");");
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try{
                db.execSQL(SCRIPT_UPDATE_DATABASE);
            }catch(Exception ex){
                Log.e("DB Upgrade:", "onUpgrade: "+ex.getMessage());
            }
        }
    }

    public String randomNameGenerator(){

        Random rnd = new Random();
        String generatedName ="";

        int randomLength = rnd.nextInt(10)+1;

        for(int i=0; i<randomLength; i++){
            generatedName += (char) ('a' + rnd.nextInt(26));
        }

        return generatedName;
    }

    public int randomScoreGenerator(){
        Random generator = new Random();
        return generator.nextInt(90)+1;
    }
}
