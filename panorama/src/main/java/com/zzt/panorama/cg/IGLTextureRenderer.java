package com.zzt.panorama.cg;

import android.graphics.Bitmap;

/**
 * Created by Android_ZzT on 2018/8/6.
 */
public interface IGLTextureRenderer {

	/**
	 * <pre>
	 * Surface创建好之后
	 * </pre>
	 */
	void onGLContextAvailable();

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

	void onAttached();

	void onDetached();

	void loadBitmap(Bitmap bitmap);

	void changeTextureBitmap(Bitmap bitmap);
}
