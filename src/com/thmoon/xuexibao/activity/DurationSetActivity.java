package com.thmoon.xuexibao.activity;


import com.thmoon.yilibao.R;
import com.thmoon.yilibao.R.id;
import com.thmoon.yilibao.R.layout;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class DurationSetActivity extends Activity {
	private EditText et;
	private SharedPreferences mPreferences;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mPreferences = getSharedPreferences("config", MODE_PRIVATE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_duration_set);
		et = (EditText) findViewById(R.id.et_enter_psw);
	}
	
	public void confirm(View view){
		String duration = et.getText().toString();
		if (!TextUtils.isEmpty(duration) && Integer.parseInt(duration) > 1440) {
			Toast.makeText(this, "时间最大为24小时(1440)", Toast.LENGTH_SHORT).show();
			return ;
		}
		mPreferences.edit().putString("duration", duration).commit();
		finish();
	}
	
	public void bt1(View view){
		String number = et.getText().toString();
		et.setText(number+1);
	}
	public void bt2(View view){
		String number = et.getText().toString();
		et.setText(number+2);
	}
	public void bt3(View view){
		String number = et.getText().toString();
		et.setText(number+3);
	}
	public void bt4(View view){
		String number = et.getText().toString();
		et.setText(number+4);
	}
	public void bt5(View view){
		String number = et.getText().toString();
		et.setText(number+5);
	}
	public void bt6(View view){
		String number = et.getText().toString();
		et.setText(number+6);
	}
	public void bt7(View view){
		String number = et.getText().toString();
		et.setText(number+7);
	}
	public void bt8(View view){
		String number = et.getText().toString();
		et.setText(number+8);
	}
	public void bt9(View view){
		String number = et.getText().toString();
		et.setText(number+9);
	}
	public void bt0(View view){
		String number = et.getText().toString();
		et.setText(number+0);
	}
	public void delete(View view){
		String number = et.getText().toString();
		if (!TextUtils.isEmpty(number)) {
			number = number.substring(0, number.length()-1);
			et.setText(number);
		}
	}
	public void deleteAll(View view){
		et.setText("");
	}
}
