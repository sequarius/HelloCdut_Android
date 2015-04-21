package com.emptypointer.hellocdut.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.adapter.ContactAdapter;
import com.emptypointer.hellocdut.customer.EPApplication;
import com.emptypointer.hellocdut.domain.User;
import com.emptypointer.hellocdut.utils.GlobalVariables;
import com.emptypointer.hellocdut.widget.SideBar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class PickContactNoCheckboxActivity extends BaseActivity {

    private ListView listView;
    private SideBar sidebar;
    protected ContactAdapter contactAdapter;
    private List<User> contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_contact_no_checkbox);
        listView = (ListView) findViewById(R.id.list);
        sidebar = (SideBar) findViewById(R.id.sidebar);
        sidebar.setListView(listView);
        contactList = new ArrayList<User>();
        // 获取设置contactlist
        getContactList();
        // 设置adapter
        contactAdapter = new ContactAdapter(this, R.layout.row_contact, contactList, sidebar);
        listView.setAdapter(contactAdapter);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onListItemClick(position);
            }
        });

    }

    protected void onListItemClick(int position) {
        if (position != 0) {
            setResult(RESULT_OK, new Intent().putExtra("username", contactAdapter.getItem(position)
                    .getUsername()));
            finish();
        }
    }

    public void back(View view) {
        finish();
    }

    private void getContactList() {
        contactList.clear();
        Map<String, User> users = EPApplication.getInstance().getContactList();
        Iterator<Entry<String, User>> iterator = users.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<String, User> entry = iterator.next();
            if (!entry.getKey().equals(GlobalVariables.NEW_FRIENDS_USERNAME) && !entry.getKey().equals(GlobalVariables.GROUP_USERNAME))
                contactList.add(entry.getValue());
        }
        // 排序
        Collections.sort(contactList, new Comparator<User>() {

            @Override
            public int compare(User lhs, User rhs) {
                return lhs.getUsername().compareTo(rhs.getUsername());
            }
        });
    }

}
