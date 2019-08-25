package com.m3bi.flickrapp.activities;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.m3bi.flickrapp.adapter.FlickrImageAdapter;
import com.m3bi.flickrapp.R;
import com.m3bi.flickrapp.model.FlickrPhoto;
import com.m3bi.flickrapp.parser.FlickrDataParser;
import com.m3bi.flickrapp.utils.Constants;
import com.m3bi.flickrapp.utils.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FlickrImagesActivity extends AppCompatActivity {

    boolean isLoading = false;

    private RecyclerView imagesList;
    private FlickrImageAdapter mAdapter;
    private List<FlickrPhoto> photoList = new ArrayList<>();
    private RecyclerView.LayoutManager mLayoutManager;
    private boolean initialLaunch = true;
    private int scrollPosition = 0;
    private EditText input;
    private String searchKeyword;
    private int loadingBarPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyclerview);
        imagesList = findViewById(R.id.images_recycler_list);
        input = findViewById(R.id.input_box);
        initScrollListener();
    }


    private void initScrollListener() {
        imagesList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                //if already loading then donot make another request.
                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == photoList.size() - 1) {
                        //bottom of list!
                        loadMore();
                    }
                }
            }
        });

    }

    private void makeFlickrRequest(String searchKey) {
        new HttpGetRequest(this).execute(Constants.REQUEST_BASE_URL + searchKey);

    }

    private void loadMore() {

        photoList.add(null);
        mAdapter.notifyItemInserted(photoList.size() - 1);
        loadingBarPos = photoList.size() - 1;

        makeFlickrRequest(searchKeyword);
        mAdapter.notifyDataSetChanged();

    }


    public class HttpGetRequest extends AsyncTask<String, Void, String> {

        private ProgressDialog dialog;

        public HttpGetRequest(FlickrImagesActivity activity) {
            dialog = new ProgressDialog(activity);
            isLoading = true;
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage(getString(R.string.please_wait));
            if (initialLaunch) {
                dialog.show();
            }
        }


        @Override
        protected String doInBackground(String... params) {
            String stringUrl = params[0];
            String result;
            String inputLine;
            try {
                //Create a URL object holding our url
                URL myUrl = new URL(stringUrl);
                //Create a connection
                HttpURLConnection connection = (HttpURLConnection)
                        myUrl.openConnection();
                //Set methods and timeouts
                connection.setRequestMethod(Constants.REQUEST_METHOD);
                connection.setReadTimeout(Constants.READ_TIMEOUT);
                connection.setConnectTimeout(Constants.CONNECTION_TIMEOUT);

                //Connect to our url
                connection.connect();
                //Create a new InputStreamReader
                InputStreamReader streamReader = new
                        InputStreamReader(connection.getInputStream());
                //Create a new buffered reader and String Builder
                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();
                //Check if the line we are reading is not null
                while ((inputLine = reader.readLine()) != null) {
                    stringBuilder.append(inputLine);
                }
                //Close our InputStream and Buffered reader
                reader.close();
                streamReader.close();
                //Set our result equal to our stringBuilder
                result = stringBuilder.toString();
            } catch (IOException e) {
                e.printStackTrace();
                result = null;
            }
            return result;
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            isLoading = false;

            //  Toast.makeText(FlickrImagesActivity.this, "response" + result, 4000).show();
            List<FlickrPhoto> photoFlickrList = new FlickrDataParser().parseResponse(result, photoList);
            dialog.dismiss();
            if (initialLaunch) {
                setDataToView(photoFlickrList);
                initialLaunch = false;
            } else {

                photoFlickrList.remove(loadingBarPos);
                mAdapter.notifyItemRemoved(loadingBarPos);
                mAdapter.notifyDataSetChanged();
                mLayoutManager.scrollToPosition(loadingBarPos);


            }
        }

    }

    private void setDataToView(List<FlickrPhoto> photoList) {
        mAdapter = new FlickrImageAdapter(this, photoList);
        mLayoutManager = new GridLayoutManager(getApplicationContext(), Constants.NUMBER_OF_COL);
        imagesList.setLayoutManager(mLayoutManager);
        imagesList.setItemAnimator(new DefaultItemAnimator());
        imagesList.setAdapter(mAdapter);

    }

    public void searchFlickrImages(View view) {
        photoList = new ArrayList<>();
        initialLaunch = true;
        searchKeyword = input.getText().toString();
        makeFlickrRequest(searchKeyword);
        Utils.hideKeboard(this);

    }


}
