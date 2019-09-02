package com.zzt.panorama.cg.glcontext.configchooser;

/**
 * Created by Android_ZzT on 2018/10/19.
 */
public class SimpleEGLConfigChooser extends ComponentSizeChooser {

	public SimpleEGLConfigChooser(boolean withDepthBuffer) {
		super(8, 8, 8, 0, withDepthBuffer ? 16 : 0, 0);
	}
}
