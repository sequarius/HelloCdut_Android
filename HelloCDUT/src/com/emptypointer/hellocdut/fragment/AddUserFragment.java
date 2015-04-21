package com.emptypointer.hellocdut.fragment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.http.message.BasicNameValuePair;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.easemob.chat.EMContactManager;
import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.activity.AddContactActivity;
import com.emptypointer.hellocdut.activity.LoginActivity;
import com.emptypointer.hellocdut.activity.ModifyInfoActivity;
import com.emptypointer.hellocdut.adapter.UserInfoAdapter;
import com.emptypointer.hellocdut.customer.EPApplication;
import com.emptypointer.hellocdut.service.EPHttpService;
import com.emptypointer.hellocdut.service.EPSecretService;
import com.emptypointer.hellocdut.utils.CommonUtils;
import com.emptypointer.hellocdut.utils.GlobalVariables;

public class AddUserFragment extends Fragment implements OnClickListener {
    private Button mBtnSearch, mBtnReturn, mBtnAdd;
    private EditText mEtUserAcount;
    public final static String USER_TO_VERIFY = "user_verify";
    public static final String TAG = "AddUserFragment";
    private LinearLayout mLayoutSerch, mLayoutResult;
    private boolean isInResult = false;
    private List<String> mListKey;
    private List<String> mListValue;
    private ListView mListView;
    private UserInfoAdapter mAapter;
    private ImageView mImageView;

    public boolean isInResult() {
        return isInResult;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = View.inflate(getActivity(), R.layout.fragment_add_user,
                null);
        mBtnSearch = (Button) view.findViewById(R.id.button_search);
        mBtnReturn = (Button) view.findViewById(R.id.button_return);
        mBtnAdd = (Button) view.findViewById(R.id.button_add);
        mEtUserAcount = (EditText) view.findViewById(R.id.editText_user_name);
        mListKey = new ArrayList<String>();
        mListValue = new ArrayList<String>();

        mLayoutResult = (LinearLayout) view.findViewById(R.id.layout_result);
        mLayoutSerch = (LinearLayout) view.findViewById(R.id.layout_search);

        mListView = (ListView) view.findViewById(R.id.listView_user_info);

        mImageView = (ImageView) view.findViewById(R.id.imageView_avatar);

        mAapter = new UserInfoAdapter(mListKey, mListValue, getActivity());

        mListView.setAdapter(mAapter);

        mBtnSearch.setOnClickListener(this);
        mBtnReturn.setOnClickListener(this);
        mBtnAdd.setOnClickListener(this);
        return view;
    }

    /**
     * 切换查询和结果的view
     *
     * @param isInput
     */
    public void changeview(boolean isInput) {

        if (isInput) {
            ((AddContactActivity) getActivity()).vedioBoxShow(true);
            mLayoutResult.setVisibility(View.GONE);
            mLayoutSerch.setVisibility(View.VISIBLE);
            isInResult = false;
        } else {
            ((AddContactActivity) getActivity()).vedioBoxShow(false);
            mLayoutResult.setVisibility(View.VISIBLE);
            mLayoutSerch.setVisibility(View.GONE);
            isInResult = true;
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.button_search:
                searchUser();

                break;
            case R.id.button_return:
                changeview(true);
                break;
            case R.id.button_add:
                addContact();
                changeview(true);
                break;

            default:
                break;
        }

    }

    private void searchUser() {
        if (mEtUserAcount.getText().toString()
                .equals(EPApplication.getInstance().getUserName())) {
            CommonUtils.showCustomToast(Toast.makeText(getActivity(),
                    getString(R.string.message_wrong_add_self),
                    Toast.LENGTH_LONG));
            return;
        }
        if (EPApplication.getInstance().getContactList()
                .containsKey(mEtUserAcount.getText().toString())) {
            CommonUtils.showCustomToast(Toast.makeText(getActivity(),
                    getString(R.string.message_wrong_add_exist_contact),
                    Toast.LENGTH_LONG));
            ;
            return;
        }
        new QueryUserTask().execute();

    }

    public void addContact() {
        // if(DemoApplication.getInstance().getUserName().equals(nameText.getText().toString())){
        // startActivity(new Intent(this, AlertDialog.class).putExtra("msg",
        // "不能添加自己"));
        // return;
        // }
        //
        // if(DemoApplication.getInstance().getContactList().containsKey(nameText.getText().toString())){
        // startActivity(new Intent(this, AlertDialog.class).putExtra("msg",
        // "此用户已是你的好友"));
        // return;
        // }

        // progressDialog = new ProgressDialog(this);
        // progressDialog.setMessage("正在发送请求...");
        // progressDialog.setCanceledOnTouchOutside(false);
        // progressDialog.show();
        Intent intent = new Intent(getActivity(), ModifyInfoActivity.class);
        intent.putExtra(GlobalVariables.INTENT_EXTRA_MODIFY, USER_TO_VERIFY);
        intent.putExtra(USER_TO_VERIFY, mEtUserAcount.getText().toString());
        startActivity(intent);
        changeview(true);

        // new Thread(new Runnable() {
        // public void run() {
        //
        // try {
        // // demo写死了个reason，实际应该让用户手动填入
        //
        // EMContactManager.getInstance().addContact(
        // mEtUserAcount.getText().toString(), "test");
        // getActivity().runOnUiThread(new Runnable() {
        // public void run() {
        // // progressDialog.dismiss();
        // Toast.makeText(getActivity(), "发送请求成功,等待对方验证",
        // Toast.LENGTH_SHORT).show();
        // }
        // });
        // } catch (final Exception e) {
        // // runOnUiThread(new Runnable() {
        // // public void run() {
        // // progressDialog.dismiss();
        // // Toast.makeText(getApplicationContext(), "请求添加好友失败:" +
        // // e.getMessage(), 1).show();
        // // }
        // // });
        // e.printStackTrace();
        // }
        // }
        // }).start();
    }

    private class QueryUserTask extends AsyncTask<Void, Void, Boolean> {
        private ProgressDialog mDialog;
        private String mMessage;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            mDialog = new ProgressDialog(getActivity());
            mDialog.setMessage(getString(R.string.str_str_loading_search));
            mDialog.setCanceledOnTouchOutside(false);

            mDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... param) {

            // TODO Auto-generated method stub
            List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
            String userName = EPSecretService.encryptByPublic(EPApplication
                    .getInstance().getUserName());
            String token = EPSecretService.encryptByPublic(EPApplication
                    .getInstance().getToken());
            params.add(new BasicNameValuePair("user_login_token", token));
            params.add(new BasicNameValuePair("user_name", userName));
            params.add(new BasicNameValuePair("action", "queryUser"));
            params.add(new BasicNameValuePair("key_words", mEtUserAcount
                    .getText().toString()));
            params.add(new BasicNameValuePair("query_by", "1"));
            params.add(new BasicNameValuePair("query_type", "1"));

            try {
                String str = EPHttpService.customerPostString(
                        GlobalVariables.SERVICE_HOST_USER_SYSTEM, params);
                JSONObject object = JSONObject.parseObject(str);
                boolean result = object.getBoolean("result");
                if (!result) {
                    mMessage = object.getString("message");
                    return false;
                }
                Map<String, String> map = JSON.parseObject(object.getJSONObject("user_info").toJSONString(),
                        new TypeReference<LinkedHashMap<String, String>>() {
                        });

                Set<Entry<String, String>> entrySet = map.entrySet();

//				Set<String> set = map.keySet();
//				for (String string : set) {
//					Log.i(TAG, string);
//				}

                // Set<Entry<String, Object>> entrySet = object.entrySet();
                Iterator<Entry<String, String>> iterator = entrySet.iterator();
                mListKey.clear();
                mListValue.clear();
                while (iterator.hasNext()) {
                    Map.Entry<String, String> me = iterator.next();
                    String key = me.getKey();
                    if (key.equals("result")) {
                        mMessage = map.get("message");
                        return false;
                    }
                    String value = (String) me.getValue();
                    if (key.equals("user_avatar_url")) {
                        if (value != null) {
                            new getImageThread(value).start();
                        }
                        // TODO avatar;
                    } else {

                        mListKey.add(key);
                        mListValue.add(value);
                    }
                }

                return true;

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                mMessage = getString(R.string.message_weak_internet);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            // TODO Auto-generated method stub
            mDialog.dismiss();
            if (result) {
                changeview(false);
                mAapter.notifyDataSetChanged();
            } else {
                CommonUtils.showCustomToast(Toast.makeText(getActivity(), mMessage, Toast.LENGTH_LONG)
                );

                if (mMessage.equals(GlobalVariables.ERRO_MESSAGE_RELOG)) {
                    Intent intent = new Intent(getActivity(),
                            LoginActivity.class);
                    EPApplication.getInstance().logout();
                    EPApplication.getInstance().clearAcitivy();
                    startActivity(intent);
                }
            }
        }
    }

    private class getImageThread extends Thread {
        private String imgeURL;

        public getImageThread(String imgeURL) {
            super();
            this.imgeURL = imgeURL;
        }

        @Override
        public void run() {
            try {
                final Bitmap bitmap = EPHttpService.customerGetBitmap(imgeURL,
                        null);
                if (bitmap != null) {
                    getActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            mImageView.setImageBitmap(bitmap);
                        }
                    });
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        CommonUtils.showCustomToast(Toast.makeText(getActivity(),
                                getString(R.string.message_weak_internet),
                                Toast.LENGTH_SHORT));
                    }
                });
            }
        }

    }
}
