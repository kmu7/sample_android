package com.makewithus.tattooonsenmap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

/**
 * Created by KEN on 10/14/15.
 */
public class SearchDialog extends DialogFragment {

    public interface SearchDialogResultCallback {
        void onSearchResult(LatLng target);
        void onDismissSearchBox();
    }

    private static final String TAG = "SearchDialog";
    private static final LatLngBounds BOUNDS_GREATER_JAPAN = new LatLngBounds(
            new LatLng(3.6527841872911404, 121.20267625898123), new LatLng(48.13418970780581,154.30744841694832));

    Activity act;

    protected GoogleApiClient mGoogleApiClient;

    private PlaceAutocompleteAdapter mAdapter;

    private AutoCompleteTextView mAutocompleteView;

    private TextView mPlaceDetailsText;

    private TextView mPlaceDetailsAttribution;

    private LatLng currentLatLng;

    Button sendBtn;

    SearchDialogResultCallback callback;

    ProgressDialog loadingDialog;

    public static SearchDialog newInstance() {
        SearchDialog f = new SearchDialog();
        try {
            Bundle args = new Bundle();
            f.setArguments(args);
            f.setCancelable(true);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return f;

    }

    public void initSearchDialog(GoogleApiClient client, SearchDialogResultCallback mback){
        mGoogleApiClient = client;
        callback = mback;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Build the dialog and set up the button click handlers
        try {
            Bundle args = this.getArguments();

            if(act==null)
                act = SearchDialog.this.getActivity();

            AlertDialog.Builder builder = new AlertDialog.Builder(act);

            LayoutInflater inflater = act.getLayoutInflater();
            View targetV = inflater.inflate(R.layout.search_dialog,
                    null);

            mAutocompleteView = (AutoCompleteTextView)
                    targetV.findViewById(R.id.keywordBox);
            mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);

            // Retrieve the TextViews that will display details and attributions of the selected place.
            mPlaceDetailsText = (TextView) targetV.findViewById(R.id.place_details);
            mPlaceDetailsAttribution = (TextView) targetV.findViewById(R.id.place_attribution);

            // Set up the adapter that will retrieve suggestions from the Places Geo Data API that cover
            // the entire world.
            mAdapter = new PlaceAutocompleteAdapter(act.getApplicationContext(), mGoogleApiClient, BOUNDS_GREATER_JAPAN,
                    null);
            mAutocompleteView.setAdapter(mAdapter);



             sendBtn = (Button)targetV.findViewById(R.id.sendBtn);

            sendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentLatLng == null)
                        return;

                    callback.onSearchResult(currentLatLng);

                    SearchDialog.this.dismiss();
                }
            });

            builder.setView(targetV);

            return builder.create();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {

        super.onDismiss(dialog);

        dismissLoading();
        callback.onDismissSearchBox();
    }

   /* private void getGeonames(String keyword){
        String urlString = TattooConstants.BASEURL + TattooConstants.API_POST_SHOP_STATUS + "?keyword="+keyword;
        StringRequest sr = new StringRequest(Request.Method.GET, urlString, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response!=null&&response.equalsIgnoreCase(String.valueOf(TattooConstants.STATUS_OK))){
                    //SUCCESS

                    //REFLECT TO OBJECT

                }else{
                    //FAIL
                    //TODO:SHOW ERROR ALERT

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, error.getMessage());
                //TODO: SHOW ERROR ALERT

            }
        }) {};
        sr.setTag(postTag);

        NetworkUtils.getInstance(act.getApplicationContext()).addToRequestQueue(sr);
    }*/


    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
            if(mAdapter.getCount()<=position)
                return;

            loadingDialog = TattooUtils.showLoadingDialog(SearchDialog.this.getContext());
            final AutocompletePrediction item = mAdapter.getItem(position);
            final String placeId = item.getPlaceId();
            final CharSequence primaryText = item.getPrimaryText(null);

            Log.i(TAG, "Autocomplete item selected: " + primaryText);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
             details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);

            Toast.makeText(act.getApplicationContext(), "Clicked: " + primaryText,
                    Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Called getPlaceById to get Place details for " + placeId);
        }
    };

    /**
     * Callback for results from a Places Geo Data API query that shows the first place result in
     * the details view on screen.
     */
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                Log.e(TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
                dismissLoading();
                return;
            }
            // Get the Place object from the buffer.
            final Place place = places.get(0);

            // Format details of the place for display and show it in a TextView.
            mPlaceDetailsText.setText(formatPlaceDetails(getResources(), place.getName(),
                    place.getId(), place.getAddress(), place.getPhoneNumber(),
                    place.getWebsiteUri()));

            // Display the third party attributions if set.
            final CharSequence thirdPartyAttribution = places.getAttributions();
            if (thirdPartyAttribution == null) {
                mPlaceDetailsAttribution.setVisibility(View.GONE);
            } else {
                mPlaceDetailsAttribution.setVisibility(View.VISIBLE);
                mPlaceDetailsAttribution.setText(Html.fromHtml(thirdPartyAttribution.toString()));
            }

            currentLatLng = place.getLatLng();

            Log.i(TAG, "Place details received: " + place.getName());

            places.release();
            dismissLoading();
        }
    };

    private static Spanned formatPlaceDetails(Resources res, CharSequence name, String id,
                                              CharSequence address, CharSequence phoneNumber, Uri websiteUri) {
        Log.e(TAG, res.getString(R.string.place_details, name, address, phoneNumber,
                websiteUri));
        return Html.fromHtml(res.getString(R.string.place_details, name, address, phoneNumber,websiteUri));

    }

    private void dismissLoading(){
        if(loadingDialog!=null){
            loadingDialog.dismiss();
            loadingDialog=null;
        }
    }

}