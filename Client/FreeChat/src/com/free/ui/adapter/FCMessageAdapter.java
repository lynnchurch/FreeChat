package com.free.ui.adapter;

import java.util.List;

import com.free.ui.FCConfig;
import com.free.ui.R;
import com.free.ui.db.NewFriendDao;
import com.free.ui.model.FCMessage;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FCMessageAdapter extends BaseAdapter {

	private List<FCMessage> fCMessages;
	private LayoutInflater inflater;
	private Context context;
	private String whose;
	private String nickname;

	public FCMessageAdapter(Context context, List<FCMessage> fCMessages,
			String nickname) {
		this.context = context;
		this.fCMessages = fCMessages;
		inflater = LayoutInflater.from(context);
		whose = FCConfig.getInstance().getNickname();
		this.nickname = nickname;
	}

	public int getCount() {
		return fCMessages.size();
	}

	public Object getItem(int position) {
		return fCMessages.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemViewType(int position) {
		int type = ((FCMessage) getItem(position)).getType();
		return type;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		FCMessage message = (FCMessage) getItem(position);
		int type = message.getType();
		ViewHolder viewHolder = null;
//		Log.e("X", "XXX");
		if (convertView == null) {
//			Log.e("Y", "YYY");
			if (getItemViewType(position) == FCMessage.RECEIVE) {
				// Log.e("X1", "XXX");
				convertView = inflater.inflate(
						R.layout.listview_chat_left_item, null);
			} else {
				// Log.e("X2", "XXX");
				convertView = inflater.inflate(
						R.layout.listview_chat_right_item, null);
			}
			viewHolder = new ViewHolder();
			viewHolder.portraitIV = (ImageView) convertView
					.findViewById(R.id.iv_portrait);
			viewHolder.timeTV = (TextView) convertView
					.findViewById(R.id.tv_time);
			viewHolder.contentTV = (TextView) convertView
					.findViewById(R.id.tv_content);
			viewHolder.nicknameTV = (TextView) convertView
					.findViewById(R.id.tv_nickname);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.timeTV.setText(message.getTime());
		viewHolder.contentTV.setText(message.getContent());
		if (type == FCMessage.RECEIVE) {
			viewHolder.nicknameTV.setText(nickname);
		} else {
			viewHolder.nicknameTV.setText(whose);
		}
		return convertView;
	}

	static class ViewHolder {
		ImageView portraitIV;
		TextView timeTV;
		TextView contentTV;
		TextView nicknameTV;
	}

}
