package com.free.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.free.ui.R;


public class XListViewHeader extends LinearLayout {
	private final String TIP_HEADER_NORMAL="下拉刷新";
    private final String TIP_HEADER_READY="松开刷新";
    private final String TIP_HEADER_LOADING="正在加载...";
    public final String TIP_HEADER_LAST_TIME="最近更新：";
    private final String TIP_HEADER_SUCCESS="刷新成功";
    private final String TIP_HEADER_FAILED="刷新失败";
	
	
	private LinearLayout mContainer;
	private ImageView mArrowImageView;
	private ImageView mTipImageView;
	private ProgressBar mProgressBar;
	private TextView mHintTextView;
	private int mState = STATE_NORMAL;

	private Animation mRotateUpAnim;
	private Animation mRotateDownAnim;
	
	private final int ROTATE_ANIM_DURATION = 180;
	
	public final static int STATE_NORMAL = 0;
	public final static int STATE_READY = 1;
	public final static int STATE_REFRESHING = 2;
	public final static int STATE_SUCCESS = 3;
	public final static int STATE_FAILED = 4;
	public final static int STATE_RECOVERY = 5;

	public XListViewHeader(Context context) {
		super(context);
		initView(context);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public XListViewHeader(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	private void initView(Context context) {
		// 初始情况，设置下拉刷新view高度为0
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, 0);
		mContainer = (LinearLayout) LayoutInflater.from(context).inflate(
				R.layout.xlistview_header, null);
		addView(mContainer, lp);
		setGravity(Gravity.BOTTOM);

		mArrowImageView = (ImageView)findViewById(R.id.xlistview_header_arrow);
		mTipImageView = (ImageView)findViewById(R.id.xlistview_header_tip);
		mHintTextView = (TextView)findViewById(R.id.xlistview_header_hint_textview);
		mProgressBar = (ProgressBar)findViewById(R.id.xlistview_header_progressbar);
		
		mHintTextView.setText(TIP_HEADER_NORMAL);
		
		mRotateUpAnim = new RotateAnimation(0.0f, -180.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		mRotateUpAnim.setDuration(ROTATE_ANIM_DURATION);
		mRotateUpAnim.setFillAfter(true);
		mRotateDownAnim = new RotateAnimation(-180.0f, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		mRotateDownAnim.setDuration(ROTATE_ANIM_DURATION);
		mRotateDownAnim.setFillAfter(true);
	}

	public void setState(int state) {
		if (state == mState) return ;
		
		if (state == STATE_REFRESHING) {	// 显示进度
			mArrowImageView.clearAnimation();
			mArrowImageView.setVisibility(View.INVISIBLE);
			mProgressBar.setVisibility(View.VISIBLE);
		} else {	// 显示箭头图片
			mArrowImageView.setVisibility(View.VISIBLE);
			mTipImageView.setVisibility(View.GONE);
			mProgressBar.setVisibility(View.INVISIBLE);
		}
		
		switch(state){
		case STATE_NORMAL:
			if (mState == STATE_READY) {
				mArrowImageView.startAnimation(mRotateDownAnim);
			}
			if (mState == STATE_REFRESHING) {
				mArrowImageView.clearAnimation();
			}
			mHintTextView.setText(TIP_HEADER_NORMAL);
			break;
		case STATE_READY:
			if (mState != STATE_READY) {
				mArrowImageView.clearAnimation();
				mArrowImageView.startAnimation(mRotateUpAnim);
				mHintTextView.setText(TIP_HEADER_READY);
			}
			break;
		case STATE_REFRESHING:
			mHintTextView.setText(TIP_HEADER_LOADING);
			break;
		case STATE_SUCCESS:
			mArrowImageView.setVisibility(View.GONE);
			mTipImageView.setVisibility(View.VISIBLE);
			mTipImageView.setImageResource(R.drawable.refresh_success);
			mHintTextView.setText(TIP_HEADER_SUCCESS);
			break;
		case STATE_FAILED:
			mArrowImageView.setVisibility(View.GONE);
			mTipImageView.setVisibility(View.VISIBLE);
			mTipImageView.setImageResource(R.drawable.refresh_fail);
			mHintTextView.setText(TIP_HEADER_FAILED);
			break;
		case STATE_RECOVERY:
			mArrowImageView.setVisibility(View.VISIBLE);
			mTipImageView.setVisibility(View.GONE);
			mHintTextView.setText(TIP_HEADER_NORMAL);
			//state=STATE_NORMAL;
			break;
			default:
		}
		
		mState = state;
	}
	
	public void setVisiableHeight(int height) {
		if (height < 0){
			height = 0;
		}
			
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mContainer
				.getLayoutParams();
		lp.height = height;
		mContainer.setLayoutParams(lp);
	}

	public int getVisiableHeight() {
		return mContainer.getHeight();
	}

}

