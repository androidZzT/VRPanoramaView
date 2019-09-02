package com.zzt.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by Android_ZzT on 2019-08-22.
 */
public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_main);
	}

	public void GotoSinglePanorama(View v) {
		Intent intent = new Intent(this, SinglePanoramaActivity.class);
		startActivity(intent);
	}

	public void GotoPanoramaList(View v) {
		Intent intent = new Intent(this, PanoramaListActivity.class);
		startActivity(intent);
	}
}
