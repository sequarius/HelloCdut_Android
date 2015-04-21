package com.emptypointer.hellocdut.activity;

import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.fragment.AddGroupFragment;
import com.emptypointer.hellocdut.fragment.AddUserFragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class AddContactActivity extends BaseActivity {

    private static final String TAG = "AddContactActivity";
    private Fragment mAddUserFragment;
    private Fragment mAddGroupFragment;
    private FragmentManager mFragmentManager;
    private RadioGroup mRadioGroup;

    @Override
    protected void onCreate(Bundle arg0) {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        setContentView(R.layout.activity_add_contact);

        mAddUserFragment = new AddUserFragment();
        mAddGroupFragment = new AddGroupFragment();

        mFragmentManager = getFragmentManager();
        changeMode(mAddUserFragment);

        mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup_add_mode);
        mRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                switch (checkedId) {
                    case R.id.radio_add_user:
                        changeMode(mAddUserFragment);
                        break;
                    case R.id.radio_add_group:

                        changeMode(mAddGroupFragment);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    public void vedioBoxShow(boolean isShow) {
        if (isShow) {
            mRadioGroup.setVisibility(View.VISIBLE);
        } else {
            mRadioGroup.setVisibility(View.GONE);
        }
    }

    private void changeMode(Fragment fragment) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.layout_add_mode, fragment);
        transaction.commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (((AddUserFragment) mAddUserFragment).isInResult()) {
                ((AddUserFragment) mAddUserFragment).changeview(true);
                return true;
            } else {
                this.finish();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);

    }
}
