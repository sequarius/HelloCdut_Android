package com.emptypointer.hellocdut.fragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.activity.CampusQueryResultActivity;
import com.emptypointer.hellocdut.activity.QueryCampusCardActivity;
import com.emptypointer.hellocdut.utils.CommonUtils;

import mirko.android.datetimepicker.date.DatePickerDialog;


public class CampusQueryFragment extends Fragment implements OnClickListener {
    public static final String[] INTENT_TYPE_QUERYDEPOSITINFO = {
            "queryDepositInfo", "queryBankInfo", "queryConsumeInfo",
            "queryCustStateInfo"};
    public static final String INTENT_ACTION = "aciton";
    public static final String INTENT_START_DATE = "start_time";
    public static final String INTENT_END_DATE = "end_time";
    private LinearLayout mLayoutStart, mLayoutEnd;
    private TextView mTvStart, mTvEnd;
    private Button mBtnCommit;
    private Spinner mSpType;
    private int mCurrentPick = -1;
    private static final int PICK_START = 0;
    private static final int PICK_END = 1;
    private static final long MILLIS_A_MOUNT = 2592000000L;

    private DatePickerDialog mDatePickerDialog;
    private Calendar mCalendar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.fragment_trade_query, null);
        mLayoutEnd = (LinearLayout) view.findViewById(R.id.layout_end_date);
        mLayoutStart = (LinearLayout) view.findViewById(R.id.layout_start_date);
        mBtnCommit = (Button) view.findViewById(R.id.button_search);
        mSpType = (Spinner) view.findViewById(R.id.spinner_type);
        mTvStart = (TextView) view.findViewById(R.id.textView_start_date);
        mTvEnd = (TextView) view.findViewById(R.id.textView_end_date);
        mLayoutEnd.setOnClickListener(this);
        mLayoutStart.setOnClickListener(this);
        mBtnCommit.setOnClickListener(this);

        mTvStart.setText(DateFormat.format("yyyy-MM-dd",
                System.currentTimeMillis() - MILLIS_A_MOUNT));
        mTvEnd.setText(DateFormat.format("yyyy-MM-dd",
                System.currentTimeMillis()));

        mCalendar = Calendar.getInstance();

        mDatePickerDialog = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePickerDialog datePickerDialog,
                                          int year, int month, int day) {
                        month++;
                        StringBuilder sb = new StringBuilder();
                        sb.append(year);
                        sb.append('-');
                        if (month < 10) {
                            sb.append('0');
                        }
                        sb.append(month);
                        sb.append('-');
                        if (day < 10) {
                            sb.append('0');
                        }
                        String date = sb.append(day).toString();
                        if (mCurrentPick == PICK_START) {
                            mTvStart.setText(date);
                        } else if (mCurrentPick == PICK_END) {
                            mTvEnd.setText(date);
                        }
                    }

                }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
                mCalendar.get(Calendar.DAY_OF_MONTH));

        return view;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.layout_end_date:
                mCurrentPick = PICK_END;
                if (!mDatePickerDialog.isVisible()) {
                    mDatePickerDialog.show(getActivity().getFragmentManager(),
                            String.valueOf(System.currentTimeMillis()));
                }
                break;
            case R.id.layout_start_date:
                mCurrentPick = PICK_START;
                if (!mDatePickerDialog.isVisible()) {
                    mDatePickerDialog.show(getActivity().getFragmentManager(),
                            String.valueOf(System.currentTimeMillis()));
                }
                break;
            case R.id.button_search:
                Date dateStart = null;
                Date dateEnd = null;
                String strEnd = mTvEnd.getText().toString();
                String strStart = mTvStart.getText().toString();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                try {
                    dateStart = dateFormat.parse(strStart);
                    dateEnd = dateFormat.parse(strEnd);
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if (dateStart.getTime() > dateEnd.getTime()) {
                    CommonUtils.showCustomToast(Toast.makeText(getActivity(), R.string.message_wrong_date_pick,
                            Toast.LENGTH_LONG));
                } else {

                    Intent intent = new Intent(getActivity(),
                            CampusQueryResultActivity.class);
                    int position = mSpType.getSelectedItemPosition();
                    intent.putExtra(INTENT_ACTION,
                            INTENT_TYPE_QUERYDEPOSITINFO[position]);
                    intent.putExtra(INTENT_START_DATE, strStart);
                    intent.putExtra(INTENT_END_DATE, strEnd);
                    startActivityForResult(intent, QueryCampusCardActivity.ACTIVITY_REQUEST_CODE);
                }
                break;

            default:
                break;
        }

    }

}
