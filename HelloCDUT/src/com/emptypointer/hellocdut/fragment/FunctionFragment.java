package com.emptypointer.hellocdut.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.activity.AddOnesActivity;
import com.emptypointer.hellocdut.activity.MainActivity;
import com.emptypointer.hellocdut.adapter.FunctionGridAdapter;
import com.emptypointer.hellocdut.customer.EPApplication;
import com.emptypointer.hellocdut.domain.FunctionItem;
import com.emptypointer.hellocdut.utils.CommonUtils;
import com.emptypointer.hellocdut.utils.GlobalVariables;

import java.util.ArrayList;
import java.util.List;

public class FunctionFragment extends Fragment {
    private GridView mGridView;
    private List<FunctionItem> mItems;
    private FunctionGridAdapter mAdapter;
    private int[] mIconID = {R.drawable.ic_function_schedule,
            R.drawable.ic_function_search,
            R.drawable.ic_function_library, R.drawable.ic_functioncampus_card,
            R.drawable.ic_function_aad_more};
//    private int[] mIconID = {R.drawable.ic_function_schedule,
//            R.drawable.ic_function_search, R.drawable.ic_function_choos_class,
//            R.drawable.ic_function_trade, R.drawable.ic_function_forums,
//            R.drawable.ic_function_library, R.drawable.ic_functioncampus_card,
//            R.drawable.ic_function_aad_more};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = initView();
        initComponent();
        return view;
    }

    private void initComponent() {
        String[] strsFunctionName = getResources().getStringArray(
                R.array.str_array_main_function_name);
        mItems = new ArrayList<FunctionItem>();
        for (int i = 0; i < mIconID.length; i++) {
            mItems.add(new FunctionItem(mIconID[i], strsFunctionName[i]));
        }
        mAdapter = new FunctionGridAdapter(mItems, getActivity());
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(new ItemOnclickListener());
    }

    private View initView() {
        View view = View.inflate(getActivity(),
                R.layout.fragment_function_list, null);
        mGridView = (GridView) view.findViewById(R.id.gridview_function_list);
        return view;
    }

    /**
     * GridView 的事件函數
     *
     * @author Sequarius
     */
    private class ItemOnclickListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            Intent intent = new Intent();
            // TODO Auto-generated method stub
            switch (position) {
                case 0:
                    if (EPApplication.getInstance().getUserStatus() > EPApplication.USER_STATUS_NORMAL) {

                        intent.setAction(GlobalVariables.ACTION_SCHEDULE);
                        startActivity(intent);
                    } else {
                        ((MainActivity) getActivity()).createTObindDiglog(
                                getString(R.string.str_aao), getActivity());
                    }

                    break;
                case 1:
                    intent.setAction(GlobalVariables.ACTION_QUERY);
                    startActivity(intent);
                    break;
                case 2:
                    if (EPApplication.getInstance().getUserLibStatus() > EPApplication.USER_STATUS_NORMAL) {

                        intent.setAction(GlobalVariables.ACTION_LIBRARY);
                        startActivity(intent);
                    } else {
                        ((MainActivity) getActivity()).createTObindDiglog(
                                getString(R.string.str_lib_card), getActivity());
                    }
                    // intent.setAction(GlobalVariables.ACTION_ADD_ONES);
                    break;
                case 3:
                    if (EPApplication.getInstance().getUserCampusStatus() > EPApplication.USER_STATUS_NORMAL) {

                        intent.setAction(GlobalVariables.ACTION_CAMPUS_CARD);
                        startActivity(intent);
                    } else {
                        ((MainActivity) getActivity()).createTObindDiglog(
                                getString(R.string.str_campus_card), getActivity());
                    }
                    break;
                case 4:

                    intent.setClass(getActivity(), AddOnesActivity.class);
                    startActivity(intent);
                    break;
                default:
                    CommonUtils.showCustomToast(Toast.makeText(getActivity(),
                            "功能将在正式版开放！", Toast.LENGTH_LONG));
                    break;
            }


        }

    }

}
