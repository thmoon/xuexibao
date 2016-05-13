package com.thmoon.xuexibao.activity;

import java.util.List;

import com.thmoon.xuexibao.service.LocationRecordService;
import com.thmoon.yilibao.R;
import com.thmoon.yilibao.R.id;
import com.thmoon.yilibao.R.layout;


import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends Activity {
	private Intent intent;
	private TextView tv;
	private SharedPreferences mPreferences;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		initUI();
		if (!isServiceRunning("com.thmoon.willpower.service.LocationRecordService")) {
			mPreferences.edit().putString("duration", "").commit();
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
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
		}
	}
	
	
	private void initUI(){
		mPreferences = getSharedPreferences("config", MODE_PRIVATE);
		tv = (TextView) findViewById(R.id.tv_home);
		intent = new Intent(HomeActivity.this, LocationRecordService.class);
	}
	
	/**
	 * 时间选择
	 * @param view
	 */
	public void set(View view){
		startActivityForResult(new Intent(HomeActivity.this, DurationSetActivity.class),1);
	}
	
	public void choose(View view){
		AlertDialog.Builder builder = new Builder(this);
		int item = mPreferences.getInt("music", 0);
		builder.setTitle("音效选择");
		builder.setSingleChoiceItems(new String[]{"星爷经典笑声","猫狗版你到底爱谁"}, item, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					mPreferences.edit().putInt("music", 0).commit();
					break;
				case 1:
					mPreferences.edit().putInt("music", 1).commit();
					break;
				default:
					break;
				}
				dialog.dismiss();
			}
		});
		builder.create().show();
	}
	
	/**
	 * 开启学习模式，会启动一个服务
	 * @param view
	 */
	public void start(View view){
		String duration = mPreferences.getString("duration", "");
		if (isServiceRunning("com.thmoon.willpower.service.LocationRecordService")) {
			Toast.makeText(HomeActivity.this, "服务已开启", Toast.LENGTH_SHORT).show();
			//System.out.println(mPreferences.getBoolean("service", false));
			return ;
		}
		if (!TextUtils.isEmpty(duration)) {
			startService(intent);
		}else {
			Builder builder = new Builder(this);
			builder.setTitle("提示");
			builder.setMessage("时间未设定,是否继续?");
			builder.setPositiveButton("确定", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					startService(intent);
				}
			});
			builder.setNegativeButton("取消", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
				}
			});
			builder.create().show();
		}
	}
	/**
	 * 停止学习模式，关闭服务
	 * @param view
	 */
	public void stop(View view){
		stopService(intent);
		Toast.makeText(HomeActivity.this, "服务已停止", Toast.LENGTH_SHORT).show();
		tv.setText("00:00");
	}
	/**
	 * 
	 */
	private boolean isServiceRunning(String serviceName){
		ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		List<RunningServiceInfo> runningServices = activityManager.getRunningServices(50);
		for (RunningServiceInfo runningServiceInfo : runningServices) {
			if(serviceName.equals(runningServiceInfo.service.getClassName())){
				return true;
			}
		}
		return false;
	}
	
}
