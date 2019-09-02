package com.zzt.panorama;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.TextureView;
import android.widget.FrameLayout;

import com.zzt.panorama.cg.GLProducerThread;
import com.zzt.panorama.sphere.SphereRenderer;
import com.zzt.panorama.util.ImageUtil;
import com.zzt.panorama.util.LogHelper;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Android_ZzT on 2018/8/1.
 * <p>
 * 显示全景图的控件
 * <p>
 * 支持功能:
 * 1.设置图片url，下载图片内容并显示图片 {@link #setBitmapUrl(String)}}
 * 2.设置陀螺仪是否可用{@link #setGyroTrackingEnabled(boolean)}
 * 3.回到图片初始角度{@link #reCenter()}
 */
public class ZPanoramaTextureView extends FrameLayout implements TextureView.SurfaceTextureListener {

	private String TAG = ZPanoramaTextureView.class.getSimpleName();

	private TextureView mRenderView;
	private SphereRenderer mRenderer;
	private GLProducerThread mGLThread;
	private boolean mIsGLThreadAvailable;
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
			mPlaceHolder = BitmapFactory.decodeResource(getResources(), android.R.color.black);

			mRenderView = new TextureView(getContext());
			mRenderer = new SphereRenderer(getContext());
			mRenderView.setSurfaceTextureListener(this);
			addView(mRenderView);
		} else {
			throw new RuntimeException("your device does not support opengles 2.0");
		}
	}

	/**
	 * 设置陀螺仪是否可用，默认可用
	 *
	 * @param enabled
	 */
	public void setGyroTrackingEnabled(boolean enabled) {
		mRenderer.enableGyroTracking(enabled);
	}

	/**
	 * 回到图片初始角度
	 */
	public void reCenter() {
		mRenderer.reCenter();
	}

	/**
	 * 设置要加载的图片地址，需要
	 *
	 * @param url
	 */
	public void setBitmapUrl(@NonNull String url) {
		mCurrentBitmapUrl = url;
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		LogHelper.d(TAG, "onAttachedToWindow");
		mRenderer.onAttached();
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		LogHelper.d(TAG, "onDetachedFromWindow");
		mRenderer.onDetached();
		if (mIsGLThreadAvailable) {
			mGLThread.enqueueEvent(() -> {
				mGLThread.releaseEglContext();
			});
		}
	}

	@Override
	public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) { //detached 以后再 attached 还会回调此方法
		if (!mIsGLThreadAvailable) { //确保从后台回来的时候只调用一次，只初始化一条 GLProducerThread
			mIsGLThreadAvailable = true;
			mGLThread = new GLProducerThread(surface, mRenderer, new AtomicBoolean(true));
			mGLThread.start();

			mGLThread.enqueueEvent(() ->
					mRenderer.onSurfaceChanged(width, height)
			);

			loadBitmapFromCurrentUrl();
		} else {
			mGLThread.refreshSurfaceTexture(surface);
			changeBitmapFromCurrentUrl();
			if (mGLThread != null) {
				LogHelper.d(TAG, "onResume");
				mGLThread.onResume();
			}
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
			if (mGLThread != null) {
				mGLThread.onPause();
			}
		}
		return true;
	}

	@Override
	public void onSurfaceTextureUpdated(SurfaceTexture surface) {

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
}
