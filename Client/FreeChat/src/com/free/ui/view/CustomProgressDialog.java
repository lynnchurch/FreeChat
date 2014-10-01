package com.free.ui.view;

import com.free.ui.R;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomProgressDialog extends Dialog {
	private static CustomProgressDialog customProgressDialog = null;
	private static Context context = null;

	private CustomProgressDialog(Context context) {
		super(context);
	}

	private CustomProgressDialog(Context context, int theme) {
		super(context, theme);
	}

	public static CustomProgressDialog createDialog(Context _context) {
		if (customProgressDialog == null || context != _context) {
			context = _context;
			customProgressDialog = new CustomProgressDialog(_context,
					R.style.CustomProgressDialogStyle);
			customProgressDialog
					.setContentView(R.layout.custom_progress_dialog);
			customProgressDialog.getWindow().getAttributes().gravity = Gravity.TOP;
			customProgressDialog.setCancelable(false);
		}
		return customProgressDialog;
	}

	public void onWindowFocusChanged(boolean hasFocus) {

		ImageView imageView = (ImageView) findViewById(R.id.animation);
		AnimationDrawable animationDrawable = (AnimationDrawable) imageView
				.getBackground();
		animationDrawable.start();
	}

	public CustomProgressDialog setTitile(String strTitle) {
		super.setTitle(strTitle);
		return customProgressDialog;
	}

	public CustomProgressDialog setMessage(String strMessage) {
		TextView tvMsg = (TextView) findViewById(R.id.password_msg);

		if (tvMsg != null) {
			tvMsg.setText(strMessage);
		}

		return customProgressDialog;
	}
}