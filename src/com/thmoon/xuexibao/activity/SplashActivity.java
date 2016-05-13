package com.thmoon.xuexibao.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.thmoon.yilibao.R;
import com.thmoon.yilibao.R.id;
import com.thmoon.yilibao.R.layout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;

public class SplashActivity extends Activity {

	private TextView tv_top;
	private TextView tv_bottom;
	private AlphaAnimation alphaAnimation;
	private TextView tv_title;
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				tv_title.setVisibility(View.VISIBLE);
				tv_title.startAnimation(alphaAnimation);
				break;
			case 2:
				tv_top.setVisibility(View.VISIBLE);
				tv_top.startAnimation(alphaAnimation);
				break;
			case 3:
				tv_bottom.setVisibility(View.VISIBLE);
				tv_bottom.startAnimation(alphaAnimation);
				break;
			case 4:
				finish();
				startActivity(new Intent(SplashActivity.this, HomeActivity.class));
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initUI();
		
	}

	private void initUI(){
		setContentView(R.layout.activity_splash);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_top = (TextView) findViewById(R.id.tv_top);
		tv_bottom = (TextView) findViewById(R.id.tv_bottom);
		alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
		alphaAnimation.setDuration(1000);
		handler.sendEmptyMessageDelayed(1,500);
		handler.sendEmptyMessageDelayed(2, 1600);
		handler.sendEmptyMessageDelayed(3, 2700);
		handler.sendEmptyMessageDelayed(4, 3700);
		
	}
}
