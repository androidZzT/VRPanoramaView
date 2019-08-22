package com.zzt.panorama.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.BaseDataSubscriber;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.RotationOptions;
import com.facebook.imagepipeline.image.CloseableBitmap;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

/**
 * Created by Android_ZzT on 2019/04/20.
 */

public class ImageUtil {
	private static final String TAG = ImageUtil.class.getSimpleName();

	public static void init(Context applicationContext) {
		Fresco.initialize(applicationContext);
	}

	private static void configRequestBuilder(ImageRequestBuilder requestBuilder) {
		requestBuilder.setRotationOptions(RotationOptions.autoRotate())
				.setLowestPermittedRequestLevel(ImageRequest.RequestLevel.FULL_FETCH)
				.setProgressiveRenderingEnabled(false);
	}

	public static void load(SimpleDraweeView target, Uri uri) {
		ImageRequestBuilder request_builder = ImageRequestBuilder.newBuilderWithSource(uri);
		configRequestBuilder(request_builder);
		load(target, request_builder.build());
	}

	public static void load(SimpleDraweeView target, String string) {
		try {
			load(target, Uri.parse(string));
		} catch (Exception e) {
			LogHelper.e( "", e);
		}
	}

	public static void load(SimpleDraweeView target, int resId) {
		ImageRequestBuilder request_builder = ImageRequestBuilder.newBuilderWithResourceId(resId);
		configRequestBuilder(request_builder);
		load(target, request_builder.build());
	}

	public static void loadBitmapFromNetwork(@NonNull Context context, @NonNull String url, @NonNull onBitmapLoadedCallback callback) {
		ImageRequestBuilder requestBuilder = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url));
		requestBuilder.setRotationOptions(RotationOptions.autoRotate())
				.setLowestPermittedRequestLevel(ImageRequest.RequestLevel.FULL_FETCH)
				.setProgressiveRenderingEnabled(false);

		DataSource<CloseableReference<CloseableImage>> dataSource = Fresco.getImagePipeline().fetchDecodedImage(requestBuilder.build(), context.getApplicationContext());
		dataSource.subscribe(new BaseDataSubscriber<CloseableReference<CloseableImage>>() {

			@Override
			public void onNewResultImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {
				if (!dataSource.isFinished()) {
					return;
				}
				CloseableReference<CloseableImage> ref = dataSource.getResult();
				if (ref != null) {
					final CloseableImage result = ref.get();
					Bitmap bitmap = ((CloseableBitmap) result).getUnderlyingBitmap();
					callback.onBitmapLoaded(bitmap);
				}
			}

			@Override
			public void onFailureImpl(DataSource dataSource) {
				Throwable t = dataSource.getFailureCause();
				if (t != null) {
					t.printStackTrace();
					callback.onBitmapLoaded(null);
				}
			}
		}, CallerThreadExecutor.getInstance());
	}

	public static Bitmap loadBitmapFromCache(Context context, String url) {
		ImageRequestBuilder requestBuilder = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url));
		ImageRequest request = requestBuilder.build();
		Bitmap bitmap = null;
		DataSource<CloseableReference<CloseableImage>> dataSource = Fresco.getImagePipeline().fetchImageFromBitmapCache(request, context.getApplicationContext());
		CloseableReference<CloseableImage> ref = dataSource.getResult();
		try {
			if (ref != null) {
				final CloseableImage result = ref.get();
				if (result instanceof CloseableBitmap) {
					bitmap = ((CloseableBitmap) result).getUnderlyingBitmap();
				} else {
					LogHelper.d(TAG, "result is not a CloseableBitmap");
				}
			}
		} finally {
			CloseableReference.closeSafely(ref);
			dataSource.close();
		}
		return bitmap;
	}

	private static void load(SimpleDraweeView target, ImageRequest request) {
		PipelineDraweeControllerBuilder controller_builder = Fresco.newDraweeControllerBuilder();
		controller_builder.setOldController(target.getController())
				.setAutoPlayAnimations(true)
				.setImageRequest(request);
		target.setController(controller_builder.build());
	}

	public interface onBitmapLoadedCallback {
		void onBitmapLoaded(@Nullable Bitmap bitmap);
	}
}
