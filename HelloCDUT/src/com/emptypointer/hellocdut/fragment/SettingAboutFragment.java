package com.emptypointer.hellocdut.fragment;

import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.activity.AgreementActivity;
import com.emptypointer.hellocdut.activity.GuideActivity;
import com.emptypointer.hellocdut.customer.EPApplication;
import com.emptypointer.hellocdut.dao.DataCacheDao;
import com.emptypointer.hellocdut.domain.DataCache;
import com.emptypointer.hellocdut.service.EPUpdateService;
import com.emptypointer.hellocdut.utils.CommonUtils;
import com.emptypointer.hellocdut.utils.GlobalVariables;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SettingAboutFragment extends Fragment implements OnClickListener {
    private RelativeLayout mLayoutUpdate, mLayoutAbout, mLayoutClean,
            mLayoutAgreement, mLayoutGuide, mLayouEvaluate;
    private EPUpdateService mUpService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = View.inflate(getActivity(),
                R.layout.fragment_setting_about, null);
        mLayouEvaluate = (RelativeLayout) view.findViewById(R.id.layout_evaluate);
        mLayouEvaluate.setOnClickListener(this);

        mLayoutGuide = (RelativeLayout) view.findViewById(R.id.layout_guide);
        mLayoutGuide.setOnClickListener(this);

        mLayoutUpdate = (RelativeLayout) view.findViewById(R.id.layout_update);
        mLayoutUpdate.setOnClickListener(this);

        mLayoutAbout = (RelativeLayout) view.findViewById(R.id.layout_about);
        mLayoutAbout.setOnClickListener(this);

        mLayoutClean = (RelativeLayout) view
                .findViewById(R.id.layout_clean_cache);
        mLayoutClean.setOnClickListener(this);
        mLayoutAgreement = (RelativeLayout) view
                .findViewById(R.id.layout_agreement);
        mLayoutAgreement.setOnClickListener(this);

        ((TextView) view.findViewById(R.id.TextView_version))
                .setText(getString(R.string.str_format_version,
                        EPUpdateService.getAppVersion(getActivity())));

        mUpService = new EPUpdateService(EPApplication.getInstance(),
                getActivity());
        return view;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.layout_update:
                mUpService.cheakVersionFront();
                break;
            case R.id.layout_clean_cache:
                creatCleanCacheDialog();
                break;
            case R.id.layout_agreement:
                startActivity(new Intent(getActivity(), AgreementActivity.class));
                break;
            case R.id.layout_about:

                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(GlobalVariables.ABOUT_URL));
                startActivity(intent);
                break;
            case R.id.layout_guide:

                startActivity(new Intent(getActivity(), GuideActivity.class));
                break;
            case R.id.layout_evaluate:
                Uri uri = Uri.parse("market://details?id=" + getActivity().getPackageName());
                Intent intentEvaluate = new Intent(Intent.ACTION_VIEW, uri);
                intentEvaluate.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentEvaluate);
                break;

            default:
                break;
        }

    }

    private void creatCleanCacheDialog() {
        AlertDialog dialog = new Builder(getActivity())
                .setTitle(R.string.hint_dangerous_operatation)
                .setMessage(R.string.message_clean_cache)
                .setPositiveButton(R.string.str_comfirm_clean,
                        new Dialog.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                // TODO Auto-generated method stub
                                cleanCache();

                            }
                        })
                .setNegativeButton(R.string.cancel,
                        new Dialog.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                // TODO Auto-generated method stub

                            }
                        }).show();
        CommonUtils.dialogTitleLineColor(dialog);
    }

    private void cleanCache() {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage(getString(R.string.str_on_clean_cache));
        dialog.show();
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                new DataCacheDao(getActivity()).cleanData();
                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                        CommonUtils.showCustomToast(Toast.makeText(
                                getActivity(),
                                getString(R.string.message_had_clean_cache),
                                Toast.LENGTH_SHORT));
                    }
                });

            }
        }).start();

    }
}
