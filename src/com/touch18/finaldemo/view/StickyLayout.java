package com.touch18.finaldemo.view;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.OverScroller;
import android.widget.ScrollView;

import com.touch18.finaldemo.R;
import com.touch18.lib.util.UiUtils;

public class StickyLayout extends LinearLayout {

	public enum StickyState{
		TOP,MIDDLE,BOTTOM
	}
	
	private View mTop;
	private View mNav;
	private ViewPager mViewPager;

	private int mTopViewHeight;
	private ViewGroup mInnerScrollView;
	private boolean isTopHidden = false;

	private OverScroller mScroller;
//	private VelocityTracker mVelocityTracker;
	private int mTouchSlop;
//	private int mMaximumVelocity, mMinimumVelocity;
	
	// 屏幕高度，屏幕的1/12触发高度(包括State改变,图片旋转)
	private int screenWidth,screenHeight,triggerValue,triggerZoom;
	
	private int middleSticky;
	
	private int headerHeight = UiUtils.Dp2Px(45);
	
	private StickyState currentState = StickyState.MIDDLE;
	
	private float mLastX;
	private float mLastY;
	private boolean mDragging;

	public StickyLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOrientation(LinearLayout.VERTICAL);

		mScroller = new OverScroller(context);
//		mVelocityTracker = VelocityTracker.obtain();
//		mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
		mTouchSlop = ViewConfiguration.getTouchSlop();
//		mMaximumVelocity = ViewConfiguration.get(context).getScaledMaximumFlingVelocity();
//		mMinimumVelocity = ViewConfiguration.get(context).getScaledMinimumFlingVelocity();
		WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(dm);
		
		screenHeight = dm.heightPixels;
		screenWidth = dm.widthPixels;
		middleSticky = (int) (screenHeight * (13/24f));
		
		triggerZoom = (int) (screenHeight * (1/24f));			// 以 middleSticky 为基准 触发放大缩小
		triggerValue = (int) (screenHeight * (1/32f));			// 改变state 的最小单位
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mTop = findViewById(R.id.id_stickylayout_topview);
		mNav = findViewById(R.id.id_stickylayout_indicator);
		View view = findViewById(R.id.id_stickylayout_viewpager);
		if (!(view instanceof ViewPager)) {
			throw new RuntimeException("id_stickynavlayout_viewpager show used by ViewPager !");
		}
		mViewPager = (ViewPager) view;
		
		mViewPager.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
			
			@Override
			public boolean onPreDraw() {
				mViewPager.getViewTreeObserver().removeOnPreDrawListener(this);
				scrollTo(0, middleSticky);
				return false;
			}
		});
		
	}
	
//	public void setNeedAnimation(boolean animation){
//		this.needAnimation = animation;
//	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		ViewGroup.LayoutParams params = mViewPager.getLayoutParams();
		params.height = getMeasuredHeight() - mNav.getMeasuredHeight() - headerHeight;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mTopViewHeight = mTop.getMeasuredHeight();
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		int action = ev.getAction();
		float y = ev.getY();
		float x = ev.getX();

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mLastX = x;
			mLastY = y;
			break;
		case MotionEvent.ACTION_MOVE:
			float dx = x - mLastX;
			float dy = y - mLastY;
			
//			mLastX = x;
			mLastY = y;
			getCurrentScrollView();

			if (Math.abs(dy) > mTouchSlop && Math.abs(dy) > Math.abs(dx)) {
				mDragging = true;

				if (mInnerScrollView instanceof ScrollView) {
					if (!isTopHidden || (mInnerScrollView.getScrollY() == 0 && isTopHidden && dy > 0)) {
						return true;
					}
				} else if (mInnerScrollView instanceof ListView) {
					ListView lv = (ListView) mInnerScrollView;
					View c = lv.getChildAt(lv.getFirstVisiblePosition());
					if (!isTopHidden || (c != null && c.getTop() == 0 && isTopHidden && dy > 0)) {
						return true;
					}
				}

			}
			break;
		}
		return super.onInterceptTouchEvent(ev);
	}

	private void getCurrentScrollView() {

		int currentItem = mViewPager.getCurrentItem();
		PagerAdapter a = mViewPager.getAdapter();
		if (a instanceof FragmentPagerAdapter) {
			FragmentPagerAdapter fadapter = (FragmentPagerAdapter) a;
			Fragment item = fadapter.getItem(currentItem);
			mInnerScrollView = (ViewGroup) (item.getView().findViewById(R.id.id_stickylayout_innerscrollview));
		} else if (a instanceof FragmentStatePagerAdapter) {
			FragmentStatePagerAdapter fsAdapter = (FragmentStatePagerAdapter) a;
			Fragment item = fsAdapter.getItem(currentItem);
			mInnerScrollView = (ViewGroup) (item.getView().findViewById(R.id.id_stickylayout_innerscrollview));
		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
//		mVelocityTracker.addMovement(event);
		int action = event.getAction();
//		float x = event.getX();
		float y = event.getY();

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			if (!mScroller.isFinished())
				mScroller.abortAnimation();
//			mVelocityTracker.clear();
//			mVelocityTracker.addMovement(event);
			mLastY = y;
			return true;
		case MotionEvent.ACTION_MOVE:
			float dy = y - mLastY;
			
			if (!mDragging && Math.abs(dy) > mTouchSlop) {
				mDragging = true;
			}
			if (mDragging) {
				scrollBy(0, (int) -dy);
				mLastY = y;
			}
			break;
		case MotionEvent.ACTION_CANCEL:
			mDragging = false;
			if (!mScroller.isFinished()) {
				mScroller.abortAnimation();
			}
			break;
		case MotionEvent.ACTION_UP:
			mDragging = false;
/*			mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
			int velocityY = (int) mVelocityTracker.getYVelocity();
			if (Math.abs(velocityY) > mMinimumVelocity) {
				fling(-velocityY);
			}
			mVelocityTracker.clear();
			break;*/
			int currentY = getScrollY(); 
			
			switch (currentState) {
			case TOP:
				if(currentY > mTopViewHeight - triggerValue){
					currentState = StickyState.TOP;
				}else if(currentY > middleSticky + triggerValue){
					currentState = StickyState.MIDDLE;
				}else{
					currentState = StickyState.BOTTOM;
				}
				break;
			case MIDDLE:
				if(currentY > middleSticky + triggerValue){
					currentState = StickyState.TOP;
				}else if(currentY < middleSticky - triggerValue){
					currentState = StickyState.BOTTOM;
				}else{
					currentState = StickyState.MIDDLE;
				}
				break;
			case BOTTOM:
				if(currentY < triggerValue){
					currentState = StickyState.BOTTOM;
				}else if(currentY > middleSticky + triggerValue){
					currentState = StickyState.TOP;
				}else{
					currentState = StickyState.MIDDLE;
				}
				break;
			}
			
			switchState(currentState);
			
		}

		return super.onTouchEvent(event);
	}

	public StickyState getCurrentState() {
		return currentState;
	}
	
	public int getMiddleSticky() {
		return middleSticky;
	}

	public void switchState(StickyState currentState) {
		this.currentState = currentState;
		
		int scrollY = getScrollY();
		int dy = 0;
		if(currentState == StickyState.TOP){
//			scrollTo(0, mTopViewHeight);
			dy = mTopViewHeight - headerHeight - scrollY;
		}else if(currentState == StickyState.MIDDLE){
//			scrollTo(0, middleSticky);
			dy = middleSticky - scrollY;
		}else if(currentState == StickyState.BOTTOM){
//			scrollTo(0, 0);
			dy = 0 - scrollY;
		}
		
		mScroller.startScroll(0, scrollY, 0, dy,Math.abs(dy));
		invalidate();
	}
	

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			scrollTo(0, mScroller.getCurrY());
			invalidate();
		}
	}

/*	public void fling(int velocityY) {
		mScroller.fling(0, getScrollY(), 0, velocityY, 0, 0, 0, mTopViewHeight);
		invalidate();
	}*/

	private boolean zoomFlag = false;
//	private boolean needAnimation = false;	// viewPager中图片是否需要旋转
	
	public interface TriggerZoomListener{
		void zoom(boolean zoomUp);
	}
	
	private TriggerZoomListener mTriggerZoomListener;
	public void setTriggerZoomListener(TriggerZoomListener mTriggerZoomListener) {
		this.mTriggerZoomListener = mTriggerZoomListener;
	}
	
	private onScrollChangedListener mScrollChangedListener;
	public interface onScrollChangedListener{
		void onScroll(int l, int t, int oldl, int oldt);
	}
	public void setOnScrollChangedListener(onScrollChangedListener mScrollChangedListener) {
		this.mScrollChangedListener = mScrollChangedListener;
	}
	
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
//		if(needAnimation){
			// TODO 变化多大触发动画   1/12f
			if(!zoomFlag && t <= middleSticky - triggerZoom && oldt - t > 0){
				// 放大
				zoomFlag = !zoomFlag;
				if(mTriggerZoomListener != null){
					mTriggerZoomListener.zoom(true);
				}
			}else if(zoomFlag && t > middleSticky - triggerZoom && oldt - t < 0){
				// 缩小
				zoomFlag = !zoomFlag;
				if(mTriggerZoomListener != null){
					mTriggerZoomListener.zoom(false);
				}
			}
//		}
		
		if(mScrollChangedListener!= null)
			mScrollChangedListener.onScroll(l, t, oldl, oldt);
		
		super.onScrollChanged(l, t, oldl, oldt);
	}
	
	@Override
	public void scrollTo(int x, int y) {
		if (y < 0) {
			y = 0;
		}
		if (y > mTopViewHeight - headerHeight) {
			y = mTopViewHeight - headerHeight;
		}
		if (y != getScrollY()) {
			super.scrollTo(x, y);
		}

		isTopHidden = (getScrollY() == mTopViewHeight - headerHeight);

	}


}
