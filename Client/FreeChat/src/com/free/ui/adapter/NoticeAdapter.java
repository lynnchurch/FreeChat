package com.free.ui.adapter;

import java.util.List;

import com.free.ui.FCConfig;
import com.free.ui.R;
import com.free.ui.db.FCMessageDao;
import com.free.ui.db.NewFriendDao;
import com.free.ui.db.NoticeDao;
import com.free.ui.model.Notice;
import com.free.ui.view.BadgeView;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NoticeAdapter extends BaseAdapter {
	private Context context;
	private List<Notice> notices;
	private LayoutInflater inflater;

	public NoticeAdapter(Context context, List<Notice> notices) {
		this.context = context;
		inflater = LayoutInflater.from(context);
		this.notices = notices;
	}

	@Override
	public int getCount() {
		return notices.size();
	}

	@Override
	public Object getItem(int id) {
		return notices.get(id);
	}

	@Override
	public long getItemId(int id) {
		return id;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.listview_notice_item, null);
			holder = new ViewHolder();
			holder.imageIV = (ImageView) convertView
					.findViewById(R.id.portrait);
			holder.titleTV = (TextView) convertView.findViewById(R.id.title);
			holder.contentTV = (TextView) convertView
					.findViewById(R.id.content);
			holder.timeTV = (TextView) convertView.findViewById(R.id.time);
			holder.badgeTV = (TextView) convertView.findViewById(R.id.badge);

			holder.badge = new BadgeView(context, holder.badgeTV);
			holder.badge.setTextSize(12);

			// 保存holder实例进行复用
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		String title = ((Notice) getItem(position)).getTitle();
		holder.titleTV.setText(title);
		holder.contentTV.setText(((Notice) getItem(position)).getContent());
		holder.timeTV.setText(((Notice) getItem(position)).getTime());

		String whose = FCConfig.getInstance().getUsername();
		int count = 0;

		if (title.equals("新朋友")) {
			count = NewFriendDao.getInstance(context).getUnReadCount(whose);
		} else {
			count = FCMessageDao.getInstance(context).getUnReadCountByWith(
					notices.get(position).getFrom(), whose);
		}
		if (count > 99) {
			holder.badge.setText("99+");
			holder.badge.show();
		} else if (count == 0) {
			holder.badge.hide();
		} else {
			holder.badge.setText("" + count);
			holder.badge.show();
		}

		return convertView;

	}

	static class ViewHolder {
		ImageView imageIV;
		TextView titleTV;
		TextView contentTV;
		TextView timeTV;
		TextView badgeTV;
		BadgeView badge;
	}

}
