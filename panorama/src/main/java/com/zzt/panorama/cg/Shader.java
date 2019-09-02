package com.zzt.panorama.cg;

import android.opengl.GLES20;

import java.nio.Buffer;

/**
 * Created by Android_ZzT on 2018/9/6.
 */
public class Shader {

	private static final String TAG = "Shader";

	/**
	 * vertex shader uniform mat4 u_MVPMatrix
	 */
	private int mMVPMatrixHandle;

	/**
	 * vertex shader uniform mat4 u_MVMatrix
	 */
	private int mMVMatrixHandle;

	/**
	 * vertex shader attribute vec4 a_Position
	 */
	private int mPositionHandle;

	/**
	 * vertex shader attribute vec4 a_Color
	 */
	private int mColorHandle;

	/**
	 * vertex shader attribute vec2 a_TextureCoordinates
	 */
	private int mTextureCoordinatesHandle;

	/**
	 * fragment shader uniform sampler2D u_TextureUnit
	 */
	private int mTextureSamplerHandle;

	public Shader() {
	}

	public void bindVertexBuffer(int programHandle, String positionAttributeName, int positionCoordinateSize, Buffer vertexBuffer) {
		mPositionHandle = GLES20.glGetAttribLocation(programHandle, positionAttributeName);

		GLES20.glVertexAttribPointer(mPositionHandle, positionCoordinateSize, GLES20.GL_FLOAT, false,
				0, vertexBuffer);

		GLES20.glEnableVertexAttribArray(mPositionHandle);
	}

	public void bindColorBuffer(int programHandle, String colorAttributeName, int colorCoordinateSize, Buffer colorBuffer) {
		mColorHandle = GLES20.glGetAttribLocation(programHandle, colorAttributeName);

		GLES20.glVertexAttribPointer(mColorHandle, colorCoordinateSize, GLES20.GL_FLOAT, false,
				0, colorBuffer);

		GLES20.glEnableVertexAttribArray(mColorHandle);
	}

	public void bindTextureCoordinatesBuffer(int programHandle, String texCoordinatesAttributeName, int textureCoordinatesSize, Buffer textureCoordinatesBuffer) {
		mTextureCoordinatesHandle = GLES20.glGetAttribLocation(programHandle, texCoordinatesAttributeName);

		GLES20.glVertexAttribPointer(mTextureCoordinatesHandle, textureCoordinatesSize, GLES20.GL_FLOAT, false,
				0, textureCoordinatesBuffer);

		GLES20.glEnableVertexAttribArray(mTextureCoordinatesHandle);
	}

	public void bindMVPMatrix(int programHandle, String MVPMatrixUniformName, float[] MVPMatrix) {
		mMVPMatrixHandle = GLES20.glGetUniformLocation(programHandle, MVPMatrixUniformName);

		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, MVPMatrix, 0);
	}

	public void bindMVMatrix(int programHandle, String MVMatrixUniformName,float[] MVMatrix) {
		mMVMatrixHandle = GLES20.glGetUniformLocation(programHandle, MVMatrixUniformName);

		GLES20.glUniformMatrix4fv(mMVMatrixHandle, 1, false, MVMatrix, 0);
	}

	public void bindTextureSampler2D(int programHandle, String textureSamplerUniformName, int textureName) {
		mTextureSamplerHandle = GLES20.glGetUniformLocation(programHandle, textureSamplerUniformName);

		GLES20.glUniform1i(mTextureSamplerHandle, textureName);
	}

	public int getMVPMatrixHandle() {
		return mMVPMatrixHandle;
	}

	public int getMVMatrixHandle() {
		return mMVMatrixHandle;
	}

	public int getPositionHandle() {
		return mPositionHandle;
	}

	public int getColorHandle() {
		return mColorHandle;
	}

	public int getTextureCoordinatesHandle() {
		return mTextureCoordinatesHandle;
	}

	public int getTextureSamplerHandle() {
		return mTextureSamplerHandle;
	}

	public void disableAllAttrbHandle() {
		GLES20.glDisableVertexAttribArray(mPositionHandle);
		GLES20.glDisableVertexAttribArray(mColorHandle);
		GLES20.glDisableVertexAttribArray(mTextureCoordinatesHandle);
	}
}
