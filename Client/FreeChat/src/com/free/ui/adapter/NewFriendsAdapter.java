package com.free.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;

import com.free.chat.core.FCContactsManager;
import com.free.chat.core.XmppConnectionManager;
import com.free.exception.FCException;
import com.free.ui.FCConfig;
import com.free.ui.R;
import com.free.ui.db.NewFriendDao;
import com.free.ui.db.UserDao;
import com.free.ui.model.NewFriend;
import com.free.ui.model.User;
import com.free.ui.view.BadgeView;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class NewFriendsAdapter extends BaseAdapter {
	private Context context;
	private List<NewFriend> newFriends;
	private LayoutInflater inflater;
	private ViewHolder holder;
	private XMPPConnection connection;

	public NewFriendsAdapter(Context context, List<NewFriend> newFriends) {
		this.context = context;
		inflater = LayoutInflater.from(context);
		this.newFriends = newFriends;
		connection = XmppConnectionManager.getConnection();
	}

	@Override
	public int getCount() {
		return newFriends.size();
	}

	@Override
	public Object getItem(int id) {
		return newFriends.get(id);
	}

	@Override
	public long getItemId(int id) {
		return id;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.listview_new_friends_item,
					null);
			holder = new ViewHolder();

			holder.imageIV = (ImageView) convertView
					.findViewById(R.id.portrait);
			holder.titleTV = (TextView) convertView.findViewById(R.id.title);
			holder.contentTV = (TextView) convertView
					.findViewById(R.id.content);
			holder.statusTV = (TextView) convertView.findViewById(R.id.status);
			holder.agreeBtn = (Button) convertView.findViewById(R.id.agree);
			holder.rejectBtn = (Button) convertView.findViewById(R.id.reject);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		// 根据新朋友状态显示按钮状态文本内容
		if (((NewFriend) getItem(position)).getCondition() == NewFriend.AGREE) {
			holder.agreeBtn.setVisibility(View.GONE);
			holder.rejectBtn.setVisibility(View.GONE);
			holder.statusTV.setText("已同意");
			holder.statusTV.setVisibility(View.VISIBLE);
		} else if (((NewFriend) getItem(position)).getCondition() == NewFriend.REJECT) {
			holder.agreeBtn.setVisibility(View.GONE);
			holder.rejectBtn.setVisibility(View.GONE);
			holder.statusTV.setText("已拒绝");
			holder.statusTV.setVisibility(View.VISIBLE);
		} else if (((NewFriend) getItem(position)).getCondition() == NewFriend.AGREED
				|| ((NewFriend) getItem(position)).getCondition() == NewFriend.REJECTED) {
			holder.agreeBtn.setVisibility(View.GONE);
			holder.rejectBtn.setVisibility(View.GONE);
		}

		holder.agreeBtn.setOnClickListener(new LVButtonListener(position));
		holder.rejectBtn.setOnClickListener(new LVButtonListener(position));

		holder.titleTV.setText(((NewFriend) getItem(position)).getNickname());
		holder.contentTV.setText(((NewFriend) getItem(position)).getContent());
		return convertView;
	}

	static class ViewHolder {
		ImageView imageIV;
		TextView titleTV;
		TextView contentTV;
		TextView statusTV;
		Button agreeBtn;
		Button rejectBtn;
	}

	// 按钮监听器
	class LVButtonListener implements OnClickListener {
		private int position;

		LVButtonListener(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View view) {
			int vid = view.getId();
			String username = ((NewFriend) getItem(position)).getUsername();
			String nickname = ((NewFriend) getItem(position)).getNickname();
			if (vid == holder.agreeBtn.getId()) {
				// 发送同意加好友数据包
				FCContactsManager.sendFriendAgree(username);
				// 将好友添加到花名册
				FCContactsManager.addContact(username, nickname, "我的好友");

				// 更新数据库
				String whose = FCConfig.getInstance().getUsername();
				User user = FCContactsManager.getContact(username, nickname,
						"我的好友");
				UserDao.getInstance(context).saveContact(user, whose);

				NewFriendDao.getInstance(context).updateCondition(
						((NewFriend) getItem(position)).getId(),
						NewFriend.AGREE, whose);

				// 更新数据集
				((NewFriend) getItem(position)).setCondition(NewFriend.AGREE);
				NewFriendsAdapter.this.notifyDataSetChanged();
			} else {
				// 发送拒绝加好友数据包
				Presence presence = new Presence(Presence.Type.unsubscribed);
				presence.setTo(StringUtils.escapeNode(username)
						+ "@iz23ds9vkjvz");
				connection.sendPacket(presence);

				NewFriendDao.getInstance(context).updateCondition(
						((NewFriend) getItem(position)).getId(),
						NewFriend.REJECT, FCConfig.getInstance().getUsername());
				((NewFriend) getItem(position)).setCondition(NewFriend.REJECT);
				NewFriendsAdapter.this.notifyDataSetChanged();
			}
		}

	}
}
