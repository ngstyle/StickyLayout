package com.touch18.finaldemo.view;

import com.touch18.finaldemo.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;


public class MyRatingBar extends LinearLayout {
	private TypedArray typedArray;
	private int ratingNum;
	private int ratingDrawableFull;
	private int ratingDrawableHalf;
	private int ratingDrawableEmpty;
	private boolean isIndicator;
	private float rating;
	private float previousRating;
	
	private Context context;
	private int width, height;
	private int layout_width;
	private int scroolX = 0;//触摸横向位移
	
	private OnTouchListener touchListener;
	private OnRatingBarChangeListener mOnRatingBarChangeListener; 
    public void setOnRatingBarChangeListener(OnRatingBarChangeListener mOnRatingBarChangeListener) {
		this.mOnRatingBarChangeListener = mOnRatingBarChangeListener;
	}

	public interface OnRatingBarChangeListener {
        void onRatingChanged(float rating);
    }

	public MyRatingBar(Context context){
		this(context,null);
	}
	
	public MyRatingBar(Context context, AttributeSet attrs) {
		super(context,attrs);
		this.context = context;
		typedArray = context.obtainStyledAttributes(attrs, R.styleable.MyRatingBar);
		initRatingView();
		typedArray.recycle();
	}
	
	
	/**初始化评分控件*/
	private void initRatingView(){
		ratingNum = typedArray.getInt(R.styleable.MyRatingBar_ratingNum, 5);
		ratingDrawableFull = typedArray.getResourceId(
				R.styleable.MyRatingBar_ratingDrawableFull, R.drawable.ic_pentagram_full);
		ratingDrawableHalf = typedArray.getResourceId(
				R.styleable.MyRatingBar_ratingDrawableHalf, R.drawable.ic_pentagram_half);
		ratingDrawableEmpty = typedArray.getResourceId(
				R.styleable.MyRatingBar_ratingDrawableEmpty, R.drawable.ic_pentagram_empty);
		rating = typedArray.getFloat(R.styleable.MyRatingBar_rating, 0);
		isIndicator = typedArray.getBoolean(R.styleable.MyRatingBar_isIndicator, true);
		width = typedArray.getDimensionPixelSize(R.styleable.MyRatingBar_pentagramWidth, width);
		height = typedArray.getDimensionPixelSize(R.styleable.MyRatingBar_pentagramHeight, height);
		layout_width = ratingNum * width;
		
		rating = rating > ratingNum ? ratingNum : rating;
		for (int i = 0; i < ratingNum; i++) 
			addView(createRatingView(ratingDrawableEmpty));
		
		drowRatingBar();
		super.setOnTouchListener(myTouchListener);
	}
	
	
	/**绘制RatingBar*/
	private void drowRatingBar() {
		// rating 0.5 - 5.0
		int decimal = (int) (rating * 10 % 10);
		int intPart = (int) (rating * 10 / 10);;
		for (int i = 0; i < intPart; i++) {
			getChildAt(i).setBackgroundResource(ratingDrawableFull);
		}
		
		if(intPart < ratingNum){
			getChildAt(intPart).setBackgroundResource(decimal == 0 ?ratingDrawableEmpty:ratingDrawableHalf);
			
			for (int j = intPart + 1; j < ratingNum; j++) {
				getChildAt(j).setBackgroundResource(ratingDrawableEmpty);
			}
		}
	}
	
	/**
	 * 创建评分控件view
	 * @param position 
	 * @param ratingId
	 * @return
	 */
	private ImageView createRatingView(int ratingId){
		ImageView iv = new ImageView(context);
		iv.setBackgroundResource(ratingId);
		LayoutParams params = new LayoutParams(width, height);//设置成等比
		iv.setLayoutParams(params);
		return iv;
	}
	
	private OnTouchListener myTouchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if(isIndicator)
				return false;
			int action = event.getAction();
			if (action == MotionEvent.ACTION_MOVE) {
				scroolX = (int) event.getX() > layout_width ? layout_width : (int) event.getX();
				scroolX = (int) event.getX() < 0  ? 0 : scroolX;
				rating = scroolX/(float)width;
				
				int decimal = (int) (rating * 10 % 10);
				double floor = Math.floor(rating);
				if(floor < ratingNum)
					rating = (float) (decimal > 5 ? floor + 1 :floor + 0.5);
				
				if(previousRating != rating){
					drowRatingBar();
					previousRating = rating;
				}
			}
			
			if(mOnRatingBarChangeListener != null){
				mOnRatingBarChangeListener.onRatingChanged(rating);
			}
			
			if (touchListener != null) {
				return touchListener.onTouch(v, event);
			}
			

			return true;
		}
	};
	
	@Override
	public void setOnTouchListener(OnTouchListener touchListener) {
		this.touchListener = touchListener;
	};
	
	/**
	 * 设置等级
	 * @param progress
	 */
	public void setProgress(float progress){
		rating = progress > ratingNum ? ratingNum : progress;
		if(rating <= 0)
			rating = 0.5f;
		drowRatingBar();
	}
	
	public float getProgress() {
		return rating;
	}
	
}
