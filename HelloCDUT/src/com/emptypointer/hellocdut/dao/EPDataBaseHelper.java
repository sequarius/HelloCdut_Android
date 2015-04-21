package com.emptypointer.hellocdut.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.emptypointer.hellocdut.customer.EPApplication;

/**
 * 统一的数据库表单创建类
 *
 * @author Sequarius
 */
public class EPDataBaseHelper extends SQLiteOpenHelper {

    private static final String DBNAME = "hellocdut.db";
    private static final int VERSION = 4;
    private static EPDataBaseHelper instance;

    private static final String USERNAME_TABLE_CREATE = "CREATE TABLE "
            + UserDao.TABLE_NAME + " (" + UserDao.COLUMN_NAME_NICK + " TEXT, "
            + UserDao.COLUMN_NAME_MOTTO + " TEXT, "
            + UserDao.COLUMN_NAME_AVATAR_URL + " TEXT, "
            + UserDao.COLUMN_NAME_NOTE + " TEXT, "
            + UserDao.COLUMN_NAME_ID + " TEXT PRIMARY KEY);";

    private static final String INIVTE_MESSAGE_TABLE_CREATE = "CREATE TABLE "
            + InviteMessgeDao.TABLE_NAME + " ("
            + InviteMessgeDao.COLUMN_NAME_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + InviteMessgeDao.COLUMN_NAME_FROM + " TEXT, "
            + InviteMessgeDao.COLUMN_NAME_GROUP_ID + " TEXT, "
            + InviteMessgeDao.COLUMN_NAME_GROUP_Name + " TEXT, "
            + InviteMessgeDao.COLUMN_NAME_REASON + " TEXT, "
            + InviteMessgeDao.COLUMN_NAME_STATUS + " INTEGER, "
            + InviteMessgeDao.COLUMN_NAME_ISINVITEFROMME + " INTEGER, "
            + InviteMessgeDao.COLUMN_NAME_TIME + " TEXT); ";
    // 教務新聞表
    private static final String AAO_NEWS_TABLE_CREATE = "CREATE TABLE \"aao_news\" ("
            + "[title] VARCHAR(50), "
            + "[url] VARCHAR(50), "
            + "[post_time] VARCHAR(15), "
            + "[get_time]VARCHAR(30),"
            + "[read_status] INT);";

    // 網絡隊列Table
    private static final String NEW_QUEUE_TABLE_CREATE = "CREATE TABLE [asyn_task] ("
            + "[task_id] INTEGER  NOT NULL PRIMARY KEY autoincrement,"
            + " [host] VARCHAR(50) NOT NULL, "
            + " [params] VARCHAR(50), "
            + "[inserrt_time] BIGINT NOT NULL, "
            + " [status] INT NOT NULL DEFAULT 1)";
    private static final String SCHEDULE_TABLE_CREATE = "CREATE TABLE [schedule] ("
            + "[id] INTEGER  NOT NULL PRIMARY KEY autoincrement,"
            + "[week_num] VARCHAR(50),"
            + "[what_day] INTEGER,"
            + "[begin] INTEGER,"
            + "[section] INTEGER,"
            + "[full_name] VARCHAR(50),"
            + "[category] VARCHAR(10),"
            + "[is_OverLap_Class] INTEGER,"
            + "[credits] CHAR(4),"
            + "[teacher] VARCHAR(10),"
            + "[period] CHAR(3),"
            + "[room] VARCHAR(10)," + "[note] VARCHAR(50));";
    // json緩存
    private static final String CACHE_DATA = "CREATE TABLE [data_cache] ("
            + "[key] VARCHAR(40) NOT NULL," + "[data] TEXT,"
            + "[update_time] BIGINT);";

    // ////
    private static String getUserDatabaseName() {
        return EPApplication.getInstance().getUserName() + ".db";
    }

    // ///////
    public static EPDataBaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new EPDataBaseHelper(context);
        }
        return instance;
    }

    private EPDataBaseHelper(Context context) {
        super(context, getUserDatabaseName(), null, VERSION);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(AAO_NEWS_TABLE_CREATE);
        db.execSQL(USERNAME_TABLE_CREATE);
        db.execSQL(INIVTE_MESSAGE_TABLE_CREATE);

        db.execSQL(NEW_QUEUE_TABLE_CREATE);
        db.execSQL(SCHEDULE_TABLE_CREATE);
        db.execSQL(CACHE_DATA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        if (oldVersion == 3) {
            db.execSQL("DROP TABLE " + UserDao.TABLE_NAME + ";");
            db.execSQL(USERNAME_TABLE_CREATE);
        }

    }

    public void closeDB() {
        if (instance != null) {
            try {
                SQLiteDatabase db = instance.getWritableDatabase();
                db.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            instance = null;
        }
    }

}
