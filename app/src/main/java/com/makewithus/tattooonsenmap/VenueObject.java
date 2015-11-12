package com.makewithus.tattooonsenmap;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by KEN on 10/4/15.
 */
public class VenueObject implements Parcelable, Comparable{
    String id;
    String name;
    String contactPhoneNo;
    String contactPhoneString;
    String locationAddress;
    double lat;
    double lon;
    int distance;
    String cc;
    String state;
    String country;
    String[] formattedAddress;
    int checkinCnt;
    int userCnt;
    String refferalID;
    String webSiteUrl;
    String facebookID;
    String twitterID;
    String photoUrl;
    String pronounce;

    int status=TattooConstants.STATUS_NODATA;
    int rating=-1;
    int rating_count=0;
    int commment_count=0;
    int status_count=0;
    int userRating=-1;
    int usersTodaysPostedStatus=-1;
    long createdTimeInMilli=0;
    boolean userFavorite=false;

    int sortTarget = -1;

    LatLng currentLatLng=null;

    public String getContactPhoneString() {
        if(contactPhoneString==null)
            return "";

        return contactPhoneString;
    }

    public String getCountry() {
        if(country==null)
            return "";

        return country;
    }

    public String getContactPhoneNo() {
        if(contactPhoneNo==null)
            return "";

        return contactPhoneNo;
    }

    public String getLocationAddress() {
        if(locationAddress==null)
            return "";

        return locationAddress;
    }

    public String getName() {
        if(name==null)
            return "";

        return name;
    }

    public String getTwitterID() {
        if(twitterID==null)
            return "";

        return twitterID;
    }

    public String getWebSiteUrl() {
        if(webSiteUrl==null)
            return "";

        return webSiteUrl;
    }

    public String getFacebookID() {
        if(facebookID==null)
            return "";

        return facebookID;
    }

    public String getPhotoUrl() {
        if(photoUrl==null)
            return "";

        return photoUrl;
    }

    public String getPronounce() {
        if(pronounce==null)
            return "";

        return pronounce;
    }

    @Override
    public int compareTo(Object another) {
        VenueObject offline = (VenueObject)another;
        switch (sortTarget){
            case TattooConstants.SORT_BY_DATE:
                long delta = this.createdTimeInMilli - offline.createdTimeInMilli;
                if(delta>0){
                    return 1;
                }else if(delta==0){
                    return 0;
                }else{
                    return -1;
                }
            case TattooConstants.SORT_BY_NAME:
                if(this.name==null) this.name="";
                if(offline.name==null) offline.name="";

                return this.name.compareTo(offline.name);
            case TattooConstants.SORT_BY_NEARBY:
                if(this.currentLatLng==null)
                    return  0;

                double range1 = TattooUtils.distance(this.currentLatLng.latitude, this.lat, this.currentLatLng.longitude, this.lon, 0.0, 0.0);
                double range2 = TattooUtils.distance(this.currentLatLng.latitude, offline.lat, this.currentLatLng.longitude, offline.lon, 0.0, 0.0);
                if(range1>range2){
                    return 1;
                }else if(range1<range2){
                    return -1;
                }else{
                    return 0;
                }
            case TattooConstants.SORT_BY_RATING:
                if(this.rating>offline.rating){
                    return 1;
                }else if(this.rating<offline.rating){
                    return  -1;
                }else{
                    return 0;
                }
            default:

                break;
        }

        return 0;
    }

    public VenueObject() {

    }

    public VenueObject(Parcel in) {
        // TODO Auto-generated constructor stub
        try {
            Bundle data = in.readParcelable(getClass().getClassLoader());

            id = data.containsKey("id")?data.getString("id"):null;
            name = data.containsKey("name")?data.getString("name"):null;
            contactPhoneString = data.containsKey("contactPhoneString")?data.getString("contactPhoneString"):null;
            locationAddress = data.containsKey("locationAddress")?data.getString("locationAddress"):null;
            cc = data.containsKey("cc")?data.getString("cc"):null;
            state = data.containsKey("state")?data.getString("state"):null;
            country = data.containsKey("country")?data.getString("country"):null;
            refferalID = data.containsKey("refferalID")?data.getString("refferalID"):null;
            webSiteUrl = data.containsKey("webSiteUrl")?data.getString("webSiteUrl"):null;
            facebookID = data.containsKey("facebookID")?data.getString("facebookID"):null;
            twitterID = data.containsKey("twitterID")?data.getString("twitterID"):null;
            formattedAddress = data.containsKey("formattedAddress")?data.getStringArray("formattedAddress"):null;
            contactPhoneNo = data.containsKey("contactPhoneNo")?data.getString("contactPhoneNo"):null;
            lat = data.containsKey("lat")?data.getDouble("lat"):0;
            lon = data.containsKey("lon")?data.getDouble("lon"):0;
            distance = data.containsKey("distance")?data.getInt("distance"):0;
            checkinCnt = data.containsKey("checkinCnt")?data.getInt("checkinCnt"):0;
            userCnt = data.containsKey("userCnt")?data.getInt("userCnt"):0;
            status = data.containsKey("status")?data.getInt("status"):TattooConstants.STATUS_NODATA;
            rating = data.containsKey("rating")?data.getInt("rating"):-1;
            rating_count = data.containsKey("rating_count")?data.getInt("rating_count"):-1;
            commment_count = data.containsKey("commment_count")?data.getInt("commment_count"):-1;
            status_count = data.containsKey("status_count")?data.getInt("status_count"):-1;
            userRating = data.containsKey("userRating")?data.getInt("userRating"):-1;
            usersTodaysPostedStatus = data.containsKey("usersTodaysPostedStatus")?data.getInt("usersTodaysPostedStatus"):-1;
            createdTimeInMilli = data.containsKey("createdTimeInMilli")?data.getLong("createdTimeInMilli"):-1;
            sortTarget = data.containsKey("sortTarget")?data.getInt("sortTarget"):-1;
            userFavorite = data.containsKey("userFavorite")?data.getBoolean("userFavorite"):false;
            photoUrl = data.containsKey("photoUrl")?data.getString("photoUrl") :null;
            pronounce = data.containsKey("pronounce")?data.getString("pronounce") :null;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO Auto-generated method stub
        try {
            Bundle data = new Bundle(getClass().getClassLoader());
            if (id != null && id.length() > 0)
                data.putString("id", id);
            if (name != null && name.length() > 0)
                data.putString("name", name);
            if (contactPhoneString != null && contactPhoneString.length() > 0)
                data.putString("contactPhoneString", contactPhoneString);
            if (locationAddress != null && locationAddress.length() > 0)
                data.putString("locationAddress", locationAddress);
            if (cc != null && cc.length() > 0)
                data.putString("cc", cc);
            if (state != null && state.length() > 0)
                data.putString("state", state);
            if (country != null && country.length() > 0)
                data.putString("country", country);
            if (refferalID != null && refferalID.length() > 0)
                data.putString("refferalID", refferalID);
            if (webSiteUrl != null && webSiteUrl.length() > 0)
                data.putString("webSiteUrl", webSiteUrl);
            if (facebookID != null && facebookID.length() > 0)
                data.putString("facebookID", facebookID);
            if (twitterID != null && twitterID.length() > 0)
                data.putString("twitterID", twitterID);
            if (formattedAddress != null && formattedAddress.length > 0)
                data.putStringArray("formattedAddress", formattedAddress);
            if (contactPhoneNo != null && contactPhoneNo.length() > 0)
                data.putString("contactPhoneNo", contactPhoneNo);
            if (photoUrl != null && photoUrl.length() > 0)
                data.putString("photoUrl", photoUrl);
            if (pronounce != null && pronounce.length() > 0)
                data.putString("pronounce", pronounce);

            data.putDouble("lat", lat);
            data.putDouble("lon", lon);
            data.putInt("distance", distance);
            data.putInt("checkinCnt", checkinCnt);
            data.putInt("userCnt", userCnt);
            data.putInt("status", status);
            data.putInt("rating", rating);
            data.putInt("rating_count", rating_count);
            data.putInt("commment_count", commment_count);
            data.putInt("status_count", status_count);
            data.putInt("userRating", userRating);
            data.putInt("usersTodaysPostedStatus", usersTodaysPostedStatus);
            data.putLong("createdTimeInMilli", createdTimeInMilli);
            data.putInt("sortTarget", sortTarget);
            data.putBoolean("userFavorite", userFavorite);

            dest.writeParcelable(data, 0);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static final Creator CREATOR = new Creator() {
        public VenueObject createFromParcel(Parcel in) {
            return new VenueObject(in);
        }

        public VenueObject[] newArray(int size) {
            return new VenueObject[size];
        }
    };
}
