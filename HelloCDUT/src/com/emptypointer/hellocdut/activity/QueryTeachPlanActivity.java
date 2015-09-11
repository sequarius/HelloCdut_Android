package com.emptypointer.hellocdut.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;


import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.customer.EPApplication;
import com.emptypointer.hellocdut.customer.EPCommonAdapter;
import com.emptypointer.hellocdut.customer.EPCommonViewHolder;
import com.emptypointer.hellocdut.domain.TeachingPlan;
import com.emptypointer.hellocdut.domain.UserInfo;
import com.emptypointer.hellocdut.service.EPJsonHttpResponseHandler;
import com.emptypointer.hellocdut.service.EPSecretService;
import com.emptypointer.hellocdut.utils.CommonUtils;
import com.emptypointer.hellocdut.utils.GlobalVariables;
import com.emptypointer.hellocdut.widget.ClearableEditText;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class QueryTeachPlanActivity extends BaseActivity {

    public static final String INTENT_PDF_URL="url";
    public static final String INTENT_NAME="name";


    private com.emptypointer.hellocdut.widget.ClearableEditText etKeyword;
    private android.widget.Button btnSearch;
    private android.widget.LinearLayout layoutsearch;
    private android.widget.ListView lvResult;
    private List<TeachingPlan> mTeachingPlan;
    private BaseAdapter adapter;

    @Override
    protected void onCreate(Bundle arg0) {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        setContentView(R.layout.activity_query_teach_plan);
        this.lvResult = (ListView) findViewById(R.id.lvResult);
        this.layoutsearch = (LinearLayout) findViewById(R.id.layout_search);
        this.btnSearch = (Button) findViewById(R.id.btnSearch);
        this.etKeyword = (ClearableEditText) findViewById(R.id.etKeyword);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadDataFromServer();
            }
        });
        mTeachingPlan = new ArrayList<>();
        adapter = new EPCommonAdapter<TeachingPlan>(this, R.layout.row_teaching_plan, mTeachingPlan) {

            @Override
            public void convert(EPCommonViewHolder holder, TeachingPlan item) {
                holder.setText(R.id.tvName, item.getName()).setText(R.id.tvMajor, getString(R.string.str_format_major, item.getMajro())).setText(R.id.tvMaketime, getString(R.string.str_format_make_date, item.getMakedate()));
            }
        };
        lvResult.setAdapter(adapter);
        lvResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(QueryTeachPlanActivity.this, ReadTeachingPlanActivity.class);
                intent.putExtra(INTENT_NAME, mTeachingPlan.get(position).getName());
                intent.putExtra(INTENT_PDF_URL, mTeachingPlan.get(position).getUrl());
                startActivity(intent);
            }
        });
        UserInfo userInfo=UserInfo.getInstance(this);
        if(!userInfo.getMajorName().isEmpty()){
            etKeyword.setText(userInfo.getMajorName());
        }
    }

    private void loadDataFromServer() {
        String keyword = etKeyword.getText().toString();
        if (keyword.length() == 0) {
            CommonUtils.customToast(R.string.message_wrong_empty_keyword, this, true);
            return;
        }
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        requestParams.add("action", "queryTeachingPlan");
        requestParams.add("user_name", EPSecretService.encryptByPublic(EPApplication.getInstance().getUserName()));
        requestParams.add("user_login_token", EPSecretService.encryptByPublic(EPApplication.getInstance().getToken()));
        requestParams.add("key_words", keyword);
        client.post(GlobalVariables.SERVICE_HOST_ADDONES, requestParams, new EPJsonHttpResponseHandler(this, true) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                mTeachingPlan.clear();
                if (result) {
                    try {
                        JSONArray array = response.getJSONArray("pdf_list");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            String name = object.getString("name");
                            String url = object.getString("url");
                            String major = object.getString("major");
                            long id = object.getLong("id");
                            String makeDate = object.getString("make_date");
                            mTeachingPlan.add(new TeachingPlan(name, major, makeDate, id, url));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
    }
}
