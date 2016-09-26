package qianfeng.a3_5contentproviderapplication;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Administrator on 2016/9/9 0009.
 */
public class MyContentProvider extends ContentProvider {

    private SQLiteDatabase db;

    // AUTHORITIES：是向外暴露数据的 域名
    private static final String AUTHORITIES = "qianfeng.a3_5contentproviderapplication.mycontentprovider";
    // 需要一个Uri匹配器对象，方便我调用
    private static final UriMatcher uri_Matcher = new UriMatcher(UriMatcher.NO_MATCH); // 如果匹配不成功，那就返回这个常量---NO_MATCH

    static {
        // 添加匹配规则
        // 相当于 qianfeng.a3_5contentproviderapplication.mycontentprovider/user
        uri_Matcher.addURI(AUTHORITIES,"user",1);// 如果匹配成功，就返回1，否则返回NO_MATCH
        // 相当于 qianfeng.a3_5contentproviderapplication.mycontentprovider/food
        uri_Matcher.addURI(AUTHORITIES,"food",2);// 如果匹配成功，就返回2，否则返回NO_MATCH
        // 相当于 qianfeng.a3_5contentproviderapplication.mycontentprovider/user/任意数字
        uri_Matcher.addURI(AUTHORITIES,"user/#",3);// 如果匹配成功，就返回2，否则返回NO_MATCH
        // 相当于 qianfeng.a3_5contentproviderapplication.mycontentprovider/food/任意字符
        uri_Matcher.addURI(AUTHORITIES,"food/*",4);// 如果匹配成功，就返回2，否则返回NO_MATCH
    }

    @Override
    public boolean onCreate() {
        db = new DBHelper(getContext()).getReadableDatabase(); // db表示创建一个数据库，里面有两张表，一张是user，一张是food
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        int code = uri_Matcher.match(uri);
        Cursor cursor = null;
        switch (code)
        {
            case 1:  // 把user表中的数据全部查询
                cursor = db.query(DBHelper.USERTABLE, null, null, null, null, null, null);// 全部查询
                break;

            case 3: // 按id查询user表中的数据
                cursor = db.query(DBHelper.USERTABLE, projection, selection, selectionArgs, null, null, sortOrder, null);
                break;
        }


        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) { // 利用匹配规则，找到数据库中的表
        int code = uri_Matcher.match(uri); // 调用匹配器对象里面的匹配规则，返回匹配结果码，根据这个结果码，执行相应的那个表的操作
        switch (code)
        {
            case 1:
                // 如果这个匹配
                db.insert(DBHelper.USERTABLE,null,values); // 这里不用传全称，是因为db已经锁定了一个数据库，就在这个数据库里面操作相应的表
                break;

            case 2:
                db.insert(DBHelper.FOODTABLE,null,values); // 向DBHepler中的FOOTABLE中插入values的值
                break;
        }

        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {  // 根据uri来解析出想要删除的数据，根据uri解析要删除的数据。

        // 无论是删除还是插入，第一步，先获取匹配码
        int code = uri_Matcher.match(uri);
        int deleteCount = 0;

        switch (code) // 只要给删除数据这个方法，传入uri，就可以删除数据了，而不用其他参数了
        {
            case 1:
                // 删除user表中所有的数据
                deleteCount = db.delete(DBHelper.USERTABLE, null, null);
                break;
            case 2:
                // 删除food表中所有的数据
                deleteCount = db.delete(DBHelper.FOODTABLE, null, null);

                break;
            case 3:
                // 根据id删除user表中指定的数据, 根据传过来的uri解析到要删除的条目的id
                String s = uri.getPathSegments().get(1);
                deleteCount = db.delete(DBHelper.USERTABLE,"_id=?",new String[]{s});

                String lastPathSegment = uri.getLastPathSegment();
                Log.d("google-my:", "delete: lastPathSegment--->" + lastPathSegment);
                break;

            case 4:
                // 根据关键字keywords删除user表中，username中包含指定字的条目
                String keywords = uri.getPathSegments().get(1);
                deleteCount = db.delete(DBHelper.USERTABLE,"username like ?",new String[]{"%" + keywords + "%"});
                break;
        }

        return deleteCount;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        int code = uri_Matcher.match(uri);
        int update = 0;
        switch (code)
        {
            case 3:
                String s = uri.getPathSegments().get(1);
                 update = db.update(DBHelper.USERTABLE, values, "id=?", new String[]{s});
                break;

            case 1:
                // 按关键字改
                String s1 = uri.getPathSegments().get(1);
                 update = db.update(DBHelper.USERTABLE, values, selection,selectionArgs);
                break;
        }

        return update;
    }
}
