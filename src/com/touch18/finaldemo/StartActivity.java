package com.touch18.finaldemo;

import com.touch18.finaldemo.util.InitUtils;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;

public class StartActivity extends FragmentActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_start);
		
		InitUtils.initImageLoader(this);
	}
	
	
	public void jump(View view){
		int id = view.getId();
		Intent intent = new Intent(this,MainActivity.class);
		switch (id) {
		case R.id.btn_animation:
			intent.putExtra("animation", true);
			break;
		case R.id.btn_no_animation:
			intent.putExtra("animation", false);
			break;
		}
		startActivity(intent);
	}
	
}
