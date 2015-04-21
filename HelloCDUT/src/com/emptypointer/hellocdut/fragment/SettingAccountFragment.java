package com.emptypointer.hellocdut.fragment;

import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.utils.GlobalVariables;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class SettingAccountFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = View.inflate(getActivity(),
                R.layout.fragment_setting_account, null);
        view.findViewById(R.id.layout_modify_password).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        Intent intent = new Intent(
                                GlobalVariables.ACTION_MODIFY_PASSWORD);
                        startActivity(intent);
                    }
                });
        view.findViewById(R.id.layout_manage_ignore_list).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        Intent intent = new Intent(
                                GlobalVariables.ACTION_IGNORE);
                        startActivity(intent);
                    }
                });

        return view;
    }

}
