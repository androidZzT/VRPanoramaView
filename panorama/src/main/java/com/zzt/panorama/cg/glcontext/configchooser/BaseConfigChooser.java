package com.zzt.panorama.cg.glcontext.configchooser;

import android.opengl.EGL14;
import android.opengl.EGLExt;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;

/**
 * Created by Android_ZzT on 2018/10/19.
 */
public abstract class BaseConfigChooser implements GLSurfaceView.EGLConfigChooser {

	private int mEGLContextClientVersion;

	public BaseConfigChooser(int[] configSpec) {
		mConfigSpec = filterConfigSpec(configSpec);
	}

	public void setEGLContextClientVersion(int clientVersion) {
		mEGLContextClientVersion = clientVersion;
	}

	public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
		int[] num_config = new int[1];
		if (!egl.eglChooseConfig(display, mConfigSpec, null, 0,
				num_config)) {
			throw new IllegalArgumentException("eglChooseConfig failed");
		}

		int numConfigs = num_config[0];

		if (numConfigs <= 0) {
			throw new IllegalArgumentException(
					"No configs match configSpec");
		}

		EGLConfig[] configs = new EGLConfig[numConfigs];
		if (!egl.eglChooseConfig(display, mConfigSpec, configs, numConfigs,
				num_config)) {
			throw new IllegalArgumentException("eglChooseConfig#2 failed");
		}
		EGLConfig config = chooseConfig(egl, display, configs);
		if (config == null) {
			throw new IllegalArgumentException("No config chosen");
		}
		return config;
	}

	abstract EGLConfig chooseConfig(EGL10 egl, EGLDisplay display,
	                                EGLConfig[] configs);

	protected int[] mConfigSpec;

	private int[] filterConfigSpec(int[] configSpec) {
		if (mEGLContextClientVersion != 2 && mEGLContextClientVersion != 3) {
			return configSpec;
		}
		/* We know none of the subclasses define EGL_RENDERABLE_TYPE.
		 * And we know the configSpec is well formed.
		 */
		int len = configSpec.length;
		int[] newConfigSpec = new int[len + 2];
		System.arraycopy(configSpec, 0, newConfigSpec, 0, len-1);
		newConfigSpec[len-1] = EGL10.EGL_RENDERABLE_TYPE;
		if (mEGLContextClientVersion == 2) {
			newConfigSpec[len] = EGL14.EGL_OPENGL_ES2_BIT;  /* EGL_OPENGL_ES2_BIT */
		} else {
			newConfigSpec[len] = EGLExt.EGL_OPENGL_ES3_BIT_KHR; /* EGL_OPENGL_ES3_BIT_KHR */
		}
		newConfigSpec[len+1] = EGL10.EGL_NONE;
		return newConfigSpec;
	}
}
