package com.makewithus.tattooonsenmap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import io.fabric.sdk.android.Fabric;


public class OnsenMapsActivity extends AppCompatActivity
        implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMyLocationButtonClickListener,
        OnMapReadyCallback{


    private static final String TAG = "OnsenMapsActivity";
    private static final int DETAIL_ACTIVITY_TAG = 1000;
    private static final int OFFLINE_ACTIVITY_TAG = 1001;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private GoogleApiClient mGoogleApiClient;

     LinkedHashMap<String, VenueObject> venueList;
    LinkedHashMap<String, VenueObject> localVenueList;

    TreeMap<Integer, String> recommendMap;
     int maxActiveness=0;

     String postURLString;
    HashMap<String, String> postParams;
     String userID;
    Location mLastLocation;
    ArrayMap<String, Marker> mapMarkerMap;

    String postAPITag;
    String postVenueID;

    ProgressDialog progressDialog;
    SearchDialog searchBox;
    //String searchKeyword=null;


    private TattooSQLiteHelper dbHelper;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_onsen_maps);

        //GET USER ID
        String[] userInfo = TattooUtils.getUserInfo(this.getApplicationContext());
        userID = userInfo[0];
        recommendMap = new TreeMap<Integer, String>();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        setUpMapIfNeeded();

    }



    private void dismissProgressDialog(){
        if(progressDialog!=null){
            progressDialog.dismiss();
            progressDialog=null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
        setUpMapIfNeeded();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);

        //GET USER ID
        String[] userInfo = TattooUtils.getUserInfo(this.getApplicationContext());
        userID = userInfo[0];

        if(venueList!=null){
            putMarkerToMap();
        }
    }

    @Override
    protected void onDestroy() {
        //CANCEL EXISTING REQUEST
        try {
            NetworkUtils.getInstance(OnsenMapsActivity.this.getApplicationContext()).getRequestQueue().cancelAll(TattooConstants.API_GET_SHOP_STATUS);
            NetworkUtils.getInstance(OnsenMapsActivity.this.getApplicationContext()).getRequestQueue().cancelAll(TattooConstants.API_FORSQUARE_EXPLORE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();

        mGoogleApiClient.disconnect();
        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();

        clearMapItem();
        if(venueList!=null)
            venueList.clear();
    }


    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        GoogleMapOptions options = new GoogleMapOptions();
        options.mapType(GoogleMap.MAP_TYPE_NORMAL)
                .compassEnabled(false)
                .rotateGesturesEnabled(false)
                .tiltGesturesEnabled(false);
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return true;
            }
        });

        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition position) {
                try {
                    //if(searchKeyword==null) {
                        maxActiveness = 0;

                    LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;
                    double yDelta = Math.abs(bounds.southwest.longitude - bounds.northeast.longitude);
                    double xDelta = Math.abs(bounds.northeast.latitude - bounds.southwest.latitude);

                    final Double minLat = Double.valueOf(bounds.southwest.latitude - xDelta);
                    final Double maxLat = Double.valueOf(bounds.northeast.latitude + xDelta);
                    final Double minLon = Double.valueOf(bounds.northeast.longitude - yDelta);
                    final Double maxLon = Double.valueOf(bounds.southwest.longitude + yDelta);

                    //GET FROM LOCAL DB
                    new GetNearByOfflineDataTask().execute(new Double[]{minLat,maxLat, minLon,maxLon});

                    //CANCEL EXISTING REQUEST
                    //NetworkUtils.getInstance(OnsenMapsActivity.this.getApplicationContext()).getRequestQueue().cancelAll(TattooConstants.API_GET_SHOP_STATUS);
                    //NetworkUtils.getInstance(OnsenMapsActivity.this.getApplicationContext()).getRequestQueue().cancelAll(TattooConstants.API_FORSQUARE_EXPLORE);


                    String keyword = URLEncoder.encode(OnsenMapsActivity.this.getString(R.string.keyword1) ,"UTF-8") + "||" + URLEncoder.encode(OnsenMapsActivity.this.getString(R.string.keyword3), "UTF-8");
                    String urlString = "https://api.foursquare.com/v2/venues/search?client_id=" + TattooConstants.forsquareCID + "&client_secret=" + TattooConstants.forsquareSECRET +
                                "&query=" + keyword + "&intent=browse&ne=" + bounds.northeast.latitude +
                                "," + bounds.northeast.longitude + "&sw=" + bounds.southwest.latitude + "," + bounds.southwest.longitude + "&v=20151004"; // &locale="+Locale.getDefault().getLanguage();

                        // Request a string response from the provided URL.
                        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlString,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        // Display the first 500 characters of the response string.
                                        new ParseForsquareExploreTask().execute(response);
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                //GET STATUS BY BOUNDS FROM OWN SERVER
                                new GetStoreStatacesByLatLonTask().execute(new Double[]{minLat, maxLat, minLon, maxLon});
                            }

                        }) {
                        };
                        stringRequest.setTag(TattooConstants.API_FORSQUARE_EXPLORE);
                        // Add the request to the RequestQueue.
                        NetworkUtils.getInstance(OnsenMapsActivity.this.getApplicationContext()).addToRequestQueue(stringRequest);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap map) {

    }

    /**
     * Callback called when connected to GCore. Implementation of {@link GoogleApiClient.ConnectionCallbacks}.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        try {
            Location mLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation == null) {
                mLastLocation = mLocation;
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 15));
            }else{
                mLastLocation = mLocation;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //DISMISS SEARCH BOX POGRESS
        dismissProgressDialog();
    }

    /**
     * Callback called when disconnected from GCore. Implementation of {@link GoogleApiClient.ConnectionCallbacks}.
     */
    @Override
    public void onConnectionSuspended(int cause) {
        // Do nothing
    }

    /**
     * Implementation of {@link GoogleApiClient.OnConnectionFailedListener}.
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Do nothing
        //DISMISS SEARCH BOX POGRESS
        dismissProgressDialog();
        if(searchBox!=null){
            searchBox.dismiss();
            searchBox=null;
            Toast.makeText(this.getApplicationContext(),R.string.search_box_error,Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }


    private class ParseForsquareExploreTask extends AsyncTask<String,Void,Void>{
        @Override
        protected Void doInBackground(String... params) {
            if(params!=null&&params.length==1){

                LinkedHashMap<String, VenueObject> tmpVenueList = parseFoursquare(params[0]);

                ArrayList<String> list = new ArrayList<String>(tmpVenueList.keySet());

                JSONObject paramJSON = new JSONObject();
                JSONArray idJSON= new JSONArray();
                for (int i = 0; i < list.size(); i++) {
                    // make a list of the venus that are loaded in the list.
                    String id = list.get(i);
                    if(id!=null&&id.length()>0)
                        idJSON.put(list.get(i));
                }

                getStoreStataces(idJSON, tmpVenueList);
            }
            return null;
        }
    }


    class GetStoreStatacesByLatLonTask extends AsyncTask<Double,Void,Void>{
        @Override
        protected Void doInBackground(Double... params) {
            if(params==null||params.length!=4)
                return null;

            //CALLED FROM SENDPOST(). SO PASS LOCAL DATA TO MARGE
            getStoreStatusByLatLon(params[0], params[1], params[2], params[3], localVenueList);
            return null;
        }
    }

    private void getStoreStatusByLatLon(double minLat, double maxLat, double minLon, double maxLon, final LinkedHashMap<String, VenueObject> tmpVenueList){
        String urlString = TattooConstants.BASEURL+TattooConstants.API_GET_SHOP_STATUS_BY_LATLON+"?min_lat="+minLat+"&&max_lat="+maxLat+"&&min_lon="+minLon+"&&max_lon="+maxLon;
        if(userID!=null&&userID.length()>0)
            urlString += "&&user_id="+userID;
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlString,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        new ParseShopStatusTask(response, tmpVenueList).execute();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.d(TAG, error.getMessage());

            }
        });
        stringRequest.setTag(TattooConstants.API_TATTOOMAP_GET_PLACEINFO);
        // Add the request to the RequestQueue.
        NetworkUtils.getInstance(OnsenMapsActivity.this.getApplicationContext()).addToRequestQueue(stringRequest);
    }


    class GetStoreStatacesTask extends AsyncTask<JSONArray,Void,Void>{
        @Override
        protected Void doInBackground(JSONArray... params) {
            if(params==null||params.length!=1)
                return null;

            //CALLED FROM SENDPOST(). SO PASS LOCAL DATA TO MARGE
            getStoreStataces(params[0], venueList);
            return null;
        }
    }


    private void getStoreStataces(JSONArray idJSON, final LinkedHashMap<String, VenueObject> tmpVenueList){
        String urlString = TattooConstants.BASEURL+TattooConstants.API_GET_SHOP_STATUS+"?shop_ids="+idJSON.toString();
        if(userID!=null&&userID.length()>0)
            urlString += "&&user_id="+userID;
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlString,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                       new ParseShopStatusTask(response, tmpVenueList).execute();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.d(TAG, error.getMessage());

            }
        });
        stringRequest.setTag(TattooConstants.API_TATTOOMAP_GET_PLACEINFO);
        // Add the request to the RequestQueue.
        NetworkUtils.getInstance(OnsenMapsActivity.this.getApplicationContext()).addToRequestQueue(stringRequest);
    }

    class ParseShopStatusTask extends AsyncTask<JSONArray,Void,Boolean>{
        String resString=null;
        LinkedHashMap<String, VenueObject> tmpVenueList=null;

        public ParseShopStatusTask(String response, LinkedHashMap<String, VenueObject> _tmpVenueList) {
            resString = response;
            tmpVenueList=_tmpVenueList;
        }

        @Override
        protected Boolean doInBackground(JSONArray... params) {
            if(resString==null||tmpVenueList==null)
                return Boolean.FALSE;

            parseShopStatus(resString, tmpVenueList);
            return Boolean.TRUE;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(result)
                putMarkerToMap();
        }
    }


    private void putMarkerToMap(){
        try {
            //CLEAR MARKER
            //mMap.clear();

            if(mapMarkerMap==null)
                mapMarkerMap = new ArrayMap<String, Marker>(TattooConstants.MAX_MAP_ITEM_SIZE);

            //REMOVE ALREADY PINNED OBJECT
            ArrayList<String> idList = new ArrayList<String>(venueList.keySet());
            ArrayList<String> mapIdList = new ArrayList<String>(mapMarkerMap.keySet());
            idList.removeAll(mapIdList);

            for (int i = 0; i < idList.size(); i++) {
                // make a list of the venus that are loaded in the list.
                String vID = idList.get(i);
                VenueObject venue = venueList.get(vID);

                int resID= getStatusIcon(vID, venue.status, venue.userFavorite);

                float alpha = 1f;
                if(maxActiveness>0) {
                    alpha = (venue.commment_count + venue.status_count + venue.rating_count) / maxActiveness * 1.0f;
                    alpha = (alpha < 0.5f) ? 0.5f : alpha;
                }


                Marker gMarker = mapMarkerMap.get(venue.id);
                if(gMarker!=null) {
                    //gMarker.remove();
                    gMarker.setIcon(BitmapDescriptorFactory.fromResource(resID));
                    gMarker.setAlpha(alpha);
                }else {
                    gMarker = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(venue.lat, venue.lon))
                            .title(venue.id)
                            .alpha(alpha)
                            .icon(BitmapDescriptorFactory.fromResource(resID)));
                }

                mapMarkerMap.put(venue.id, gMarker);

            }

            //TODO:UPDATE EXISTING MARKER IN SCREEN

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getStatusIcon(String vID, int status, boolean favoriteIcon){
        int resID=0;
        if(recommendMap.containsValue(vID)){
            if(favoriteIcon) {
                resID = R.drawable.recommend_favorite;
            }else{
                resID = R.drawable.recommend_icon;
            }
        }else {
            resID = TattooUtils.getStatusIconResNo(status, favoriteIcon);
        }

        return resID;
    }

    private static LinkedHashMap<String, VenueObject> parseFoursquare(final String response) {

        try {
            LinkedHashMap<String, VenueObject> temp = new LinkedHashMap<String, VenueObject>(TattooConstants.MAX_MAP_ITEM_SIZE);

            // make an jsonObject in order to parse the response
            JSONObject jsonObject = new JSONObject(response);
            JSONObject responseJSON = jsonObject.getJSONObject("response");
            JSONArray venueArrayJSON = responseJSON.getJSONArray("venues");
            if(venueArrayJSON!=null&&venueArrayJSON.length()>0){
                for(int i=0;i<venueArrayJSON.length();i++){
                    try {
                        JSONObject venueJSON = venueArrayJSON.getJSONObject(i);
                        VenueObject venue = new VenueObject();
                        venue.id = venueJSON.has("id")?venueJSON.getString("id"):null;
                        venue.name = venueJSON.has("name")?venueJSON.getString("name"):null;
                        JSONObject contactJSON = venueJSON.has("contact")?venueJSON.getJSONObject("contact"):null;
                        if(contactJSON!=null){
                            venue.contactPhoneNo = contactJSON.has("phone")?contactJSON.getString("phone"):null;
                            venue.contactPhoneString = contactJSON.has("formattedPhone")?contactJSON.getString("formattedPhone"):null;
                            venue.twitterID = contactJSON.has("twitter")?contactJSON.getString("twitter"):null;
                            venue.facebookID = contactJSON.has("facebook")?contactJSON.getString("facebook"):null;
                        }

                        venue.webSiteUrl = venueJSON.has("url")?venueJSON.getString("url"):null;

                        JSONObject locationJSON = venueJSON.has("location")?venueJSON.getJSONObject("location"):null;
                        if(locationJSON!=null){
                            venue.locationAddress = locationJSON.has("address")?locationJSON.getString("address"):null;
                            venue.lat = locationJSON.has("lat")?locationJSON.getDouble("lat"):0;
                            venue.lon = locationJSON.has("lng")?locationJSON.getDouble("lng"):0;
                            venue.distance = locationJSON.has("distance")?locationJSON.getInt("distance"):-1;
                            venue.cc = locationJSON.has("cc")?locationJSON.getString("cc"):null;
                            venue.state = locationJSON.has("state")?locationJSON.getString("state"):null;
                            venue.country = locationJSON.has("country")?locationJSON.getString("country"):null;

                            JSONArray fmtAddrArrayJSON =  locationJSON.has("formattedAddress")?locationJSON.getJSONArray("formattedAddress"):null;
                            if(fmtAddrArrayJSON!=null&&fmtAddrArrayJSON.length()>0){
                                venue.formattedAddress = new String[fmtAddrArrayJSON.length()];
                                for(int j=0;j<fmtAddrArrayJSON.length();j++){
                                    venue.formattedAddress[j] = fmtAddrArrayJSON.getString(j);
                                }
                            }
                        }

                        JSONObject statsJSON = venueJSON.optJSONObject("stats");
                        if(statsJSON!=null){
                            venue.checkinCnt = statsJSON.optInt("checkinsCount", -1);
                            venue.userCnt = statsJSON.optInt("usersCount", -1);
                        }

                        venue.refferalID = venueJSON.has("referralId")?venueJSON.getString("referralId"):null;
                        temp.put(venue.id, venue);
                        Log.d(TAG, "ADDED name="+venue.name);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            return temp;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    //PARSE ONLINE DATA
    private void parseShopStatus(final String response, LinkedHashMap<String, VenueObject> tmpVenueList) {

        try {
            // make an jsonObject in order to parse the response
            JSONArray statsuJSONArray = new JSONArray(response);
            if(recommendMap==null)
                recommendMap = new TreeMap<Integer, String >();
            if(statsuJSONArray!=null&&statsuJSONArray.length()>0){
                int tmpMaxActivenss = 0;
                for(int i=0;i<statsuJSONArray.length();i++){
                    try {
                        JSONObject statusJSON = statsuJSONArray.getJSONObject(i);
                        String shopID= statusJSON.has("shop_id")?statusJSON.getString("shop_id"):null;
                        VenueObject mVenue = tmpVenueList.get(shopID);
                        mVenue = TattooUtils.parseVenue(statusJSON, mVenue);

                        //MARGE WITH LOCAL DATA
                        if(localVenueList!=null){
                            VenueObject lVenue = localVenueList.get(mVenue.id);
                            if(lVenue!=null){
                                mVenue.userFavorite = lVenue.userFavorite;
                                mVenue.createdTimeInMilli = lVenue.createdTimeInMilli;
                            }
                        }

                        //CALC MAX ACTIVENESS
                        int activeness = mVenue.commment_count+mVenue.rating_count+mVenue.status_count;
                        tmpMaxActivenss = (tmpMaxActivenss<activeness)?activeness:tmpMaxActivenss;

                        tmpVenueList.put(mVenue.id, mVenue);

                        //CALC RECOMMEND VENUE
                        calcRecommendMap(mVenue);
                        Log.d(TAG, "ADDED name="+mVenue.name);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }


            if(venueList==null) {
                venueList = new LinkedHashMap<String, VenueObject>(TattooConstants.MAX_MAP_ITEM_SIZE);
                venueList.putAll(tmpVenueList);
            }else{
                venueList.putAll(tmpVenueList);
            }

            margeVenueData();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    void calcRecommendMap(VenueObject venue){
        //CALC RECOMMEND VENUE
        if(venue.status==TattooConstants.STATUS_OK) {
            int recommendVal = venue.rating*TattooConstants.RECOMMEND_RATING_WEIGHT + venue.rating_count*TattooConstants.RECOMMEND_RATING_CNT_WEIGHT +
                    venue.commment_count*TattooConstants.RECOMMEND_RATING_COMNT_CNT_WEIGHT + venue.status_count*TattooConstants.RECOMMEND_RATING_STATUS_CNT_WEIGHT +
                    venue.checkinCnt+TattooConstants.RECOMMEND_RATING_FORSQUARE_CHECKIN_WEIGHT;
            if(recommendMap.size()<TattooConstants.RECOMMEND_MAX_CNT){
                recommendMap.put(Integer.valueOf(recommendVal), venue.id);
            }else{
                ArrayList<Integer> keyList = new ArrayList<>(recommendMap.keySet());
                for(int j=0; j<keyList.size(); j++){
                    Integer key = keyList.get(j);
                    if(key.intValue()<recommendVal){
                        recommendMap.remove(key);
                        recommendMap.put(Integer.valueOf(recommendVal), venue.id);
                        break;
                    }
                }
            }

        }
    }

    /*
    *  params[0] = min LAT,  params[1] = max LAT, params[2] = min LON, params[3] = max LON
     */
    class GetNearByOfflineDataTask extends AsyncTask<Double, Void, Boolean>{

        @Override
        protected Boolean doInBackground(Double... params) {
            if(params==null||params.length!=4)
                return  null;

            TattooSQLiteHelper dbHelper = TattooSQLiteHelper.getInstance(OnsenMapsActivity.this.getApplicationContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            Cursor cursor = db.query(TattooSQLiteHelper.VenueMaster.TABLE_NAME, null, TattooSQLiteHelper.VenueMaster.COLUMN_VENUE_LAT + ">= ? AND " + TattooSQLiteHelper.VenueMaster.COLUMN_VENUE_LAT + " <= ? AND " +
                            TattooSQLiteHelper.VenueMaster.COLUMN_VENUE_LON + " >= ? AND " + TattooSQLiteHelper.VenueMaster.COLUMN_VENUE_LON + " <= ?",
                    new String[]{params[0].toString(), params[1].toString(), params[2].toString(), params[3].toString()}, null, null, null, null);

            int vIDIndex = cursor.getColumnIndex(TattooSQLiteHelper.VenueMaster.COLUMN_VENUE_ID);
            int nameIndex = cursor.getColumnIndex(TattooSQLiteHelper.VenueMaster.COLUMN_VENUE_NAME);
            int createdIndex = cursor.getColumnIndex(TattooSQLiteHelper.VenueMaster.COLUMN_CREATED_TIME);
            int addressIndex = cursor.getColumnIndex(TattooSQLiteHelper.VenueMaster.COLUMN_VENUE_ADDRESS);
            int ratingIndex = cursor.getColumnIndex(TattooSQLiteHelper.VenueMaster.COLUMN_VENUE_RATING);
            int fbIndex = cursor.getColumnIndex(TattooSQLiteHelper.VenueMaster.COLUMN_VENUE_FACEBOOKID);
            int twitterIndex = cursor.getColumnIndex(TattooSQLiteHelper.VenueMaster.COLUMN_VENUE_TWITTER);
            int latIndex = cursor.getColumnIndex(TattooSQLiteHelper.VenueMaster.COLUMN_VENUE_LAT);
            int lonIndex = cursor.getColumnIndex(TattooSQLiteHelper.VenueMaster.COLUMN_VENUE_LON);
            int phoneIndex = cursor.getColumnIndex(TattooSQLiteHelper.VenueMaster.COLUMN_VENUE_PHONE);
            int phoneFormatedIndex = cursor.getColumnIndex(TattooSQLiteHelper.VenueMaster.COLUMN_VENUE_PHONE_FROMATED);
            int statusIndex = cursor.getColumnIndex(TattooSQLiteHelper.VenueMaster.COLUMN_VENUE_STATUS);
            int urlIndex = cursor.getColumnIndex(TattooSQLiteHelper.VenueMaster.COLUMN_VENUE_URL);
            int favoriteIndex = cursor.getColumnIndex(TattooSQLiteHelper.VenueMaster.COLUMN_USER_FAVORITE);
            int pronounceIndex = cursor.getColumnIndex(TattooSQLiteHelper.VenueMaster.COLUMN_PRONOUNCE);
            int photoUrlIndex = cursor.getColumnIndex(TattooSQLiteHelper.VenueMaster.COLUMN_PHOTO_URL);

            if (localVenueList==null)
                localVenueList = new LinkedHashMap<String, VenueObject>(TattooConstants.MAX_MAP_ITEM_SIZE);

            if(cursor.moveToFirst()) {
                do {
                    VenueObject venue = new VenueObject();
                    if (!cursor.isNull(vIDIndex))
                        venue.id = cursor.getString(vIDIndex);
                    if (!cursor.isNull(nameIndex))
                        venue.name = cursor.getString(nameIndex);
                    if (!cursor.isNull(createdIndex))
                        venue.createdTimeInMilli = cursor.getLong(createdIndex);
                    if (!cursor.isNull(addressIndex)) {
                        String[] address = {cursor.getString(addressIndex)};
                        venue.formattedAddress = address;
                    }
                    if (!cursor.isNull(ratingIndex))
                        venue.rating = cursor.getInt(ratingIndex);
                    if (!cursor.isNull(fbIndex))
                        venue.facebookID = cursor.getString(fbIndex);
                    if (!cursor.isNull(twitterIndex))
                        venue.twitterID = cursor.getString(twitterIndex);
                    if (!cursor.isNull(latIndex))
                        venue.lat = cursor.getDouble(latIndex);
                    if (!cursor.isNull(lonIndex))
                        venue.lon = cursor.getDouble(lonIndex);
                    if (!cursor.isNull(phoneIndex))
                        venue.contactPhoneNo = cursor.getString(phoneIndex);
                    if (!cursor.isNull(phoneFormatedIndex))
                        venue.contactPhoneString = cursor.getString(phoneFormatedIndex);
                    if (!cursor.isNull(statusIndex))
                        venue.status = cursor.getInt(statusIndex);
                    if (!cursor.isNull(urlIndex))
                        venue.webSiteUrl = cursor.getString(urlIndex);
                    if (!cursor.isNull(favoriteIndex))
                        venue.userFavorite = (cursor.getInt(favoriteIndex)==TattooConstants.STATUS_OK)?true:false;
                    if (!cursor.isNull(pronounceIndex))
                        venue.pronounce = cursor.getString(pronounceIndex);
                    if (!cursor.isNull(photoUrlIndex))
                        venue.photoUrl = cursor.getString(photoUrlIndex);

                    localVenueList.put(venue.id, venue);

                    //CALC MAX ACTIVENESS
                    int activeness = venue.commment_count+venue.rating_count+venue.status_count;
                    maxActiveness = (maxActiveness<activeness)?activeness:maxActiveness;

                    //CALC RECOMMEND VENUE
                    calcRecommendMap(venue);
                }while (cursor.moveToNext());

                cursor.close();

                //MARGE VENUE DATA
                margeVenueData();

                return true;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(result){
                putMarkerToMap();
            }
        }
    }

    private void margeVenueData(){
        //MARGE VENUE DATA
        if(venueList!=null) {
            synchronized (venueList) {
                ArrayList<String> keyList = new ArrayList<>(localVenueList.keySet());
                for(String key : keyList){
                    VenueObject mVenue = venueList.get(key);
                    if(mVenue!=null){
                        VenueObject lVenue = localVenueList.get(key);
                                /*
                                 *    USE LOCAL DATA : userFavorite, createdTimeInMillis
                                 *    USE LATEST DATA : REST OF ALL
                                 */

                        mVenue.userFavorite = lVenue.userFavorite;
                        mVenue.createdTimeInMilli = lVenue.createdTimeInMilli;
                        venueList.put(mVenue.id, mVenue);
                    }
                }
            }
        }else{
            venueList = new LinkedHashMap<>(TattooConstants.MAX_MAP_ITEM_SIZE);
            venueList.putAll(localVenueList);
        }
    }

    @Override
    public void onTrimMemory(int level) {
        switch(level){
            case TRIM_MEMORY_UI_HIDDEN:
                clearMapItem();
                break;
            case TRIM_MEMORY_RUNNING_LOW:
               // clearMapItem();
                break;
            case TRIM_MEMORY_RUNNING_CRITICAL:
               // clearMapItem();
                break;
            case TRIM_MEMORY_BACKGROUND:
                clearMapItem();
                break;
            case TRIM_MEMORY_MODERATE:
                clearMapItem();
                break;
            case TRIM_MEMORY_COMPLETE:
                clearMapItem();
                break;
        }
        super.onTrimMemory(level);
    }

    private synchronized void clearMapItem(){
        if(mapMarkerMap!=null){
            mapMarkerMap.clear();
        }
        if(mMap!=null){
            mMap.clear();
        }
        if(localVenueList!=null)
            localVenueList.clear();
    }
}
