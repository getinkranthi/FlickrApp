package com.m3bi.flickrapp.parser;

import com.m3bi.flickrapp.model.FlickrPhoto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class FlickrDataParser {

    public List<FlickrPhoto> parseResponse(String result, List<FlickrPhoto> photoList) {

        try {

            JSONObject responseJsonObj = new JSONObject(result);
            JSONObject photos = responseJsonObj.getJSONObject("photos");
            JSONArray photosJsonArray = photos.getJSONArray("photo");


            for (int i = 0; i < photosJsonArray.length(); i++) {
                JSONObject photoObj = photosJsonArray.getJSONObject(i);

                FlickrPhoto photo = new FlickrPhoto();
                String id = photoObj.getString("id");
                photo.setId(id);
                String owner = photoObj.getString("owner");
                photo.setOwner(owner);
                String secret = photoObj.getString("secret");
                photo.setSecret(secret);
                String server = photoObj.getString("server");
                photo.setServer(server);
                int farm = photoObj.getInt("farm");
                photo.setFarm(farm);
                String title = photoObj.getString("title");
                photo.setTitle(title);
                int isPublic = photoObj.getInt("ispublic");
                photo.setIsPublic(isPublic);
                int isFriend = photoObj.getInt("isfriend");
                photo.setIsFriend(isFriend);
                int isFamily = photoObj.getInt("isfamily");
                photo.setIsFamily(isFamily);

                photoList.add(photo);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return photoList;
    }
}
