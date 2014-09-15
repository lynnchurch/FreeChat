package com.free.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.util.Log;

import com.free.ui.model.User;

public class GroupUtils {
	private static List<String> groupsList;

	/**
	 * 返回用户组
	 * 
	 * @param users
	 * @return List<String>
	 */
	public static List<String> getGroups(List<User> users) {
		groupsList = new ArrayList<String>();
		for (User user : users) {
			if (!groupsList.contains(user.getGroup())) {
				groupsList.add(user.getGroup());
			}
		}
		
		return groupsList;
	}

	/**
	 * 返回已分好组的好友列表
	 * 
	 * @param users
	 * @return List<List<User>>
	 */
	public static List<List<User>> getFriends(List<User> users) {

		List<List<User>> paramUsers = new ArrayList<List<User>>();
		for(int i = 0; i < groupsList.size(); i++){
			paramUsers.add(new ArrayList<User>());
		}
		for (User user : users) {
			int i=groupsList.indexOf(user.getGroup());
			paramUsers.get(i).add(user);
		}
		return paramUsers;
	}
}
