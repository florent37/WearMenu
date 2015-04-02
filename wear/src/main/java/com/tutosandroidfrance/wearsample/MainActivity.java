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

public class MainActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, MessageApi.MessageListener, DataApi.DataListener {

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

        //elementList = creerListElements();
        //pager.setAdapter(new ElementGridPagerAdapter(elementList,getFragmentManager()));

        final WearMenu wearMenu = (WearMenu) findViewById(R.id.wear_menu);
        wearMenu.setMenuElements(
                new String[]{
                        "titre1",
                        "titre2",
                        "titre3",
                        "titre4"
                },
                new Drawable[]{
                        getResources().getDrawable(R.drawable.common_full_open_on_phone,null),
                        getResources().getDrawable(R.drawable.common_ic_googleplayservices,null),
                        getResources().getDrawable(R.drawable.common_signin_btn_icon_dark,null),
                        getResources().getDrawable(R.drawable.common_signin_btn_icon_light,null)
                }
        );
        wearMenu.setWearMenuListener(new WearMenu.WearMenuListener() {
            @Override
            public void onWearMenuListClicked(int position) {

            }
        });
    }

    /**
     * A l'ouverture, connecte la montre au Google API Client / donc au smartphone
     */
    @Override
    protected void onStart() {
        super.onStart();
        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mApiClient.connect();
    }

    /**
     * Si nous avons une connection aux Google API, donc au smartphone
     * Nous autorisons l'envoie de messages
     */
    @Override
    public void onConnected(Bundle bundle) {
        Wearable.MessageApi.addListener(mApiClient, this);
        Wearable.DataApi.addListener(mApiClient, this);

        //envoie le premier message
        sendMessage("bonjour", "smartphone");
    }

    /**
     * A la fermeture de l'application, desactive le GoogleApiClient
     * Et ferme l'envoie de message
     */
    @Override
    protected void onStop() {
        if (null != mApiClient && mApiClient.isConnected()) {
            Wearable.MessageApi.removeListener(mApiClient, this);
            Wearable.DataApi.removeListener(mApiClient, this);
            mApiClient.disconnect();
        }
        super.onStop();
    }


    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    /**
     * Appellé à la réception d'un message envoyé depuis le smartphone
     *
     * @param messageEvent message reçu
     */
    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        //traite le message reçu
        final String path = messageEvent.getPath();
        //récupère le contenu du message
        final String message = new String(messageEvent.getData());

        if (path.equals("bonjour")) {
            elementList = new ArrayList<>();
            elementList.add(new Element("Message reçu", message, Color.parseColor("#F44336")));
            startMainScreen();
        }
    }

    public void startMainScreen() {
        //penser à effectuer les actions graphiques dans le UIThread
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //nous affichons ici dans notre viewpager

                if (pager != null && pager.getAdapter() == null)
                    pager.setAdapter(new ElementGridPagerAdapter(getApplicationContext(),elementList, getFragmentManager()));
            }
        });
    }

    /**
     * Envoie un message à au smartphone
     *
     * @param path    identifiant du message
     * @param message message à transmettre
     */
    protected void sendMessage(final String path, final String message) {
        //effectué dans un trhead afin de ne pas être bloquant
        new Thread(new Runnable() {
            @Override
            public void run() {
                //envoie le message à tous les noeuds/montres connectées
                final NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(mApiClient).await();
                for (Node node : nodes.getNodes()) {
                    Wearable.MessageApi.sendMessage(mApiClient, node.getId(), path, message.getBytes()).await();

                }
            }
        }).start();
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        //appellé lorsqu'une donnée à été mise à jour, nous utiliserons une autre méthode

        for (DataEvent event : dataEvents) {
            //on attend les "elements"
            if (event.getType() == DataEvent.TYPE_CHANGED && event.getDataItem().getUri().getPath().startsWith("/elements/")) {
                DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
                List<DataMap> elementsDataMap = dataMapItem.getDataMap().getDataMapArrayList("/list/");

                if (elementList == null || elementList.isEmpty()) {
                    elementList = new ArrayList<>();

                    for (DataMap dataMap : elementsDataMap) {
                        elementList.add(getElement(dataMap));
                    }

                    //charge les images puis affiche le main screen
                    startMainScreen();
                }

            }
        }
    }

    /**
     * Récupère un element depuis sa position
     */
    public Element getElement(DataMap elementDataMap) {
        return new Element(
                elementDataMap.getString("titre"),
                elementDataMap.getString("description"),
                elementDataMap.getString("url"));
    }


}
