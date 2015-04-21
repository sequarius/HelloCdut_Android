package com.emptypointer.hellocdut.activity;

import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.emptypointer.hellocdut.R;

public class SearchActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle arg0) {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        setContentView(R.layout.activity_search);
        overridePendingTransition(android.R.anim.fade_in,
                android.R.anim.fade_out);

        ActionBar actionBar = getActionBar();

        actionBar.setCustomView(R.layout.actionbar_search_view);
        final EditText etSearch = (EditText) actionBar.getCustomView()
                .findViewById(R.id.editText_search);
        etSearch.setHint(R.string.str_search);
        ((ImageButton) actionBar.getCustomView().findViewById(
                R.id.button_return)).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                SearchActivity.this.finish();
            }
        });
        ;
        etSearch.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                // TODO Auto-generated method stub
                if (actionId == EditorInfo.IME_ACTION_SEND
                        || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                    etSearch.clearFocus();

                    return true;
                }
                return false;
            }
        });
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
    }

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.search_options, menu);
//		return true;
//	}

}
