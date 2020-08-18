package om.gov.moh.phr.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

import om.gov.moh.phr.apimodels.Notification;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "CaptureNotifications.db";
    private static final int DATABASE_VERSION = 2;
    private static final String TABLE_NAME = "CaptureNotificationsData";
    private static final String KEY_ID = "keyId";
    private static final String KEY_TITLE = "title";
    private static final String KEY_BODY = "body";
    private static final String KEY_PNSTYPE = "pnsType";
    private static final String KEY_LABTYPE = "labType";
    private static final String KEY_DATE = "createdDate";
    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME +
                "("
                + KEY_ID + " integer primary key autoincrement,"
                + KEY_TITLE + " text,"
                + KEY_BODY + " text,"
                + KEY_PNSTYPE + " text,"
                + KEY_LABTYPE + " text,"
                + KEY_DATE + " text );"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME);
        onCreate(db);
    }
    public Boolean insertDate(Notification notification) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues content = new ContentValues();

        content.put(KEY_TITLE, notification.getTitle());
        content.put(KEY_BODY, notification.getBody());
        content.put(KEY_PNSTYPE, notification.getPnsType());
        content.put(KEY_LABTYPE, notification.getLabType());
        content.put(KEY_DATE, notification.getCreatedDate());

        long result = sqLiteDatabase.insert(TABLE_NAME, null, content);
        return result != -1;
    }
    public ArrayList<Notification> retrieveNotificationsRecord() {
        ArrayList<Notification> notificationItemArrayList = new ArrayList<Notification>();
        Cursor cursor = getWritableDatabase().rawQuery("select * from " + TABLE_NAME, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                Notification info = new Notification();
                info.setKeyId(cursor.getString(cursor.getColumnIndex(KEY_ID)));
                info.setTitle(cursor.getString(cursor.getColumnIndex(KEY_TITLE)));
                info.setBody(cursor.getString(cursor.getColumnIndex(KEY_BODY)));
                info.setPnsType(cursor.getString(cursor.getColumnIndex(KEY_PNSTYPE)));
                info.setLabType(cursor.getString(cursor.getColumnIndex(KEY_LABTYPE)));
                info.setCreatedDate(cursor.getString(cursor.getColumnIndex(KEY_DATE)));
                notificationItemArrayList.add(info);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return notificationItemArrayList;
    }
    //---deletes a particular title---
    public boolean deleteTitle(String keyID)
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        return sqLiteDatabase.delete(TABLE_NAME, KEY_ID + "=" + keyID, null) > 0;
    }
}
