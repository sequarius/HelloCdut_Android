package com.emptypointer.hellocdut.fragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.customer.EPApplication;
import com.emptypointer.hellocdut.domain.UserInfo;
import com.emptypointer.hellocdut.service.EPHttpService;
import com.emptypointer.hellocdut.utils.CommonUtils;
import com.emptypointer.hellocdut.utils.GlobalVariables;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class UserInfoFragment extends Fragment implements OnClickListener {

    private TextView mTvUserName;
    private TextView mTvNickName;
    private ImageView mImageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = View.inflate(getActivity(), R.layout.fragment_user_center,
                null);

        RelativeLayout layoutUserInfo = (RelativeLayout) view
                .findViewById(R.id.layout_user_center_user_info);

        LinearLayout layoutRelativeAccount = (LinearLayout) view
                .findViewById(R.id.layout_user_center_relative_account);
        LinearLayout layoutCollection = (LinearLayout) view
                .findViewById(R.id.layout_user_center_user_collection);
        LinearLayout layoutExam = (LinearLayout) view
                .findViewById(R.id.layout_user_center_user_exam);
        LinearLayout layoutSetting = (LinearLayout) view
                .findViewById(R.id.layout_user_center_setting);
        mTvNickName = (TextView) view.findViewById(R.id.textView_nick_name);
        mTvUserName = (TextView) view.findViewById(R.id.textView_user_name);

        mImageView = (ImageView) view
                .findViewById(R.id.imageView_user_center_avatar);
        mTvUserName.setText(getString(R.string.str_format_user_name,
                EPApplication.getInstance().getUserName()));

        layoutCollection.setOnClickListener(this);
        layoutExam.setOnClickListener(this);
        layoutRelativeAccount.setOnClickListener(this);
        layoutSetting.setOnClickListener(this);
        layoutUserInfo.setOnClickListener(this);
        return view;

    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        mTvNickName.setText(EPApplication.getInstance().getNickName());
        String pathName = getActivity().getDir("user", Context.MODE_PRIVATE)
                + File.separator + EPApplication.getInstance().getUserName();
        File file = new File(pathName);
        String imageURL = UserInfo.getInstance(getActivity()).getImageURL();
        if (file.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(pathName);
            if (bitmap != null) {
                mImageView.setImageBitmap(bitmap);
            }
        } else if ((!file.exists()) && (!imageURL.equals(""))) {
            new getImageThread(imageURL).start();
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        Intent intent = new Intent();

        switch (v.getId()) {
            case R.id.layout_user_center_user_info:
                intent.setAction(GlobalVariables.ACTION_USERINFO);
                break;

            case R.id.layout_user_center_relative_account:
                intent.setAction(GlobalVariables.ACTION_ACCOUNTMANAGE);
                break;

            case R.id.layout_user_center_user_collection:
                CommonUtils.showCustomToast(Toast.makeText(getActivity(), "收藏功能尚未开放！", Toast.LENGTH_LONG));
//			intent.setAction(GlobalVariables.ACTION_COLLECTIONMANAGE);
                break;

            case R.id.layout_user_center_user_exam:
                CommonUtils.showCustomToast(Toast.makeText(getActivity(), "考试功能尚未开放！", Toast.LENGTH_LONG));
//			intent.setAction(GlobalVariables.ACTION_EXAMMANAGE);
                break;

            case R.id.layout_user_center_setting:
                intent.setAction(GlobalVariables.ACTION_SETTING);
                break;
            default:
                break;
        }
        if (intent.getAction() != null) {
            startActivity(intent);
        }
    }

    private class getImageThread extends Thread {
        private String imgeURL;

        public getImageThread(String imgeURL) {
            super();
            this.imgeURL = imgeURL;
        }

        @Override
        public void run() {
            try {
                final Bitmap bitmap = EPHttpService.customerGetBitmap(imgeURL,
                        null);
                if (bitmap != null) {
                    File file = new File(getActivity().getDir("user",
                            Context.MODE_PRIVATE), EPApplication.getInstance()
                            .getUserName());
                    FileOutputStream outputStream = null;
                    try {
                        outputStream = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100,
                                outputStream);
                        outputStream.flush();

                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } finally {
                        if (outputStream != null) {
                            try {
                                outputStream.close();
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }

                    getActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            mImageView.setImageBitmap(bitmap);
                        }
                    });
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        CommonUtils.showCustomToast(Toast.makeText(getActivity(),
                                getString(R.string.message_weak_internet),
                                Toast.LENGTH_SHORT));
                    }
                });
            }
        }

    }

}
