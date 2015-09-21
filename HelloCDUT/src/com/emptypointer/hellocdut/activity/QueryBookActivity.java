package com.emptypointer.hellocdut.activity;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.adapter.BookQueryAdapter;
import com.emptypointer.hellocdut.customer.EPApplication;
import com.emptypointer.hellocdut.domain.QueryBook;
import com.emptypointer.hellocdut.service.EPHttpService;
import com.emptypointer.hellocdut.service.EPSecretService;
import com.emptypointer.hellocdut.utils.CommonUtils;
import com.emptypointer.hellocdut.utils.GlobalVariables;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

/**
 * 查询图书
 *
 * @author Sequarius
 */
public class QueryBookActivity extends BaseActivity {
    public static final String TAG = "QueryBookActivity";
    private PtrFrameLayout mPtrFrame;
    private PullToRefreshListView mListView;
    private BookQueryAdapter mAdapter;
    private List<QueryBook> mItems;
    private String mQueryContent;
    private int mCurrentPage = 1;
    private int mEndPage = 1;

    private boolean mOnRefresh = false;

    @Override
    protected void onCreate(Bundle arg0) {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        overridePendingTransition(android.R.anim.fade_in,
                android.R.anim.fade_out);
        setContentView(R.layout.fragment_library_borrowed);
        // mTvLastTime = (TextView) findViewById(R.id.textView_last_updated);
        mPtrFrame = (PtrFrameLayout) findViewById(R.id.material_style_ptr_frame);
        mListView = (PullToRefreshListView) findViewById(R.id.PullToRefreshListView);

        mItems = new ArrayList<QueryBook>();

        mAdapter = new BookQueryAdapter(mItems, this);

        mListView.setAdapter(mAdapter);

        ActionBar actionBar = getActionBar();

        actionBar.setCustomView(R.layout.actionbar_search_view);
        final EditText etSearch = (EditText) actionBar.getCustomView()
                .findViewById(R.id.editText_search);

        ((ImageButton) actionBar.getCustomView().findViewById(
                R.id.button_return)).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                QueryBookActivity.this.finish();
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
                    String strContent = etSearch.getText().toString();
                    if (strContent.length() > 0) {
                        mQueryContent = strContent;
                        mPtrFrame.postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                mPtrFrame.autoRefresh(true);
                                new CommitTask().execute(0);
                            }
                        }, 150);
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(0,
                                InputMethodManager.HIDE_NOT_ALWAYS);
                        etSearch.clearFocus();
                    }
                    return true;
                }
                return false;
            }
        });
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        mListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

            @Override
            public void onPullDownToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onPullUpToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                // TODO Auto-generated method stub
                if (mCurrentPage < mEndPage && (!mOnRefresh)) {
                    new CommitTask().execute(mCurrentPage + 1);
                } else {
                    refreshView.post(new Runnable() {
                        @Override
                        public void run() {
                            mListView.onRefreshComplete();
                        }
                    });
                    CommonUtils.showCustomToast(Toast
                            .makeText(QueryBookActivity.this,
                                    getString(R.string.str_no_more),
                                    Toast.LENGTH_SHORT));
                    return;
                }

            }

        });

        // header
        final MaterialHeader header = new MaterialHeader(this);
        int[] colors = getResources().getIntArray(R.array.google_colors);
        header.setColorSchemeColors(colors);
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, dp2px(10), 0, dp2px(4));
        header.setPtrFrameLayout(mPtrFrame);

        mPtrFrame.setLoadingMinTime(1000);
        mPtrFrame.setDurationToCloseHeader(1500);
        mPtrFrame.setHeaderView(header);
        mPtrFrame.addPtrUIHandler(header);

        mPtrFrame.setPtrHandler(new PtrHandler() {

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame,
                                             View content, View header) {

                return false;
            }
        });

//        mListView.setOnItemClickListener(new OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view,
//                                    int position, long id) {
//                // TODO Auto-generated method stub
//                if()
//                new QueryDetailTask().execute(mItems.get(position)
//                        .getHrefIndex());
//
//            }
//        });

    }

    /**
     * 查询book详情
     *
     * @author Sequarius
     */
    private class QueryDetailTask extends AsyncTask<String, Void, Boolean> {
        ProgressDialog mDialog;
        private String mMessage;

        @Override
        protected void onPreExecute() {
            // mDialog = new ProgressDialog(getActivity());
            // mDialog.setTitle(getString(R.string.str_feed_back));
            // mDialog.setMessage(getString(R.string.message_loading));
            // mDialog.show();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            // mDialog.dismiss();
            if (result) {
                // Toast.makeText(getActivity(), mMessage,
                // Toast.LENGTH_SHORT).show();
                // getActivity().finish();
            } else {
                // Toast.makeText(getActivity(), mMessage,
                // Toast.LENGTH_LONG).show();
                if (mMessage.equals(GlobalVariables.ERRO_MESSAGE_RELOG)) {
                    Intent intent = new Intent(QueryBookActivity.this,
                            LoginActivity.class);
                    EPApplication.getInstance().logout();
                    EPApplication.getInstance().clearAcitivy();
                    startActivity(intent);
                }
            }
        }

        @Override
        protected Boolean doInBackground(String... paramin) {
            // TODO Auto-generated method stub
            List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
            params.add(new BasicNameValuePair("action", "queryBookDetail"));
            params.add(new BasicNameValuePair("user_name", EPSecretService
                    .encryptByPublic(EPApplication.getInstance().getUserName())));
            params.add(new BasicNameValuePair("user_login_token",
                    EPSecretService.encryptByPublic(EPApplication.getInstance()
                            .getToken())));
            params.add(new BasicNameValuePair("book_detail_index", paramin[0]));

            try {
                String str = EPHttpService.customerPostString(
                        GlobalVariables.SERVICE_HOST_QUERY_BOOK, params);
                JSONObject object = JSONObject.parseObject(str);
                mMessage = object.getString("message");
                boolean result = object.getBooleanValue("result");
                return result;
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                mMessage = getString(R.string.message_weak_internet);
                return false;
            }
        }
    }

    private class CommitTask extends AsyncTask<Integer, Void, Boolean> {

        private String mMessage;
        private int impage;

        @Override
        protected void onPreExecute() {
            mOnRefresh = true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            mListView.onRefreshComplete();
            mPtrFrame.refreshComplete();

            mOnRefresh = false;
            if (result) {
                mAdapter.notifyDataSetChanged();
            } else {
                CommonUtils.showCustomToast(Toast.makeText(
                        QueryBookActivity.this, mMessage, Toast.LENGTH_LONG));
                if (mMessage.equals(GlobalVariables.ERRO_MESSAGE_RELOG)) {
                    Intent intent = new Intent(QueryBookActivity.this,
                            LoginActivity.class);
                    EPApplication.getInstance().logout();
                    EPApplication.getInstance().clearAcitivy();
                    startActivity(intent);
                }
            }
        }

        @Override
        protected Boolean doInBackground(Integer... paramin) {
            impage = paramin[0];
            // TODO Auto-generated method stub
            List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();

            params.add(new BasicNameValuePair("user_name", EPSecretService
                    .encryptByPublic(EPApplication.getInstance().getUserName())));
            params.add(new BasicNameValuePair("user_login_token",
                    EPSecretService.encryptByPublic(EPApplication.getInstance()
                            .getToken())));
            params.add(new BasicNameValuePair("book_name", mQueryContent));
            if (paramin[0] != 0) {
                String str_value = String.valueOf(paramin[0]);
                params.add(new BasicNameValuePair("action", "jumpPage"));
                params.add(new BasicNameValuePair("jump_page", str_value));
            } else {
                params.add(new BasicNameValuePair("action", "searchBook"));
            }

            try {
                String str = EPHttpService.customerPostString(
                        GlobalVariables.SERVICE_HOST_QUERY_BOOK, params);
                JSONObject object = JSONObject.parseObject(str);

                boolean result = object.getBooleanValue("result");
                if (result) {
                    if (impage == 0) {
                        jsonParse(object, false);

                    } else {
                        jsonParse(object, true);
                    }
                } else {
                    mMessage = object.getString("message");
                }
                return result;
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                mMessage = getString(R.string.message_weak_internet);
                return false;
            }
        }

    }

    // @Override
    // public boolean onCreateOptionsMenu(Menu menu) {
    // // TODO Auto-generated method stub
    // MenuInflater inflater = getMenuInflater();
    // inflater.inflate(R.menu.book_search, menu);
    // MenuItem item = menu.findItem(R.id.action_search);
    //
    // SearchManager searchManager = (SearchManager)
    // getSystemService(Context.SEARCH_SERVICE);
    // final SearchView searchView = (SearchView) item.getActionView();
    // int searchPlateId = searchView.getContext().getResources()
    // .getIdentifier("android:id/search_src_text", null, null);
    // EditText searchPlate = (EditText) searchView
    // .findViewById(searchPlateId);
    // searchPlate.setTextColor(getResources()
    // .getColor(R.color.color_ep_white));
    // searchPlate.setHint(R.string.str_query_book);
    // //
    // searchPlate.setBackgroundResource(android.R.drawable.edit_text_holo_light);
    // searchPlate.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
    //
    // try {
    // Field searchField = SearchView.class
    // .getDeclaredField("mCloseButton");
    // searchField.setAccessible(true);
    // ImageView closeBtn = (ImageView) searchField.get(searchView);
    // closeBtn.setImageResource(R.drawable.delete_expression);
    //
    // // searchField = SearchView.class.getDeclaredField("mVoiceButton");
    // // searchField.setAccessible(true);
    // // ImageView voiceBtn = (ImageView) searchField.get(searchView);
    // // voiceBtn.setImageResource(R.drawable.ic_menu_voice_input);
    // // mSearchHintIcon
    // // ImageView searchButton = (ImageView)
    // // searchView.findViewById(R.id.abs_search_button);
    // // Field searchHintField =
    // // SearchView.class.getDeclaredField("mSearchHintIcon");
    // // searchHintField.setAccessible(true);
    // // ImageView searchButton = (ImageView) searchField.get(searchView);
    // // searchButton.setImageResource(R.drawable.ic_actionbar_search);
    //
    // } catch (NoSuchFieldException e) {
    // Log.e("SearchView", e.getMessage(), e);
    // } catch (IllegalAccessException e) {
    // Log.e("SearchView", e.getMessage(), e);
    // }
    //
    // searchView.setOnQueryTextListener(new OnQueryTextListener() {
    //
    // @Override
    // public boolean onQueryTextSubmit(String query) {
    // // TODO Auto-generated method stub
    // if (query.length() > 0) {
    // mQueryContent = query;
    // mPtrFrame.postDelayed(new Runnable() {
    //
    // @Override
    // public void run() {
    // mPtrFrame.autoRefresh(true);
    // new CommitTask().execute(0);
    // }
    // }, 150);
    // searchView.clearFocus();
    // }
    //
    // return false;
    // }
    //
    // @Override
    // public boolean onQueryTextChange(String newText) {
    // // TODO Auto-generated method stub
    // return false;
    // }
    // });
    //
    // // item.collapseActionView();
    // // 是搜索框默认展开
    // // item.expandActionView();
    // searchView.setSearchableInfo(searchManager
    // .getSearchableInfo(getComponentName()));
    // // searchView.setIconifiedByDefault(false); // Do not iconify the widget;
    // // // expand it by default、
    //
    // return super.onCreateOptionsMenu(menu);
    // }

    private void jsonParse(JSONObject object, boolean appendable) {
        if (!appendable) {
            mItems.clear();
        }
        if (object.getString("current_page") != null) {
            mCurrentPage = object.getIntValue("current_page");
        }
        if (object.getString("total_page") != null) {
            mEndPage = object.getIntValue("total_page");
        }
        JSONArray array = object.getJSONArray("books");
        if (array != null) {
            for (int i = 0; i < array.size(); i++) {
                JSONObject objBook = array.getJSONObject(i);
                String name = objBook.getString("book_name");
                String index = objBook.getString("book_index");
                String publish = objBook.getString("book_press");
                String pubTime = objBook.getString("book_publish_time");
                String introduction = objBook
                        .getString("book_brief_introduction");
                String writer = objBook.getString("book_writer");
                String institution = objBook.getString("institution");
                String bookCount = objBook.getString("book_count");
                String location = objBook.getString("campus");
                String bookDatabase = objBook.getString("book_database");
                String hrefIndex = objBook.getString("href_index");
                String avatarURL = objBook.getString("pic_href");
                QueryBook book = new QueryBook(name, index, publish, pubTime,
                        introduction, writer, bookCount, location, avatarURL,
                        hrefIndex, institution, bookDatabase);
                mItems.add(book);

            }
        } else {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    CommonUtils.showCustomToast(Toast.makeText(
                            QueryBookActivity.this,
                            R.string.message_no_book_be_found,
                            Toast.LENGTH_LONG));
                }
            });

        }
    }

}
