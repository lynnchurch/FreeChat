package com.free.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import com.free.chat.core.FCContactsManager;
import com.free.ui.FCConfig;
import com.free.ui.R;
import com.free.ui.activity.AddFriendActivity;
import com.free.ui.activity.ChatActivity;
import com.free.ui.adapter.ContactsAdapter;
import com.free.ui.db.UserDao;
import com.free.ui.model.User;
import com.free.ui.view.XExpandableListView;
import com.free.util.GroupUtils;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class ContactsFragment extends Fragment {
	private XExpandableListView xExpandableListview;
	private TextView addTextView;
	// 联系人列表
	private List<User> users;
	// 群组
	private List<String> groups;
	// 好友
	private List<List<User>> friends;
	private BaseExpandableListAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View contactsLayout = inflater.inflate(R.layout.contacts_layout,
				container, false);
		return contactsLayout;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// 配置XExpandableListView实例
		xExpandableListview = (XExpandableListView) getActivity().findViewById(
				R.id.expandable_listview);
		// 设置下拉可用
		xExpandableListview.setPullRefreshEnable(true);
		// 设置上拉不可用
		xExpandableListview.setPullLoadEnable(false);
		// 设置刷新监听器
		xExpandableListview
				.setXListViewListener(new XExpandableListView.IXListViewListener() {
					public void onRefresh() {
						// 刷新时所执行的任务
						new Thread(new Runnable() {
							@Override
							public void run() {
								// 更新好友信息
								String whose = FCConfig.getInstance()
										.getUsername();
								UserDao userDao = UserDao
										.getInstance(getActivity());

								userDao.updateContacts(whose);
								List<User> _users = userDao.getContacts(whose);

								List<List<User>> _friends = GroupUtils
										.getFriends(_users);
								for (int i = 0; i < _friends.size(); i++) {
									for (int j = 0; j < _friends.get(i).size(); j++) {
										friends.get(i)
												.get(j)
												.setNickname(
														_friends.get(i).get(j)
																.getNickname());
										friends.get(i)
												.get(j)
												.setSignature(
														_friends.get(i).get(j)
																.getSignature());
									}
								}
								
								// 更新UI
								getActivity().runOnUiThread(new Runnable() {
									public void run() {
										adapter.notifyDataSetChanged();
										xExpandableListview.stopRefresh();
									}
								});

							}
						}).start();

					}

					@Override
					public void onLoadMore() {
						//
					}
				});

		xExpandableListview.setOnChildClickListener(new OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				String nickname = friends.get(groupPosition).get(childPosition)
						.getNickname();
				String username = friends.get(groupPosition).get(childPosition)
						.getUsername();
				Intent intent = new Intent(getActivity(), ChatActivity.class);
				intent.putExtra("nickname", nickname);
				intent.putExtra("username", username);
				getActivity().startActivity(intent);
				return true;
			}
		});

		xExpandableListview
				.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
					@Override
					public void onCreateContextMenu(ContextMenu menu, View v,
							ContextMenuInfo menuInfo) {
						Toast.makeText(getActivity(), "嘿嘿,被长按了",
								Toast.LENGTH_SHORT).show();
					}
				});

		addTextView = (TextView) getActivity().findViewById(R.id.add);
		addTextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),
						AddFriendActivity.class);
				startActivity(intent);
			}
		});
	}

	@Override
	public void onStart() {
		super.onStart();
		users = UserDao.getInstance(getActivity()).getContacts(
				FCConfig.getInstance().getUsername());
		groups = GroupUtils.getGroups(users);
		friends = GroupUtils.getFriends(users);
		adapter = new ContactsAdapter(this.getActivity(), groups, friends);
		xExpandableListview.setAdapter(adapter);
	}
}