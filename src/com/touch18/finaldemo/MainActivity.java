package com.touch18.finaldemo;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.touch18.finaldemo.util.ImageLoaderUtil;
import com.touch18.finaldemo.util.UiUtils;
import com.touch18.finaldemo.view.PagerSlidingTabStrip;
import com.touch18.finaldemo.view.StickyLayout;
import com.touch18.finaldemo.view.StickyLayout.StickyState;
import com.touch18.finaldemo.view.StickyLayout.TriggerZoomListener;
import com.touch18.finaldemo.view.StickyLayout.onScrollChangedListener;


public class MainActivity extends FragmentActivity {
	private String[] mTitles = new String[] { "简介", "评价", "相关" };
	
	private RelativeLayout mTopView,mTitleBar;
	private RelativeLayout mBottomDownload,mBottomComment;
	private TextView mScore;
	private View bgView;
	private StickyLayout stickyLayout;
	private LinearLayout llDots;
	private PagerSlidingTabStrip mIndicator;
	private ViewPager mTopViewPager,mViewPager;
	private FragmentPagerAdapter mAdapter;
	private PagerAdapter mTopAdapter;
	private TabFragment[] mFragments = new TabFragment[mTitles.length];

	private List<ImageView> imageViews;
	private boolean needAnimation;
	
	private int screenWidth,screenHeight;
	private int mBottomHeight;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		
		needAnimation = getIntent().getBooleanExtra("animation",false);
		WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(dm);
		screenWidth = dm.widthPixels;
		screenHeight = dm.heightPixels;
		
		mBottomHeight = UiUtils.Dp2Px(40);
		
		initViews();
		initDatas();
		initEvents();
	}

	private int ANIM_DURATION = 240;
	private int ANIM_DELAY = 80;
	private void initEvents() {
		stickyLayout.setTriggerZoomListener(new TriggerZoomListener() {
			
			@Override
			public void zoom(boolean zoomUp) {
				if(needAnimation){
					ViewGroup.LayoutParams params = mTopViewPager.getLayoutParams();
					if(zoomUp){
						// 放大
						params.height = stickyLayout.getHeight();
						mTopViewPager.setLayoutParams(params);
						// TODO 这个值得计算
						float scaleUpX = (float)stickyLayout.getHeight() / screenWidth; 
						float scaleUpY = (float)screenWidth / imageViews.get(mTopViewPager.getCurrentItem()).getHeight(); 
						
						float translationUpY = (float)(stickyLayout.getHeight() - imageViews.get(mTopViewPager.getCurrentItem()).getHeight()) / 2;
						
						for (int i = 0; i < imageViews.size(); i++) {
							if(i != mTopViewPager.getCurrentItem())
								imageViews.get(i).setVisibility(View.GONE);
							
							ImageView view = imageViews.get(i);
							
							ViewHelper.setPivotX(view, view.getWidth()/2);
							ViewHelper.setPivotY(view, view.getHeight()/2);
							ObjectAnimator animator = ObjectAnimator.ofFloat(view, "rotation",90.0f).setDuration(ANIM_DURATION);
							animator.setInterpolator(new AccelerateDecelerateInterpolator());
							animator.start();
							
							animator.addListener(new AnimatorListenerAdapter(){  
								@Override  
								public void onAnimationEnd(Animator animation){  
									for (ImageView view : imageViews) {
										view.setVisibility(View.VISIBLE);
									}
								}  
							});
							
//						System.out.println("bigw: " + view.getWidth() + " bigh: " + view.getHeight());
							ObjectAnimator o1 = ObjectAnimator.ofFloat(view, "scaleX", 1.0F,  scaleUpX).setDuration(ANIM_DURATION - ANIM_DELAY);
							o1.setStartDelay(ANIM_DELAY); 
							o1.start();
							
							ObjectAnimator o2 = ObjectAnimator.ofFloat(view, "scaleY", 1.0F,  scaleUpY).setDuration(ANIM_DURATION - ANIM_DELAY); 
							o2.setStartDelay(ANIM_DELAY);
							o2.start();
							
							ObjectAnimator.ofFloat(view, "translationY", translationUpY).setDuration(ANIM_DURATION).start();  
							
						}
						
					}else{
						params.height = (int) Math.ceil((float)screenWidth * 1080 / 1920);
						mTopViewPager.setLayoutParams(params);
						for (int i = 0; i < imageViews.size(); i++) {
							if(i != mTopViewPager.getCurrentItem())
								imageViews.get(i).setVisibility(View.GONE);
							
							ImageView view = imageViews.get(i);
							ObjectAnimator animator = ObjectAnimator.ofFloat(view, "rotation", 0.0f).setDuration(ANIM_DURATION);
							animator.setInterpolator(new AccelerateDecelerateInterpolator());
							animator.start();
							
							animator.addListener(new AnimatorListenerAdapter(){  
								@Override  
								public void onAnimationEnd(Animator animation){  
									for (ImageView view : imageViews) {
										view.setVisibility(View.VISIBLE);
									}
								}  
							});
							
//						System.out.println("w: " + view.getWidth() + " h: " + view.getHeight());
							ObjectAnimator o1 = ObjectAnimator.ofFloat(view, "scaleX", 1.0f).setDuration(ANIM_DURATION - ANIM_DELAY);
							o1.setStartDelay(ANIM_DELAY);
							o1.start();
							
							ObjectAnimator o2 = ObjectAnimator.ofFloat(view, "scaleY", 1.0f).setDuration(ANIM_DURATION - ANIM_DELAY);
							o2.setStartDelay(ANIM_DELAY);
							o2.start();  
							
							ObjectAnimator.ofFloat(view, "translationY", 0).setDuration(ANIM_DURATION).start();
						}
					}
				}
				
				mBottomDownload.setVisibility(zoomUp ? View.GONE : View.VISIBLE);
				mBottomComment.setVisibility(zoomUp ? View.GONE : View.VISIBLE);
				
			}
		});
		
		mTopViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				for (int i = 0; i < llDots.getChildCount(); i++) {
					llDots.getChildAt(i).setBackgroundResource(R.drawable.dots_normal);
				}
				
				llDots.getChildAt(position).setBackgroundResource(R.drawable.dots_selected);
			}
			
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				
			}
		});
		
		stickyLayout.setOnScrollChangedListener(new onScrollChangedListener() {
			
			@Override
			public void onScroll(int l, int t, int oldl, int oldt) {
				// 头部viewPager 的错觉显示
				ViewHelper.setTranslationY(mTopView, t);
				ViewHelper.setTranslationY(mTitleBar, t);

				int all = stickyLayout.getHeight() - stickyLayout.getMiddleSticky() - mTitleBar.getHeight();
				int part = t - stickyLayout.getMiddleSticky();
				
				float alpha = (float) part/all;
				
				if(t >= stickyLayout.getMiddleSticky())
					mTitleBar.setBackgroundColor(Color.argb((int)(255 * alpha), 228, 115, 30));
				
				float offset =  (float)t / (stickyLayout.getHeight() - mTitleBar.getHeight()) * 0.5f;
				
				// TODO 头部viewPager表面透明度 t越大越模糊
				bgView.setBackgroundColor(Color.argb((int)(255 * offset), 0, 0, 0));

				// TODO mScore透明度变换
				if(t < stickyLayout.getMiddleSticky())
					ViewHelper.setAlpha(mScore, (float)t / stickyLayout.getMiddleSticky());
			}
		});
		
		mIndicator.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				if(position != 0 && position != 1){
					ViewHelper.setTranslationY(mBottomDownload,mBottomHeight);
					ViewHelper.setTranslationY(mBottomComment,mBottomHeight);
				}
			}
			
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
				if(position == 0){
					ViewHelper.setTranslationY(mBottomDownload,mBottomHeight * positionOffset * 2);
					ViewHelper.setTranslationY(mBottomComment,mBottomHeight * (1 - positionOffset) * 2);
				}else if(position == 1){
					ViewHelper.setTranslationY(mBottomComment,mBottomHeight * positionOffset);
//					ViewHelper.setTranslationY(mBottomDownload,mBottomHeight * (1 - positionOffset));
				}
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				
			}
		});
		
		findViewById(R.id.rl_back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
	}

	private void initDatas(){

		for (int i = 0; i < mTitles.length; i++)
			mFragments[i] = (TabFragment) TabFragment.newInstance(mTitles[i]);

		mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()){
			@Override
			public int getCount(){
				return mTitles.length;
			}

			@Override
			public Fragment getItem(int position){
				return mFragments[position];
			}

			@Override
			public CharSequence getPageTitle(int position) {
				return mTitles[position];
			}
			
		};

		mViewPager.setAdapter(mAdapter);
		mIndicator.setViewPager(mViewPager);
		
		String[] urls;
		if(!needAnimation){
			urls = new String[]{
					"http://f.hiphotos.baidu.com/image/pic/item/a08b87d6277f9e2fa2a1472e1c30e924b899f304.jpg",
					"http://f.hiphotos.baidu.com/image/pic/item/a08b87d6277f9e2fa2a1472e1c30e924b899f304.jpg",
					"http://f.hiphotos.baidu.com/image/pic/item/a08b87d6277f9e2fa2a1472e1c30e924b899f304.jpg",
					"http://f.hiphotos.baidu.com/image/pic/item/a08b87d6277f9e2fa2a1472e1c30e924b899f304.jpg",
					"http://f.hiphotos.baidu.com/image/pic/item/a08b87d6277f9e2fa2a1472e1c30e924b899f304.jpg"};
		}else{
			// 横向
			urls = new String[]{
					"http://photo.enterdesk.com/2011-6-2/enterdesk.com-422374E95B160CEAC0C7A01DAD7BBB74.jpg",
					"http://bizhi.zhuoku.com/2013/01/20/meinv/Meinv045.jpg",
					"http://www.33.la/uploads/20140525bztp/12303.jpg",
					"http://bizhi.zhuoku.com/2012/08/14/jingyingbudui/Jingyingbudui09.jpg",
					"http://www.33lc.com/article/UploadPic/2012-7/201272711182351761.jpg"};
			
//			http://bizhi.zhuoku.com/2012/08/14/jingyingbudui/Jingyingbudui09.jpg
//			"http://qn.18touch.com/magicbox_images/upload/res_snap/2015-05-28/55667be5f38a56.42451287.jpg",  // 超好玩
//			"http://d.hiphotos.baidu.com/image/pic/item/5243fbf2b2119313e36edab767380cd791238d83.jpg"
		}
		
//		stickyLayout.setNeedAnimation(needAnimation);
		if(needAnimation){
			ViewGroup.LayoutParams params = mTopViewPager.getLayoutParams();
			params.height = (int) Math.ceil((float)screenWidth * 1080 / 1920);
			mTopViewPager.setLayoutParams(params);
		}
				
		imageViews = new ArrayList<ImageView>();
		
		for (int i = 0; i < urls.length; i++) {
			ImageView view = new ImageView(MainActivity.this);
			view.setScaleType(ScaleType.FIT_START);
			ImageLoaderUtil.setImage(view, urls[i]);
			
			view.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					stickyLayout.switchState(
							stickyLayout.getCurrentState() != StickyState.MIDDLE ?
							StickyState.MIDDLE : 
							StickyState.BOTTOM);
				}
			});
			
			imageViews.add(view);
			
			View dot = new View(MainActivity.this);
			dot.setBackgroundResource(R.drawable.dots_normal);
			llDots.addView(dot,UiUtils.Dp2Px(8),UiUtils.Dp2Px(8));
			
			if(i == 0)
				dot.setBackgroundResource(R.drawable.dots_selected);
			
			android.widget.LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams) dot.getLayoutParams();
			params.rightMargin = 8;
		}
		
		mTopAdapter = new PagerAdapter() {
			
			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}
			
			@Override
			public int getCount() {
				return imageViews.size();
			}
			
			@Override
			public Object instantiateItem(ViewGroup container, int position) {
				ImageView iv = imageViews.get(position);
				LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
				container.addView(iv,params);
				return iv;
			}
			
			@Override
			public void destroyItem(ViewGroup container, int position,Object object) {
				container.removeView(imageViews.get(position));
			}
			
		};
		
		mTopViewPager.setAdapter(mTopAdapter);
		mTopViewPager.setOffscreenPageLimit(imageViews.size());
		
		SpannableString spanText = new SpannableString("8.2");
		spanText.setSpan(new AbsoluteSizeSpan(UiUtils.Dp2Px(18)), 1, 3,Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		mScore.setText(spanText);
	}
	
	private void initViews(){
		mTopView = (RelativeLayout) findViewById(R.id.rl_top);
		bgView = findViewById(R.id.view_bg);
		mTitleBar = (RelativeLayout) findViewById(R.id.title_bar);
		stickyLayout = (StickyLayout) findViewById(R.id.stickyLayout);
		llDots = (LinearLayout) findViewById(R.id.ll_dots);
		mIndicator = (PagerSlidingTabStrip) findViewById(R.id.id_stickylayout_indicator);
		mTopViewPager = (ViewPager) findViewById(R.id.id_stickylayout_top_viewpager);
		mViewPager = (ViewPager) findViewById(R.id.id_stickylayout_viewpager);
		
		mScore = (TextView) findViewById(R.id.tv_score);
		mBottomDownload = (RelativeLayout) findViewById(R.id.rl_download);
		mBottomComment = (RelativeLayout) findViewById(R.id.rl_send);
	}
	
	
}
