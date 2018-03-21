package com.zhongke.market.gui.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.zhongke.market.R;
import com.zhongke.market.model.Carousel;
import com.zhongke.market.util.glide.GlideLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class SlideShowView extends FrameLayout implements OnPageChangeListener {
	private ViewPager viewPager;
	private AdvertisementAdapter adapter;

	private List<ImageView> imageViews;
	private List<ImageView> dotViews;
	private ScheduledExecutorService scheduledExecutorService; // 定时任务
	private Context context;
	private LinearLayout layout;

	public SlideShowView(Context context, AttributeSet attrs ) {
		super(context, attrs);
		initView();
		this.context = context;
	}
	private void initView() {
		LayoutInflater.from(getContext()).inflate(R.layout.slideshowview_item,
				this, true);
		viewPager =  findViewById(R.id.integral_advertisement);
		layout =  findViewById(R.id.integral_advertisement_layout);
		imageViews = new ArrayList<>();
		dotViews = new ArrayList<>();
	}

	public void initDate(List<Carousel> slideDate) {
		addImageView(slideDate);
		adapter = new AdvertisementAdapter();
		viewPager.setAdapter(adapter);
		viewPager.setOnPageChangeListener(this);
		startSlide();
	}

	public void addImageView(List<Carousel> slideDate) {
		for (int i=0;i<slideDate.size();++i) {
			Carousel carousel =slideDate.get(i);
			imageViews.add(getImageView(carousel));
			ImageView imageView = getDotView();
			if(i!=0){
				imageView.setAlpha(0.5f);
			}
			dotViews.add(imageView);
			layout.addView(imageView);
		}
		if (imageViews.size() < 4) {
			copyImageView(slideDate);
		}
	}

	public void copyImageView(List<Carousel> slideDate) {
		for (Carousel carousel : slideDate) {
			imageViews.add(getImageView(carousel));
		}
	}

	public ImageView getDotView() {
		ImageView imageView = new ImageView(context);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				10, 10);
		layoutParams.leftMargin = 4;
		layoutParams.rightMargin = 4;
		imageView.setLayoutParams(layoutParams);
		imageView.setBackgroundResource(R.drawable.shape_slide_dot);
		return imageView;
	}

	public ImageView getImageView(final Carousel carousel) {
		ImageView iv = new ImageView(getContext());
		iv.setScaleType(ImageView.ScaleType.FIT_XY);
		GlideLoader.loadNetWorkResource(getContext(),carousel.imageUrl,iv);
		iv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});
		return iv;
	}



	public boolean isFirs = true;

	public void startSlide() {
		if ((!isFirs)||imageViews.size()==0) {
			    return;
		}
		if (scheduledExecutorService == null || scheduledExecutorService.isShutdown()) {
			scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
		}
		// 每5秒告诉ui线程切换一次图片显示
		scheduledExecutorService.scheduleAtFixedRate(slideshow_runnable, 5, 5,
				TimeUnit.SECONDS);
		isFirs = false;
	}

	public void stopSlide() {
		isFirs = true;
		if (scheduledExecutorService != null
				&& !scheduledExecutorService.isShutdown()) {
			scheduledExecutorService.shutdown();
		}
	}

	private int i = 0;
	// 执行轮播图切换任务
	private Runnable slideshow_runnable = new Runnable() {
		@Override
		public void run() {
			Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
			synchronized (viewPager) {
				handler.obtainMessage().sendToTarget();
			}
		}
	};
	private Handler handler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message arg0) {
			setCurrentPage();
			return false;
		}
	});

	public void setCurrentPage() {
		viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
	}

	public static final String TAG = SlideShowView.class.getSimpleName();

	private class AdvertisementAdapter extends PagerAdapter {
		@Override
		public int getCount() {
			return Integer.MAX_VALUE / 2;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			
			
			((ViewPager) container).removeView(imageViews.get(position
					% imageViews.size()));
		}

		@Override
		public Object instantiateItem(View container, final int position) {
	
			ImageView imageView = imageViews.get(position % imageViews.size());
			
			((ViewPager) container).addView(imageView);
			return imageView;
		}
	}

	public boolean isScroll = false;

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int position) {
		int pageIndex = position % imageViews.size();
		int index;
		if (pageIndex >= dotViews.size()) {
			index = pageIndex % dotViews.size();
		} else {
			index = pageIndex;
		}
		for (int i = 0; i < dotViews.size(); ++i) {
			ImageView iv=(ImageView) layout.getChildAt(i);
			if(iv!=null){
				if(i ==index){
					iv.setBackgroundResource(R.drawable.shape_slide_dot);
					iv.setAlpha(1f);
				}else{
					iv.setBackgroundResource(R.drawable.shape_slide_dot);
					iv.setAlpha(0.5f);
				}
			}
		}
	}
	
	
}
