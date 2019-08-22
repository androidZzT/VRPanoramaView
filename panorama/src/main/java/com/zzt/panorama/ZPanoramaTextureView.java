package com.zzt.panorama;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.Matrix;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.TextureView;
import android.widget.FrameLayout;

import com.zzt.panorama.renderer.GLProducerThread;
import com.zzt.panorama.util.ImageUtil;
import com.zzt.panorama.util.LogHelper;

import java.util.concurrent.atomic.AtomicBoolean;

import static android.hardware.SensorManager.SENSOR_DELAY_FASTEST;

/**
 * Created by Android_ZzT on 2018/8/1.
 */
public class ZPanoramaTextureView extends FrameLayout implements TextureView.SurfaceTextureListener, SensorEventListener {

	private String TAG = ZPanoramaTextureView.class.getSimpleName();

	private TextureView mRenderView;
	private SphereTextureRenderer mRenderer;
	private GLProducerThread mGLThread;

	private SensorManager mSensorManager;
	private Sensor mSensor;

	private boolean mFirstFrameFlag = true;
	private boolean mIsGyroTrackingEnabled;
	private boolean mIsGLThreadAvailable;

	private float[] rotVecValues = null;
	private float[] rotationQuaternion = new float[4];
	private float[] rotationMatrix = new float[16];

	private String mCurrentBitmapUrl;
	private Bitmap mPlaceHolder;

	public ZPanoramaTextureView(@NonNull Context context) {
		this(context, null);
	}

	public ZPanoramaTextureView(@NonNull Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		try {
			init();
		} catch (RuntimeException e) {
			LogHelper.e(TAG, e.getMessage());
		}
	}

	private void init() throws RuntimeException {
		final ActivityManager activityManager =
				(ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
		final ConfigurationInfo configurationInfo =
				activityManager.getDeviceConfigurationInfo();
		final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;
		if (supportsEs2) {
			mSensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
			mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
			mPlaceHolder = BitmapFactory.decodeResource(getResources(), android.R.color.black);

			mRenderView = new TextureView(getContext());
			mRenderer = new SphereTextureRenderer(getContext());
			mRenderView.setSurfaceTextureListener(this);
			addView(mRenderView);
		} else {
			throw new RuntimeException("your device does not support opengles 2.0");
		}
	}

	public void setGyroTrackingEnabled(boolean enabled) {
		mIsGyroTrackingEnabled = enabled;
		if (enabled) {
			mFirstFrameFlag = true;
		} else {
			reCenter();
		}
	}

	public void reCenter() {
		float[] invertMatrix = new float[16];
		Matrix.invertM(invertMatrix, 0, rotationMatrix, 0);
		mRenderer.setBiasMatrix(invertMatrix);
	}

	public void setBitmapUrl(@NonNull String url) {
		mCurrentBitmapUrl = url;
	}

	public void onResume() {
		if (mGLThread != null) {
			LogHelper.d(TAG, "onResume");
			mGLThread.onResume();
		}
	}

	public void onPause() {
		if (mGLThread != null) {
			mGLThread.onPause();
		}
	}

	public void onDestroy() {
		if (mGLThread != null) {
			mGLThread.onDestroy();
		}
	}

	private void loadBitmapFromCurrentUrl() {
		Bitmap bitmap = ImageUtil.loadBitmapFromCache(getContext(), mCurrentBitmapUrl);
		if (bitmap != null) {
			loadBitmapToGLTexture(bitmap);
		} else {
			loadBitmapToGLTexture(mPlaceHolder);
			ImageUtil.loadBitmapFromNetwork(getContext(), mCurrentBitmapUrl, tempBitmap -> {
				if (tempBitmap != null) {
					loadBitmapToGLTexture(tempBitmap);
				}
			});
		}
	}

	private void loadBitmapToGLTexture(Bitmap bitmap) {
		if (mGLThread != null) {
			mGLThread.enqueueEvent(() -> {
				mRenderer.loadBitmap(bitmap);
			});
		}
	}

	private void changeBitmapFromCurrentUrl() {
		Bitmap bitmap = ImageUtil.loadBitmapFromCache(getContext(), mCurrentBitmapUrl);
		if (bitmap != null) {
			changeBitmapToGLTexture(bitmap);
		} else {
			loadBitmapToGLTexture(mPlaceHolder);
			ImageUtil.loadBitmapFromNetwork(getContext(), mCurrentBitmapUrl, tempBitmap -> {
				if (tempBitmap != null) {
					changeBitmapToGLTexture(tempBitmap);
				}
			});
		}
	}

	private void changeBitmapToGLTexture(Bitmap bitmap) {
		if (mGLThread != null) {
			mGLThread.enqueueEvent(() -> {
				mRenderer.changeTextureBitmap(bitmap);
			});
		}
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		LogHelper.d(TAG, "onAttachedToWindow");
		mSensorManager.registerListener(this, mSensor, SENSOR_DELAY_FASTEST);
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		LogHelper.d(TAG, "onDetachedFromWindow");
		mSensorManager.unregisterListener(this);
		if (mIsGLThreadAvailable) {
			mGLThread.enqueueEvent(() -> {
				mRenderer.unBindTexture();
				mGLThread.releaseSurfaceTexture();
			});
		}
	}

	@Override
	public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) { //detached 以后再 attached 还会回调此方法
		Log.e(TAG, "onSurfaceTextureAvailable: ");
		if (!mIsGLThreadAvailable) { //确保从后台回来的时候只调用一次
			mIsGLThreadAvailable = true;
			mGLThread = new GLProducerThread(surface, mRenderer, new AtomicBoolean(true));
			mGLThread.start();

			mGLThread.enqueueEvent(() ->
					mRenderer.onSurfaceChanged(width, height)
			);

			loadBitmapFromCurrentUrl();
		} else { //TextureView destroy 以后
			mGLThread.refreshSurfaceTexture(surface);
			changeBitmapFromCurrentUrl();
			onResume();
		}
	}

	@Override
	public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
		mGLThread.enqueueEvent(() ->
				mRenderer.onSurfaceChanged(width, height)
		);
	}

	@Override
	public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
		if (mIsGLThreadAvailable) {
			onPause();
		}
		return true;
	}

	@Override
	public void onSurfaceTextureUpdated(SurfaceTexture surface) {

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
			if (mFirstFrameFlag) { //初始化时，先给一个初始角度，以便能绘制出第一帧的图
				mFirstFrameFlag = false;
				float[] orientationMatrix = new float[16];
				Matrix.setIdentityM(orientationMatrix, 0);

				if (rotVecValues == null) {
					rotVecValues = new float[event.values.length];
				}
				for (int i = 0; i < rotVecValues.length; i++) {
					rotVecValues[i] = event.values[i];
				}

				if (rotVecValues != null) {
					SensorManager.getQuaternionFromVector(rotationQuaternion, rotVecValues);
					SensorManager.getRotationMatrixFromVector(orientationMatrix, rotVecValues);
					mRenderer.setRotationMatrix(orientationMatrix);
				}
				float[] invertMatrix = new float[16];
				Matrix.invertM(invertMatrix, 0, orientationMatrix, 0);
				mRenderer.setBiasMatrix(invertMatrix);
				return;
			}

			if (mIsGyroTrackingEnabled) {
				for (int i = 0; i < rotVecValues.length; i++) {
					rotVecValues[i] = event.values[i];
				}

				if (rotVecValues != null) {
					SensorManager.getQuaternionFromVector(rotationQuaternion, rotVecValues);
					SensorManager.getRotationMatrixFromVector(rotationMatrix, rotVecValues);
					mRenderer.setRotationMatrix(rotationMatrix);
				}
			}
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}
}
