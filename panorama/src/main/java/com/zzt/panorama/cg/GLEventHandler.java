package com.zzt.panorama.cg;

import java.util.LinkedList;

/**
 * Created by Android_ZzT on 2018/8/7.
 */
public class GLEventHandler {

	private LinkedList<Runnable> mGLQueue = new LinkedList<>();

	public void dequeueEventAndRun() {
		while (!mGLQueue.isEmpty()) {
			mGLQueue.removeFirst().run();
		}
	}

	public void enqueueEvent(Runnable runnable) {
		synchronized (mGLQueue) {
			mGLQueue.addLast(runnable);
		}
	}
}
