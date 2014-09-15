package com.free.ui.activity;

import java.util.Map;

import com.free.chat.core.FCContactsManager;
import com.free.chat.core.XmppConnectionManager;
import com.free.exception.FCException;
import com.free.ui.FCConfig;
import com.free.ui.R;
import com.free.ui.db.UserDao;
import com.free.ui.fragment.ContactsFragment;
import com.free.ui.fragment.GroupsFragment;
import com.free.ui.fragment.NoticeFragment;
import com.free.ui.view.DragPager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;

public class MainActivity extends BaseActivity implements OnClickListener {

	private NoticeFragment noticeFragment;
	private ContactsFragment contactsFragment;
	private GroupsFragment groupsFragment;

	private View noticeLayout;
	private View contactsLayout;
	private View groupsLayout;

	private ImageView noticeImage;
	private ImageView contactsImage;
	private ImageView groupsImage;

	private TextView noticeText;
	private TextView contactsText;
	private TextView groupsText;

	private TextView usernameTV;
	private TextView nicknameTV;
	private TextView signatureTV;

	private TextView editDataTV;

	private Map<String, String> result;
	private String nickname;
	private String signature;

	/**
	 * 用于对Fragment进行管理
	 */
	private FragmentManager fragmentManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initRoster();
		initView();
		fragmentManager = getFragmentManager();
		// 第一次启动时选中第0个tab
		setTabSelection(0);

	}

	@Override
	public void initView() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		DragPager dragPager = new DragPager(this);
		// 设置背景图
		dragPager.setBackgroundResource(R.drawable.bg);
		// 设置内容实例
		LayoutInflater inflater = getLayoutInflater();
		View content = inflater.inflate(R.layout.activity_main, null);
		dragPager.setContent(content);

		// 设置菜单实例
		View mentu = inflater.inflate(R.layout.menu, null);
		dragPager.setMenu(mentu);

		setContentView(dragPager);
		noticeLayout = findViewById(R.id.notice_layout);
		contactsLayout = findViewById(R.id.contacts_layout);
		groupsLayout = findViewById(R.id.groups_layout);

		noticeImage = (ImageView) findViewById(R.id.notice_image);
		contactsImage = (ImageView) findViewById(R.id.contacts_image);
		groupsImage = (ImageView) findViewById(R.id.groups_image);

		noticeText = (TextView) findViewById(R.id.notice_text);
		contactsText = (TextView) findViewById(R.id.contacts_text);
		groupsText = (TextView) findViewById(R.id.groups_text);

		noticeLayout.setOnClickListener(this);
		contactsLayout.setOnClickListener(this);
		groupsLayout.setOnClickListener(this);

		usernameTV = (TextView) findViewById(R.id.tv_username);
		nicknameTV = (TextView) findViewById(R.id.tv_nickname);
		signatureTV = (TextView) findViewById(R.id.tv_signature);

		editDataTV = (TextView) findViewById(R.id.tv_edit_data);
		editDataTV.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(MainActivity.this,
						EditDataActivity.class);
				intent.putExtra("nickname", nickname);
				intent.putExtra("signature", signature);
				startActivity(intent);
			}

		});
	}

	@Override
	public void onStart() {
		super.onStart();
		Map<String, String> result = null;
		try {
			result = FCContactsManager.search(config.getUsername());
		} catch (FCException e) {
		}
		usernameTV.setText(config.getUsername());
		nickname = result.get("Name");
		FCConfig.getInstance().setNickname(nickname);
		nicknameTV.setText(nickname);
		signature = result.get("Signature");
		signatureTV.setText(signature);
	}

	/**
	 * 初始化花名册 服务重启时，更新花名册
	 */
	private void initRoster() {
		new Thread(new Runnable() {
			public void run() {
				// 如果SQLite中不存在当前登录用户的联系人列表，则保存联系人列表到SQLite（减少网络数据请求，节省流量）
				UserDao userDao = UserDao.getInstance(context);
				if (!userDao.isExist(config.getUsername())) {

					userDao.saveContacts(FCContactsManager.getContacts(),
							config.getUsername());
				}

			}
		}).start();

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.notice_layout:
			setTabSelection(0);
			break;
		case R.id.contacts_layout:
			setTabSelection(1);
			break;
		case R.id.groups_layout:
			setTabSelection(2);
			break;
		default:
			break;
		}
	}

	/**
	 * 根据传入的index参数来设置选中的tab页。
	 * 
	 * @param index
	 * 
	 */
	private void setTabSelection(int index) {
		clearSelection();
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		hideFragments(transaction);
		switch (index) {
		case 0:
			noticeImage.setImageResource(R.drawable.notice_selected);
			noticeText.setTextColor(Color.WHITE);
			if (noticeFragment == null) {
				noticeFragment = new NoticeFragment();
				transaction.add(R.id.content, noticeFragment);
			} else {
				transaction.show(noticeFragment);
			}
			break;
		case 1:
			contactsImage.setImageResource(R.drawable.contacts_selected);
			contactsText.setTextColor(Color.WHITE);
			if (contactsFragment == null) {
				contactsFragment = new ContactsFragment();
				transaction.add(R.id.content, contactsFragment);
			} else {
				transaction.show(contactsFragment);
			}
			break;
		case 2:
			groupsImage.setImageResource(R.drawable.groups_selected);
			groupsText.setTextColor(Color.WHITE);
			if (groupsFragment == null) {
				groupsFragment = new GroupsFragment();
				transaction.add(R.id.content, groupsFragment);
			} else {
				transaction.show(groupsFragment);
			}
			break;
		default:
			noticeImage.setImageResource(R.drawable.notice_selected);
			noticeText.setTextColor(Color.WHITE);
			if (noticeFragment == null) {
				noticeFragment = new NoticeFragment();
				transaction.add(R.id.content, noticeFragment);
			} else {
				transaction.show(noticeFragment);
			}
			break;
		}
		transaction.commit();
	}

	/**
	 * 清除掉所有的选中状态。
	 */
	private void clearSelection() {
		noticeImage.setImageResource(R.drawable.notice_unselected);
		noticeText.setTextColor(Color.parseColor("#82858b"));
		contactsImage.setImageResource(R.drawable.contacts_unselected);
		contactsText.setTextColor(Color.parseColor("#82858b"));
		groupsImage.setImageResource(R.drawable.groups_unselected);
		groupsText.setTextColor(Color.parseColor("#82858b"));
	}

	/**
	 * 将所有的Fragment都置为隐藏状态。
	 * 
	 * @param transaction
	 */
	private void hideFragments(FragmentTransaction transaction) {
		if (noticeFragment != null) {
			transaction.hide(noticeFragment);
		}
		if (contactsFragment != null) {
			transaction.hide(contactsFragment);
		}
		if (groupsFragment != null) {
			transaction.hide(groupsFragment);
		}
	}

	/**
	 * 捕获返回键
	 */
	@Override
	public void onBackPressed() {
		moveTaskToBack(true);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		stopService();
		XmppConnectionManager.disConnect();
	}
}
