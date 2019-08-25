package com.m3bi.flickrapp.imageutils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.m3bi.flickrapp.network.EasySSLSocketFactory;
import com.m3bi.flickrapp.R;
import com.m3bi.flickrapp.utils.Constants;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.Stack;
import java.util.WeakHashMap;

public class ImageLoader {

	MemoryCache memoryCache = new MemoryCache();
 
	private Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());

	public ImageLoader(Context context) {
		// Make the background thread low priority. This way it will not affect
		// the UI performance
		photoLoaderThread.setPriority(Thread.NORM_PRIORITY - 1);
		 
	}

	final int errorImg = R.drawable.error;
	final int stub_id1 = R.drawable.dot;

	/**
	 * This method is to display the image accordingly if image not loaded
	 * showing the progress bar else displaying the downlaoded image from URL
	 */
	public void displayImage(String url, Activity activity,
			ImageView imageView, ProgressBar progressBar) {
		imageViews.put(imageView, url);
		Bitmap bitmap = memoryCache.get(url);

		// Logger.doLog("", "bitmap is " + bitmap + url);

		if (bitmap != null) {
			progressBar.setVisibility(View.GONE);
			imageView.setImageBitmap(bitmap);
		} else {

			queuePhoto(url, activity, imageView, progressBar);
			progressBar.setVisibility(View.VISIBLE);
			//imageView.setImageResource(stub_id1);
		}

		Log.v("", "memoryCache.getSize()" + memoryCache.getSize());
	}

	/**
	 * This ImageView may be used for other images before. So there may be some
	 * old tasks in the queue. We need to discard them.
	 */

	private void queuePhoto(String url, Activity activity, ImageView imageView,
			ProgressBar progressBar) {

		photosQueue.clean(imageView);
		PhotoToLoad p = new PhotoToLoad(url.trim(), imageView, progressBar);
		synchronized (photosQueue.photosToLoad) {
			photosQueue.photosToLoad.push(p);
			photosQueue.photosToLoad.notifyAll();
		}

		// start thread if it's not started yet
		if (photoLoaderThread.getState() == Thread.State.NEW)
			photoLoaderThread.start();
	}




	public static Bitmap downloadBitmap(String src) {

		try {
			URL url = new URL(src);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			Bitmap myBitmap = BitmapFactory.decodeStream(input);
			return myBitmap;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}



	// Task for the queue
	private class PhotoToLoad {
		public String url;
		public ImageView imageView;
		public ProgressBar progressBar;

		public PhotoToLoad(String u, ImageView i, ProgressBar progressBar) {
			url = u;
			imageView = i;
			this.progressBar = progressBar;
		}
	}

	PhotosQueue photosQueue = new PhotosQueue();

	public void stopThread() {
		photoLoaderThread.interrupt();
	}

	// stores list of photos to down load
	class PhotosQueue {
		private Stack<PhotoToLoad> photosToLoad = new Stack<PhotoToLoad>();

		// removes all instances of this ImageView
		public void clean(ImageView image) {
			for (int j = 0; j < photosToLoad.size();) {
				if (photosToLoad.get(j).imageView == image)
					photosToLoad.remove(j);
				else
					++j;
			}
		}
	}

	// Creating separate threads for each.
	class PhotosLoader extends Thread {
		public void run() {
			try {
				while (true) {
					// thread waits until there are any images to load in the
					// queue
					if (photosQueue.photosToLoad.size() == 0)
						synchronized (photosQueue.photosToLoad) {
							photosQueue.photosToLoad.wait();
						}
					if (photosQueue.photosToLoad.size() != 0) {
						PhotoToLoad photoToLoad;
						synchronized (photosQueue.photosToLoad) {
							photoToLoad = photosQueue.photosToLoad.pop();
						}
						Bitmap bmp = downloadBitmap(photoToLoad.url);

						/*if(memoryCache.getSize()>500) {
							clearCache();
						}*/
						memoryCache.put(photoToLoad.url, bmp);
						String tag = imageViews.get(photoToLoad.imageView);
						// String tag1 =
						// progressBars.get(photoToLoad.progressBar);
						if (tag != null && tag.equals(photoToLoad.url)) {
							BitmapDisplayer bd = new BitmapDisplayer(bmp,
									photoToLoad.imageView,
									photoToLoad.progressBar);
							Activity a = (Activity) photoToLoad.imageView
									.getContext();
							a.runOnUiThread(bd);
						}
					}
					if (Thread.interrupted())
						break;
				}
			} catch (InterruptedException e) {
				// allow thread to exit
				e.printStackTrace();
				e.getMessage();
			}
		}
	}

	PhotosLoader photoLoaderThread = new PhotosLoader();

	// Used to display bitmap in the UI thread
	class BitmapDisplayer implements Runnable {
		Bitmap bitmap;
		ImageView imageView;
		ProgressBar progressBar;

		public BitmapDisplayer(Bitmap b, ImageView i, ProgressBar progressBar) {
			bitmap = b;
			imageView = i;
			this.progressBar = progressBar;
		}

		public void run() {
			if (bitmap != null) {
				progressBar.setVisibility(View.GONE);
				imageView.setImageBitmap(bitmap);
			} else {
				progressBar.setVisibility(View.GONE);
				imageView.setImageResource(errorImg);
			}
		}
	}

	// This is used to clear the cache bitmap stored.
	public void clearCache() {
		memoryCache.clear();
	}

}
