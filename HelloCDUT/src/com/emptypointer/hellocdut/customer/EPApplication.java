package com.emptypointer.hellocdut.customer;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.view.ViewConfiguration;

import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import com.emptypointer.hellocdut.activity.CoreService;
import com.emptypointer.hellocdut.dao.DataCacheDao;
import com.emptypointer.hellocdut.dao.EPDataBaseHelper;
import com.emptypointer.hellocdut.dao.UserDao;
import com.emptypointer.hellocdut.domain.User;
import com.emptypointer.hellocdut.receiver.VoiceCallReceiver;
import com.emptypointer.hellocdut.service.EPHttpQueueService;
import com.emptypointer.hellocdut.service.EPScheduleService;
import com.emptypointer.hellocdut.service.EPSecretService;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class EPApplication extends Application {
    public static Context applicationContext;

    private int mUserStatus = Integer.MIN_VALUE;

    private int mLibBindStatus = Integer.MIN_VALUE;

    private int mCampusBindStatus = Integer.MIN_VALUE;
    // 未登录
    public static final int USER_STATUS_UNLOGIN = -1;
    // 普通用户
    public static final int USER_STATUS_NORMAL = 0;
    // 认证用户
    public static final int USER_STATUS_CERTIFICATE = 1;
    // 认证用户 /当前绑定密码错误
    public static final int USER_STATUS_CERTIFICATE_WITHOUT_PASSWORD = 2;

    private static EPApplication instance;

    private static final String PREF_USERNAME = "user_name";

    private static final String PREF_PASSWORD = "password";

    private static final String PREF_NICKNAME = "nick_name";

    private static final String PREF_TOKEN = "token";

    private static final String PREF_CHAT_TOKEN = "chat_token";

    private static final String PREF_USER_STATUS = "user_status";
    private static final String PREF_LIBRARY_STATUS = "user_lib_status";
    private static final String PREF_CAMPUS_STATUS = "user_campus_status";

    private static final String TAG = "EPAppliaction";

    private String mUserName = null;

    private String mPassWord = null;

    private String mChatToken;

    // 用户Token
    private String mToken = null;

    private List<Activity> mActivityStack;

    public void pushActivity(Activity activity) {
        if (mActivityStack == null) {
            mActivityStack = new ArrayList<Activity>();
        }
        mActivityStack.add(activity);
    }

    public void removeActivity(Activity activity) {
        if (mActivityStack.contains(activity) && mActivityStack != null) {
            mActivityStack.remove(activity);
        }
    }

    public Activity getBottomActivity() {
        return mActivityStack.get(0);
    }

    public void clearAcitivy() {
        if (mActivityStack != null) {
            for (Activity activity : mActivityStack) {
                if (activity != null) {
                    activity.finish();
                }
            }
        }
    }

    // 用户昵称
    private String mNickName = null;

    private Map<String, User> mContactList;

    public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you
        // may tune some of them,
        // or you can create default configuration by
        // ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(
                context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs();
        // config.writeDebugLogs(); // Remove for release app

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();

        //让部分有菜单键的手机可以显示overflowmenu
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class
                    .getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        applicationContext = this;
        instance = this;

        initImageLoader(this);

        EMChat.getInstance().init(applicationContext);

        // EMChat.getInstance().setDebugMode(true);
        // debugmode设为true后，就能看到sdk打印的log了
        // 获取到EMChatOptions对象
        EMChatOptions options = EMChatManager.getInstance().getChatOptions();
        // 默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(false);
        SharedPreferences preferences = getSharedPreferences("setting",
                MODE_PRIVATE);

        // 设置收到消息是否有新消息通知，默认为true
        options.setNotificationEnable(preferences.getBoolean(
                "message_notification", true));
        // 设置收到消息是否有声音提示，默认为true
        options.setNoticeBySound(preferences.getBoolean("message_sound", true));
        // 设置收到消息是否震动 默认为true
        options.setNoticedByVibrate(preferences.getBoolean("message_brate",
                false));
        // 设置语音消息播放是否设置为扬声器播放 默认为true
        options.setUseSpeaker(preferences.getBoolean("void_message_by_speaker",
                true));
        options.setUseRoster(true);
        IntentFilter callFilter = new IntentFilter(EMChatManager.getInstance()
                .getIncomingVoiceCallBroadcastAction());
        registerReceiver(new VoiceCallReceiver(), callFilter);
        Intent intent = new Intent(this, CoreService.class);
        if (mUserName != null) {
            startService(intent);
            // bindService(intent, connection, BIND_AUTO_CREATE);
        }
    }

    public static EPApplication getInstance() {
        return instance;
    }

    /**
     * 获取用户校园卡的绑定状态
     *
     * @return
     */
    public int getUserCampusStatus() {
        if (mCampusBindStatus == Integer.MIN_VALUE) {
            SharedPreferences preferences = getSharedPreferences("user",
                    MODE_PRIVATE);
            mCampusBindStatus = preferences.getInt(PREF_CAMPUS_STATUS,
                    Integer.MIN_VALUE);
        }
        return mCampusBindStatus;
    }

    /**
     * 设置校园卡的绑定状态
     *
     * @param mode
     */
    public void setUserCampusStatus(int mode) {
        SharedPreferences preferences = getSharedPreferences("user",
                MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        if (editor.putInt(PREF_CAMPUS_STATUS, mode).commit()) {
            mCampusBindStatus = mode;
        }
    }

    /**
     * 获取用户图书馆的绑定状态
     *
     * @return
     */
    public int getUserLibStatus() {
        if (mLibBindStatus == Integer.MIN_VALUE) {
            SharedPreferences preferences = getSharedPreferences("user",
                    MODE_PRIVATE);
            mLibBindStatus = preferences.getInt(PREF_LIBRARY_STATUS,
                    Integer.MIN_VALUE);
        }
        return mLibBindStatus;
    }

    /**
     * 设置用户图书馆的绑定状态
     *
     * @param mode
     */
    public void setUserLibStatus(int mode) {
        SharedPreferences preferences = getSharedPreferences("user",
                MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        if (editor.putInt(PREF_LIBRARY_STATUS, mode).commit()) {
            mLibBindStatus = mode;
        }
    }

    /**
     * 获取用户认证的状态
     *
     * @return
     */
    public int getUserStatus() {
        if (mUserStatus == Integer.MIN_VALUE) {
            SharedPreferences preferences = getSharedPreferences("user",
                    MODE_PRIVATE);
            mUserStatus = preferences.getInt(PREF_USER_STATUS,
                    Integer.MIN_VALUE);
        }
        return mUserStatus;
    }

    /**
     * 设置用户认证状态
     *
     * @param mode
     */
    public void setUserStatus(int mode) {
        SharedPreferences preferences = getSharedPreferences("user",
                MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        if (editor.putInt(PREF_USER_STATUS, mode).commit()) {
            mUserStatus = mode;
        }
    }

    /**
     * 获取用户名
     *
     * @return
     */
    public String getUserName() {
        if (mUserName == null) {
            SharedPreferences preferences = getSharedPreferences("user",
                    MODE_PRIVATE);
            mUserName = preferences.getString(PREF_USERNAME, null);
        }

        return mUserName;
    }

    /**
     * 获取密码
     *
     * @return
     */
    public String getPassWord() {
        if (mPassWord == null) {
            SharedPreferences preferences = getSharedPreferences("user",
                    MODE_PRIVATE);
            String out_password = preferences.getString(PREF_PASSWORD, null);
            if (out_password != null) {
                mPassWord = EPSecretService.decryptByPublic(out_password);
            }
        }
        return mPassWord;
    }

    /**
     * 获取Token
     *
     * @return
     */
    public String getToken() {
        if (mToken == null) {
            SharedPreferences preferences = getSharedPreferences("user",
                    MODE_PRIVATE);
            String out_token = preferences.getString(PREF_TOKEN, null);
            if (out_token != null) {
                mToken = EPSecretService.decryptByPublic(out_token);
            }
        }
        return mToken;
    }

    /**
     * 获取聊天Token
     *
     * @return
     */
    public String getChatToken() {
        if (mChatToken == null) {
            SharedPreferences preferences = getSharedPreferences("user",
                    MODE_PRIVATE);
            String out_password = preferences.getString(PREF_CHAT_TOKEN, null);
            if (out_password != null) {
                mChatToken = EPSecretService.decryptByPublic(out_password);
            }
        }
        return mChatToken;
    }

    /**
     * 获取用户昵称
     *
     * @return
     */
    public String getNickName() {
        if (mNickName == null) {
            SharedPreferences preferences = getSharedPreferences("user",
                    MODE_PRIVATE);
            mNickName = preferences.getString(PREF_NICKNAME, null);
        }
        return mNickName;

    }

    /**
     * 设置用户名
     *
     * @param user
     */
    public void setUserName(String username) {
        if (username != null) {
            SharedPreferences preferences = getSharedPreferences("user",
                    MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            if (editor.putString(PREF_USERNAME, username).commit()) {
                mUserName = username;
            }
        }
    }

    /**
     * 设置聊天Token
     *
     * @param user
     */
    public void setChatToken(String token) {
        if (token != null) {
            SharedPreferences preferences = getSharedPreferences("user",
                    MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            if (editor.putString(PREF_CHAT_TOKEN, token).commit()) {
                mChatToken = EPSecretService.decryptByPublic(token);
            }
        }
    }

    /**
     * 设置用密码
     *
     * @param user
     */
    public void setPassWord(String password) {
        SharedPreferences preferences = getSharedPreferences("user",
                MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        if (editor.putString(PREF_PASSWORD, password).commit()) {
            if (password != null) {
                mPassWord = EPSecretService.decryptByPublic(password);
            } else {
                mPassWord = null;
            }
        }
    }

    /**
     * 设置Token
     *
     * @param user
     */
    public void setToken(String token) {

        SharedPreferences preferences = getSharedPreferences("user",
                MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        if (editor.putString(PREF_TOKEN, token).commit()) {
            if (token != null) {
                mToken = EPSecretService.decryptByPublic(token);
            } else {
                mToken = null;
            }
        }

    }

    /**
     * 设置昵称
     *
     * @param user
     */
    public void setNickName(String nickName) {
        if (nickName != null) {
            SharedPreferences preferences = getSharedPreferences("user",
                    MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            if (editor.putString(PREF_NICKNAME, nickName).commit()) {
                mNickName = nickName;
            }
        }
    }

    /**
     * 获取内存中好友user list
     *
     * @return
     */
    public Map<String, User> getContactList() {
        if (getUserName() != null && mContactList == null) {
            UserDao dao = new UserDao(applicationContext);
            // 获取本地好友user list到内存,方便以后获取好友list
            mContactList = dao.getContactList();
        }
        return mContactList;
    }

    public void refreshContact() {
        UserDao dao = new UserDao(applicationContext);
        mContactList = dao.getContactList();
    }

    /**
     * 设置好友user list到内存中
     *
     * @param contactList
     */
    public void setContactList(Map<String, User> contactList) {
        this.mContactList = contactList;
    }

    public User findUserByUserName(String name) {
        if (mContactList == null) {
            mContactList = getContactList();
        }
        for (User user : mContactList.values()) {
            if (user.getUsername().equals(name)) {
                return user;
            }
        }
        return new User(name);
    }

    /**
     * 退出登录,清空数据
     */
    public void logout() {
        // 先调用sdk logout，在清理app中自己的数据
        EMChatManager.getInstance().logout();
        EPDataBaseHelper.getInstance(applicationContext).closeDB();
        // 清空队列所有请求
        EPHttpQueueService.getInstance(applicationContext).clearAllTask();
        // reset password to null

        setToken(null);
        setContactList(null);
        new DataCacheDao(instance).cleanData();
        new EPScheduleService(instance).clearSchedule();
        setUserStatus(Integer.MIN_VALUE);
    }

    public boolean isLogined() {
        return EMChat.getInstance().isLoggedIn() && mToken != null;
    }

    // private ServiceConnection connection = new ServiceConnection() {
    //
    // @Override
    // public void onServiceDisconnected(ComponentName name) {
    // // TODO Auto-generated method stub
    // Log.i(TAG, "disconnected");
    // }
    //
    // @Override
    // public void onServiceConnected(ComponentName name, IBinder service) {
    // // TODO Auto-generated method stub
    // Log.i(TAG, "connected");
    // }
    // };
}
