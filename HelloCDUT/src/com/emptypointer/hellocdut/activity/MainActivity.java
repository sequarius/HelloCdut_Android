package com.emptypointer.hellocdut.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import android.app.*;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactListener;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.EMMessage.Type;
import com.easemob.chat.EMNotifier;
import com.easemob.chat.GroupChangeListener;
import com.easemob.chat.TextMessageBody;
import com.easemob.util.EMLog;
import com.easemob.util.HanziToPinyin;
import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.adapter.EPPagerAdapter;
import com.emptypointer.hellocdut.customer.EPApplication;
import com.emptypointer.hellocdut.dao.DataCacheDao;
import com.emptypointer.hellocdut.dao.InviteMessgeDao;
import com.emptypointer.hellocdut.dao.UserDao;
import com.emptypointer.hellocdut.domain.DataCache;
import com.emptypointer.hellocdut.domain.InviteMessage;
import com.emptypointer.hellocdut.domain.InviteMessage.InviteMesageStatus;
import com.emptypointer.hellocdut.domain.User;
import com.emptypointer.hellocdut.service.EPAAONewsService;
import com.emptypointer.hellocdut.service.EPJsonHttpResponseHandler;
import com.emptypointer.hellocdut.service.EPSecretService;
import com.emptypointer.hellocdut.service.EPUpdateService;
import com.emptypointer.hellocdut.utils.CommonUtils;
import com.emptypointer.hellocdut.utils.GlobalVariables;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.readystatesoftware.viewbadger.BadgeView;
import com.viewpagerindicator.TabPageIndicator;

import org.apache.http.Header;
import org.json.JSONException;

public class MainActivity extends NoBackBasaActivity {
    private static final String PRE_LAST_NOTIFY_ID = "last_notify_id";
    private boolean isConflictDialogShow;
    public static final String CACHE_SCHEDULED_TASK = "main_scheduled_task";
    private UserDao mUserDao;
    private InviteMessgeDao mInviteDao;

    private NewMessageBroadcastReceiver msgReceiver;

    private DataCacheDao mDao;

    private EPPagerAdapter mPagerAdapetr;
    private ViewPager mViewPager;
    private TabPageIndicator mIndicator;
    private EPAAONewsService mNewsGetter;
    private EPUpdateService mUPdateService;
    private Builder conflictBuilder;
    private BadgeView mBvFunction;// 功能tab的數值指示器
    private BadgeView mBvContact;// 聯係人的tab數值指示器
    private BadgeView mBvMessage;// 消息Tab的數值指示器
    private BadgeView mBvUserCenter;// 用戶中心tab的數值指示器

    // 账号在别处登录
    public boolean isConflict = false;

    @Override
    public void onBackPressed() {
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addCategory(Intent.CATEGORY_HOME);
        startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView(savedInstanceState);
        if (savedInstanceState != null
                && savedInstanceState.getBoolean("isConflict", false)) {
            // 防止被T后，没点确定按钮然后按了home键，长期在后台又进app导致的crash
            // 三个fragment里加的判断同理
            finish();
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }
        SharedPreferences sharedPreferences = getSharedPreferences("init",
                MODE_PRIVATE);
        if (!sharedPreferences.getBoolean("init_guide_page", false)) {
            startActivity(new Intent(this, GuideActivity.class));
            sharedPreferences.edit().putBoolean("init_guide_page", true)
                    .commit();
        }

        if (getIntent().getBooleanExtra("conflict", false)
                && !isConflictDialogShow)
            showConflictDialog();
        mUserDao = new UserDao(this);
        mInviteDao = new InviteMessgeDao(this);
        int upid = Resources.getSystem().getIdentifier("up", "id", "android");
        ImageView returnImage = (ImageView) findViewById(upid);
        returnImage.setVisibility(View.GONE);

        // 注册一个接收消息的BroadcastReceiver
        msgReceiver = new NewMessageBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(EMChatManager
                .getInstance().getNewMessageBroadcastAction());
        intentFilter.setPriority(3);
        registerReceiver(msgReceiver, intentFilter);

        // 注册一个ack回执消息的BroadcastReceiver
        IntentFilter ackMessageIntentFilter = new IntentFilter(EMChatManager
                .getInstance().getAckMessageBroadcastAction());
        ackMessageIntentFilter.setPriority(3);
        registerReceiver(ackMessageReceiver, ackMessageIntentFilter);

        // 监听联系人变化
        EMContactManager.getInstance().setContactListener(
                new EPContactListener());

        // 监听连接变化
        EMChatManager.getInstance().addConnectionListener(
                new EPConnectionListener());
        // 监听群组变化
        EMGroupManager.getInstance().addGroupChangeListener(
                new EPGroupChangeListener());
        // 初始化完成
        EMChat.getInstance().setAppInited();
        initComponent();

    }

    // 所有在Oncreate方法中New出的Adpater，List 及其他组件统一抽取到initComponent
    private void initComponent() {
        mDao = new DataCacheDao(this);
        mPagerAdapetr = new EPPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapetr);
        mIndicator.setViewPager(mViewPager);
        mNewsGetter = new EPAAONewsService(this);
        mUPdateService = new EPUpdateService(EPApplication.getInstance(), this);

        mBvFunction = new BadgeView(this, mIndicator);

        int offset = (int) (getScreenWith() / 1.30);
        mBvFunction.setBadgeMargin(offset, 10);

        mBvContact = new BadgeView(this, mIndicator);
        offset = (int) (getScreenWith() / 1.90);
        mBvContact.setBadgeMargin(offset, 10);
        mBvContact.show();

        mBvMessage = new BadgeView(this, mIndicator);

        offset = (int) (getScreenWith() / 3.40);
        mBvMessage.setBadgeMargin(offset, 10);
        mBvMessage.show();

        mBvUserCenter = new BadgeView(this, mIndicator);

        offset = (int) (getScreenWith() / 24);
        mBvUserCenter.setBadgeMargin(offset, 10);

        mViewPager.setOffscreenPageLimit(7);

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUnreadLabel();
        updateUnreadAddressLable();
        loadNotifyFromServer();
        if (!isConflict) {
            EMChatManager.getInstance().activityResumed();
        }
        if (!EMChat.getInstance().isLoggedIn()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            this.finish();

        } else {
            DataCache cache = mDao.getCache(CACHE_SCHEDULED_TASK);
            long time = 0;
            if (cache != null) {
                time = Long.valueOf(cache.getDate());
            }
            if (System.currentTimeMillis() - time > 86400000) {
                new Thread() {

                    @Override
                    public void run() {
                        mNewsGetter.getNews();
                        mUPdateService.cheakVersionBack();
                        mDao.saveCache(CACHE_SCHEDULED_TASK,
                                String.valueOf(System.currentTimeMillis()));
                    }

                }.start();

                loadNotifyFromServer();
            }
        }

    }

    public void loadNotifyFromServer(){
        final SharedPreferences preferences=getSharedPreferences(GlobalVariables.SHARED_PERFERENCR_SETTING, Context.MODE_PRIVATE);
        final Long recentID=preferences.getLong(PRE_LAST_NOTIFY_ID,Long.MIN_VALUE);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        requestParams.add("action", "loginNotify");
        requestParams.add("user_name", EPSecretService.encryptByPublic(EPApplication.getInstance().getUserName()));
        requestParams.add("user_login_token", EPSecretService.encryptByPublic(EPApplication.getInstance().getToken()));
        client.post(GlobalVariables.SERVICE_HOST,requestParams,new EPJsonHttpResponseHandler(this){
            @Override
            public void onSuccess(int statusCode, Header[] headers, org.json.JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                if(!result){
                    return;
                }
                try {
                    final long id=response.getLong("id");
                    if(id==recentID){
                        return;
                    }
                    String content=response.getString("content");
                    boolean hasURL=response.getBoolean("has_url");
                    Builder builder= new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle(R.string.prompt);
                    builder.setMessage(content);
                    builder.setNegativeButton(R.string.str_knowed,
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    preferences.edit().putLong(PRE_LAST_NOTIFY_ID,id).commit();
                                    dialog.dismiss();
                                }
                            });
                    if(hasURL) {
                        final String url = response.getString("return_url");
                        builder.setPositiveButton(R.string.str_read_detail,new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                startActivity(intent);
                                dialog.dismiss();
                            }
                        });
                    }
                    CommonUtils.dialogTitleLineColor(builder.show());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
//        requestParams.add("key_words", keyword);
    }

    // 所有在Oncreate方法中XML文件的findeView；及动态new出的View对象统一抽取到initView
    private void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        mViewPager = (ViewPager) findViewById(R.id.viewpager_index_guide);
        mIndicator = (TabPageIndicator) findViewById(R.id.indicator_index_guide);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent intent = new Intent();
        switch (id) {
            case R.id.action_add:
                intent.setAction(GlobalVariables.ACTION_ADD_CONTACT);
                break;
            case R.id.action_search:
                intent.setAction(GlobalVariables.ACTION_SEARCH);
                break;

            default:
                break;
        }
        if (intent.getAction() != null) {
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 好友变化listener
     */
    private class EPContactListener implements EMContactListener {

        @Override
        public void onContactAdded(List<String> usernameList) {
            // 保存增加的联系人
            Map<String, User> localUsers = EPApplication.getInstance()
                    .getContactList();
            Map<String, User> toAddUsers = new HashMap<String, User>();
            for (String username : usernameList) {
                User user = setUserHead(username);
                // 暂时有个bug，添加好友时可能会回调added方法两次
                if (!localUsers.containsKey(username)) {
                    mUserDao.saveContact(user);
                }
                toAddUsers.put(username, user);
            }
            localUsers.putAll(toAddUsers);
            // // 刷新ui
            // if (currentTabIndex == 1)
            mPagerAdapetr.refreshContact();

        }

        @Override
        public void onContactDeleted(final List<String> usernameList) {
            // 被删除
            Map<String, User> localUsers = EPApplication.getInstance()
                    .getContactList();
            for (String username : usernameList) {
                localUsers.remove(username);
                mUserDao.deleteContact(username);
                mInviteDao.deleteMessage(username);
            }
            runOnUiThread(new Runnable() {
                public void run() {
                    // 如果正在与此用户的聊天页面
                    if (ChatActivity.activityInstance != null
                            && usernameList
                            .contains(ChatActivity.activityInstance
                                    .getToChatUsername())) {
                        CommonUtils.showCustomToast(Toast.makeText(
                                MainActivity.this,
                                ChatActivity.activityInstance
                                        .getToChatUsername() + "已把你从他好友列表里移除",
                                Toast.LENGTH_SHORT));
                        ChatActivity.activityInstance.finish();
                    }
                    updateUnreadLabel();
                }
            });
            mPagerAdapetr.refreshContact();

        }

        @Override
        public void onContactInvited(String username, String reason) {
            List<InviteMessage> msgs = mInviteDao.getMessagesList();
            for (InviteMessage inviteMessage : msgs) {
                if (inviteMessage.getGroupId() == null
                        && inviteMessage.getFrom().equals(username)) {
                    mInviteDao.deleteMessage(username);
                }
            }
            // 自己封装的javabean
            InviteMessage msg = new InviteMessage();
            msg.setFrom(username);
            msg.setTime(System.currentTimeMillis());
            msg.setReason(reason);

            // 设置相应status
            msg.setStatus(InviteMesageStatus.BEINVITEED);
            notifyNewIviteMessage(msg);

        }

        @Override
        public void onContactAgreed(String username) {
            List<InviteMessage> msgs = mInviteDao.getMessagesList();
            for (InviteMessage inviteMessage : msgs) {
                if (inviteMessage.getFrom().equals(username)) {
                    return;
                }
            }
            // 自己封装的javabean
            InviteMessage msg = new InviteMessage();
            msg.setFrom(username);
            msg.setTime(System.currentTimeMillis());
            msg.setStatus(InviteMesageStatus.BEAGREED);
            // notifyNewIviteMessage(msg);

        }

        @Override
        public void onContactRefused(String username) {
            // 参考同意，被邀请实现此功能,demo未实现

        }

    }

    /**
     * 保存提示新消息
     *
     * @param msg
     */
    private void notifyNewIviteMessage(InviteMessage msg) {
        saveInviteMsg(msg);
        // 提示有新消息
        EMNotifier.getInstance(getApplicationContext()).notifyOnNewMsg();

        // 刷新bottom bar消息未读数
        updateUnreadAddressLable();
        // 刷新好友页面ui
        // if (currentTabIndex == 1)
        mPagerAdapetr.refreshContact();
    }

    /**
     * 保存邀请等msg
     *
     * @param msg
     */
    private void saveInviteMsg(InviteMessage msg) {
        // 保存msg
        mInviteDao.saveMessage(msg);
        // 未读数加1
        User user = EPApplication.getInstance().getContactList()
                .get(GlobalVariables.NEW_FRIENDS_USERNAME);
        user.setUnreadMsgCount(user.getUnreadMsgCount() + 1);
    }

    /**
     * set head
     *
     * @param username
     * @return
     */
    User setUserHead(String username) {
        User user = new User();
        user.setUsername(username);
        String headerName = null;
        if (!TextUtils.isEmpty(user.getNick())) {
            headerName = user.getNick();
        } else {
            headerName = user.getUsername();
        }
        if (username.equals(GlobalVariables.NEW_FRIENDS_USERNAME)) {
            user.setHeader("");
        } else if (Character.isDigit(headerName.charAt(0))) {
            user.setHeader("#");
        } else {
            user.setHeader(HanziToPinyin.getInstance()
                    .get(headerName.substring(0, 1)).get(0).target.substring(0,
                            1).toUpperCase());
            char header = user.getHeader().toLowerCase().charAt(0);
            if (header < 'a' || header > 'z') {
                user.setHeader("#");
            }
        }
        return user;
    }

    /**
     * 显示帐号在别处登录dialog
     */
    private void showConflictDialog() {
        isConflictDialogShow = true;
        EPApplication.getInstance().logout();

        if (!MainActivity.this.isFinishing()) {
            // clear up global variables
            try {
                if (conflictBuilder == null)
                    conflictBuilder = new android.app.AlertDialog.Builder(
                            MainActivity.this);
                conflictBuilder.setTitle("下线通知");
                conflictBuilder.setMessage(R.string.connect_conflict);
                conflictBuilder.setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                                conflictBuilder = null;
                                finish();
                                startActivity(new Intent(MainActivity.this,
                                        LoginActivity.class));
                            }
                        });
                conflictBuilder.setCancelable(false);
                CommonUtils.dialogTitleLineColor(conflictBuilder.show());
                isConflict = true;
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 注销广播接收者
        try {
            unregisterReceiver(msgReceiver);
        } catch (Exception e) {
        }
        try {
            unregisterReceiver(ackMessageReceiver);
        } catch (Exception e) {
        }
        // try {
        // unregisterReceiver(offlineMessageReceiver);
        // } catch (Exception e) {
        // }

        if (conflictBuilder != null) {
            conflictBuilder.create().dismiss();
            conflictBuilder = null;
        }

    }

    /**
     * 新消息广播接收者
     */
    private class NewMessageBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 主页面收到消息后，主要为了提示未读，实际消息内容需要到chat页面查看

            String from = intent.getStringExtra("from");
            // 消息id
            String msgId = intent.getStringExtra("msgid");
            EMMessage message = EMChatManager.getInstance().getMessage(msgId);
            // 2014-10-22 修复在某些机器上，在聊天页面对方发消息过来时不立即显示内容的bug
            if (ChatActivity.activityInstance != null) {
                if (message.getChatType() == ChatType.GroupChat) {
                    if (message.getTo().equals(
                            ChatActivity.activityInstance.getToChatUsername()))
                        return;
                } else {
                    if (from.equals(ChatActivity.activityInstance
                            .getToChatUsername()))
                        return;
                }
            }

            // 注销广播接收者，否则在ChatActivity中会收到这个广播
            abortBroadcast();

            // mark notifyNewMessage(message);

            // 刷新bottom bar消息未读数
            updateUnreadLabel();
            // if (currentTabIndex == 0) {
            // // 当前页面如果为聊天历史页面，刷新此页面
            // if ( != null) {
            // chatHistoryFragment.refresh();
            // }
            // }
            // 刷新
            mPagerAdapetr.refreshNotify();

        }
    }

    /**
     * 消息回执BroadcastReceiver
     */
    private BroadcastReceiver ackMessageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            abortBroadcast();

            String msgid = intent.getStringExtra("msgid");
            String from = intent.getStringExtra("from");

            EMConversation conversation = EMChatManager.getInstance()
                    .getConversation(from);
            if (conversation != null) {
                // 把message设为已读
                EMMessage msg = conversation.getMessage(msgid);

                if (msg != null) {

                    // 2014-11-5 修复在某些机器上，在聊天页面对方发送已读回执时不立即显示已读的bug
                    if (ChatActivity.activityInstance != null) {
                        if (msg.getChatType() == ChatType.Chat) {
                            if (from.equals(ChatActivity.activityInstance
                                    .getToChatUsername()))
                                return;
                        }
                    }

                    msg.isAcked = true;
                }
            }

        }
    };

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (getIntent().getBooleanExtra("conflict", false)
                && !isConflictDialogShow)
            showConflictDialog();
    }

    /**
     * MyGroupChangeListener
     */
    private class EPGroupChangeListener implements GroupChangeListener {

        @Override
        public void onInvitationReceived(String groupId, String groupName,
                                         String inviter, String reason) {
            boolean hasGroup = false;
            for (EMGroup group : EMGroupManager.getInstance().getAllGroups()) {
                if (group.getGroupId().equals(groupId)) {
                    hasGroup = true;
                    break;
                }
            }
            if (!hasGroup)
                return;

            // 被邀请
            EMMessage msg = EMMessage.createReceiveMessage(Type.TXT);
            msg.setChatType(ChatType.GroupChat);
            msg.setFrom(inviter);
            msg.setTo(groupId);
            msg.setMsgId(UUID.randomUUID().toString());
            msg.addBody(new TextMessageBody(inviter + "邀请你加入了群聊"));
            // 保存邀请消息
            EMChatManager.getInstance().saveMessage(msg);
            // 提醒新消息
            EMNotifier.getInstance(getApplicationContext()).notifyOnNewMsg();

            runOnUiThread(new Runnable() {
                public void run() {
                    updateUnreadLabel();
                    // // 刷新ui
                    // if (currentTabIndex == 0)
                    // chatHistoryFragment.refresh();
                    // if
                    // (CommonUtils.getTopActivity(MainActivity.this).equals(GroupsActivity.class.getName()))
                    // {
                    // GroupsActivity.instance.onResume();
                    // }
                    mPagerAdapetr.refreshNotify();
                }
            });

        }

        @Override
        public void onInvitationAccpted(String groupId, String inviter,
                                        String reason) {

        }

        @Override
        public void onInvitationDeclined(String groupId, String invitee,
                                         String reason) {

        }

        @Override
        public void onUserRemoved(String groupId, String groupName) {
            // 提示用户被T了，demo省略此步骤
            // 刷新ui
            runOnUiThread(new Runnable() {
                public void run() {
                    try {
                        updateUnreadLabel();
                        // if (currentTabIndex == 0)
                        // chatHistoryFragment.refresh();
                        mPagerAdapetr.refreshNotify();
                        if (CommonUtils.getTopActivity(MainActivity.this)
                                .equals(GroupsActivity.class.getName())) {
                            GroupsActivity.instance.onResume();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        }

        @Override
        public void onGroupDestroy(String groupId, String groupName) {
            // 群被解散
            // 提示用户群被解散,demo省略
            // 刷新ui
            runOnUiThread(new Runnable() {
                public void run() {
                    updateUnreadLabel();
                    // if (currentTabIndex == 0)
                    // chatHistoryFragment.refresh();
                    mPagerAdapetr.refreshNotify();
                    if (CommonUtils.getTopActivity(MainActivity.this).equals(
                            GroupsActivity.class.getName())) {
                        GroupsActivity.instance.onResume();
                    }
                }
            });

        }

        @Override
        public void onApplicationReceived(String groupId, String groupName,
                                          String applyer, String reason) {
            // 用户申请加入群聊
            InviteMessage msg = new InviteMessage();
            msg.setFrom(applyer);
            msg.setTime(System.currentTimeMillis());
            msg.setGroupId(groupId);
            msg.setGroupName(groupName);
            msg.setReason(reason);
            msg.setStatus(InviteMesageStatus.BEAPPLYED);
            notifyNewIviteMessage(msg);
        }

        @Override
        public void onApplicationAccept(String groupId, String groupName,
                                        String accepter) {
            // 加群申请被同意
            EMMessage msg = EMMessage.createReceiveMessage(Type.TXT);
            msg.setChatType(ChatType.GroupChat);
            msg.setFrom(accepter);
            msg.setTo(groupId);
            msg.setMsgId(UUID.randomUUID().toString());
            msg.addBody(new TextMessageBody(accepter + "同意了你的群聊申请"));
            // 保存同意消息
            EMChatManager.getInstance().saveMessage(msg);
            // 提醒新消息
            EMNotifier.getInstance(getApplicationContext()).notifyOnNewMsg();

            runOnUiThread(new Runnable() {
                public void run() {
                    updateUnreadLabel();
                    // 刷新ui
                    // if (currentTabIndex == 0)
                    // chatHistoryFragment.refresh();
                    // if
                    // (CommonUtils.getTopActivity(MainActivity.this).equals(GroupsActivity.class.getName()))
                    // {
                    // GroupsActivity.instance.onResume();
                    // }
                    mPagerAdapetr.refreshNotify();
                }
            });
        }

        @Override
        public void onApplicationDeclined(String groupId, String groupName,
                                          String decliner, String reason) {
            // 加群申请被拒绝，demo未实现
        }

    }

    /**
     * 刷新未读消息数
     */
    public void updateUnreadLabel() {
        int count = getUnreadMsgCountTotal();
        if (count > 0) {
            mBvMessage.setText(String.valueOf(count));
            mBvMessage.setVisibility(View.VISIBLE);
        } else {
            mBvMessage.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 获取未读消息数
     *
     * @return
     */
    public int getUnreadMsgCountTotal() {
        int unreadMsgCountTotal = 0;
        unreadMsgCountTotal = EMChatManager.getInstance().getUnreadMsgsCount();
        return unreadMsgCountTotal;
    }

    /**
     * 连接监听listener
     */
    private class EPConnectionListener implements EMConnectionListener {

        @Override
        public void onConnected() {
        }

        @Override
        public void onDisconnected(final int error) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (error == EMError.CONNECTION_CONFLICT) {
                        // 显示帐号在其他设备登陆dialog
                        showConflictDialog();
                    } else {

                    }
                }

            });
        }
    }

    /**
     * 刷新申请与通知消息数
     */
    public void updateUnreadAddressLable() {
        runOnUiThread(new Runnable() {
            public void run() {
                int count = getUnreadAddressCountTotal();
                if (count > 0) {
                    mBvContact.setText(String.valueOf(count));
                    mBvContact.setVisibility(View.VISIBLE);
                } else {
                    mBvContact.setVisibility(View.INVISIBLE);
                }
            }
        });

    }

    /**
     * 获取未读申请与通知消息
     *
     * @return
     */
    public int getUnreadAddressCountTotal() {
        int unreadAddressCountTotal = 0;
        if (EPApplication.getInstance().getContactList()
                .get(GlobalVariables.NEW_FRIENDS_USERNAME) != null)
            unreadAddressCountTotal = EPApplication.getInstance()
                    .getContactList().get(GlobalVariables.NEW_FRIENDS_USERNAME)
                    .getUnreadMsgCount();
        return unreadAddressCountTotal;
    }
}
