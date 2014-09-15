package com.free.ui.fragment;

import java.util.List;

import com.free.ui.Constant;
import com.free.ui.FCConfig;
import com.free.ui.R;
import com.free.ui.activity.ChatActivity;
import com.free.ui.activity.NewFriendsActivity;
import com.free.ui.adapter.NoticeAdapter;
import com.free.ui.db.FCMessageDao;
import com.free.ui.db.NewFriendDao;
import com.free.ui.db.NoticeDao;
import com.free.ui.db.UserDao;
import com.free.ui.model.Notice;
import com.free.ui.view.BadgeView;
import com.free.ui.view.XListView;
import com.free.util.SortList;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

public class NoticeFragment extends Fragment {
	private XListView xListview;
	private List<Notice> notices;
	private NoticeAdapter adapter;
	private NoticeReceiver receiver;
	private BadgeView noticeBV; // 通知气泡
	private int unreadCount; // 未读通知总数
	private String whose;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View messageLayout = inflater.inflate(R.layout.notice_layout,
				container, false);
		return messageLayout;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		whose = FCConfig.getInstance().getUsername();
		receiver = new NoticeReceiver();
		// 配置消息数提示气泡
		noticeBV = new BadgeView(getActivity(), getActivity().findViewById(
				R.id.notice_image));
		noticeBV.setTextSize(12);
		noticeBV.setBadgeMargin(33, 0);
		// 配置XListView实例
		xListview = (XListView) getActivity().findViewById(R.id.listview);
		// 设置下拉可用
		xListview.setPullRefreshEnable(true);
		// 设置上拉不可用
		xListview.setPullLoadEnable(false);
		// 设置刷新监听器
		xListview.setXListViewListener(new XListView.IXListViewListener() {
			public void onRefresh() {
				// 刷新时所执行的任务
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							Thread.sleep(3000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						getActivity().runOnUiThread(new Runnable() {
							public void run() {
								xListview.stopRefresh();
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
	}

	@Override
	public void onStart() {
		super.onStart();
		notices = NoticeDao.getInstance(getActivity()).getNotices(
				FCConfig.getInstance().getUsername());
		// 调用排序通用类
		SortList<Notice> sortList = new SortList<Notice>();
		sortList.sort(notices, "getTime",SortList.DESC);
		
		adapter = new NoticeAdapter(this.getActivity(), notices);
		xListview.setAdapter(adapter);
		// 注册广播接收器
		IntentFilter filter = new IntentFilter();
		// 好友订阅
		filter.addAction(Constant.ACTION_ROSTER_SUBSCRIPTION);
		filter.addAction(Constant.ACTION_CHAT_MESSAGE);
		getActivity().registerReceiver(receiver, filter);
		// 设置列表项点击监听器
		xListview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view,
					int position, long arg3) {
				if (notices.get(position - 1).getType() == Notice.NEWFRIEND) {
					// 设置所有的新朋友读取状态为已读
					NewFriendDao.getInstance(getActivity()).setRead(
							FCConfig.getInstance().getUsername());

					getActivity()
							.startActivity(
									new Intent(getActivity(),
											NewFriendsActivity.class));
				} else {
					String username = notices.get(position - 1).getFrom();
					// 设置某好友消息读取状态为已读
					FCMessageDao.getInstance(getActivity()).setRead(username,
							whose);
					String nickname = UserDao.getInstance(getActivity())
							.getNickname(username, whose);
					Intent intent = new Intent(getActivity(),
							ChatActivity.class);
					intent.putExtra("nickname", nickname);
					intent.putExtra("username", username);
					getActivity().startActivity(intent);

				}
			}
		});

		// 设置列表项长按监听器
		xListview
				.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

					@Override
					public void onCreateContextMenu(ContextMenu menu, View v,
							ContextMenuInfo menuInfo) {
						menu.setHeaderTitle("长按菜单");
						menu.add(0, 0, 0, "长按菜单项0");
						menu.add(0, 1, 0, "长按菜单项1");
					}
				});
		refreshBadge();
	}

	@Override
	public void onStop() {
		super.onStop();
		getActivity().unregisterReceiver(receiver);
	}

	/**
	 * 对未读通知数气泡进行刷新
	 */
	public void refreshBadge() {
		unreadCount = NewFriendDao.getInstance(this.getActivity())
				.getUnReadCount(whose);
		unreadCount = unreadCount
				+ FCMessageDao.getInstance(this.getActivity()).getUnReadCount(
						whose);
		if (unreadCount == 0) {
			noticeBV.hide();
		} else if (unreadCount > 99) {
			noticeBV.setText("99+");
			noticeBV.show();
		} else {
			noticeBV.setText("" + unreadCount);
			noticeBV.show();
		}
	}

	private class NoticeReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Notice notice = (Notice) intent.getSerializableExtra("notice");
			String action = intent.getAction();
			// 更新新朋友通知
			if (action.equals(Constant.ACTION_ROSTER_SUBSCRIPTION)) {
				boolean isExist = false;
				for (int i = 0; i < notices.size(); i++) {
					if (notices.get(i).getType() == Notice.NEWFRIEND) {
						notices.get(i).setTime(notice.getTime());
						notices.get(i).setContent(notice.getContent());
						notices.get(i).setStatus(Notice.UNREAD);
						isExist = true;
					}
				}
				if (!isExist) {
					notices.add(notice);
				}
			}
			// 更新聊天消息通知
			if (action.endsWith(Constant.ACTION_CHAT_MESSAGE)) {
				boolean isExist = false;
				for (int i = 0; i < notices.size(); i++) {
					if (notices.get(i).getFrom().equals(notice.getFrom())) {
						notices.get(i).setTime(notice.getTime());
						notices.get(i).setContent(notice.getContent());
						notices.get(i).setStatus(Notice.UNREAD);
						isExist = true;
					}
				}
				if (!isExist) {
					notices.add(notice);
				}
			}

			adapter.notifyDataSetChanged();
			refreshBadge();
		}
	}
}
