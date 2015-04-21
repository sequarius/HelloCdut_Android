package com.emptypointer.hellocdut.fragment;

import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.utils.CommonUtils;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class AddGroupFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = View.inflate(getActivity(), R.layout.fragment_add_group,
                null);
        ((Button) view.findViewById(R.id.button_query))
                .setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        CommonUtils.showCustomToast(Toast.makeText(
                                getActivity(), "自定义群组功能将在正式版开放！",
                                Toast.LENGTH_LONG));
                    }
                });
        return view;
    }
}
