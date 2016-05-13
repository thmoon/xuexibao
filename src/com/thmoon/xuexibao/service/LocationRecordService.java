package com.thmoon.xuexibao.service;





import com.thmoon.xuexibao.activity.ScreenActivity;
import com.thmoon.yilibao.R;
import com.thmoon.yilibao.R.raw;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

public class LocationRecordService extends Service {

	private SharedPreferences mPreferences;
	private MyBroadcastReceiver receiver;
	private SensorManager sensorManager;
	private MySensorEventListener listener;
	private Sensor magneticSensor;
	private Sensor accelerometerSensor;
	
	private float[] accelerometerValues  = new float[3];
	private float[] magneticFieldValues = new float[3];
	private float[] R = new float[9];
	private float[] values = new float[3];
	
	private int[] music = new int[]{com.thmoon.yilibao.R.raw.zxc,com.thmoon.yilibao.R.raw.maogou};
	
	private boolean mScreenOff = false;
	private boolean mScreenOn = false;
	private MediaPlayer mediaPlayer;
	private boolean isCalling = false;
	private TelephonyManager telManager;
	private MyPhoneStateListener phoneListener;
	private CountDownTimer countDownTimer;
	private long countDownTime;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		init();
	}
	/**
	 * 注册拦截屏幕关闭及开启的广播
	 */
	private void init(){
		mPreferences = getSharedPreferences("config", MODE_PRIVATE);
		receiver = new MyBroadcastReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(Intent.ACTION_SCREEN_ON);
		registerReceiver(receiver, filter);
		initSensor();
		initMusicPlay();
		initTelListener();
		
		//countDown();
	}
	/**
	 * 初始化重力加速度跟磁感应传感器
	 * 并进行监听
	 */
	private void initSensor(){
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		listener = new MySensorEventListener();
		sensorManager.registerListener(listener, magneticSensor, SensorManager.SENSOR_DELAY_NORMAL);
		sensorManager.registerListener(listener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
	}
	/**
	 * 计算方位角度,跟正北方向的偏移
	 * @return 跟正北方向的偏移角度
	 */
	private float computeOrientation(){
		if (sensorManager.getRotationMatrix(R, null, accelerometerValues, magneticFieldValues)) {
			sensorManager.getOrientation(R, values);
		}
		//转换为角度表示
		return (float) Math.toDegrees(values[0]);
		
	}
	

	private class MySensorEventListener implements SensorEventListener{

		@Override
		public void onSensorChanged(SensorEvent event) {
			switch (event.sensor.getType()) {
			case Sensor.TYPE_ACCELEROMETER:
				for (int i = 0; i < event.values.length; i++) {
					accelerometerValues[i] = event.values[i];
				}
				break;
			case Sensor.TYPE_MAGNETIC_FIELD:
				for (int i = 0; i < event.values.length; i++) {
					magneticFieldValues[i] = event.values[i];
				}
				break;
			}
			float angle = 0;
			if (accelerometerValues[2] != 0 && magneticFieldValues[0] != 0) {
				angle = computeOrientation();
			}else {
				return ;
			}
			//当屏幕熄灭时，mScreenOff置为true
			if (!mScreenOff ) {
				return ;
			}
			//当屏幕点亮是,mScreenOn置为false
			if (mScreenOn) {
				//屏幕点亮时进行位置比较
				float old_angle = mPreferences.getFloat("oldangle", 0);
				float i = (angle - old_angle);
				if (Math.abs(angle - old_angle) > 10) {
					mScreenOff = false;
					if (!mediaPlayer.isPlaying()) {
						mediaPlayer.start();
					}
				}
				
			}else {
				mPreferences.edit().putFloat("oldangle", angle).commit();
				sensorManager.unregisterListener(listener);
			}
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			
		}
		
	}
	/**
	 * 监听屏幕的关闭与开启
	 * @author Administrator
	 *
	 */
	private class MyBroadcastReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
				if (countDownTimer == null) {
					countDown();
				}
				mScreenOff = true;
				mScreenOn = false;
				if (mediaPlayer.isPlaying()) {
					mediaPlayer.pause();
				}
			}else if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
				//当电话响铃引起的屏幕开启，此次广播接收不起作用
				if (isCalling) {
					mScreenOff = false;
				}else {
					mScreenOn = true;
					Intent intentScreen = new Intent(LocationRecordService.this, ScreenActivity.class);
					intentScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intentScreen);
				}
				sensorManager.registerListener(listener, magneticSensor, SensorManager.SENSOR_DELAY_NORMAL);
				sensorManager.registerListener(listener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
			}
		}
		
	}
	
	
	/**
	 * 初始化化音乐播放器
	 */
	private void initMusicPlay(){
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
		}
		
		mediaPlayer = MediaPlayer.create(LocationRecordService.this, music[mPreferences.getInt("music", 0)]);
		try {
			mediaPlayer.setVolume(0.7f, 0.7f);
			mediaPlayer.setLooping(true);
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} 
		
	}
	/**
	 * 监听电话状态
	 */
	private void initTelListener(){
		telManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		phoneListener = new MyPhoneStateListener();
		telManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
		
	}

	private class MyPhoneStateListener extends PhoneStateListener{
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			if (state == TelephonyManager.CALL_STATE_RINGING ) {
				//当来电响铃时，让方位监听不起作用
				isCalling = true;
			}else if(state == TelephonyManager.CALL_STATE_IDLE){
				//只有在空闲状态,方位监听才能起作用
				isCalling = false;
			}
		}
	}
	/**
	 * 倒计时
	 */
	private void countDown(){
		String duration = mPreferences.getString("duration", "");
		if (!TextUtils.isEmpty(duration)) {
			countDownTime = Integer.parseInt(duration)*1000*60;
			countDownTimer = new CountDownTimer(countDownTime,59900) {
				
				@Override
				public void onTick(long millisUntilFinished) {
					countDownTime -= 60000;
					mPreferences.edit().putString("duration",millisUntilFinished/59900+"").commit();
				}
				
				@Override
				public void onFinish() {
					stopSelf();
				}
				
			}.start();
		}
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		//取消传感器监听
		if (listener != null) {
			sensorManager.unregisterListener(listener);
		}
		//取消广播接受者
		unregisterReceiver(receiver);
		
		//取消音乐播放器
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
		}
		//取消电话监听
		telManager.listen(phoneListener, PhoneStateListener.LISTEN_NONE);
		//移除duration参数
		mPreferences.edit().putString("duration","").commit();
		
		//取消倒计时
		if (countDownTimer != null) {
			countDownTimer.cancel();
		}
	}
	
}
