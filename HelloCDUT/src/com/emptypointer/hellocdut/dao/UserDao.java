package com.emptypointer.hellocdut.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.easemob.util.HanziToPinyin;
import com.emptypointer.hellocdut.domain.User;
import com.emptypointer.hellocdut.utils.GlobalVariables;

public class UserDao {
    public static final String TABLE_NAME = "uers";
    public static final String COLUMN_NAME_ID = "username";
    public static final String COLUMN_NAME_NICK = "nick";
    public static final String COLUMN_NAME_IS_STRANGER = "is_stranger";
    public static final String COLUMN_NAME_MOTTO = "motto";
    public static final String COLUMN_NAME_AVATAR_URL = "avtar_url";
    public static final String COLUMN_NAME_NOTE = "note";

    private EPDataBaseHelper dbHelper;

    public UserDao(Context context) {
        dbHelper = EPDataBaseHelper.getInstance(context);
    }

    /**
     * 保存好友list
     *
     * @param contactList
     */
    public void saveContactList(List<User> contactList) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.delete(TABLE_NAME, null, null);
            for (User user : contactList) {
                ContentValues values = new ContentValues();
                values.put(COLUMN_NAME_ID, user.getUsername());
                if (user.getNick() != null)
                    values.put(COLUMN_NAME_NICK, user.getNicKName());
                if (user.getMotto() != null)
                    values.put(COLUMN_NAME_MOTTO, user.getMotto());
                if (user.getImageURL() != null)
                    values.put(COLUMN_NAME_AVATAR_URL, user.getImageURL());
                if (user.getNote() != null)
                    values.put(COLUMN_NAME_NOTE, user.getNote());
                db.replace(TABLE_NAME, null, values);
            }
        }
    }

    /**
     * 获取好友list
     *
     * @return
     */
    public Map<String, User> getContactList() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Map<String, User> users = new HashMap<String, User>();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery(
                    "select * from " + TABLE_NAME /* + " desc" */, null);
            while (cursor.moveToNext()) {
                String username = cursor.getString(cursor
                        .getColumnIndex(COLUMN_NAME_ID));
                String nick = cursor.getString(cursor
                        .getColumnIndex(COLUMN_NAME_NICK));
                String url = cursor.getString(cursor
                        .getColumnIndex(COLUMN_NAME_AVATAR_URL));
                String metto = cursor.getString(cursor
                        .getColumnIndex(COLUMN_NAME_MOTTO));
                String note = cursor.getString(cursor
                        .getColumnIndex(COLUMN_NAME_NOTE));
                User user = new User();
                user.setImageURL(url);
                user.setMotto(metto);
                user.setNote(note);
                user.setUsername(username);
                user.setNicKName(nick);
                String headerName = null;
                if (!TextUtils.isEmpty(user.getNick())) {
                    headerName = user.getNick();
                } else {
                    headerName = user.getUsername();
                }

                if (username.equals(GlobalVariables.NEW_FRIENDS_USERNAME)
                        || username.equals(GlobalVariables.GROUP_USERNAME)) {
                    user.setHeader("");
                } else if (Character.isDigit(headerName.charAt(0))) {
                    user.setHeader("#");
                } else {
                    user.setHeader(HanziToPinyin.getInstance()
                            .get(headerName.substring(0, 1)).get(0).target
                            .substring(0, 1).toUpperCase());
                    char header = user.getHeader().toLowerCase().charAt(0);
                    if (header < 'a' || header > 'z') {
                        user.setHeader("#");
                    }
                }
                users.put(username, user);
            }
            cursor.close();
        }
        return users;
    }

    /**
     * 删除一个联系人
     *
     * @param username
     */
    public void deleteContact(String username) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.delete(TABLE_NAME, COLUMN_NAME_ID + " = ?",
                    new String[]{username});
        }
    }

    /**
     * 保存一个联系人
     *
     * @param user
     */
    public void saveContact(User user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME_ID, user.getUsername());
        if (user.getNicKName() != null)
            values.put(COLUMN_NAME_NICK, user.getNicKName());
        if (user.getMotto() != null)
            values.put(COLUMN_NAME_MOTTO, user.getMotto());
        if (user.getImageURL() != null)
            values.put(COLUMN_NAME_AVATAR_URL, user.getImageURL());
        if (user.getNote() != null)
            values.put(COLUMN_NAME_NOTE, user.getNote());
        if (db.isOpen()) {
            db.replace(TABLE_NAME, null, values);
        }
    }

}
