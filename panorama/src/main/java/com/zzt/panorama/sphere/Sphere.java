package com.zzt.panorama.sphere;

import com.zzt.panorama.cg.AbstractMesh;
import com.zzt.panorama.util.ListBuilder;
import com.zzt.panorama.util.OpenGLUtil;

import java.util.ArrayList;

/**
 * Created by Android_ZzT on 2018/7/11.
 */
public class Sphere extends AbstractMesh {

	private static final String TAG = Sphere.class.getSimpleName();

	private ListBuilder<Float> mPositions = new ListBuilder<>();
	private ListBuilder<Float> mTextureCoordinates = new ListBuilder<>();
	private ListBuilder<Float> mColors = new ListBuilder<>();
	private ListBuilder<Short> mIndices = new ListBuilder<>();

	private float[] mPositionFloats;
	private float[] mColorFloats;
	private float[] mTextureFloats;
	private short[] mIndicesShorts;

	private Sphere() {

	}

	public Sphere(float radius,
				  int widthSegments, int heightSegments,
				  double phiStart, double phiLength,
				  double thetaStart, double thetaLength) {
		generate(radius, widthSegments, heightSegments, phiStart, phiLength, thetaStart, thetaLength);
	}

	public static Sphere getDefaultSphere() {
		Sphere defaultSphere = new Sphere();
		defaultSphere.generate(
				5f,
				48, 48,
				0, Math.PI * 2,
				0, Math.PI);
		return defaultSphere;
	}

	private void generate(
			float radius,
			int widthSegments, int heightSegments,
			double phiStart, double phiLength,
			double thetaStart, double thetaLength) {

		double thetaEnd = thetaStart + thetaLength;
		int vertexCount = ((widthSegments + 1) * (heightSegments + 1));

		int index = 0;
		ArrayList<ArrayList<Integer>> vertices = new ArrayList<>();

		for (int y = 0; y <= heightSegments; y++) {

			ArrayList<Integer> verticesRow = new ArrayList<>();
			float v = y / (float) heightSegments;

			for (int x = 0; x <= widthSegments; x++) {

				float u = x / (float) widthSegments;
				float px = (float) (-radius * Math.cos(phiStart + u * phiLength) * Math.sin(thetaStart + v * thetaLength));
				float py = (float) (radius * Math.cos(thetaStart + v * thetaLength));
				float pz = (float) (radius * Math.sin(phiStart + u * phiLength) * Math.sin(thetaStart + v * thetaLength));

				mPositions.add(-px, py, pz); // x → y ↑ z 前
				mTextureCoordinates.add(u, v);
				mColors.add(u, v, u, 1f);

				verticesRow.add(index);
				index++;
			}

			vertices.add(verticesRow);
		}

		for (int y = 0; y < heightSegments; y++) {
			for (int x = 0; x < widthSegments; x++) {

				int v1 = vertices.get(y).get(x + 1);
				int v2 = vertices.get(y).get(x);
				int v3 = vertices.get(y + 1).get(x);
				int v4 = vertices.get(y + 1).get(x + 1);

				if (y != 0 || thetaStart > 0) {
					mIndices.add((short) v1, (short) v4, (short) v2);
				}

				if (y != heightSegments - 1 || thetaEnd < Math.PI) {
					mIndices.add((short) v2, (short) v4, (short) v3);
				}
			}
		}

		//create buffer
		mPositionFloats = OpenGLUtil.toPrimitiveArray(mPositions.list, float[].class);
		mVertexBuffer = OpenGLUtil.floatArray2FloatBuffer(mPositionFloats);

		mColorFloats = OpenGLUtil.toPrimitiveArray(mColors.list, float[].class);
		mColorBuffer = OpenGLUtil.floatArray2FloatBuffer(mColorFloats);

		mTextureFloats = OpenGLUtil.toPrimitiveArray(mTextureCoordinates.list, float[].class);
		mTextureCoordinatesBuffer = OpenGLUtil.floatArray2FloatBuffer(mTextureFloats);

		mIndicesShorts = OpenGLUtil.toPrimitiveArray(mIndices.list, short[].class);
		mIndicesBuffer = OpenGLUtil.shortArray2ShortBuffer(mIndicesShorts);
	}

	short[] getIndicesShortArray() {
		return mIndicesShorts;
	}
}
