package com.zzt.panorama.cg;

import android.opengl.Matrix;

import static java.lang.Math.PI;

/**
 * Created by Android_ZzT on 2018/7/12.
 */
public class Camera {

	private FOV mFov;

	private float mScreenRatio;

	private final float[] mMVPMatrix = new float[16];
	private final float[] mProjectionMatrix = new float[16];
	private final float[] mViewMatrix = new float[16];

	public Camera(float screenRatio) {
		mScreenRatio = screenRatio;
		init();
	}

	public void init() {
		FOV defaultFov = new FOV();
		setFov(defaultFov);
	}

	public void setFov(FOV fov) {
		mFov = fov;
		float top = (float) (Math.tan(mFov.getViewAngle() * PI / 360.0f) * mFov.getZNear());
		float bottom = -top;
		float left = mScreenRatio * bottom;
		float right = mScreenRatio * top;

		Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, mFov.getZNear(), mFov.getZFar());
	}

	public void lookAt() {
		rebuildViewMatrix();
		//TODO
	}

	public void rotate(float[] rotationMatrix) {
		Matrix.multiplyMM(mViewMatrix, 0, mViewMatrix, 0, rotationMatrix, 0);
	}

	public void rotate(float angle, Axis axis) {
		switch (axis) {
			case AXIS_X:
				Matrix.rotateM(mViewMatrix, 0, angle, 1, 0, 0);
				break;
			case AXIS_Y:
				Matrix.rotateM(mViewMatrix, 0, angle, 0, 1, 0);
				break;
			case AXIS_Z:
				Matrix.rotateM(mViewMatrix, 0, angle, 0, 0, 1);
				break;
		}
	}

	public void translate(float translationMatrix) {
		//TODO
	}

	public void rebuildViewMatrix() {
		Matrix.setIdentityM(mViewMatrix, 0);
	}

	public void rebuildProjectionMatrix() {
	}

	public float[] getViewMatrix() {
		return mViewMatrix;
	}

	public float[] getProjectionMatrix() {
		return mProjectionMatrix;
	}

	public float[] getMVPMatrix() {
		Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
		return mMVPMatrix;
	}

	public class FOV {
		private float zNear = 0.1f;

		private float zFar = 100.0f;

		private float viewAngle = 90;

		public FOV() {

		}

		public FOV(float viewAngle, float zNear, float zFar) {
			this.viewAngle = viewAngle;
			this.zNear = zNear;
			this.zFar = zFar;
		}

		public float getZNear() {
			return zNear;
		}

		public void setZNear(float zNear) {
			this.zNear = zNear;
		}

		public float getZFar() {
			return zFar;
		}

		public void setZFar(float zFar) {
			this.zFar = zFar;
		}

		public float getViewAngle() {
			return viewAngle;
		}

		public void setViewAngle(float viewAngle) {
			this.viewAngle = viewAngle;
		}
	}

	public enum Axis {
		AXIS_X,
		AXIS_Y,
		AXIS_Z
	}
}

