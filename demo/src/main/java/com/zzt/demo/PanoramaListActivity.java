package com.zzt.demo;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.zzt.panorama.ZPanoramaTextureView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Android_ZzT on 2019-09-02.
 */
public class PanoramaListActivity extends AppCompatActivity {

	private static final List<String> urls;

	static {
		urls = new ArrayList<>();
		urls.add("https://d147wy1xnm9vni.cloudfront.net/rPJ4uS65Qc9Jdowxjg5WNw/thumbnails/360_180_source_104_491786.jpg-video.vr.medium");
		urls.add("https://d22779be5rhkgh.cloudfront.net/17cdc124e1004ccc91aa6a2992125679.png-video.vr.medium");
		urls.add("https://d22779be5rhkgh.cloudfront.net/_w9QbsGPj6JmY8pdkR2Pjw/thumbnails/360_180_source_102.jpg-video.vr.medium");
		urls.add("https://d147wy1xnm9vni.cloudfront.net/rPJ4uS65Qc9Jdowxjg5WNw/thumbnails/360_180_source_104_491786.jpg-video.vr.medium");
		urls.add("https://d22779be5rhkgh.cloudfront.net/17cdc124e1004ccc91aa6a2992125679.png-video.vr.medium");
		urls.add("https://d147wy1xnm9vni.cloudfront.net/rPJ4uS65Qc9Jdowxjg5WNw/thumbnails/360_180_source_104_491786.jpg-video.vr.medium");
		urls.add("https://d22779be5rhkgh.cloudfront.net/17cdc124e1004ccc91aa6a2992125679.png-video.vr.medium");
	}

	private static final int PANORAMA_MAX_COUNT = 3;

	private RecyclerView recyclerView;
	private PanoramaListAdapter adapter;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_panorama_list);

		recyclerView = findViewById(R.id.panorama_list);
		adapter = new PanoramaListAdapter(this);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));
		recyclerView.setAdapter(adapter);
	}

	private static class PanoramaListAdapter extends RecyclerView.Adapter<PanoramaListAdapter.ViewHolder> {

		private List<ZPanoramaTextureView> panoramaViews;

		private WeakReference<Context> contextWeakReference;

		public PanoramaListAdapter(Context context) {
			contextWeakReference = new WeakReference<>(context);
			initPanoramaViews();
		}

		@NonNull
		@Override
		public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
			LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
			View root = inflater.inflate(R.layout.item_panorama, viewGroup, false);
			return new ViewHolder(root);
		}

		@Override
		public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

		}

		@Override
		public void onViewAttachedToWindow(@NonNull ViewHolder holder) {
			super.onViewAttachedToWindow(holder);
			int position = holder.getAdapterPosition();
			String url = urls.get(position);

			ZPanoramaTextureView panoramaView = getPanoramaView();
			panoramaView.setBitmapUrl(url);
			addPanoramaView(panoramaView, holder);
		}

		@Override
		public void onViewDetachedFromWindow(@NonNull ViewHolder holder) {
			super.onViewDetachedFromWindow(holder);
			removePanoramaView(holder);
		}

		@Override
		public int getItemCount() {
			return urls.size();
		}

		private void initPanoramaViews() {
			panoramaViews = new ArrayList<>();
			for (int i = 0; i < PANORAMA_MAX_COUNT; i++) {
				ZPanoramaTextureView panoramaTextureView = new ZPanoramaTextureView(contextWeakReference.get());
				panoramaViews.add(panoramaTextureView);
			}
		}

		private void addPanoramaView(ZPanoramaTextureView panoramaView, ViewHolder holder) {
			ViewParent parent = panoramaView.getParent();
			if (parent != null) {
				ViewGroup viewGroup = (ViewGroup) parent;
				viewGroup.removeView(panoramaView);
			}
			holder.container.addView(panoramaView);
		}

		private void removePanoramaView(ViewHolder holder) {
			holder.container.removeAllViews();
		}

		private ZPanoramaTextureView getPanoramaView() {
			for (ZPanoramaTextureView panoramaTextureView : panoramaViews) {
				if (!panoramaTextureView.isAttachedToWindow()) {
					return panoramaTextureView;
				}
			}
			throw new RuntimeException("no more VeeRPanoramaTextureView to use!");
		}

		private static class ViewHolder extends RecyclerView.ViewHolder {

			ViewGroup container;

			public ViewHolder(@NonNull View itemView) {
				super(itemView);
				container = itemView.findViewById(R.id.item_panorama_container);
			}
		}
	}
}
