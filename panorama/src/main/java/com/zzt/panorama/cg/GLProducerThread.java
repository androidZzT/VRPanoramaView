package com.zzt.panorama.cg;

import android.graphics.SurfaceTexture;

import com.zzt.panorama.util.LogHelper;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Android_ZzT on 2018/8/1.
 * <p>
 * 提供 Egl context 的渲染线程
 * 1.绑定了简单的消息队列 {@link GLEventHandler}
 * 2.通过 {@link EglHelper} 管理Egl相关操作
 */
public class GLProducerThread extends Thread {

	private static final String TAG = GLProducerThread.class.getSimpleName();

	public final static int RENDERMODE_WHEN_DIRTY = 0;
	public final static int RENDERMODE_CONTINUOUSLY = 1;

	//constructor
	private AtomicBoolean mShouldRender;
	private SurfaceTexture mSurfaceTexture;
	private IGLTextureRenderer mTextureRenderer;
	private int mRenderMode;

	private EglHelper mEGLHelper;

	//event
	private Object LOCK = new Object();
	private GLEventHandler mEventHandler;

	private boolean mIsPaused;

	public GLProducerThread(SurfaceTexture surfaceTexture, IGLTextureRenderer textureRenderer, AtomicBoolean shouldRender) {
		mSurfaceTexture = surfaceTexture;
		mTextureRenderer = textureRenderer;
		mEGLHelper = new EglHelper();
		mShouldRender = shouldRender;
		mEventHandler = new GLEventHandler();
	}

	public void setRenderMode(int renderMode) {
		mRenderMode = renderMode;
	}

	/**
	 * 控制线程是否保持循环处理任务队列
	 *
	 * @param shouldRender
	 */
	public void setShouldRender(boolean shouldRender) {
		mShouldRender.set(shouldRender);
	}

	/**
	 * 向线程入队一个任务
	 *
	 * @param runnable
	 */
	public void enqueueEvent(Runnable runnable) {
		mEventHandler.enqueueEvent(runnable);
	}

	/**
	 * 控制线程循环暂停
	 */
	public void onPause() {
		mIsPaused = true;
	}

	/**
	 * 控制线程循环继续
	 */
	public void onResume() {
		mIsPaused = false;
		requestRender();
	}

	public void requestRender() {
		synchronized (LOCK) {
			LOCK.notifyAll();
		}
	}

	public void releaseEglContext() {
		mEGLHelper.releaseEGLContext();
	}

	public void refreshSurfaceTexture(SurfaceTexture surfaceTexture) {
		enqueueEvent(() -> {
			mEGLHelper.refreshSurfaceTexture(surfaceTexture);
		});
	}

	@Override
	public void run() {
		try {
			mEGLHelper.initEGLContext(mSurfaceTexture);
			mTextureRenderer.onGLContextAvailable();

			while (mShouldRender != null && mShouldRender.get() != false) {

				mEventHandler.dequeueEventAndRun();//先执行事件队列中没完成的任务

				mTextureRenderer.onDrawFrame();

				mEGLHelper.swapBuffers();

				if (mIsPaused) {
					pauseLoop();
				}

				Thread.sleep(5);//预防帧数过高，手机发热
			}
		} catch (Exception e) {
			LogHelper.e(TAG, e.getMessage());
		}
	}

	private void pauseLoop() {
		synchronized (LOCK) {
			try {
				LOCK.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}


}
