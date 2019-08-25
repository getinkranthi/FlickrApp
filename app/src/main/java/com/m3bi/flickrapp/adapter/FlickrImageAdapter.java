package com.m3bi.flickrapp.adapter;


import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.m3bi.flickrapp.R;
import com.m3bi.flickrapp.model.FlickrPhoto;
import com.m3bi.flickrapp.imageutils.ImageLoader;

import java.util.List;


public class FlickrImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity activity;
    private List<FlickrPhoto> data;
    public ImageLoader imageLoader;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    public FlickrImageAdapter(Activity a, List<FlickrPhoto> mStrings) {
        activity = a;
        data = mStrings;
        imageLoader = new ImageLoader(activity.getApplicationContext());
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == VIEW_TYPE_ITEM) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View listItem = layoutInflater.inflate(R.layout.list_item, parent, false);
            ViewHolder viewHolder = new ViewHolder(listItem);
            return viewHolder;
        } else {

            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View listItem = layoutInflater.inflate(R.layout.item_loading, parent, false);
            LoadingViewHolder viewHolder = new LoadingViewHolder(listItem);
            return viewHolder;
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof ViewHolder) {
            populateItemRows((ViewHolder) viewHolder, position);
        } else if (viewHolder instanceof LoadingViewHolder) {
            showLoadingView((LoadingViewHolder) viewHolder, position);
        }
    }

    private void populateItemRows(ViewHolder viewHolder, int position) {

        imageLoader.displayImage(data.get(position).getImgURL().trim(), activity, viewHolder.imageView, viewHolder.progressBar);
       // viewHolder.title.setText(data.get(position).getTitle() + position);
    }

    private void showLoadingView(LoadingViewHolder viewHolder, int position) {
        //ProgressBar would be displayed

    }

    @Override
    public int getItemViewType(int position) {
        return data.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public ProgressBar progressBar;
        public TextView title;

        public ViewHolder(View itemView) {
            super(itemView);
            this.imageView = (ImageView) itemView.findViewById(R.id.list_item_img);
            this.progressBar = (ProgressBar) itemView.findViewById(R.id.progressbar);
            this.title = (TextView) itemView.findViewById(R.id.title);
        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);

        }
    }
}
