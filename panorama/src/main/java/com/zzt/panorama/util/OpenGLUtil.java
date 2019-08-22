package com.zzt.panorama.util;

import android.content.Context;
import android.opengl.GLES20;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Android_ZzT on 2018/7/10.
 */
public class OpenGLUtil {

	private static final String TAG = OpenGLUtil.class.getSimpleName();

	/**
	 * 传入 shader 文件 resId，获得 shaderHandle
	 *
	 * @param context
	 * @param rawRes
	 * @param shaderType GLES20.GL_VERTEX_SHADER or GLES20.GL_FRAGMENT_SHADER
	 * @return shaderHandle
	 */
	public static int loadAndCompileShader(Context context, int rawRes, int shaderType) {
		String shaderSource = FileUtil.readFileFromRawResource(context, rawRes);
		int shaderHandle = GLES20.glCreateShader(shaderType);

		if (shaderHandle != 0) {
			// Pass in the shader source.
			GLES20.glShaderSource(shaderHandle, shaderSource);

			// Compile the shader.
			GLES20.glCompileShader(shaderHandle);

			// Get the compilation status.
			final int[] compileStatus = new int[1];
			GLES20.glGetShaderiv(shaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

			// If the compilation failed, delete the shader.
			if (compileStatus[0] == 0) {
				LogHelper.e(TAG, "Error compiling shader: " + GLES20.glGetShaderInfoLog(shaderHandle));
				GLES20.glDeleteShader(shaderHandle);
				shaderHandle = 0;
			}
		}

		if (shaderHandle == 0) {
			throw new RuntimeException("Error creating shader.");
		}

		return shaderHandle;
	}


	/**
	 * Helper function to compile and link a program.
	 *
	 * @param vertexShaderHandle   An OpenGL handle to an already-compiled vertex shader.
	 * @param fragmentShaderHandle An OpenGL handle to an already-compiled fragment shader.
	 * @param attributes           Attributes that need to be bound to the program.
	 * @return An OpenGL handle to the program.
	 */
	public static int createAndLinkProgram(final int vertexShaderHandle, final int fragmentShaderHandle, final String[] attributes) {
		int programHandle = GLES20.glCreateProgram();

		if (programHandle != 0) {
			// Bind the vertex shader to the program.
			GLES20.glAttachShader(programHandle, vertexShaderHandle);

			// Bind the fragment shader to the program.
			GLES20.glAttachShader(programHandle, fragmentShaderHandle);

			// Bind attributes
			if (attributes != null) {
				final int size = attributes.length;
				for (int i = 0; i < size; i++) {
					GLES20.glBindAttribLocation(programHandle, i, attributes[i]);
				}
			}

			// Link the two shaders together into a program.
			GLES20.glLinkProgram(programHandle);

			// Get the link status.
			final int[] linkStatus = new int[1];
			GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);

			// If the link failed, delete the program.
			if (linkStatus[0] == 0) {
				LogHelper.e(TAG, "Error compiling program: " + GLES20.glGetProgramInfoLog(programHandle));
				GLES20.glDeleteProgram(programHandle);
				programHandle = 0;
			}
		}

		if (programHandle == 0) {
			throw new RuntimeException("Error creating program.");
		}

		return programHandle;
	}

	public static FloatBuffer floatArray2FloatBuffer(float[] floats) {
		ByteBuffer bb = ByteBuffer.allocateDirect(floats.length * 4);
		bb.order(ByteOrder.nativeOrder());
		FloatBuffer floatBuffer = bb.asFloatBuffer();
		floatBuffer.put(floats);
		floatBuffer.position(0);
		return floatBuffer;
	}

	public static ShortBuffer shortArray2ShortBuffer(short[] shorts) {
		ByteBuffer bb = ByteBuffer.allocateDirect(shorts.length * 2);
		bb.order(ByteOrder.nativeOrder());
		ShortBuffer shortBuffer = bb.asShortBuffer();
		shortBuffer.put(shorts);
		shortBuffer.position(0);
		return shortBuffer;
	}

	public static <P> P toPrimitiveArray(List<?> list, Class<P> arrayType) {
		if (!arrayType.isArray()) {
			throw new IllegalArgumentException(arrayType.toString());
		}
		Class<?> primitiveType = arrayType.getComponentType();
		if (!primitiveType.isPrimitive()) {
			throw new IllegalArgumentException(primitiveType.toString());
		}

		P array = arrayType.cast(Array.newInstance(primitiveType, list.size()));

		for (int i = 0; i < list.size(); i++) {
			Array.set(array, i, list.get(i));
		}

		return array;
	}


	public static float[] makeFrustumMatrix(float left, float right,
											float bottom, float top,
											float nearZ, float farZ) {
		float ral = right + left;
		float rsl = right - left;
		float tsb = top - bottom;
		float tab = top + bottom;
		float fan = farZ + nearZ;
		float fsn = farZ - nearZ;

		float[] result = new float[]{2.0f * nearZ / rsl, 0.0f, 0.0f, 0.0f,
				0.0f, 2.0f * nearZ / tsb, 0.0f, 0.0f,
				ral / rsl, tab / tsb, -fan / fsn, -1.0f,
				0.0f, 0.0f, (-2.0f * farZ * nearZ) / fsn, 0.0f};

		return result;
	}
}
