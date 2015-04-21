package com.emptypointer.hellocdut.activity;

import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.customer.EPApplication;
import com.emptypointer.hellocdut.dao.InviteMessgeDao;
import com.emptypointer.hellocdut.dao.NewFriendsMsgAdapter;
import com.emptypointer.hellocdut.domain.InviteMessage;
import com.emptypointer.hellocdut.utils.GlobalVariables;

public class InvitationManageActivity extends BaseActivity {
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitation_manage);

        listView = (ListView) findViewById(R.id.listView_add_list);
        InviteMessgeDao dao = new InviteMessgeDao(this);
        List<InviteMessage> msgs = dao.getMessagesList();
//		//设置adapter
        NewFriendsMsgAdapter adapter = new NewFriendsMsgAdapter(this, 1, msgs);
        listView.setAdapter(adapter);
//		EPAppliaction.getInstance().getContactList().get(GlobalVariables.NEW_FRIENDS_USERNAME).setUnreadMsgCount(0);

    }

    public void back(View view) {
        finish();
    }

}
