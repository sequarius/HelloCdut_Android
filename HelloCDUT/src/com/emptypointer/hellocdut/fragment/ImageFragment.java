package com.emptypointer.hellocdut.fragment;

import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.activity.GuideActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;

public class ImageFragment extends Fragment {

    private int mImageID;

    @SuppressLint("ValidFragment")
    public ImageFragment(int mImageID) {
        super();
        this.mImageID = mImageID;
    }

    public ImageFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mImageID != R.layout.fragment_drawable_with_bottom_button) {
            // TODO Auto-generated method stub
            ImageView imageView = new ImageView(getActivity());
            imageView.setLayoutParams(new LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            imageView.setBackgroundResource(mImageID);
            return imageView;
        } else {
            View view = inflater.inflate(R.layout.fragment_drawable_with_bottom_button, null);
            Button button = (Button) view.findViewById(R.id.button_quit);
            button.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    getActivity().finish();
                }
            });
            button.setVisibility(View.VISIBLE);
            AlphaAnimation animation = new AlphaAnimation(0.3f,
                    1.0f);
            animation.setDuration(1000);
            button.startAnimation(animation);
            return view;
        }
    }

}
