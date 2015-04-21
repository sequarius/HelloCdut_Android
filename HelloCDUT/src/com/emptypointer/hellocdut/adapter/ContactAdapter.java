package com.emptypointer.hellocdut.adapter;


import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.domain.User;
import com.emptypointer.hellocdut.utils.GlobalVariables;
import com.emptypointer.hellocdut.widget.SideBar;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;


/**
 * 简单的好友Adapter实现
 */
public class ContactAdapter extends ArrayAdapter<User> implements SectionIndexer {

    private static final String TAG = "ContactAdapter";
    private LayoutInflater layoutInflater;
    //	private EditText query;
//	private ImageButton clearSearch;
    private SparseIntArray positionOfSection;
    private SparseIntArray sectionOfPosition;
    //	private SideBar sidebar;
    private int res;

    private DisplayImageOptions options;

    public ContactAdapter(Context context, int resource, List<User> objects, SideBar sidebar) {
        super(context, resource, objects);
        this.res = resource;
//		this.sidebar=sidebar;
        layoutInflater = LayoutInflater.from(context);
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.default_avatar)
                .showImageForEmptyUri(R.drawable.default_avatar)
                .showImageOnFail(R.drawable.ic_error_loaded)
                .cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
    }

//	@Override
//	public int getViewTypeCount() {
//		return 2;
//	}
//
//	@Override
//	public int getItemViewType(int position) {
//		return position == 0 ? 0 : 1;
//	}

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//		if (position == 0) {//搜索框
//			if(convertView == null){
//				convertView = layoutInflater.inflate(R.layout.search_bar_with_padding, null);
//				query = (EditText) convertView.findViewById(R.id.query);
//				clearSearch = (ImageButton) convertView.findViewById(R.id.search_clear);
//				query.addTextChangedListener(new TextWatcher() {
//					public void onTextChanged(CharSequence s, int start, int before, int count) {
//						getFilter().filter(s);
//						if (s.length() > 0) {
//							clearSearch.setVisibility(View.VISIBLE);
//							if (sidebar != null)
//								sidebar.setVisibility(View.GONE);
//						} else {
//							clearSearch.setVisibility(View.INVISIBLE);
//							if (sidebar != null)
//								sidebar.setVisibility(View.VISIBLE);
//						}
//					}
//	
//					public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//					}
//	
//					public void afterTextChanged(Editable s) {
//					}
//				});
//				clearSearch.setOnClickListener(new OnClickListener() {
//					@Override
//					public void onClick(View v) {
//						InputMethodManager manager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//						if (((Activity) getContext()).getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
//							if (((Activity) getContext()).getCurrentFocus() != null)
//							manager.hideSoftInputFromWindow(((Activity) getContext()).getCurrentFocus().getWindowToken(),
//									InputMethodManager.HIDE_NOT_ALWAYS);
//						//清除搜索框文字
//						query.getText().clear();
//					}
//				});
//			}
//		}else{
        if (convertView == null) {
            convertView = layoutInflater.inflate(res, null);
        }
        TextView tvMotto = (TextView) convertView.findViewById(R.id.signature);
        ImageView avatar = (ImageView) convertView.findViewById(R.id.avatar);
        TextView unreadMsgView = (TextView) convertView.findViewById(R.id.unread_msg_number);
        TextView nameTextview = (TextView) convertView.findViewById(R.id.name);
        TextView tvHeader = (TextView) convertView.findViewById(R.id.header);
        User user = getItem(position);
        if (user == null) {
            return convertView;
        }
        //设置nick，demo里不涉及到完整user，用username代替nick显示
        String username = user.getNicKName();
        String header = user.getHeader();


        if (position == 0 || header != null && !header.equals(getItem(position - 1).getHeader())) {
            if ("".equals(header)) {
                tvHeader.setVisibility(View.GONE);
            } else {
                tvHeader.setVisibility(View.VISIBLE);
                tvHeader.setText(header);

            }
        } else {
            tvHeader.setVisibility(View.GONE);
        }
        //显示申请与通知item
        if (user.getUsername().equals(GlobalVariables.NEW_FRIENDS_USERNAME)) {
            nameTextview.setText(user.getNicKName());
            avatar.setImageResource(R.drawable.new_friends_icon);
            tvMotto.setVisibility(View.GONE);
            if (user.getUnreadMsgCount() > 0) {
                unreadMsgView.setVisibility(View.VISIBLE);
                unreadMsgView.setText(user.getUnreadMsgCount() + "");
            } else {
                unreadMsgView.setVisibility(View.INVISIBLE);
            }
        } else if (user.getUsername().equals(GlobalVariables.GROUP_USERNAME)) {
            //群聊item
            tvMotto.setVisibility(View.GONE);
            nameTextview.setText(user.getNicKName());
            avatar.setImageResource(R.drawable.groups_icon);
        } else {
            nameTextview.setText(user.getNicKName() != null ? user.getNicKName() : username);
            if (unreadMsgView != null)
                unreadMsgView.setVisibility(View.INVISIBLE);

            tvMotto.setVisibility(View.VISIBLE);
            tvMotto.setText(user.getMotto());
            String imageUrl = user.getImageURL();
            if (imageUrl != null) {
                ImageLoader.getInstance().displayImage(imageUrl,
                        avatar, options);
            } else {
                avatar.setImageResource(R.drawable.default_avatar);
            }

        }
//		}

        return convertView;
    }

//	@Override
//	public User getItem(int position) {
//		return position == 0 ? new User() : super.getItem(position - 1);
//	}

//	@Override
//	public int getCount() {
//		//有搜索框，count+1
//		return super.getCount() + 1;
//	}

    public int getPositionForSection(int section) {
        return positionOfSection.get(section);
    }

    public int getSectionForPosition(int position) {
        return sectionOfPosition.get(position);
    }

    @Override
    public Object[] getSections() {
        positionOfSection = new SparseIntArray();
        sectionOfPosition = new SparseIntArray();
        int count = getCount();
        List<String> list = new ArrayList<String>();
        list.add(getContext().getString(R.string.search_header));
        positionOfSection.put(0, 0);
        sectionOfPosition.put(0, 0);
        for (int i = 1; i < count; i++) {

            String letter = getItem(i).getHeader();
            System.err.println("contactadapter getsection getHeader:" + letter + " name:" + getItem(i).getUsername());
            int section = list.size() - 1;
            if (list.get(section) != null && !list.get(section).equals(letter)) {
                list.add(letter);
                section++;
                positionOfSection.put(section, i);
            }
            sectionOfPosition.put(i, section);
        }
        return list.toArray(new String[list.size()]);
    }

}