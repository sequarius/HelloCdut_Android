package com.emptypointer.hellocdut.task;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import com.alibaba.fastjson.JSONObject;
import com.emptypointer.hellocdut.customer.EPApplication;
import com.emptypointer.hellocdut.dao.NetQueueDao;
import com.emptypointer.hellocdut.domain.NetQueueItem;
import com.emptypointer.hellocdut.service.EPHttpService;
import com.emptypointer.hellocdut.service.EPSecretService;
import com.emptypointer.hellocdut.utils.GlobalVariables;

import android.content.Context;
import android.os.AsyncTask;

/**
 * 异步网络请求类
 *
 * @author Sequarius
 */

public class UpdateUserInfoTask extends AsyncTask<NetQueueItem, Void, Void> {
    private EPApplication application = EPApplication.getInstance();
    private Context mContext;

    /**
     * 参数1 action 参数2 value 参数3 permission;
     */
    @Override
    protected Void doInBackground(NetQueueItem... params) {
        // TODO Auto-generated method stub
        String action = params[0].getAction();
        String value = params[0].getValue();
        String permission = params[2].getPermission();
        String _username = EPSecretService.encryptByPublic(application.getUserName());
        String _token = EPSecretService.encryptByPublic(application.getToken());
        List<BasicNameValuePair> nameParams = new ArrayList<BasicNameValuePair>();
        nameParams.add(new BasicNameValuePair("user_name", _username));
        nameParams.add(new BasicNameValuePair("user_login_token", _token));
        nameParams.add(new BasicNameValuePair("action", "modifyUserInfo"));
        nameParams.add(new BasicNameValuePair("modify_category", action));
        nameParams.add(new BasicNameValuePair("modify_value", value));
        nameParams.add(new BasicNameValuePair("modify_permission", permission));

        try {
            String str = EPHttpService.customerPostString(GlobalVariables.SERVICE_HOST_USER_SYSTEM, nameParams);
            JSONObject jsonObject = JSONObject.parseObject(str);
            boolean result = jsonObject.getBooleanValue("result");
            if (result) {
//				NetQueueDao dao=new NetQueueDao(mContext);
//				dao.updateStatus(params[0].getId());
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public UpdateUserInfoTask(Context mContext) {
        super();
        this.mContext = mContext;
    }


}
