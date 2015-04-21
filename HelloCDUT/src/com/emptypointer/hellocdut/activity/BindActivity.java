package com.emptypointer.hellocdut.activity;

import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.fragment.BindBasicFragment;
import com.emptypointer.hellocdut.fragment.BindCampusCardFragment;
import com.emptypointer.hellocdut.fragment.BindMailFragment;
import com.emptypointer.hellocdut.utils.GlobalVariables;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

public class BindActivity extends BaseActivity {
    private int mBindCategory;
    public static final int MODE_AAO = 1;
    public static final int MODE_CAMPUS_CARD = 2;
    public static final int MODE_LIB = 3;
    public static final int MODE_MAIL = 4;

    @Override
    protected void onCreate(Bundle arg0) {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        setContentView(R.layout.activity_support_fragment);
        Intent intent = getIntent();
        mBindCategory = intent.getIntExtra(GlobalVariables.INTENT_EXTRA_BIND_MODE, -1);
        FragmentManager mFragmentManager = getFragmentManager();
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        switch (mBindCategory) {

            case -1:
                transaction.commit();
                this.finish();
                break;
            case MODE_AAO:

                transaction.replace(R.id.fragment_beach, new BindBasicFragment(BindBasicFragment.ACTION_BIND_AAO));
                transaction.commit();
                break;
            case MODE_CAMPUS_CARD:
                transaction.replace(R.id.fragment_beach, new BindCampusCardFragment());
                transaction.commit();
                break;
            case MODE_LIB:
                transaction.replace(R.id.fragment_beach, new BindBasicFragment(BindBasicFragment.ACTION_BIND_LIB));
                transaction.commit();
                break;
            case MODE_MAIL:
                transaction.replace(R.id.fragment_beach, new BindMailFragment());
                transaction.commit();
                break;

            default:
                transaction.commit();
                this.finish();
                break;
        }
    }

}
