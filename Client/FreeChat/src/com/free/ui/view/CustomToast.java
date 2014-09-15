package com.free.ui.view;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.free.ui.R;

public class CustomToast {

	private static Toast toast=null;

	public static void showShortToast(Context context, String message) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// 获取自定义布局实例	
		View view = inflater.inflate(R.layout.custom_toast, null);
		// 设置提示内容
		TextView text = (TextView) view.findViewById(R.id.toast_message);
		text.setText(message);
		// 确保只有一个Toast实例创建
		if (toast == null) {
			toast = new Toast(context);
			toast.setDuration(Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);		
		}
		// 加载自定义布局
		toast.setView(view);
		toast.show();
	}
}