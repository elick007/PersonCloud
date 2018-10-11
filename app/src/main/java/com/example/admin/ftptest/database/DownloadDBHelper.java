package com.example.admin.ftptest.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.admin.ftptest.utils.MyLogger;

import org.apache.commons.net.ftp.FTPFile;

public class DownloadDBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "DOWNLOADDB.db";
    private static final String TABLE_NAME = "DOWNLOAD_TABLE";
    private static final String _ID = "id";
    private static final String COLUMN_NAME_FILE_NAME = "name";
    private static final String COLUMN_NAME_TOTAL_SIZE = "totalSize";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    _ID + " INTEGER PRIMARY KEY," +
                    COLUMN_NAME_FILE_NAME + "TEXT," +
                    COLUMN_NAME_TOTAL_SIZE + "INTEGER)";

    public DownloadDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public boolean insertDownloadEntity(FTPFile downloadEntity) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME_FILE_NAME, downloadEntity.getName());
        contentValues.put(COLUMN_NAME_TOTAL_SIZE, downloadEntity.getSize());
        boolean result = db.insert(TABLE_NAME, null, contentValues) != -1;
        if (result) {
            MyLogger.d("insert download entity success");
        } else {
            MyLogger.d("insert download entity failed");
        }
        return result;
    }

    public boolean deleteDownloadEntity(String fileName) {
        SQLiteDatabase db = getWritableDatabase();
        String whereClause = COLUMN_NAME_FILE_NAME + "=?";
        String[] whereArgs = {fileName};
        boolean result=db.delete(TABLE_NAME,whereClause,whereArgs)!=0;
        if (result){
            MyLogger.d("delete download entity success");
        }else {
            MyLogger.d("delete download entity failed");
        }
        return result;
    }
//    public long queryCurrentDownloadSize(String fileName){
//        SQLiteDatabase db=getWritableDatabase();
//        String[] columns = {COLUMN_NAME_FILE_NAME};
//        Cursor cursor=db.query(TABLE_NAME,null,COLUMN_NAME_FILE_NAME+"=?",new String[]{fileName},null,null,null);
//        if (cursor.getCount()==1){
//            long currentSize=cursor.getLong(cursor.getColumnIndex(COLUMN_NAME_CURRENT_SIZE));
//            MyLogger.d("query download entity is not only");
//            return currentSize;
//        }else {
//            MyLogger.d("query download entity is not only");
//            return 0;
//        }
//    }

public String[] queryDownloadFTPFile(){
        SQLiteDatabase db=getWritableDatabase();
        Cursor cursor=db.query(TABLE_NAME,null,null,null,null,null,null);
        String[] fileCount=new String[cursor.getCount()];
        for (int i=0;i<cursor.getCount();i++) {
            fileCount[i]=cursor.getString(cursor.getColumnIndex(COLUMN_NAME_FILE_NAME));
    }
    return fileCount;
}
    public static class DownloadEntity {
        private String name;
        private long currentSize;
        private long totalSize;

        public DownloadEntity(String name, long currentSize, long totalSize) {
            this.name = name;
            this.currentSize = currentSize;
            this.totalSize = totalSize;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setCurrentSize(long currentSize) {
            this.currentSize = currentSize;
        }

        public void setTotalSize(long totalSize) {
            this.totalSize = totalSize;
        }

        public String getName() {
            return name;
        }

        public long getCurrentSize() {
            return currentSize;
        }

        public long getTotalSize() {
            return totalSize;
        }
    }
}
