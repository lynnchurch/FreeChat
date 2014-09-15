package com.free.ui.activity;

import java.util.List;

import com.free.ui.FCConfig;
import com.free.ui.R;
import com.free.ui.adapter.ContactsAdapter;
import com.free.ui.adapter.NewFriendsAdapter;
import com.free.ui.db.NewFriendDao;
import com.free.ui.db.UserDao;
import com.free.ui.model.NewFriend;
import com.free.util.GroupUtils;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ExpandableListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class NewFriendsActivity extends BaseActivity {
	private TextView titleTV;
	private ListView listView;
	private List<NewFriend> newFriends;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
	}

	public void initView() {
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_new_friends);
		// 加载自定义标题栏
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebar_in);
		titleTV=(TextView)findViewById(R.id.title);
		titleTV.setText("新朋友");
		listView = (ListView) findViewById(R.id.listview);
	}

	@Override
	public void onStart() {
		super.onStart();
		String username = FCConfig.getInstance().getUsername();
		NewFriendDao.getInstance(context).setRead(username);
		newFriends = NewFriendDao.getInstance(this).getNewFriends(username);

		NewFriendsAdapter adapter = new NewFriendsAdapter(this, newFriends);
		listView.setAdapter(adapter);
	}

	public void back(View view) {
		finish();
	}
}
