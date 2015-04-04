package com.tutosandroidfrance.wearsample;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.wearable.view.DotsPageIndicator;
import android.support.wearable.view.GridViewPager;
import android.support.wearable.view.WearableListView;
import android.util.Log;
import android.widget.Toast;

import com.github.florent37.WearMenu;
import com.github.florent37.WearMenuListListViewAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private final static String TAG = MainActivity.class.getCanonicalName();

    private GridViewPager pager;
    private DotsPageIndicator dotsPageIndicator;

    //la liste des éléments à afficher
    private List<Element> elementList;

    protected GoogleApiClient mApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pager = (GridViewPager) findViewById(R.id.pager);
        dotsPageIndicator = (DotsPageIndicator) findViewById(R.id.page_indicator);
        dotsPageIndicator.setPager(pager);

        elementList = creerListElements();
        pager.setAdapter(new ElementGridPagerAdapter(this,elementList,getFragmentManager()));

        final WearMenu wearMenu = (WearMenu) findViewById(R.id.wear_menu);
        wearMenu.setMenuElements(
                new String[]{
                        "title 1",
                        "title 2",
                        "title 3",
                        "title 4"
                },
                new Drawable[]{
                        getResources().getDrawable(R.drawable.ic_car,null),
                        getResources().getDrawable(R.drawable.ic_notif,null),
                        getResources().getDrawable(R.drawable.ic_picture,null),
                        getResources().getDrawable(R.drawable.ic_speak,null)
                }
        );
        wearMenu.setWearMenuListener(new WearMenu.WearMenuListener() {
            @Override
            public void onWearMenuListClicked(int position) {

            }
        });
    }

    private List<Element> creerListElements() {
        List<Element> list = new ArrayList<>();

        list.add(new Element("Element 1", "Description 1", Color.parseColor("#F44336")));
        list.add(new Element("Element 2", "Description 2", Color.parseColor("#E91E63")));
        list.add(new Element("Element 3", "Description 3", Color.parseColor("#9C27B0")));
        list.add(new Element("Element 4", "Description 4", Color.parseColor("#673AB7")));
        list.add(new Element("Element 5", "Description 5", Color.parseColor("#3F51B5")));
        list.add(new Element("Element 6", "Description 6", Color.parseColor("#2196F3")));

        return list;
    }
}
