package com.emptypointer.hellocdut.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.message.BasicNameValuePair;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMGroupManager;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.HanziToPinyin;
import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.customer.EPApplication;
import com.emptypointer.hellocdut.dao.UserDao;
import com.emptypointer.hellocdut.domain.User;
import com.emptypointer.hellocdut.domain.UserInfo;
import com.emptypointer.hellocdut.service.EPHttpService;
import com.emptypointer.hellocdut.service.EPSecretService;
import com.emptypointer.hellocdut.utils.CommonUtils;
import com.emptypointer.hellocdut.utils.GlobalVariables;
import com.emptypointer.hellocdut.utils.StringChecker;

public class LoginActivity extends NoBackBasaActivity implements
        OnClickListener {
    private Button mBtnLogin;
    private TextView mTvRetrieve;
    private TextView mTvRegister;
    private EditText mEtUserName;
    private EditText mEtPassWord;

    // private static final String INTERFACE_LOGIN="apiRequestPro.php";

    @Override
    protected void onCreate(Bundle arg0) {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        overridePendingTransition(android.R.anim.fade_in,
                android.R.anim.fade_out);
        SharedPreferences sharedPreferences = getSharedPreferences("init",
                MODE_PRIVATE);
        if (!sharedPreferences.getBoolean("init_guide_page", false)) {
            startActivity(new Intent(this, GuideActivity.class));
            sharedPreferences.edit().putBoolean("init_guide_page", true)
                    .commit();
        }
        setContentView(R.layout.activity_login);

        mBtnLogin = (Button) findViewById(R.id.button_login);
        mTvRegister = (TextView) findViewById(R.id.textView_register);
        mTvRetrieve = (TextView) findViewById(R.id.textView_retrieve_password);
        mEtUserName = (EditText) findViewById(R.id.editText_user_name);
        mEtPassWord = (EditText) findViewById(R.id.editText_password);

        mBtnLogin.setOnClickListener(this);
        mTvRegister.setOnClickListener(this);
        mTvRetrieve.setOnClickListener(this);

        mEtUserName.addTextChangedListener(new EPTtextWatcher());
        initInputs();
    }

    private void initInputs() {
        /**
         * 如果账号密码Token 认证状态全部存在直接进入主界面
         */
        String userName = EPApplication.getInstance().getUserName();
        String passWord = EPApplication.getInstance().getPassWord();
        String token = EPApplication.getInstance().getToken();
        int userMode = EPApplication.getInstance().getUserStatus();
        if (userName != null && passWord != null && token != null
                && userMode > EPApplication.USER_STATUS_UNLOGIN) {
            startActivity(new Intent(GlobalVariables.ACTION_MAIN_ACTIVITY));

            finish();

        } else {
            if (userName != null) {
                mEtUserName.setText(userName);
            }
            if (passWord != null) {
                mEtPassWord.setText(passWord);
            }

        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        initInputs();

    }

    /**
     * 如果用户名改变 清空密码
     *
     * @author Sequarius
     */
    private class EPTtextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub
            mEtPassWord.setText(null);
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            // TODO Auto-generated method stub

        }

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub

        }

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.button_login:
                doLogin();

                break;
            case R.id.textView_register:

                intent.setAction(GlobalVariables.ACTION_REGISTER);
                break;
            case R.id.textView_retrieve_password:
                intent.setAction(GlobalVariables.ACTION_RETRIEVE);
                break;

            default:
                break;
        }
        if (intent.getAction() != null) {
            startActivity(intent);
        }
    }

    private void doLogin() {
        String username = mEtUserName.getText().toString().trim();
        String password = mEtPassWord.getText().toString().trim();
        if (!StringChecker.isLegalUserName(username)) {
            CommonUtils.showCustomToast(Toast.makeText(this,
                    getString(R.string.message_wrong_username_toast),
                    Toast.LENGTH_LONG));
            return;
        }
        if (!StringChecker.isLegalPassword(password)) {
            CommonUtils.showCustomToast(Toast.makeText(this,
                    getString(R.string.message_wrong_password_toast),
                    Toast.LENGTH_LONG));
            return;
        }
        new LoginTask().execute(username, password);

    }

    private class LoginTask extends AsyncTask<String, Integer, Boolean> {

        private ProgressDialog mDialog;

        private String mMessage = "";

        @Override
        protected void onPreExecute() {
            mDialog = new ProgressDialog(LoginActivity.this);
            mDialog.setMessage(getString(R.string.str_logining));
            mDialog.setCanceledOnTouchOutside(false);

            mDialog.show();
            // mDialog = ProgressDialog.show(LoginActivity.this,
            // getString(R.string.str_logining),
            // getString(R.string.message_loading));
        }

        @Override
        protected Boolean doInBackground(String... put_params) {
            // 泛型形参 用户名[0]和密码[1]

            String _username = EPSecretService.encryptByPublic(put_params[0]);
            String _password = EPSecretService.encryptByPublic(put_params[1]);

            // Map<String, String>params=new HashMap<String, String>();
            // params.put("user_name",_username);
            // params.put("user_password", _password);
            // params.put("action", "userLogin");

            try {

                List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                params.add(new BasicNameValuePair("user_name", _username));
                params.add(new BasicNameValuePair("user_password", _password));
                params.add(new BasicNameValuePair("action", "userLogin"));
                String str2 = EPHttpService.customerPostString(
                        GlobalVariables.SERVICE_HOST_USER_SYSTEM, params);

                JSONObject JsonObject = JSONObject.parseObject(str2);
                boolean result = JsonObject.getBooleanValue("result");
                mMessage = JsonObject.getString("message");
                if (!result) {
                    return false;
                }

                // 验证通过
                EPApplication application = EPApplication.getInstance();

                application.setChatToken(JsonObject
                        .getString("user_chat_token"));
                application.setUserName(JsonObject.getString("user_name"));
                application.setToken(JsonObject.getString("user_login_token"));

                application.setUserStatus(JsonObject
                        .getIntValue("user_aao_status"));
                application.setUserCampusStatus(JsonObject
                        .getIntValue("user_campus_status"));
                application.setUserLibStatus(JsonObject
                        .getIntValue("user_lib_status"));
                application.setNickName(JsonObject.getString("user_nick_name"));
                application.setUserName(JsonObject.getString("user_name"));
                application.setPassWord(JsonObject
                        .getString("user_password_hash"));

                application.setChatToken(JsonObject
                        .getString("user_chat_token"));
                // 存储用户信息
                UserInfo info = UserInfo.getInstance(LoginActivity.this);
                info.setStudentID(JsonObject.getString("user_stu_id"));
                info.setMail(JsonObject.getString("user_email"));
                info.setGender(JsonObject.getIntValue("user_gender"));
                info.setMetto(JsonObject.getString("user_motto"));
                info.setLoveStatus(JsonObject.getIntValue("user_love_status"));
                info.setSexOrientation(JsonObject
                        .getIntValue("user_sex_orientation"));
                info.setRealName(JsonObject.getString("user_real_name"));
                info.setBirthDate(JsonObject.getString("user_birthdate"));
                info.setInstituteName(JsonObject.getString("user_institute"));
                info.setMajorName(JsonObject.getString("user_major"));
                info.setClassID(JsonObject.getString("user_class_id"));
                info.setEntryYear(JsonObject.getString("user_entrance_year"));
                info.setImageURL(JsonObject.getString("user_avatar_url"));
                info.setPermissionLoveStatus(JsonObject
                        .getIntValue("user_love_status_permission"));
                info.setPermissionSexOrientation(JsonObject
                        .getIntValue("user_sex_orientation_permission"));
                info.setPermissionRealName(JsonObject
                        .getIntValue("user_real_name_permission"));
                info.setPermissionBirthDate(JsonObject
                        .getIntValue("user_birthdate_permission"));
                info.setPermissionStudentID(JsonObject
                        .getIntValue("user_stu_num_permission"));
                info.setPermissionInstitute(JsonObject
                        .getIntValue("user_institute_id_permission"));
                info.setPermissionMajor(JsonObject
                        .getIntValue("user_major_id_permission"));
                info.setPermissionClass(JsonObject
                        .getIntValue("user_class_id_permission"));
                info.setPermissionEntryYear(JsonObject
                        .getIntValue("user_entrance_year_permission"));
                info.setPermissionSchedule(JsonObject
                        .getIntValue("user_schedule_permission"));
                info.setPermissionGroupSchedule(JsonObject
                        .getIntValue("user_group_schedule_permission"));
                // 开始获取好友列表
                publishProgress(1);

                if (getContactFromEP(application)) return false;

                // 获取群聊列表(群聊里只有groupid和groupname的简单信息),sdk会把群组存入到内存和db中

                return true;

            } catch (Exception e) {
                e.printStackTrace();
                mMessage = getString(R.string.message_weak_internet);
                return false;
            }

        }

        private boolean getContactFromEP(EPApplication application) throws Exception {
            boolean result;List<BasicNameValuePair> paramUserList = new ArrayList<BasicNameValuePair>();
            paramUserList.add(new BasicNameValuePair("action",
                    "getUserFriendList"));
            paramUserList.add(new BasicNameValuePair("user_name",
                    EPSecretService.encryptByPublic(application
                            .getUserName())));
            paramUserList
                    .add(new BasicNameValuePair("user_login_token",
                            EPSecretService.encryptByPublic(application
                                    .getToken())));

            String str = EPHttpService
                    .customerPostString(
                            GlobalVariables.SERVICE_HOST_USER_SYSTEM,
                            paramUserList);
            JSONObject object = JSONObject.parseObject(str);

            result = object.getBooleanValue("result");
            if (!result) {
                mMessage = object.getString("message");
                return true;
            }
            Map<String, User> userlist = new HashMap<String, User>();
            JSONArray array = object.getJSONArray("friendList");
            if (array != null) {
                for (int i = 0; i < array.size(); i++) {
                    JSONObject objUser = array.getJSONObject(i);
                    User user = new User();
                    String userName = objUser.getString("user_name");
                    if (userName != null) {
                        user.setUsername(userName);
                    } else {
                        break;
                    }
                    String nickName = objUser.getString("user_nick_name");

                    if (nickName != null) {
                        user.setNicKName(nickName);
                    } else {
                        user.setNicKName(userName);
                    }
                    String url = objUser.getString("user_avatar_url");
                    if (url != null) {
                        user.setImageURL(url);
                    }
                    String motto = objUser.getString("user_motto");
                    if (motto != null) {
                        user.setMotto(motto);
                    }
                    setUserHearder(nickName, user);
                    userlist.put(userName, user);
                }
            }
            // for (String username : usernames) {
            // Log.i(TAG, "username==" + username);
            // User user = new User();
            // user.setUsername(username);
            // setUserHearder(username, user);
            // userlist.put(username, user);
            // }
            // 添加user"申请与通知"
            User newFriends = new User();
            newFriends.setUsername(GlobalVariables.NEW_FRIENDS_USERNAME);
            newFriends.setNicKName("申请与通知");
            newFriends.setHeader("");
            userlist.put(GlobalVariables.NEW_FRIENDS_USERNAME, newFriends);
            // 添加"群聊"
            User groupUser = new User();
            groupUser.setUsername(GlobalVariables.GROUP_USERNAME);
            groupUser.setNicKName("群聊");
            groupUser.setHeader("");
            userlist.put(GlobalVariables.GROUP_USERNAME, groupUser);

            // 存入内存
            application.setContactList(userlist);
            // 存入db
            UserDao dao = new UserDao(LoginActivity.this);
            List<User> users = new ArrayList<User>(userlist.values());
            dao.saveContactList(users);
            return false;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            // TODO Auto-generated method stub
            if (values[0] == 1) {
                mDialog.setMessage("正在获取好友列表，请稍候！");
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            // TODO Auto-generated method stub

            if (result) {
                // mDialog.setTitle("用户验证已通过");

                final EPApplication appliaction = EPApplication.getInstance();
                String strChatToken = appliaction.getChatToken();
                String userName = appliaction.getUserName();

                EMChatManager.getInstance().login(userName, strChatToken,
                        new EMCallBack() {

                            @Override
                            public void onSuccess() {
                                try {
                                    EMGroupManager.getInstance()
                                            .getGroupsFromServer();
                                    // // 获取黑名单列表
                                    // List<String> blackList = EMContactManager
                                    // .getInstance()
                                    // .getBlackListUsernamesFromServer();
                                    // // 保存黑名单
                                    // EMContactManager.getInstance()
                                    // .saveBlackList(blackList);
                                } catch (EaseMobException e) {
                                    // TODO Auto-generated catch block
                                    mMessage = "获取组消息群失败！";
                                    EPApplication.getInstance().logout();
                                    e.printStackTrace();
                                }
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        mDialog.dismiss();
                                        CommonUtils.showCustomToast(Toast
                                                .makeText(LoginActivity.this,
                                                        mMessage,
                                                        Toast.LENGTH_SHORT));
                                    }
                                });
                                // EMChatManager.getInstance()
                                // .updateCurrentUserNick(
                                // EPApplication.getInstance()
                                // .getNickName());

                                startActivity(new Intent(
                                        GlobalVariables.ACTION_MAIN_ACTIVITY));
                                finish();

                            }

                            @Override
                            public void onProgress(int arg0, String arg1) {
                                // TODO Auto-generated method stub

                            }

                            @Override
                            public void onError(int arg0, String arg1) {
                                final String erooCode = String.valueOf(arg0);
                                // TODO Auto-generated method stub
                                EPApplication.getInstance().logout();
                                mDialog.dismiss();
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        CommonUtils.showCustomToast(Toast
                                                .makeText(LoginActivity.this,
                                                        "错误:" + erooCode,
                                                        Toast.LENGTH_LONG));
                                    }
                                });

                            }
                        });

            } else {
                EPApplication.getInstance().logout();
                mDialog.dismiss();
                CommonUtils.showCustomToast(Toast.makeText(LoginActivity.this,
                        mMessage, Toast.LENGTH_LONG));
            }
            super.onPostExecute(result);
        }

    }

    /**
     * 设置hearder属性，方便通讯中对联系人按header分类显示，以及通过右侧ABCD...字母栏快速定位联系人
     *
     * @param username
     * @param user
     */
    protected void setUserHearder(String username, User user) {
        String headerName = null;
        if (!TextUtils.isEmpty(user.getNicKName())) {
            headerName = user.getNicKName();
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
    }

    // public void getUserList() {
    //
    // }

    // private class GetUserTask extends AsyncTask<String, Void, Boolean> {
    //
    // private String mMessage;
    //
    // @Override
    // protected void onPreExecute() {
    //
    // }
    //
    // @Override
    // protected void onPostExecute(Boolean result) {
    //
    // if (result) {
    //
    // } else {
    // // CommonUtils.showCustomToast(Toast.makeText(getActivity(),
    // // mMessage,
    // // Toast.LENGTH_LONG));
    // // if (mMessage.equals(GlobalVariables.ERRO_MESSAGE_RELOG)) {
    // // Intent intent = new Intent(getActivity(),
    // // LoginActivity.class);
    // // EPApplication.getInstance().setToken(null);
    // // EPApplication.getInstance().clearAcitivy();
    // // startActivity(intent);
    // // }
    // }
    // }
    //
    // @Override
    // protected Boolean doInBackground(String... paramin) {
    // // TODO Auto-generated method stub
    // List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
    // params.add(new BasicNameValuePair("action", "getUserFriendList"));
    // params.add(new BasicNameValuePair("user_name", EPSecretService
    // .encryptByPublic(EPApplication.getInstance().getUserName())));
    // params.add(new BasicNameValuePair("user_login_token",
    // EPSecretService.encryptByPublic(EPApplication.getInstance()
    // .getToken())));
    //
    // try {
    // String str = EPHttpService.customerPostString(
    // GlobalVariables.SERVICE_HOST_USER_SYSTEM, params);
    // Log.i(TAG, str);
    // JSONObject object = JSONObject.parseObject(str);
    // // mMessage = object.getString("message");
    // // boolean result = object.getBooleanValue("result");
    // return true;
    // } catch (Exception e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // mMessage = getString(R.string.message_weak_internet);
    // return false;
    // }
    // }
    // }
}
