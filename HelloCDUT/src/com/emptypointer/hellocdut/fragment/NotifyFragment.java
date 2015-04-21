package com.emptypointer.hellocdut.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContact;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.activity.ChatActivity;
import com.emptypointer.hellocdut.activity.MainActivity;
import com.emptypointer.hellocdut.adapter.NotifyAdapter;
import com.emptypointer.hellocdut.customer.EPApplication;
import com.emptypointer.hellocdut.dao.AAONewsDao;
import com.emptypointer.hellocdut.dao.InviteMessgeDao;
import com.emptypointer.hellocdut.domain.AAOContact;
import com.emptypointer.hellocdut.domain.User;
import com.emptypointer.hellocdut.utils.CommonUtils;
import com.emptypointer.hellocdut.utils.GlobalVariables;

public class NotifyFragment extends Fragment {

    private InputMethodManager inputMethodManager;
    private ListView listView;
    private Map<String, User> contactList;
    private NotifyAdapter adapter;
    // private EditText query;
    // private ImageButton clearSearch;
    public RelativeLayout errorItem;
    public TextView errorText;
    private boolean hidden;
    private AAONewsDao mDao;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater
                .inflate(R.layout.fragment_notify_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        inputMethodManager = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        errorItem = (RelativeLayout) getView().findViewById(R.id.rl_error_item);
        errorText = (TextView) errorItem.findViewById(R.id.tv_connect_errormsg);
        // contact list
        mDao = new AAONewsDao(getActivity());
        contactList = EPApplication.getInstance().getContactList();
        listView = (ListView) getView().findViewById(R.id.list);
        adapter = new NotifyAdapter(getActivity(), 1, loadUsersWithRecentChat());
        // 设置adapter
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                EMContact emContact = adapter.getItem(position);
                if (adapter.getItem(position).getUsername()
                        .equals(EPApplication.getInstance().getUserName()))
                    CommonUtils.showCustomToast(Toast.makeText(getActivity(),
                            "不能和自己聊天", 0));
                else {
                    // 进入聊天页面
                    Intent intent = new Intent();
                    if (emContact instanceof EMGroup) {
                        // it is group chat
                        intent.setAction(GlobalVariables.ACTION_CHAT);
                        intent.putExtra("chatType", ChatActivity.CHATTYPE_GROUP);
                        intent.putExtra("groupId",
                                ((EMGroup) emContact).getGroupId());

                    } else if (emContact instanceof AAOContact) {
                        intent.setAction(GlobalVariables.ACTION_AAONEWS);
                    } else {
                        // it is single chat
                        intent.setAction(GlobalVariables.ACTION_CHAT);
                        intent.putExtra("userId", emContact.getUsername());
                    }
                    startActivity(intent);
                }
            }
        });
        // 注册上下文菜单
        registerForContextMenu(listView);

        listView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 隐藏软键盘
                if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
                    if (getActivity().getCurrentFocus() != null)
                        inputMethodManager.hideSoftInputFromWindow(
                                getActivity().getCurrentFocus()
                                        .getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                }
                return false;
            }
        });
        // // 搜索框
        // query = (EditText) getView().findViewById(R.id.query);
        // // 搜索框中清除button
        // clearSearch = (ImageButton)
        // getView().findViewById(R.id.search_clear);
        // query.addTextChangedListener(new TextWatcher() {
        // public void onTextChanged(CharSequence s, int start, int before,
        // int count) {
        //
        // adapter.getFilter().filter(s);
        // if (s.length() > 0) {
        // clearSearch.setVisibility(View.VISIBLE);
        // } else {
        // clearSearch.setVisibility(View.INVISIBLE);
        // }
        // }
        //
        // public void beforeTextChanged(CharSequence s, int start, int count,
        // int after) {
        // }
        //
        // public void afterTextChanged(Editable s) {
        // }
        // });
        // clearSearch.setOnClickListener(new OnClickListener() {
        // @Override
        // public void onClick(View v) {
        // query.getText().clear();
        //
        // }
        // });

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        if (!adapter.isAAOItem(((AdapterContextMenuInfo) menuInfo).position)) {
            getActivity().getMenuInflater()
                    .inflate(R.menu.delete_message, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete_message) {
            EMContact tobeDeleteUser = adapter
                    .getItem(((AdapterContextMenuInfo) item.getMenuInfo()).position);
            if (!(tobeDeleteUser instanceof AAOContact)) {
                boolean isGroup = false;
                if (tobeDeleteUser instanceof EMGroup)
                    isGroup = true;
                // 删除此会话
                EMChatManager.getInstance().deleteConversation(
                        tobeDeleteUser.getUsername(), isGroup);
                InviteMessgeDao inviteMessgeDao = new InviteMessgeDao(
                        getActivity());
                inviteMessgeDao.deleteMessage(tobeDeleteUser.getUsername());
                adapter.remove(tobeDeleteUser);
                adapter.notifyDataSetChanged();
                // 更新消息未读数
                ((MainActivity) getActivity()).updateUnreadLabel();
            }
            return true;
        }
        return super.onContextItemSelected(item);
    }

    /**
     * 刷新页面
     */
    public void refresh() {
        adapter = new NotifyAdapter(getActivity(), R.layout.row_chat_history,
                loadUsersWithRecentChat());
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    /**
     * 获取有聊天记录的users和groups
     *
     * @param context
     * @return
     */
    private List<EMContact> loadUsersWithRecentChat() {
        List<EMContact> resultList = new ArrayList<EMContact>();
        AAOContact aaoContact = new AAOContact();
        aaoContact.setUsername("教务新闻");
        resultList.add(aaoContact);
        // 获取有聊天记录的users，不包括陌生人
        // if (contactList == null) {
        contactList = EPApplication.getInstance().getContactList();
        // }
        for (User user : contactList.values()) {
            EMConversation conversation = EMChatManager.getInstance()
                    .getConversation(user.getUsername());
            if (conversation.getMsgCount() > 0) {
                resultList.add(user);
            }
        }
        for (EMGroup group : EMGroupManager.getInstance().getAllGroups()) {
            EMConversation conversation = EMChatManager.getInstance()
                    .getConversation(group.getGroupId());
            if (conversation.getMsgCount() > 0) {
                resultList.add(group);
            }

        }

        // 排序
        sortUserByLastChatTime(resultList);
        return resultList;
    }

    /**
     * 根据最后一条消息的时间排序
     *
     * @param usernames
     */
    private void sortUserByLastChatTime(List<EMContact> contactList) {
        Collections.sort(contactList, new Comparator<EMContact>() {
            @Override
            public int compare(final EMContact user1, final EMContact user2) {
                long time1 = 0;
                long time2 = time1;
                if (!(user1 instanceof AAOContact)) {
                    EMConversation conversation1 = EMChatManager.getInstance()
                            .getConversation(user1.getUsername());
                    EMMessage user1LastMessage = conversation1.getLastMessage();
                    time1 = user1LastMessage.getMsgTime();

                } else {
                    time1 = mDao.getTime();
                }
                if (!(user2 instanceof AAOContact)) {
                    EMConversation conversation2 = EMChatManager.getInstance()
                            .getConversation(user2.getUsername());
                    EMMessage user2LastMessage = conversation2.getLastMessage();
                    time2 = user2LastMessage.getMsgTime();

                } else {
                    time2 = mDao.getTime();
                }


                if (time1 == time2) {
                    return 0;
                } else if (time1 > time2) {
                    return -1;
                } else {
                    return 1;
                }
            }

        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        this.hidden = hidden;
        if (!hidden) {
            refresh();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!hidden) {
            refresh();
        }
    }

}
