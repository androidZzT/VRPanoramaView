package com.zzt.panorama.cg.glcontext.configchooser;

import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;

import com.zzt.panorama.util.LogHelper;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;

/**
 * Created by Android_ZzT on 2018/10/19.
 */
public class AndroidConfigChooser implements GLSurfaceView.EGLConfigChooser {

	private static final String TAG = AndroidConfigChooser.class.getSimpleName();

	//	private static final Logger logger = Logger.getLogger(AndroidConfigChooser.class.getName());
	protected int clientOpenGLESVersion = 0;
	protected EGLConfig bestConfig = null;
	protected EGLConfig fastestConfig = null;
	protected EGLConfig choosenConfig = null;
	protected ConfigType type;
	protected int pixelFormat;
	protected boolean verbose = false;
	private final static int EGL_OPENGL_ES2_BIT = 4;

	public enum ConfigType {
		/**
		 * RGB565, 0 alpha, 16 depth, 0 stencil
		 */
		FASTEST,
		/**
		 * RGB???, 0 alpha, >=16 depth, 0 stencil
		 */
		BEST,
		/**
		 * Turn off config chooser and use hardcoded
		 * setEGLContextClientVersion(2); setEGLConfigChooser(5, 6, 5, 0, 16,
		 * 0);
		 */
		LEGACY
	}

	public AndroidConfigChooser(ConfigType type) {
		this.type = type;
	}

	/**
	 * Gets called by the GLSurfaceView class to return the best config
	 */
	@Override
	public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
//		logger.info("GLSurfaceView asks for egl config, returning: ");
//		logEGLConfig(choosenConfig, display, egl);
		return choosenConfig;
	}

	/**
	 * findConfig is used to locate the best config and init the chooser with
	 *
	 * @param egl
	 * @param display
	 * @return true if successfull, false if no config was found
	 */
	public boolean findConfig(EGL10 egl, EGLDisplay display) {
		if (type == ConfigType.BEST) {
			ComponentSizeChooser compChooser = new ComponentSizeChooser(8, 8, 8, 8, 16, 0);
			choosenConfig = compChooser.chooseConfig(egl, display);
			if (choosenConfig == null) {
				compChooser = new ComponentSizeChooser(8, 8, 8, 8, 16, 0);
				choosenConfig = compChooser.chooseConfig(egl, display);
				if (choosenConfig == null) {
					compChooser = new ComponentSizeChooser(8, 8, 8, 0, 16, 0);
					choosenConfig = compChooser.chooseConfig(egl, display);
				}
			}
//			logger.info("JME3 using best EGL configuration available here: ");
		} else {
			ComponentSizeChooser compChooser = new ComponentSizeChooser(5, 6, 5, 0, 16, 0);
			choosenConfig = compChooser.chooseConfig(egl, display);
//			logger.info("JME3 using fastest EGL configuration available here: ");
		}
		if (choosenConfig != null) {
//			logger.info("JME3 using choosen config: ");
//			logEGLConfig(choosenConfig, display, egl);
			pixelFormat = getPixelFormat(choosenConfig, display, egl);
			clientOpenGLESVersion = getOpenGLVersion(choosenConfig, display, egl);
			return true;
		} else {
//			logger.severe("###ERROR### Unable to get a valid OpenGL ES 2.0 config, nether Fastest nor Best found! Bug. Please report this.");
			clientOpenGLESVersion = 1;
			pixelFormat = PixelFormat.UNKNOWN;
			return false;
		}
	}

	private int getPixelFormat(EGLConfig conf, EGLDisplay display, EGL10 egl) {
		int[] value = new int[1];
		int result = PixelFormat.RGB_565;
		egl.eglGetConfigAttrib(display, conf, EGL10.EGL_RED_SIZE, value);
		if (value[0] == 8) {
			result = PixelFormat.RGBA_8888;
            /*
            egl.eglGetConfigAttrib(display, conf, EGL10.EGL_ALPHA_SIZE, value);
            if (value[0] == 8)
            {
                result = PixelFormat.RGBA_8888;
            }
            else
            {
                result = PixelFormat.RGB_888;
            }*/
		}
		if (verbose) {
//			logger.log(Level.INFO, "Using PixelFormat {0}", result);
		}
		//return result; TODO Test pixelformat
		return PixelFormat.TRANSPARENT;
	}

	private int getOpenGLVersion(EGLConfig conf, EGLDisplay display, EGL10 egl) {
		int[] value = new int[1];
		int result = 1;
		egl.eglGetConfigAttrib(display, conf, EGL10.EGL_RENDERABLE_TYPE, value);
		// Check if conf is OpenGL ES 2.0
		LogHelper.d(TAG,"EGL_RENDERABLE_TYPE value = " + value[0]);
		if ((value[0] & EGL_OPENGL_ES2_BIT) != 0) {
			result = 2;
		}
		return result;
	}

	/**
	 * log output with egl config details
	 *
	 * @param conf
	 * @param display
	 * @param egl
	 */
	public void logEGLConfig(EGLConfig conf, EGLDisplay display, EGL10 egl) {
		int[] value = new int[1];
		egl.eglGetConfigAttrib(display, conf, EGL10.EGL_RED_SIZE, value);
//		logger.info(String.format("EGL_RED_SIZE  = %d", value[0]));
		egl.eglGetConfigAttrib(display, conf, EGL10.EGL_GREEN_SIZE, value);
//		logger.info(String.format("EGL_GREEN_SIZE  = %d", value[0]));
		egl.eglGetConfigAttrib(display, conf, EGL10.EGL_BLUE_SIZE, value);
//		logger.info(String.format("EGL_BLUE_SIZE  = %d", value[0]));
		egl.eglGetConfigAttrib(display, conf, EGL10.EGL_ALPHA_SIZE, value);
//		logger.info(String.format("EGL_ALPHA_SIZE  = %d", value[0]));
		egl.eglGetConfigAttrib(display, conf, EGL10.EGL_DEPTH_SIZE, value);
//		logger.info(String.format("EGL_DEPTH_SIZE  = %d", value[0]));
		egl.eglGetConfigAttrib(display, conf, EGL10.EGL_STENCIL_SIZE, value);
//		logger.info(String.format("EGL_STENCIL_SIZE  = %d", value[0]));
		egl.eglGetConfigAttrib(display, conf, EGL10.EGL_RENDERABLE_TYPE, value);
//		logger.info(String.format("EGL_RENDERABLE_TYPE  = %d", value[0]));
		egl.eglGetConfigAttrib(display, conf, EGL10.EGL_SURFACE_TYPE, value);
//		logger.info(String.format("EGL_SURFACE_TYPE  = %d", value[0]));
	}

	public int getClientOpenGLESVersion() {
		return clientOpenGLESVersion;
	}

	public void setClientOpenGLESVersion(int clientOpenGLESVersion) {
		this.clientOpenGLESVersion = clientOpenGLESVersion;
	}

	public int getPixelFormat() {
		return pixelFormat;
	}
}
