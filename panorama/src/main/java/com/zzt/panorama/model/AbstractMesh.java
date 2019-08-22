package com.zzt.panorama.model;

import android.content.Context;

import com.zzt.panorama.renderer.Shader;
import com.zzt.panorama.renderer.Texture;

import java.lang.ref.WeakReference;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by Android_ZzT on 2018/7/11.
 */
public abstract class AbstractMesh {

	protected static int COORDINATES_PER_VERTEX = 3;
	protected static int COORDINATES_PER_COLOR = 4;
	protected static int COORDINATES_PER_TEXTURE_COORDINATES = 2;

	/**
	 * attach vertexShader and fragmentShader
	 */
	protected int mProgramHandle;

	protected int mVertexShaderHandle;

	protected int mFragmentShaderHandle;

	protected FloatBuffer mVertexBuffer;

	protected FloatBuffer mColorBuffer;

	protected FloatBuffer mTextureCoordinatesBuffer;

	protected ShortBuffer mIndicesBuffer;

	protected Shader mVertexShader;

	protected Shader mFragmentShader;

	protected Texture mTexture;

	protected WeakReference<Context> mContextWeakReference;

	public void init(Context context) {
		mContextWeakReference = new WeakReference<>(context);
	}

	public void destroy() {
		mContextWeakReference.clear();
	}

	/**
	 * 绘画每一帧
	 *
	 * @param MVPMatrix 最终计算出来的 Model View Projection Matrix
	 */
	public abstract void drawFrame(float[] MVPMatrix);

	public void setTexture(Texture texture) {
		if (texture.isAvailable()) {
			mTexture = texture;
		}
	}


}
