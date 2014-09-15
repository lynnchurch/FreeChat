package com.free.ui.view;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

public class DragPager extends RelativeLayout {

	private View mMenu; // 菜单
	private View mContent; // 内容视图

	private int mStartx = 0; // 点击开始
	private float mContentStartTransX = 0; // 内容视图开始点击滑动的距离
	private float mMenuStartTransX = 0;// 菜单开始点击滑动的距离
	private int DEFAULT_RIGHT_MARGIN = getResources().getDisplayMetrics().widthPixels * 3 / 8;

	ObjectAnimator mContent_animator;// 内容视图动画
	ObjectAnimator mMenu_animator;// 菜单动画
	private boolean misDrag = false; // 当前是否拖动
	final int DEFALT_DRAG_DISTANCE = 40;// 滑动的缺省值

	public DragPager(Context context) {
		super(context);
	}

	public DragPager(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public DragPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setMenu(View menu) {
		mMenu = menu;
		LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		layoutParams.rightMargin = DEFAULT_RIGHT_MARGIN;
		addView(menu, layoutParams);
		mMenu.setScaleY(0.75f);
		mMenu.setTranslationX(-DEFAULT_RIGHT_MARGIN * 3);
	}

	public void setContent(View content) {
		mContent = content;
		LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		addView(content, layoutParams);
	}

	/**
	 * 计算当前MenuX可滑动的位置
	 * 
	 * @param distance
	 * @return
	 */
	private float getMenuDragX(float distance) {
		// Log.e("Menudistance",""+distance);
		float newX = distance + mMenuStartTransX;
		if (newX <= -DEFAULT_RIGHT_MARGIN * 3) {
			newX = -DEFAULT_RIGHT_MARGIN * 3;
		} else if (newX >= 0) {
			newX = 0;
		}
		return newX;
	}

	/**
	 * 计算内容视图X滑动的位置
	 * 
	 * @param distance
	 * @return
	 */
	private float getContentDragX(float distance) {
		// Log.e("Contentdistance",""+distance);
		float newX = distance + mContentStartTransX;
		if (newX <= 0) {
			newX = 0;
		} else if (newX >= mContent.getWidth() - DEFAULT_RIGHT_MARGIN) {
			newX = mContent.getWidth() - DEFAULT_RIGHT_MARGIN;
		}
		return newX;
	}

	/**
	 * 停止动画
	 */
	private void stopAnimation() {
		if (mContent_animator != null && mMenu_animator != null) {
			mContent_animator.cancel();
			mMenu_animator.cancel();
		}
	}

	// 移动
	private void move(float distance) {
		float nowx = getContentDragX(distance);
		if (nowx != mContent.getTranslationX()) {
			mContent.setTranslationX(nowx);
			float scale = nowx / (mContent.getWidth() - DEFAULT_RIGHT_MARGIN); // 计算alph范围是0-1
			mContent.setScaleY(1 - scale * 0.25f); // 内容的Y缩放
			mContent.setScaleX(1 - scale * 0.25f);
			mMenu.setTranslationX(getMenuDragX(distance)); // 设置菜单的距离
			mMenu.setScaleY(0.75f + scale * 0.25f);// 设置菜单的Y缩放
			mMenu.setAlpha(scale);// 菜单的Alpha
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			stopAnimation(); // 停止动画
			mStartx = (int) ev.getRawX(); // 获取点击初始化x
			if (mContent != null)
				mContentStartTransX = mContent.getTranslationX();// 获取初始化x
			if (mMenu != null)
				mMenuStartTransX = mMenu.getTranslationX();
			break;
		case MotionEvent.ACTION_MOVE:
			// 当左右滑动到阀值就认为是在拖动了
			if (Math.abs(ev.getRawX() - mStartx) > DEFALT_DRAG_DISTANCE) {
				misDrag = true;
			}
			break;
		case MotionEvent.ACTION_UP:
			misDrag = false;
			break;
		}
		return super.dispatchTouchEvent(ev);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (misDrag) {
			return true;// 不进行向下分发了已经拦截
		} else {
			return false; // 把事件交给子view处理然后通过子view的dispatch分发
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float distance = event.getRawX() - mStartx;
		if (event.getActionIndex() > 1)
			return true;
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			return true; // 消费掉这个事件让它不传递
		case MotionEvent.ACTION_MOVE:
			move(distance);
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			boolean orientation; // 滑动的方向，true向右，false左
			if (distance > 0)
				orientation = true;
			else
				orientation = false;
			toggle(orientation); // 窗口回弹
			break;
		}
		return super.onTouchEvent(event);
	}

	/**
	 * 回弹的动画
	 */
	private void toggle(boolean orientation) {
		// Log.e("orientation",""+orientation);
		// Log.e("mContentX",""+mContent.getTranslationX());
		// Log.e("DEFAULT_RIGHT_MARGIN",""+DEFAULT_RIGHT_MARGIN);

		if ((orientation == true && mContent.getTranslationX() > DEFAULT_RIGHT_MARGIN / 5)
				|| (orientation == false && mContent.getTranslationX() > DEFAULT_RIGHT_MARGIN * 3 / 2)) {
			// 向右动画
			// 先是content动画
			PropertyValuesHolder content_transani = PropertyValuesHolder
					.ofFloat("translationX", mContent.getTranslationX(),
							mContent.getWidth() - DEFAULT_RIGHT_MARGIN);

			PropertyValuesHolder content_ScaleY = PropertyValuesHolder.ofFloat(
					"scaleY", mContent.getScaleY(), 0.75f);
			PropertyValuesHolder content_ScaleX = PropertyValuesHolder.ofFloat(
					"scaleX", mContent.getScaleX(), 0.75f);

			mContent_animator = ObjectAnimator.ofPropertyValuesHolder(mContent,
					content_transani, content_ScaleY, content_ScaleX);
			mContent_animator.start();

			// 这里菜单动画

			PropertyValuesHolder menu_transani = PropertyValuesHolder.ofFloat(
					"translationX", mMenu.getTranslationX(), 0);
			PropertyValuesHolder menu_ScaleY = PropertyValuesHolder.ofFloat(
					"scaleY", mMenu.getScaleY(), 1f);
			PropertyValuesHolder menu_Alpha = PropertyValuesHolder.ofFloat(
					"Alpha", mMenu.getAlpha(), 1f);
			mMenu_animator = ObjectAnimator.ofPropertyValuesHolder(mMenu,
					menu_transani, menu_ScaleY, menu_Alpha);
			mMenu_animator.start();

		} else {
			// 向左动画
			PropertyValuesHolder content_transanx = PropertyValuesHolder
					.ofFloat("translationX", mContent.getTranslationX(), 0);

			PropertyValuesHolder content_ScaleY = PropertyValuesHolder.ofFloat(
					"scaleY", mContent.getScaleY(), 1.0f);
			PropertyValuesHolder content_ScaleX = PropertyValuesHolder.ofFloat(
					"scaleX", mContent.getScaleX(), 1.0f);

			mContent_animator = ObjectAnimator.ofPropertyValuesHolder(mContent,
					content_transanx, content_ScaleX, content_ScaleY);

			mContent_animator.start();

			PropertyValuesHolder menu_transani = PropertyValuesHolder.ofFloat(
					"translationX", mMenu.getTranslationX(),
					-DEFAULT_RIGHT_MARGIN * 3);
			PropertyValuesHolder menu_ScaleY = PropertyValuesHolder.ofFloat(
					"scaleY", mMenu.getScaleY(), 0.75f);
			PropertyValuesHolder menu_Alpha = PropertyValuesHolder.ofFloat(
					"Alpha", mMenu.getAlpha(), 0f);
			mMenu_animator = ObjectAnimator.ofPropertyValuesHolder(mMenu,
					menu_transani, menu_ScaleY, menu_Alpha);
			mMenu_animator.start();
		}

	}

}