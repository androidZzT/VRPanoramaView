package com.zzt.panorama.cg;

import android.graphics.SurfaceTexture;
import android.opengl.EGL14;

import com.zzt.panorama.cg.glcontext.configchooser.AndroidConfigChooser;
import com.zzt.panorama.util.LogHelper;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

/**
 * Created by Android_ZzT on 2019-09-02.
 */
public class EglHelper {

	private static final String TAG = EglHelper.class.getSimpleName();

	//EGL context
	private EGL10 mEgl = null;
	private EGLDisplay mEGLDisplay = EGL10.EGL_NO_DISPLAY;
	private EGLContext mEGLContext = EGL10.EGL_NO_CONTEXT;
	//	private EGLConfig[] mEGLConfig = new EGLConfig[1];
	private EGLConfig mEGLConfig;
	private EGLSurface mEglSurface;
	private SurfaceTexture mSurfaceTexture;

	public void initEGLContext(SurfaceTexture surfaceTexture) {
		//获取TextureView内置的SurfaceTexture作为EGL的绘图表面，也就是跟系统屏幕打交道
		if (surfaceTexture == null) {
			LogHelper.e(TAG,"no surfaceTexture!");
			return;
		}
		mSurfaceTexture = surfaceTexture;

		//获取系统的EGL对象
		mEgl = (EGL10) EGLContext.getEGL();
		if (mEgl == null) {
			LogHelper.e(TAG,"egl not initialized");
		}

		//获取显示设备
		mEGLDisplay = mEgl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
		if (mEGLDisplay == EGL10.EGL_NO_DISPLAY) {
			LogHelper.e(TAG,"eglGetDisplay failed! " + mEgl.eglGetError());
			return;
		}

		//version中存放当前的EGL版本号，版本号即为version[0].version[1]，如1.0
		int[] version = new int[2];

		//初始化EGL
		if (!mEgl.eglInitialize(mEGLDisplay, version)) {
			LogHelper.e(TAG,"eglInitialize failed! " + mEgl.eglGetError());
			return;
		}

		//GLSurfaceView 中的代码，configChooser 可能会适配各种机型的 EGLConfig
		// Create a config chooser
		AndroidConfigChooser configChooser = new AndroidConfigChooser(AndroidConfigChooser.ConfigType.FASTEST);
		configChooser.setClientOpenGLESVersion(2);
		if (configChooser.findConfig(mEgl, mEGLDisplay)) {
			mEGLConfig = configChooser.chooseConfig(mEgl, mEGLDisplay);
		}

		//根据SurfaceTexture创建EGL绘图表面
		mEglSurface = mEgl.eglCreateWindowSurface(mEGLDisplay, mEGLConfig, mSurfaceTexture, null);
		if (mEglSurface == EGL10.EGL_NO_SURFACE) {
			LogHelper.e(TAG,"eglCreateWindowSurface failed! " + mEgl.eglGetError());
			return;
		}

		//指定哪个版本的OpenGL ES上下文，本文为OpenGL ES 2.0
		int[] contextAttribs = {
				EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
				EGL10.EGL_NONE
		};

		//创建上下文，EGL10.EGL_NO_CONTEXT表示不和别的上下文共享资源
		mEGLContext = mEgl.eglCreateContext(mEGLDisplay, mEGLConfig, EGL10.EGL_NO_CONTEXT, contextAttribs);
		if (mEGLContext == EGL10.EGL_NO_CONTEXT) {
			LogHelper.e(TAG,"eglCreateContext failed! " + mEgl.eglGetError());
			return;
		}

		//指定mEGLContext为当前系统的EGL上下文，你可能发现了使用两个mEglSurface，第一个表示绘图表面，第二个表示读取表面
		if (!mEgl.eglMakeCurrent(mEGLDisplay, mEglSurface, mEglSurface, mEGLContext)) {
			LogHelper.e(TAG,"eglMakeCurrent failed! " + mEgl.eglGetError());
			return;
		}
	}

	public void refreshSurfaceTexture(SurfaceTexture surfaceTexture) {
		mSurfaceTexture = surfaceTexture;
		//根据SurfaceTexture创建EGL绘图表面
		mEglSurface = mEgl.eglCreateWindowSurface(mEGLDisplay, mEGLConfig, mSurfaceTexture, null);
		if (mEglSurface == EGL10.EGL_NO_SURFACE) {
			throw new RuntimeException("eglCreateWindowSurface failed! " + mEgl.eglGetError());
		}

		//指定哪个版本的OpenGL ES上下文，本文为OpenGL ES 2.0
		int[] contextAttribs = {
				EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
				EGL10.EGL_NONE
		};
		//创建上下文，EGL10.EGL_NO_CONTEXT表示不和别的上下文共享资源
		mEGLContext = mEgl.eglCreateContext(mEGLDisplay, mEGLConfig, EGL10.EGL_NO_CONTEXT, contextAttribs);
		if (mEGLContext == EGL10.EGL_NO_CONTEXT) {
			throw new RuntimeException("eglCreateContext fail failed! " + mEgl.eglGetError());
		}

		//指定mEGLContext为当前系统的EGL上下文，你可能发现了使用两个mEglSurface，第一个表示绘图表面，第二个表示读取表面
		if (!mEgl.eglMakeCurrent(mEGLDisplay, mEglSurface, mEglSurface, mEGLContext)) {
			throw new RuntimeException("eglMakeCurrent failed! " + mEgl.eglGetError());
		}
	}

	public void swapBuffers() {
		mEgl.eglSwapBuffers(mEGLDisplay, mEglSurface);
	}

	public void releaseEGLContext() {
		mEgl.eglDestroyContext(mEGLDisplay, mEGLContext);
		mEgl.eglDestroySurface(mEGLDisplay, mEglSurface);
		mEGLContext = EGL10.EGL_NO_CONTEXT;
		mEglSurface = EGL10.EGL_NO_SURFACE;
		mSurfaceTexture.release();
	}
}
