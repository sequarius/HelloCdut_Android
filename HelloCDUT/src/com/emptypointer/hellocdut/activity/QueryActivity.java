package com.emptypointer.hellocdut.activity;

import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.customer.EPApplication;
import com.emptypointer.hellocdut.utils.GlobalVariables;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

public class QueryActivity extends BaseActivity implements OnClickListener {

    @Override
    protected void onCreate(Bundle arg0) {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        setContentView(R.layout.activity_query);
        LinearLayout layoutGrade = (LinearLayout) findViewById(R.id.layout_query_grade);
        layoutGrade.setOnClickListener(this);
        LinearLayout layoutNationalExam = (LinearLayout) findViewById(R.id.layout_query_national_exam);
        layoutNationalExam.setOnClickListener(this);
        LinearLayout layoutClassRoom = (LinearLayout) findViewById(R.id.layout_query_class_room);
        layoutClassRoom.setOnClickListener(this);
        LinearLayout layoutTel = (LinearLayout) findViewById(R.id.layout_query_tel);
        layoutTel.setOnClickListener(this);
        LinearLayout layoutTeachPlane = (LinearLayout) findViewById(R.id.layout_query_teach_plan);
        layoutTeachPlane.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.layout_query_grade:
                if (EPApplication.getInstance().getUserStatus() > EPApplication.USER_STATUS_NORMAL) {

                    intent.setAction(GlobalVariables.ACTION_QUERY_GRADE);
                } else {
                    createTObindDiglog(getString(R.string.str_aao), this);
                }

                break;
            case R.id.layout_query_national_exam:
                intent.setAction(GlobalVariables.ACTION_QUERY_NATIONAL_EXAM);
                break;
            case R.id.layout_query_class_room:
                if (EPApplication.getInstance().getUserStatus() > EPApplication.USER_STATUS_NORMAL) {

                    intent.setAction(GlobalVariables.ACTION_QUERY_CLASS_ROOM);
                } else {
                    createTObindDiglog(getString(R.string.str_aao), this);
                }

                break;
            case R.id.layout_query_tel:
                intent.setAction(GlobalVariables.ACTION_QUERY_TEL);
                break;
            case R.id.layout_query_teach_plan:
                intent.setAction(GlobalVariables.ACTION_QUERY_TEACH_PLAN);
                break;


            default:
                break;
        }
        if (intent.getAction() != null) {
            startActivity(intent);
        }

    }

}
