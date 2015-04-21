package com.emptypointer.hellocdut.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContact;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.util.DateUtils;
import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.activity.MainActivity;
import com.emptypointer.hellocdut.dao.AAONewsDao;
import com.emptypointer.hellocdut.dao.DataCacheDao;
import com.emptypointer.hellocdut.domain.AAOContact;
import com.emptypointer.hellocdut.domain.User;
import com.emptypointer.hellocdut.utils.GlobalVariables;
import com.emptypointer.hellocdut.utils.SmileUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Date;
import java.util.List;

public class NotifyAdapter extends ArrayAdapter<EMContact> {

    private LayoutInflater inflater;
    private DisplayImageOptions options;

    public NotifyAdapter(Context context, int textViewResourceId,
                         List<EMContact> objects) {
        super(context, textViewResourceId, objects);
        inflater = LayoutInflater.from(context);
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.default_avatar)
                .showImageForEmptyUri(R.drawable.default_avatar)
                .showImageOnFail(R.drawable.ic_error_loaded)
                .cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
    }

    public boolean isAAOItem(int position) {
        return getItem(position) instanceof AAOContact;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.row_chat_history, parent,
                    false);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        if (holder == null) {
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.unreadLabel = (TextView) convertView
                    .findViewById(R.id.unread_msg_number);
            holder.message = (TextView) convertView.findViewById(R.id.message);
            holder.time = (TextView) convertView.findViewById(R.id.time);
            holder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
            holder.msgState = convertView.findViewById(R.id.msg_state);
            holder.list_item_layout = (RelativeLayout) convertView
                    .findViewById(R.id.list_item_layout);
            convertView.setTag(holder);
        }
        holder.list_item_layout
                .setBackgroundResource(R.drawable.selector_white_btn_dress_with_layout);

        EMContact user = getItem(position);
        if (user instanceof EMGroup) {
            // 群聊消息，显示群聊头像
            holder.avatar.setImageResource(R.drawable.groups_icon);
        } else if (user instanceof AAOContact) {
            AAONewsDao dao = new AAONewsDao(getContext());
            DataCacheDao dateDao = new DataCacheDao(getContext());
            if (dateDao.getCache(MainActivity.CACHE_SCHEDULED_TASK) != null) {
                holder.message.setText(dao.getFistTitle());
                holder.time.setText(DateFormat.format("M月dd日", dao.getTime()));
            }
            holder.avatar.setImageResource(R.drawable.ic_aao_avatar);
        } else {
            String imageUrl = ((User) user).getImageURL();
            if (imageUrl != null) {
                ImageLoader.getInstance().displayImage(imageUrl, holder.avatar,
                        options);
            } else {
                holder.avatar.setImageResource(R.drawable.default_avatar);
            }
        }

        String username = user.getUsername();
        // 获取与此用户/群组的会话
        EMConversation conversation = EMChatManager.getInstance()
                .getConversation(username);
        if (user instanceof EMGroup) {
            holder.name.setText(user.getNick() != null ? user.getNick()
                    : username);
        } else {
            holder.name
                    .setText(((User) user).getNicKName() != null ? ((User) user)
                            .getNicKName() : username);

        }

        if (conversation.getUnreadMsgCount() > 0) {
            // 显示与此用户的消息未读数
            holder.unreadLabel.setText(String.valueOf(conversation
                    .getUnreadMsgCount()));
            holder.unreadLabel.setVisibility(View.VISIBLE);
        } else {
            holder.unreadLabel.setVisibility(View.INVISIBLE);
        }

        if (conversation.getMsgCount() != 0) {
            // 把最后一条消息的内容作为item的message内容
            EMMessage lastMessage = conversation.getLastMessage();
            holder.message
                    .setText(
                            SmileUtils.getSmiledText(
                                    getContext(),
                                    getMessageDigest(lastMessage,
                                            (this.getContext()))),
                            BufferType.SPANNABLE);

            holder.time.setText(DateUtils.getTimestampString(new Date(
                    lastMessage.getMsgTime())));
            if (lastMessage.direct == EMMessage.Direct.SEND
                    && lastMessage.status == EMMessage.Status.FAIL) {
                holder.msgState.setVisibility(View.VISIBLE);
            } else {
                holder.msgState.setVisibility(View.GONE);
            }
        }

        return convertView;
    }

    /**
     * 根据消息内容和消息类型获取消息内容提示
     *
     * @param message
     * @param context
     * @return
     */
    private String getMessageDigest(EMMessage message, Context context) {
        String digest = "";
        switch (message.getType()) {
            case LOCATION: // 位置消息
                if (message.direct == EMMessage.Direct.RECEIVE) {
                    // 从sdk中提到了ui中，使用更简单不犯错的获取string方法
                    // digest = EasyUtils.getAppResourceString(context,
                    // "location_recv");
                    digest = getStrng(context, R.string.location_recv);
                    digest = String.format(digest, message.getFrom());
                    return digest;
                } else {
                    // digest = EasyUtils.getAppResourceString(context,
                    // "location_prefix");
                    digest = getStrng(context, R.string.location_prefix);
                }
                break;
            case IMAGE: // 图片消息
                ImageMessageBody imageBody = (ImageMessageBody) message.getBody();
                digest = getStrng(context, R.string.picture)
                        + imageBody.getFileName();
                break;
            case VOICE:// 语音消息
                digest = getStrng(context, R.string.voice);
                break;
            case VIDEO: // 视频消息
                digest = getStrng(context, R.string.video);
                break;
            case TXT: // 文本消息
                if (!message.getBooleanAttribute(
                        GlobalVariables.MESSAGE_ATTR_IS_VOICE_CALL, false)) {
                    TextMessageBody txtBody = (TextMessageBody) message.getBody();
                    digest = txtBody.getMessage();
                } else {
                    TextMessageBody txtBody = (TextMessageBody) message.getBody();
                    digest = getStrng(context, R.string.voice_call)
                            + txtBody.getMessage();
                }
                break;
            case FILE: // 普通文件消息
                digest = getStrng(context, R.string.file);
                break;
            default:
                System.err.println("error, unknow type");
                return "";
        }

        return digest;
    }

    private static class ViewHolder {
        /**
         * 和谁的聊天记录
         */
        TextView name;
        /**
         * 消息未读数
         */
        TextView unreadLabel;
        /**
         * 最后一条消息的内容
         */
        TextView message;
        /**
         * 最后一条消息的时间
         */
        TextView time;
        /**
         * 用户头像
         */
        ImageView avatar;
        /**
         * 最后一条消息的发送状态
         */
        View msgState;
        /**
         * 整个list中每一行总布局
         */
        RelativeLayout list_item_layout;

    }

    String getStrng(Context context, int resId) {
        return context.getResources().getString(resId);
    }
}
