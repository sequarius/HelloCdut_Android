package com.emptypointer.hellocdut.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.customer.EPApplication;
import com.emptypointer.hellocdut.domain.LibBooksItem;
import com.emptypointer.hellocdut.fragment.LibraryOnBorrowFragment;
import com.emptypointer.hellocdut.service.EPHttpService;
import com.emptypointer.hellocdut.service.EPSecretService;
import com.emptypointer.hellocdut.utils.CommonUtils;
import com.emptypointer.hellocdut.utils.GlobalVariables;

import org.apache.http.message.BasicNameValuePair;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LibBookOnBorrowAdapter extends LibBookHistoryAdapter {

    private LibraryOnBorrowFragment mFragment;

    public LibBookOnBorrowAdapter(List<LibBooksItem> mItems, Context mContext,
                                  LibraryOnBorrowFragment mFragment) {
        super(mItems, mContext);
        // TODO Auto-generated constructor stub
        this.mFragment = mFragment;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final LibBooksItem item = mItems.get(position);
        // TODO Auto-generated method stub
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_on_borrow, null);
        }

        final String strTitle = item.getTitle();
        ((TextView) convertView.findViewById(R.id.textView_title))
                .setText(strTitle);
        ((TextView) convertView.findViewById(R.id.textView_id))
                .setText(mContext.getString(R.string.str_format_book_id,
                        item.getId()));
        ((TextView) convertView.findViewById(R.id.textView_index_id))
                .setText(mContext.getString(R.string.str_format_book_index_id,
                        item.getIndexID()));
        ((TextView) convertView.findViewById(R.id.textView_borrow_time))
                .setText(mContext.getString(R.string.str_format_borrow_time,
                        item.getBorrowTime()));
        final TextView tvReturnTime = (TextView) convertView
                .findViewById(R.id.textView_return_time);
        tvReturnTime.setText(mContext.getString(
                R.string.str_format_return_time, item.getReturnTime()));
        ((TextView) convertView.findViewById(R.id.textView_location))
                .setText(mContext.getString(R.string.str_format_book_location,
                        item.getLocation()));
        ((TextView) convertView.findViewById(R.id.textView_renew_time))
                .setText(mContext.getString(R.string.str_format_renew_time,
                        item.getRenewTime()));
        SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd");
        Button btnReNew = (Button) convertView.findViewById(R.id.button_renew);
        try {
            long returnTimeMillis = fm.parse(item.getReturnTime()).getTime();
            if (returnTimeMillis < System.currentTimeMillis()) {
                btnReNew.setText(R.string.str_over_dead_line);
                btnReNew.setEnabled(false);
            } else {
                btnReNew
                        .setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                // TODO Auto-generated method stub
                                new NetTask().execute(item.getRenewURL(), tvReturnTime,
                                        strTitle);
                            }
                        });
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return convertView;
    }

    private class NetTask extends AsyncTask<Object, Void, Boolean> {
        private JSONObject imObject;
        private ProgressDialog imDialog;
        private TextView imTvReturn;
        private String imMessage;
        private String imTitle;

        @Override
        protected void onPreExecute() {
            imDialog = new ProgressDialog(mContext);
            imDialog.setCanceledOnTouchOutside(false);

            imDialog.setMessage(mContext.getString(R.string.str_on_renew_book));
            imDialog.show();

        }

        @Override
        protected Boolean doInBackground(Object... params1) {

            imTvReturn = (TextView) params1[1];
            imTitle = (String) params1[2];
            List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
            params.add(new BasicNameValuePair("user_name", EPSecretService
                    .encryptByPublic(EPApplication.getInstance().getUserName())));
            params.add(new BasicNameValuePair("user_login_token",
                    EPSecretService.encryptByPublic(EPApplication.getInstance()
                            .getToken())));
            params.add(new BasicNameValuePair("action", "queryLibInfo"));
            params.add(new BasicNameValuePair("flag", "4"));

            params.add(new BasicNameValuePair("renew_href", (String) params1[0]));

            try {
                String str = EPHttpService.customerPostString(
                        GlobalVariables.SERVICE_HOST_QUERYSYSTEM, params);

                imObject = JSONObject.parseObject(str);
                boolean result = imObject.getBooleanValue("result");
                imMessage = imObject.getString("message");
                return result;

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                imMessage = mContext.getString(R.string.message_weak_internet);
                return false;
            }

        }

        @Override
        protected void onPostExecute(Boolean result) {
            imDialog.dismiss();
            if (result) {
                String strRetrunTime = imObject.getString("back_time");
                imTvReturn.setText(mContext.getString(
                        R.string.str_format_return_time, strRetrunTime));
                CommonUtils.showCustomToast(Toast.makeText(
                                mContext,
                                mContext.getString(
                                        R.string.message_format_renew_book_finish,
                                        imTitle, strRetrunTime), Toast.LENGTH_SHORT)
                );

                mFragment.LoadDate();

            } else {
                CommonUtils.showCustomToast(Toast.makeText(mContext, imMessage, Toast.LENGTH_LONG));
            }
        }
    }

}
