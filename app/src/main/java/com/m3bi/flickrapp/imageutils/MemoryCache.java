package com.m3bi.flickrapp.imageutils;

import android.graphics.Bitmap;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MemoryCache {
    public static HashMap<String, SoftReference<Bitmap>> cache=new HashMap<String, SoftReference<Bitmap>>();
    public static List<String> urls = new ArrayList<String>();
    public Bitmap get(String id){
        if(!cache.containsKey(id))
            return null;
        SoftReference<Bitmap> ref=cache.get(id);
        return ref.get();
    }
    
    public void put(String id, Bitmap bitmap){
    	urls.add(id);
        cache.put(id, new SoftReference<Bitmap>(bitmap));
    }

    public void clear() {
        cache.clear();
    }
    public int getSize() {
    	return cache.size();
    }
}