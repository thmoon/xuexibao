package com.thmoon.xuexibao.activity;


import com.thmoon.yilibao.R;
import com.thmoon.yilibao.R.anim;
import com.thmoon.yilibao.R.id;
import com.thmoon.yilibao.R.layout;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ScreenActivity extends Activity {

	private SharedPreferences mPreferences;
	private TextView tv;
	private GestureDetector mDetector;
	private LinearLayout ll;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
		
	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mDetector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}
	
	private void init(){
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
		setContentView(R.layout.activity_screen);
		mPreferences = getSharedPreferences("config", MODE_PRIVATE);
		tv = (TextView) findViewById(R.id.tv_screen);
		ll = (LinearLayout) findViewById(R.id.ll_screen);
		MyOnGestureListener listener = new MyOnGestureListener();
		mDetector = new GestureDetector(this, listener);
	}
	
	private class MyOnGestureListener extends SimpleOnGestureListener{
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			if(e2.getRawX()-e1.getRawX()>200){
				finish();
				overridePendingTransition(0, R.anim.tran_previous_out);
				return true;
			}
			if (e1.getRawX()-e2.getRawX()>200) {
				finish();
				overridePendingTransition(0, R.anim.tran_next_out);
				return true;
			} 
			
			return super.onFling(e1, e2, velocityX, velocityY);
		}
	}
	
	
	@Override
	protected void onStart() {
		super.onResume();
		timeShow();
	}
	
	private void timeShow(){
		String duration = mPreferences.getString("duration", "");
		if (!TextUtils.isEmpty(duration)) {
			int time = Integer.parseInt(duration);
			int hour = time/60;
			int min = time%60;
			String hourStr = hour/10 >= 1? hour+"" : "0"+hour;
			String minStr = min/10 >= 1? min+"" : "0"+min;
			tv.setText(hourStr+":"+minStr);
		}else {
			tv.setText("00:00");
			ll.setVisibility(View.INVISIBLE);
			
		}
	}
	
}
