package com.zzt.demo;

import com.zzt.panorama.util.ImageUtil;

/**
 * Created by Android_ZzT on 2019-08-22.
 */
public class Application extends android.app.Application {

	@Override
	public void onCreate() {
		super.onCreate();
		ImageUtil.init(this);
	}
}
