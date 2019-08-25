package com.m3bi.flickrapp.utils;

public interface Constants {

    String REQUEST_BASE_URL = "https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=dc2242530334eff5c97106c9110de945&%20format=json&nojsoncallback=1&safe_search=1&text=";
    int NUMBER_OF_COL = 3;
    String REQUEST_METHOD = "GET";
    int READ_TIMEOUT = 15000;
    int CONNECTION_TIMEOUT = 30000;
    /*int CONNECTION_TIMEOUT = 30000;
    String HTTPS = "https";
    String HTTP = "http";
    String HTTP_PROTOCOL_EXPECT_CONTINUE = "http.protocol.expect-continue";
    String UTF_8 = "utf-8";*/

}
