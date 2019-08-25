package com.m3bi.flickrapp.model;

public class FlickrPhoto {

    private String id;
    private String owner;
    private String secret;
    private String server;
    private String title;
    private int farm;
    private int isPublic;
    private int isFriend;
    private int isFamily;
    private String imgURL;

    public String getImgURL() {
       // return imgURL;
       // http://farm{farm}.static.flickr.com/{server}/{id}_{secret}.jpg
       return  "https://farm"+farm+".static.flickr.com/"+server+"/"+id+"_"+secret+".jpg ";
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getFarm() {
        return farm;
    }

    public void setFarm(int farm) {
        this.farm = farm;
    }

    public int getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(int isPublic) {
        this.isPublic = isPublic;
    }

    public int getIsFriend() {
        return isFriend;
    }

    public void setIsFriend(int isFriend) {
        this.isFriend = isFriend;
    }

    public int getIsFamily() {
        return isFamily;
    }

    public void setIsFamily(int isFamily) {
        this.isFamily = isFamily;
    }
}
