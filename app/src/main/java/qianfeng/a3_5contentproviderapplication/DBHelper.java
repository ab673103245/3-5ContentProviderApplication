package qianfeng.a3_5contentproviderapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2016/9/9 0009.
 */
public class DBHelper extends SQLiteOpenHelper {

    private final static String DBNAME = "qf.db";
    public final static String USERTABLE = "usertable";
    public final static String FOODTABLE = "foodtable";
    private final static int DBVERSION = 1;

    public DBHelper(Context context) {
        super(context, DBNAME, null, DBVERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS "+USERTABLE+" (_id INTEGER PRIMARY KEY,USERNAME,PASSWORD,NICKNAME);");
        db.execSQL("CREATE TABLE IF NOT EXISTS "+FOODTABLE+" (_id INTEGER PRIMARY KEY,FOODNAME,FOODCOLOR,FOODPRICE);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
