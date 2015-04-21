package com.emptypointer.hellocdut.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.easemob.chat.EMContactManager;
import com.easemob.exceptions.EaseMobException;
import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.activity.AddContactActivity;
import com.emptypointer.hellocdut.activity.ChatActivity;
import com.emptypointer.hellocdut.activity.GroupsActivity;
import com.emptypointer.hellocdut.activity.UserInfoCardActivity;
import com.emptypointer.hellocdut.adapter.ContactAdapter;
import com.emptypointer.hellocdut.customer.EPApplication;
import com.emptypointer.hellocdut.dao.InviteMessgeDao;
import com.emptypointer.hellocdut.dao.UserDao;
import com.emptypointer.hellocdut.domain.User;
import com.emptypointer.hellocdut.utils.CommonUtils;
import com.emptypointer.hellocdut.utils.GlobalVariables;
import com.emptypointer.hellocdut.widget.SideBar;

public class ContacatsFragment extends Fragment {

    private ContactAdapter adapter;
    private List<User> contactList;
    private ListView listView;
    private boolean hidden;
    private SideBar sidebar;
    private InputMethodManager inputMethodManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contact_list, container,
                false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        inputMethodManager = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        listView = (ListView) getView().findViewById(R.id.list);
        sidebar = (SideBar) getView().findViewById(R.id.sidebar);
        sidebar.setListView(listView);
        contactList = new ArrayList<User>();
        // 获取设置contactlist
        getContactList();
        // 设置adapter
        adapter = new ContactAdapter(getActivity(), R.layout.row_contact,
                contactList, sidebar);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String username = adapter.getItem(position).getUsername();
                if (GlobalVariables.NEW_FRIENDS_USERNAME.equals(username)) {
                    // 进入申请与通知页面
                    User user = EPApplication.getInstance().getContactList()
                            .get(GlobalVariables.NEW_FRIENDS_USERNAME);
                    user.setUnreadMsgCount(0);
                    startActivity(new Intent(
                            GlobalVariables.ACTION_INVITATION_MANAGE));
                } else if (GlobalVariables.GROUP_USERNAME.equals(username)) {
                    // 进入群聊列表页面
                    startActivity(new Intent(getActivity(),
                            GroupsActivity.class));
                } else {
                    // demo中直接进入聊天页面，实际一般是进入用户详情页
                    startActivity(new Intent(getActivity(),
                            UserInfoCardActivity.class).putExtra(
                            UserInfoCardActivity.ENTENT_EXTRA_USER_NAME,
                            adapter.getItem(position).getUsername()).putExtra(
                            UserInfoCardActivity.ENTENT_EXTRA_OPENMODE, true));
                }
            }
        });
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

        // ImageView addContactView = (ImageView)
        // getView().findViewById(R.id.iv_new_contact);
        // // 进入添加好友页
        // addContactView.setOnClickListener(new OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        // startActivity(new Intent(getActivity(), AddContactActivity.class));
        // }
        // });
        registerForContextMenu(listView);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        // 长按前两个不弹menu
        if (((AdapterContextMenuInfo) menuInfo).position > 1) {
            getActivity().getMenuInflater().inflate(
                    R.menu.context_contact_list, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete_contact) {
            User tobeDeleteUser = adapter
                    .getItem(((AdapterContextMenuInfo) item.getMenuInfo()).position);
            // 删除此联系人
            deleteContact(tobeDeleteUser);
            // 删除相关的邀请消息
            InviteMessgeDao dao = new InviteMessgeDao(getActivity());
            dao.deleteMessage(tobeDeleteUser.getUsername());
            return true;
        } else if (item.getItemId() == R.id.add_to_blacklist) {
            User user = adapter.getItem(((AdapterContextMenuInfo) item
                    .getMenuInfo()).position);
            moveToBlacklist(user.getUsername());
            return true;
        }
        return super.onContextItemSelected(item);
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

    /**
     * 删除联系人
     *
     * @param toDeleteUser
     */
    public void deleteContact(final User tobeDeleteUser) {
        final ProgressDialog pd = new ProgressDialog(getActivity());
        pd.setMessage("正在删除...");
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        new Thread(new Runnable() {
            public void run() {
                try {
                    EMContactManager.getInstance().deleteContact(
                            tobeDeleteUser.getUsername());
                    // 删除db和内存中此用户的数据
                    UserDao dao = new UserDao(getActivity());
                    dao.deleteContact(tobeDeleteUser.getUsername());
                    EPApplication.getInstance().getContactList()
                            .remove(tobeDeleteUser.getUsername());
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            pd.dismiss();
                            adapter.remove(tobeDeleteUser);
                            adapter.notifyDataSetChanged();

                        }
                    });
                } catch (final Exception e) {
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            pd.dismiss();
                            CommonUtils.showCustomToast(Toast.makeText(getActivity(),
                                    "删除失败: " + e.getMessage(), 1));
                        }
                    });

                }

            }
        }).start();

    }

    /**
     * 把user移入到黑名单
     */
    private void moveToBlacklist(final String username) {
        final ProgressDialog pd = new ProgressDialog(getActivity());
        pd.setMessage("正在移入黑名单...");
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        new Thread(new Runnable() {
            public void run() {
                try {
                    // 加入到黑名单
                    EMContactManager.getInstance().addUserToBlackList(username,
                            true);
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            pd.dismiss();
                            CommonUtils.showCustomToast(Toast.makeText(getActivity(), "移入黑名单成功", 0));
                            UserDao dao = new UserDao(getActivity());
                            dao.deleteContact(username);
                            EPApplication.getInstance().getContactList()
                                    .remove(username);
                            refresh();
                        }
                    });


                    ;
                } catch (EaseMobException e) {
                    e.printStackTrace();
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            pd.dismiss();
                            CommonUtils.showCustomToast(Toast.makeText(getActivity(), "移入黑名单失败", 0));
                        }
                    });
                }
            }
        }).start();

    }

    // 刷新ui
    public void refresh() {
        try {
            // 可能会在子线程中调到这方法
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    getContactList();
                    adapter.notifyDataSetChanged();

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getContactList() {
        contactList.clear();
        Map<String, User> users = EPApplication.getInstance().getContactList();
        Iterator<Entry<String, User>> iterator = users.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<String, User> entry = iterator.next();
            if (!entry.getKey().equals(GlobalVariables.NEW_FRIENDS_USERNAME)
                    && !entry.getKey().equals(GlobalVariables.GROUP_USERNAME))
                contactList.add(entry.getValue());
        }
        // 排序
        Collections.sort(contactList, new Comparator<User>() {

            @Override
            public int compare(User lhs, User rhs) {
                return lhs.getHeader().compareTo(rhs.getHeader());
            }
        });

        // 加入"申请与通知"和"群聊"
        contactList.add(0, users.get(GlobalVariables.GROUP_USERNAME));
        // 把"申请与通知"添加到首位
        contactList.add(0, users.get(GlobalVariables.NEW_FRIENDS_USERNAME));
    }

}
