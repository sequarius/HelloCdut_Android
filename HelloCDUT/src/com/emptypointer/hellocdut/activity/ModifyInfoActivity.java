package com.emptypointer.hellocdut.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.easemob.chat.EMContactManager;
import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.customer.EPApplication;
import com.emptypointer.hellocdut.domain.UserInfo;
import com.emptypointer.hellocdut.fragment.AddUserFragment;
import com.emptypointer.hellocdut.utils.CommonUtils;
import com.emptypointer.hellocdut.utils.GlobalVariables;

public class ModifyInfoActivity extends BaseActivity {
    protected static final String TAG = "ModifyInfoActivity";
    private EditText mEditText;
    public int mModifyMode = Integer.MIN_VALUE;
    private static final int MODIFY_NICKNAME = 1;
    private static final int MODIFY_MOTTO = 2;
    private static final int MODIFY_VERIFY = 3;
    private String mCurrentNickName;
    private String mCurrentMetto;
    private String mContactName;

    @Override
    protected void onCreate(Bundle arg0) {
        // TODO Auto-generated method stub
        Intent intent = getIntent();
        String categroy = intent
                .getStringExtra(GlobalVariables.INTENT_EXTRA_MODIFY);
        super.onCreate(arg0);

        setContentView(R.layout.activity_modify_user_info);
        mEditText = (EditText) findViewById(R.id.editText_modify);
        ActionBar actionBar = getActionBar();
        if (categroy.equals(UserInfoActivity.USER_NICK_NAME)) {
            mEditText.setHint(getString(R.string.str_hint_modify_nickname));
            mEditText
                    .setFilters(new InputFilter[]{new InputFilter.LengthFilter(
                            16)});
            mCurrentNickName = EPApplication.getInstance().getNickName();
            mEditText.setText(mCurrentNickName);
            mEditText.setSelection(mCurrentNickName.length());
            actionBar.setTitle(getString(R.string.str_nick_name));
            mModifyMode = MODIFY_NICKNAME;
        } else if (categroy.equals(UserInfoActivity.USER_MOTTO)) {
            mEditText.setHint(getString(R.string.str_hint_modify_motto));
            actionBar.setTitle(getString(R.string.str_motto));
            mCurrentMetto = UserInfo.getInstance(getApplicationContext())
                    .getMetto();
            mEditText.setText(mCurrentMetto);
            mEditText.setSelection(mCurrentMetto.length());
            mEditText
                    .setFilters(new InputFilter[]{new InputFilter.LengthFilter(
                            70)});
            mModifyMode = MODIFY_MOTTO;
        } else if (categroy.equals(AddUserFragment.USER_TO_VERIFY)) {
            mContactName = getIntent().getStringExtra(
                    AddUserFragment.USER_TO_VERIFY);
            mEditText.setHint(getString(R.string.str_hint_verify_contact));
            actionBar.setTitle(getString(R.string.str_verify_contact));
            mEditText.setText(getString(R.string.str_format_i_am, EPApplication
                    .getInstance().getNickName()));
            mEditText.setSelection(2, mEditText.getText().toString().length());
            mModifyMode = MODIFY_VERIFY;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu arg0) {
        // TODO Auto-generated method stub
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.modify_user_info, arg0);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        if (item.getItemId() == R.id.button_confirm
                && mModifyMode != MODIFY_VERIFY) {
            changeInfo();
        } else {
            new addContactThread().start();
        }
        return super.onOptionsItemSelected(item);
    }

    private void changeInfo() {
        String text = mEditText.getText().toString();
        if (mModifyMode == MODIFY_NICKNAME) {
            if (text.length() > 16) {
                CommonUtils.showCustomToast(Toast.makeText(getApplicationContext(),
                                R.string.message_wrong_input_lengh, Toast.LENGTH_LONG)
                );
                return;
            }
            if (text.equals(mCurrentNickName)) {
                this.setResult(RESULT_CANCELED);
                this.finish();
                return;
            }
        } else if (mModifyMode == MODIFY_MOTTO) {
            if (text.length() > 70) {
                CommonUtils.showCustomToast(Toast.makeText(getApplicationContext(),
                                R.string.message_wrong_input_lengh, Toast.LENGTH_LONG)
                );
                return;
            }
            if (text.equals(mCurrentMetto)) {
                this.setResult(RESULT_CANCELED);
                this.finish();
                return;
            }
        } else {
            return;
        }
        Intent intent = new Intent();
        // 把返回数据存入Intent
        intent.putExtra(UserInfoActivity.RESULT_INTENT, text);
        // 设置返回数据
        this.setResult(RESULT_OK, intent);
        // 关闭Activity
        this.finish();

    }

    private class addContactThread extends Thread {

        @Override
        public void run() {
            try {
                // demo写死了个reason，实际应该让用户手动填入

                EMContactManager.getInstance().addContact(mContactName,
                        mEditText.getText().toString());
                runOnUiThread(new Runnable() {
                    public void run() {
                        // progressDialog.dismiss();
                        CommonUtils.showCustomToast(Toast.makeText(ModifyInfoActivity.this,
                                "发送请求成功,等待对方验证", Toast.LENGTH_SHORT));
                    }
                });
                ModifyInfoActivity.this.finish();
            } catch (final Exception e) {
                // runOnUiThread(new Runnable() {
                // public void run() {
                // progressDialog.dismiss();
                // Toast.makeText(getApplicationContext(), "请求添加好友失败:" +
                // e.getMessage(), 1).show();
                // }
                // });
                e.printStackTrace();
            }
        }

    }

}
