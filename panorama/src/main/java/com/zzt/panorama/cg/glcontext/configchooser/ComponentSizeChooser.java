package com.zzt.panorama.cg.glcontext.configchooser;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;

/**
 * Created by Android_ZzT on 2018/10/19.
 */
public class ComponentSizeChooser extends BaseConfigChooser {

	private int[] mValue;
	// Subclasses can adjust these values:
	protected int mRedSize;
	protected int mGreenSize;
	protected int mBlueSize;
	protected int mAlphaSize;
	protected int mDepthSize;
	protected int mStencilSize;

	public ComponentSizeChooser(int redSize, int greenSize, int blueSize,
	                            int alphaSize, int depthSize, int stencilSize) {
		super(new int[]{
				EGL10.EGL_RED_SIZE, redSize,
				EGL10.EGL_GREEN_SIZE, greenSize,
				EGL10.EGL_BLUE_SIZE, blueSize,
				EGL10.EGL_ALPHA_SIZE, alphaSize,
				EGL10.EGL_DEPTH_SIZE, depthSize,
				EGL10.EGL_STENCIL_SIZE, stencilSize,
				EGL10.EGL_NONE});
		mValue = new int[1];
		mRedSize = redSize;
		mGreenSize = greenSize;
		mBlueSize = blueSize;
		mAlphaSize = alphaSize;
		mDepthSize = depthSize;
		mStencilSize = stencilSize;
	}

	@Override
	public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display,
	                              EGLConfig[] configs) {
		for (EGLConfig config : configs) {
			int d = findConfigAttrib(egl, display, config,
					EGL10.EGL_DEPTH_SIZE, 0);
			int s = findConfigAttrib(egl, display, config,
					EGL10.EGL_STENCIL_SIZE, 0);
			if ((d >= mDepthSize) && (s >= mStencilSize)) {
				int r = findConfigAttrib(egl, display, config,
						EGL10.EGL_RED_SIZE, 0);
				int g = findConfigAttrib(egl, display, config,
						EGL10.EGL_GREEN_SIZE, 0);
				int b = findConfigAttrib(egl, display, config,
						EGL10.EGL_BLUE_SIZE, 0);
				int a = findConfigAttrib(egl, display, config,
						EGL10.EGL_ALPHA_SIZE, 0);
				if ((r == mRedSize) && (g == mGreenSize)
						&& (b == mBlueSize) && (a == mAlphaSize)) {
					return config;
				}
			}
		}
		return null;
	}

	private int findConfigAttrib(EGL10 egl, EGLDisplay display,
	                             EGLConfig config, int attribute, int defaultValue) {

		if (egl.eglGetConfigAttrib(display, config, attribute, mValue)) {
			return mValue[0];
		}
		return defaultValue;
	}
}
