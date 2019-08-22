package com.zzt.panorama;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;

import com.zzt.panorama.model.Sphere;
import com.zzt.panorama.renderer.Camera;
import com.zzt.panorama.renderer.ITextureRenderer;
import com.zzt.panorama.renderer.Texture;

import java.lang.ref.WeakReference;

/**
 * Created by Android_ZzT on 2018/8/1.
 */
public class SphereTextureRenderer implements ITextureRenderer {

	private static final String TAG = SphereTextureRenderer.class.getSimpleName();

	private Sphere mSphere;

	private Camera mCamera;

	private float[] mRotationMatrix = new float[]{
			1, 0, 0, 0,
			0, 1, 0, 0,
			0, 0, 1, 0,
			0, 0, 0, 0
	};
	private float[] mBiasMatrix = new float[16];

	private Texture mTexture;

	private WeakReference<Context> mContextWeakReference;

	public SphereTextureRenderer(Context context) {
		mSphere = Sphere.getDefaultSphere();
		mContextWeakReference = new WeakReference<>(context);
	}

	public void setRotationMatrix(float[] rotationMatrix) {
		mRotationMatrix = rotationMatrix;
	}

	public void setBiasMatrix(float[] biasMatrix) {
		mBiasMatrix = biasMatrix;
	}

	@Override
	public void onSurfaceCreated() {
		mTexture = new Texture();
		mTexture.create();
		mSphere.setTexture(mTexture);
		mSphere.init(mContextWeakReference.get());
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
	}

	@Override
	public void onSurfaceChanged(int width, int height) {
		GLES20.glViewport(0, 0, width, height);
		float ratio = (float) width / height;
		mCamera = new Camera(ratio);
	}

	@Override
	public void onDrawFrame() {
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

		controlCamera();

		mSphere.drawFrame(mCamera.getMVPMatrix());
	}

	@Override
	public void loadBitmap(Bitmap bitmap) {
		mTexture.loadBitmapToGLTexture(bitmap);
	}

	@Override
	public void changeTextureBitmap(Bitmap bitmap) {
//		mTexture = new Texture();
		mTexture.destroy();
		mTexture.create();
		mSphere.setTexture(mTexture);
		mSphere.reCompileShaderAndLinkProgram();
		mTexture.loadBitmapToGLTexture(bitmap);
	}

	@Override
	public void bindTexture() {

	}

	@Override
	public void unBindTexture() {
		mTexture.destroy();
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0); //默认黑色，解绑 texture 即为黑色
	}

	@Override
	public void onResume() {
		//...
	}

	@Override
	public void onPause() {
		//...
	}

	@Override
	public void onDestroy() {
		//...
	}

	private void controlCamera() {
		mCamera.rebuildViewMatrix();
		mCamera.rotate(mRotationMatrix);
		mCamera.rotate(mBiasMatrix);
		mCamera.rotate(-90, Camera.Axis.AXIS_Y);
	}
}
