package com.free.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import com.free.ui.R;
import com.free.ui.model.User;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class ContactsAdapter extends BaseExpandableListAdapter {
	private List<String> groups;
	private List<List<User>> friends;
	private LayoutInflater inflater;

	public ContactsAdapter(Context context, List<String> groups,
			List<List<User>> friend) {
		inflater = LayoutInflater.from(context);
		this.groups = groups;
		this.friends = friend;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return friends.get(groupPosition).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		convertView = inflater.inflate(R.layout.listview_child_item, null);
		TextView nickTextView = (TextView) convertView
				.findViewById(R.id.listview_child_nick);
		nickTextView.setText(((User) getChild(groupPosition, childPosition))
				.getNickname());
		TextView signaTextView = (TextView) convertView
				.findViewById(R.id.listview_child_signature);
		signaTextView.setText(((User) getChild(groupPosition, childPosition))
				.getSignature());
		return convertView;

	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return friends.get(groupPosition).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return groups.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return groups.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup arg3) {
		convertView = inflater.inflate(R.layout.listview_group_item, null);
		TextView groupNameTextView = (TextView) convertView
				.findViewById(R.id.listview_group_name);
		groupNameTextView.setText(getGroup(groupPosition).toString());
		return convertView;
	}

	public boolean hasStableIds() {
		return true;
	}

	// 子选项是否可以选择
	public boolean isChildSelectable(int arg0, int arg1) {
		return true;
	}
}