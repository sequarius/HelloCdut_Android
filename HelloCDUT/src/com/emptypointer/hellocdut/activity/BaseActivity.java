package com.emptypointer.hellocdut.activity;


import android.app.ActionBar;
import android.app.AlertDialog.Builder;
import android.app.Notification.Action;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;

import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.utils.CommonUtils;
import com.emptypointer.hellocdut.utils.GlobalVariables;

public class BaseActivity extends NoBackBasaActivity {


    @Override
    protected void onCreate(Bundle arg0) {
        // TODO Auto-generated method stub

        super.onCreate(arg0);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        int upid = Resources.getSystem().getIdentifier("up", "id", "android");
        ImageView returnImage = (ImageView) findViewById(upid);
        returnImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_actionbar_return));


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void createTObindDiglog(String BindType, Context context) {
        Builder builder = new Builder(context);
        builder.setTitle(R.string.str_need_to_bind);
        builder.setMessage(getString(R.string.message_need_to_bind, BindType));
        builder.setPositiveButton(R.string.str_bind_immediately, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(GlobalVariables.ACTION_ACCOUNTMANAGE);
                startActivity(intent);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(R.string.str_bind_delay, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        });
        CommonUtils.dialogTitleLineColor(builder.show());
    }

}
