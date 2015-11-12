package com.makewithus.tattooonsenmap;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by KEN on 10/8/15.
 */
public class TattooSQLiteHelper extends SQLiteOpenHelper{

    private static TattooSQLiteHelper mInstance;
    private static Context mCtx;

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "TattooOnsenMap.db";

     static final String TEXT_TYPE = " TEXT";
    static final String REAL_TYPE = " REAL";
    static final String INTEGER_TYPE = " INTEGER";
     static final String COMMA_SEP = ",";
     static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + VenueMaster.TABLE_NAME + " (" +
                    //VenueMaster._ID + " INTEGER PRIMARY KEY," +
                    VenueMaster.COLUMN_VENUE_ID + TEXT_TYPE + " PRIMARY KEY" + COMMA_SEP +
                    VenueMaster.COLUMN_VENUE_NAME + TEXT_TYPE + COMMA_SEP +
                    VenueMaster.COLUMN_VENUE_LAT + REAL_TYPE + COMMA_SEP +
                    VenueMaster.COLUMN_VENUE_LON + REAL_TYPE + COMMA_SEP +
                    VenueMaster.COLUMN_VENUE_PHONE + TEXT_TYPE + COMMA_SEP +
                    VenueMaster.COLUMN_VENUE_PHONE_FROMATED + TEXT_TYPE + COMMA_SEP +
                    VenueMaster.COLUMN_VENUE_ADDRESS + TEXT_TYPE + COMMA_SEP +
                    VenueMaster.COLUMN_VENUE_URL + TEXT_TYPE + COMMA_SEP +
                    VenueMaster.COLUMN_VENUE_FACEBOOKID + TEXT_TYPE + COMMA_SEP +
                    VenueMaster.COLUMN_VENUE_TWITTER + TEXT_TYPE + COMMA_SEP +
                    VenueMaster.COLUMN_VENUE_CHECKINS + TEXT_TYPE + COMMA_SEP +
                    VenueMaster.COLUMN_VENUE_RATING + INTEGER_TYPE + COMMA_SEP +
                    VenueMaster.COLUMN_VENUE_STATUS + INTEGER_TYPE + COMMA_SEP +
                    VenueMaster.COLUMN_VENUE_RATING_CNT + INTEGER_TYPE + COMMA_SEP +
                    VenueMaster.COLUMN_VENUE_STATUS_CNT + INTEGER_TYPE + COMMA_SEP +
                    VenueMaster.COLUMN_VENUE_COMMENT_CNT + INTEGER_TYPE  + COMMA_SEP +
                    VenueMaster.COLUMN_CREATED_TIME + INTEGER_TYPE  + COMMA_SEP +
                    VenueMaster.COLUMN_USER_FAVORITE + INTEGER_TYPE  + COMMA_SEP +
                    VenueMaster.COLUMN_PRONOUNCE + TEXT_TYPE  + COMMA_SEP +
                    VenueMaster.COLUMN_PHOTO_URL + TEXT_TYPE  +
                    " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + VenueMaster.TABLE_NAME;


    public static synchronized TattooSQLiteHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new TattooSQLiteHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        return mInstance;
    }

    private TattooSQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    private TattooSQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }


    /* Inner class that defines the table contents */
    public static abstract class VenueMaster {
        static final String TABLE_NAME = "venue_master_table";
        static final String COLUMN_VENUE_ID = "vid";
        static final String COLUMN_VENUE_NAME = "vname";
        static final String COLUMN_VENUE_LAT = "lat";
        static final String COLUMN_VENUE_LON = "lon";
        static final String COLUMN_VENUE_URL = "url";
        static final String COLUMN_VENUE_PHONE = "phone";
        static final String COLUMN_VENUE_PHONE_FROMATED = "phone_formated";
        static final String COLUMN_VENUE_ADDRESS = "address";
        static final String COLUMN_VENUE_FACEBOOKID = "facebookid";
        static final String COLUMN_VENUE_TWITTER = "twitterid";
        static final String COLUMN_VENUE_RATING = "rating";
        static final String COLUMN_VENUE_STATUS = "status";
        static final String COLUMN_VENUE_RATING_CNT = "rating_cnt";
        static final String COLUMN_VENUE_STATUS_CNT = "status_cnt";
        static final String COLUMN_VENUE_COMMENT_CNT = "comment_cnt";
        static final String COLUMN_VENUE_CHECKINS = "checkins";
        static final String COLUMN_CREATED_TIME = "created";
        static final String COLUMN_USER_FAVORITE = "user_favorite";
        static final String COLUMN_PRONOUNCE = "pronounce";
        static final String COLUMN_PHOTO_URL = "photo_url";
    }
}
