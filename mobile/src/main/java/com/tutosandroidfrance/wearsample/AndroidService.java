package com.tutosandroidfrance.wearsample;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;

public interface AndroidService {

    public static final String ENDPOINT = "http://tutos-android-france.com/wp-content/uploads/2015/03";

    @GET("/android_versions.json")
    public void getElements(Callback<List<Element>> callback);
}
