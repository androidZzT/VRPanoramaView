package com.zzt.panorama.util;

import java.util.ArrayList;

/**
 * Created by Android_ZzT on 2019-09-02.
 */
public class ListBuilder<T> {

	public final ArrayList<T> list = new ArrayList<>();

	@SafeVarargs
	final public void add(T... items) {
		for (T item : items) {
			list.add(item);
		}
	}
}
