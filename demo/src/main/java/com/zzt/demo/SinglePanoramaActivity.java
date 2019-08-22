package com.zzt.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.zzt.panorama.ZPanoramaTextureView;

/**
 * Created by Android_ZzT on 2019-08-22.
 */
public class SinglePanoramaActivity extends AppCompatActivity {

	private static final String TAG = SinglePanoramaActivity.class.getSimpleName();
	private static final String IMAGE_URL = "https://d147wy1xnm9vni.cloudfront.net/jrtBOYwNIfKgmbc-lEqbsg/thumbnails/360_180_source_150.jpg-video.vr.medium";

	private FrameLayout mPanoramaContainer;
	private ZPanoramaTextureView mPanoramaTextureView;
	private Button mBtnGyroController;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_single_panorama);

		mPanoramaContainer = findViewById(R.id.panorama_container);
		mBtnGyroController = findViewById(R.id.btn_gyro_controller);

		mPanoramaTextureView = new ZPanoramaTextureView(this);
		mPanoramaTextureView.setBitmapUrl(IMAGE_URL);
		mPanoramaTextureView.setGyroTrackingEnabled(true);
		mPanoramaContainer.addView(mPanoramaTextureView);

		mBtnGyroController.setTag(true);
		mBtnGyroController.setText("关闭陀螺仪");
	}

	public void recenter(View view) {
		mPanoramaTextureView.reCenter();
	}

	public void enableGyroTracking(View v) {
		boolean enable = !(boolean) mBtnGyroController.getTag();
		mBtnGyroController.setText(enable ? "关闭陀螺仪" : "打开陀螺仪");
		mBtnGyroController.setTag(enable);
		mPanoramaTextureView.setGyroTrackingEnabled(enable);
	}
}
