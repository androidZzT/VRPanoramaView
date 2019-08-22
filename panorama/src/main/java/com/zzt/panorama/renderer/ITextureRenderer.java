package com.zzt.panorama.renderer;

import android.graphics.Bitmap;

/**
 * Created by Android_ZzT on 2018/8/6.
 */
public interface ITextureRenderer {

	/**
	 * <pre>
	 * Surface创建好之后
	 * </pre>
	 */
	void onSurfaceCreated();

	/**
	 * <pre>
	 * 界面大小有更改
	 * </pre>
	 *
	 * @param width
	 * @param height
	 */
	void onSurfaceChanged(int width, int height);

	/**
	 * <pre>
	 * 绘制每一帧
	 * </pre>
	 */
	void onDrawFrame();

	/**
	 * <pre>
	 * Activity的onResume时的操作
	 * </pre>
	 */
	void onResume();

	/**
	 * <pre>
	 * Activity的onPause时的操作
	 * </pre>
	 */
	void onPause();

	/**
	 * <pre>
	 * Activity的onDestroy时的操作
	 * </pre>
	 */
	void onDestroy();

	void loadBitmap(Bitmap bitmap);

	void changeTextureBitmap(Bitmap bitmap);

	void bindTexture();

	void unBindTexture();

}
