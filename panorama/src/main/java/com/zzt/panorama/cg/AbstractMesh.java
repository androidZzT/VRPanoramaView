package com.zzt.panorama.cg;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by Android_ZzT on 2018/7/11.
 */
public abstract class AbstractMesh {

	public static final int COORDINATES_PER_VERTEX = 3;
	public static final int COORDINATES_PER_COLOR = 4;
	public static final int COORDINATES_PER_TEXTURE_COORDINATES = 2;

	protected FloatBuffer mVertexBuffer;

	protected FloatBuffer mColorBuffer;

	protected FloatBuffer mTextureCoordinatesBuffer;

	protected ShortBuffer mIndicesBuffer;

	public FloatBuffer getVertexBuffer() {
		return mVertexBuffer;
	}

	public FloatBuffer getColorBuffer() {
		return mColorBuffer;
	}

	public FloatBuffer getTextureCoordinatesBuffer() {
		return mTextureCoordinatesBuffer;
	}

	public ShortBuffer getIndicesBuffer() {
		return mIndicesBuffer;
	}
}
