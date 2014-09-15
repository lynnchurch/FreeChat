package com.free.util;

import android.graphics.Color;
import android.os.CountDownTimer;
import android.widget.Button;

public class FCCountDownTimer extends CountDownTimer {
	Button button;

	public FCCountDownTimer(long millisInFuture, long countDownInterval,
			Button button) {
		super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
		this.button = button;
	}

	@Override
	public void onFinish() {// 计时完毕时触发
		button.setText("重新发送");
		button.setClickable(true);
	}

	@Override
	public void onTick(long millisUntilFinished) {// 计时过程显示
		button.setClickable(false);
		button.setText(millisUntilFinished / 1000 + "秒后重新发送");
	}
}
