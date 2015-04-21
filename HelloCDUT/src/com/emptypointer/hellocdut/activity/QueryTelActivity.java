package com.emptypointer.hellocdut.activity;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.adapter.QueryTelAdapter;
import com.emptypointer.hellocdut.domain.CallLogItem;
import com.emptypointer.hellocdut.utils.CommonUtils;
import com.emptypointer.hellocdut.utils.StringChecker;

public class QueryTelActivity extends BaseActivity implements OnClickListener {
    private EditText mEtTelNum;
    private Button mBtnCommit;
    private ImageView mImCallLog;
    private static final String DESTINATION_ADDRESS = "10086086";
    private List<CallLogItem> mItems;
    private boolean isGetted = false;
    private int CALL_LOG_MAX_COUNT = 15;

    @Override
    protected void onCreate(Bundle arg0) {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        setContentView(R.layout.activity_query_tel);
        mEtTelNum = (EditText) findViewById(R.id.editText_query_tel_number);
        mBtnCommit = (Button) findViewById(R.id.button_query_tel_commit);
        mImCallLog = (ImageView) findViewById(R.id.imageView_open_call_log);
        mBtnCommit.setOnClickListener(this);
        mItems = new ArrayList<CallLogItem>();
        mImCallLog.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.button_query_tel_commit:

                querying();
                break;
            case R.id.imageView_open_call_log:
                if (!isGetted) {
                    getCallRecords();
                    isGetted = true;
                }
                showDialog();
                break;
            default:
                break;
        }
    }

    private void querying() {
        String strInput = mEtTelNum.getText().toString();
        if (StringChecker.isShortNum(strInput)
                || StringChecker.isPhoneNum(strInput)) {
            SmsManager smsManager = SmsManager.getDefault();

            smsManager.sendTextMessage(DESTINATION_ADDRESS, null, "CX"
                    + strInput, null, null);

            CommonUtils.showCustomToast(Toast.makeText(this,
                    getString(R.string.message_query_tel_sent_sucess_toast),
                    Toast.LENGTH_SHORT));
        } else {
            mEtTelNum.setText("");
            CommonUtils.showCustomToast(Toast.makeText(this,
                    getString(R.string.message_query_tel_wrong_number_toast),
                    Toast.LENGTH_LONG));
        }
    }

    private void getCallRecords() {
        // 查询通话记录
        ContentResolver cr = getContentResolver();
        final Cursor cursor = cr.query(CallLog.Calls.CONTENT_URI,
                new String[]{CallLog.Calls.NUMBER, CallLog.Calls.CACHED_NAME,
                        CallLog.Calls.TYPE, CallLog.Calls.DATE,
                        CallLog.Calls.DURATION}, null, null,
                CallLog.Calls.DEFAULT_SORT_ORDER);
        for (int i = 0; i < cursor.getCount()
                && mItems.size() < CALL_LOG_MAX_COUNT; i++) {
            cursor.moveToPosition(i);
            String number = cursor.getString(0);// 电话号码
            String name = cursor.getString(1);// 名字
            int type = cursor.getInt(2);// 类型
            long calltime = Long.parseLong(cursor.getString(3));// 打电话的时间
            long duration = cursor.getLong(4);
            SimpleDateFormat sfd = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date date = new Date(calltime);// 打电话的日期
            String sCallTime = sfd.format(date);
            CallLogItem item = new CallLogItem(number, name, sCallTime,
                    duration, type);
            if (!mItems.contains(item)) {
                mItems.add(item);
            }
        }
        cursor.close();
    }

    private void showDialog() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        builderSingle.setTitle(getString(R.string.str_call_log));
        QueryTelAdapter adapter = new QueryTelAdapter(mItems, this);
        builderSingle.setNegativeButton(getString(R.string.cancel),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builderSingle.setAdapter(adapter,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mEtTelNum.setText(mItems.get(which).getNumber());
                        ;
                    }
                });
        CommonUtils.dialogTitleLineColor(builderSingle.show());
    }

}
