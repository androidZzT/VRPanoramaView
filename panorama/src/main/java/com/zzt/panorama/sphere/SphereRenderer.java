package com.zzt.panorama.sphere;

import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.zzt.panorama.R;
import com.zzt.panorama.cg.Camera;
import com.zzt.panorama.cg.IGLTextureRenderer;
import com.zzt.panorama.cg.Shader;
import com.zzt.panorama.cg.Texture;
import com.zzt.panorama.util.OpenGLUtil;

import java.lang.ref.WeakReference;

import static android.hardware.SensorManager.SENSOR_DELAY_FASTEST;
import static com.zzt.panorama.cg.AbstractMesh.COORDINATES_PER_COLOR;
import static com.zzt.panorama.cg.AbstractMesh.COORDINATES_PER_TEXTURE_COORDINATES;
import static com.zzt.panorama.cg.AbstractMesh.COORDINATES_PER_VERTEX;

/**
 * Created by Android_ZzT on 2018/8/1.
 * <p>
 * 球模型渲染器，主要负责球的绘制，陀螺仪监听，计算旋转矩阵，ModelViewProjection矩阵
 */
public class SphereRenderer implements IGLTextureRenderer, SensorEventListener {

	private static final String TAG = SphereRenderer.class.getSimpleName();

	/**
	 * cg attrs
	 */
	private Sphere mSphere;

	private Texture mTexture;

	private Shader mVertexShader;

	private Shader mFragmentShader;

	private Camera mCamera;

	/**
	 * gl attrs
	 */
	private int mProgramHandle;
	private int mVertexShaderHandle;
	private int mFragmentShaderHandle;

	/**
	 * gyro sensor attrs
	 */
	private SensorManager mSensorManager;
	private Sensor mSensor;

	private boolean mFirstFrameFlag = true;
	private boolean mIsGyroTrackingEnabled = true;
	private float[] mRotVecValues = null;
	private float[] mRotationQuaternion = new float[4];

	private float[] mRotationMatrix = new float[]{
			1, 0, 0, 0,
			0, 1, 0, 0,
			0, 0, 1, 0,
			0, 0, 0, 0
	};
	private float[] mBiasMatrix = new float[16];

	private WeakReference<Context> mContextWeakReference;

	public SphereRenderer(Context context) {
		mContextWeakReference = new WeakReference<>(context);
		mSphere = Sphere.getDefaultSphere();
		mVertexShader = new Shader();
		mFragmentShader = new Shader();

		mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
	}

	public void reCenter() {
		float[] invertMatrix = new float[16];
		Matrix.invertM(invertMatrix, 0, mRotationMatrix, 0);
		mBiasMatrix = invertMatrix;
	}

	public void enableGyroTracking(boolean enabled) {
		mIsGyroTrackingEnabled = enabled;
	}

	@Override
	public void onGLContextAvailable() { //run in gl context
		mTexture = new Texture();
		mTexture.create();

		// compile shader and link program
		mVertexShaderHandle = OpenGLUtil.loadAndCompileShader(mContextWeakReference.get(), R.raw.sphere_vertex_shader, GLES20.GL_VERTEX_SHADER);
		mFragmentShaderHandle = OpenGLUtil.loadAndCompileShader(mContextWeakReference.get(), R.raw.sphere_fragment_shader, GLES20.GL_FRAGMENT_SHADER);
		mProgramHandle = OpenGLUtil.createAndLinkProgram(mVertexShaderHandle, mFragmentShaderHandle,
				new String[]{"a_Position", "a_Color", "a_TexCoordinate"});
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

		renderSphere();
	}

	@Override
	public void onAttached() {
		mSensorManager.registerListener(this, mSensor, SENSOR_DELAY_FASTEST);
	}

	@Override
	public void onDetached() {
		mSensorManager.unregisterListener(this);
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
		recompileShaders();
		mTexture.loadBitmapToGLTexture(bitmap);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
			if (mFirstFrameFlag) { //初始化时，先给一个初始角度，以便能绘制出第一帧的图
				mFirstFrameFlag = false;
				float[] orientationMatrix = new float[16];
				Matrix.setIdentityM(orientationMatrix, 0);

				if (mRotVecValues == null) {
					mRotVecValues = new float[event.values.length];
				}
				for (int i = 0; i < mRotVecValues.length; i++) {
					mRotVecValues[i] = event.values[i];
				}

				if (mRotVecValues != null) {
					SensorManager.getQuaternionFromVector(mRotationQuaternion, mRotVecValues);
					SensorManager.getRotationMatrixFromVector(orientationMatrix, mRotVecValues);
					mRotationMatrix = orientationMatrix;
				}
				float[] invertMatrix = new float[16];
				Matrix.invertM(invertMatrix, 0, orientationMatrix, 0);
				mBiasMatrix = invertMatrix;
				return;
			}

			if (mIsGyroTrackingEnabled) {
				for (int i = 0; i < mRotVecValues.length; i++) {
					mRotVecValues[i] = event.values[i];
				}

				if (mRotVecValues != null) {
					SensorManager.getQuaternionFromVector(mRotationQuaternion, mRotVecValues);
					SensorManager.getRotationMatrixFromVector(mRotationMatrix, mRotVecValues);
				}
			}
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	private void controlCamera() {
		mCamera.rebuildViewMatrix();
		mCamera.rotate(mRotationMatrix);
		mCamera.rotate(mBiasMatrix);
		mCamera.rotate(-90, Camera.Axis.AXIS_Y);
	}

	private void renderSphere() {
		GLES20.glUseProgram(mProgramHandle);

		mVertexShader.bindVertexBuffer(mProgramHandle, "a_Position", COORDINATES_PER_VERTEX, mSphere.getVertexBuffer());
		mVertexShader.bindColorBuffer(mProgramHandle, "a_Color", COORDINATES_PER_COLOR, mSphere.getVertexBuffer());
		mVertexShader.bindTextureCoordinatesBuffer(mProgramHandle, "a_TextureCoordinates", COORDINATES_PER_TEXTURE_COORDINATES, mSphere.getTextureCoordinatesBuffer());

		mFragmentShader.bindTextureSampler2D(mProgramHandle, "u_Texture", mTexture.getTextureName());

		mVertexShader.bindMVPMatrix(mProgramHandle, "u_MVPMatrix", mCamera.getMVPMatrix());

		mTexture.bindSampler(mFragmentShader.getTextureSamplerHandle());

		GLES20.glDrawElements(GLES20.GL_TRIANGLES, mSphere.getIndicesShortArray().length, GLES20.GL_UNSIGNED_SHORT, mSphere.getIndicesBuffer());

		mVertexShader.disableAllAttrbHandle();
	}

	private void recompileShaders() {
		mVertexShaderHandle = OpenGLUtil.loadAndCompileShader(mContextWeakReference.get(), R.raw.sphere_vertex_shader, GLES20.GL_VERTEX_SHADER);
		mFragmentShaderHandle = OpenGLUtil.loadAndCompileShader(mContextWeakReference.get(), R.raw.sphere_fragment_shader, GLES20.GL_FRAGMENT_SHADER);
		mProgramHandle = OpenGLUtil.createAndLinkProgram(mVertexShaderHandle, mFragmentShaderHandle,
				new String[]{"a_Position", "a_Color", "a_TexCoordinate"});
	}
}
