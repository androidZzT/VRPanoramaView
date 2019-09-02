package com.zzt.panorama.cg;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.support.annotation.NonNull;

import com.zzt.panorama.util.LogHelper;

/**
 * Created by Android_ZzT on 2018/9/6.
 */
public class Texture {

	private static final String TAG = Texture.class.getSimpleName();

	private int mTexName;
	private int mTexUnit;

	public Texture() {
	}

	public void loadBitmapToGLTexture(@NonNull Bitmap bitmap) {
		if (bitmap == null) return;
		if (bitmap.isRecycled()) return;
		if (isAvailable()) {
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexName);

			// 设置缩小的情况下过滤方式
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
			// 设置放大的情况下过滤方式
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);

			GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

			// 为当前绑定的纹理自动生成所有需要的多级渐远纹理
			// 生成 MIP 贴图
//		    GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);

			// 解除与纹理的绑定，避免用其他的纹理方法意外地改变这个纹理
//		    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
		} else {
			throw new RuntimeException("your opengl texture is not available or your bitmap is recycled");
		}
	}

	public void create() {
		int[] array = new int[1];
		GLES20.glGenTextures(1, array, 0); //生成 textureName
		mTexName = array[0];
		LogHelper.d(TAG,"mTexName = " + mTexName);

		mTexUnit = GLES20.GL_TEXTURE0 + mTexName; //激活对应的纹理单元
		LogHelper.d(TAG,"mTexUnit = " + mTexUnit);

		GLES20.glActiveTexture(mTexUnit);
	}

	public void bindSampler(int textureHandle) {
		GLES20.glUniform1i(textureHandle, mTexName);
	}

	public int getTextureName() {
		return mTexName;
	}

	public void destroy() {
		GLES20.glDeleteTextures(1, new int[]{mTexName}, 0);
	}

	public boolean isAvailable() {
		return mTexName != 0;
	}

}
