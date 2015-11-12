package com.makewithus.tattooonsenmap;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.support.v4.util.SimpleArrayMap;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

/**
 * Created by KEN on 10/8/15.
 */
public class TattooUtils {

    static void saveToDB(Context ctx, VenueObject venue) {
        new SaveToDBTask(ctx).execute(venue);

    }

    static class SaveToDBTask extends AsyncTask<VenueObject, Void, Void> {
        Context ctx;

        public SaveToDBTask(Context mCtx) {
            ctx = mCtx;
        }

        @Override
        protected Void doInBackground(VenueObject... params) {
            if (params == null || params.length != 1)
                return null;

            TattooSQLiteHelper dbHelper = TattooSQLiteHelper.getInstance(ctx);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(TattooSQLiteHelper.VenueMaster.COLUMN_VENUE_ID, params[0].id);
            values.put(TattooSQLiteHelper.VenueMaster.COLUMN_VENUE_NAME, params[0].name);
            values.put(TattooSQLiteHelper.VenueMaster.COLUMN_VENUE_LAT, params[0].lat);
            values.put(TattooSQLiteHelper.VenueMaster.COLUMN_VENUE_LON, params[0].lon);
            String formatAddressString = TattooUtils.getAddressFromArray(params[0].formattedAddress);
            values.put(TattooSQLiteHelper.VenueMaster.COLUMN_VENUE_ADDRESS, formatAddressString);
            values.put(TattooSQLiteHelper.VenueMaster.COLUMN_VENUE_FACEBOOKID, params[0].facebookID);
            values.put(TattooSQLiteHelper.VenueMaster.COLUMN_VENUE_TWITTER, params[0].twitterID);
            values.put(TattooSQLiteHelper.VenueMaster.COLUMN_VENUE_URL, params[0].webSiteUrl);
            values.put(TattooSQLiteHelper.VenueMaster.COLUMN_VENUE_PHONE, params[0].contactPhoneNo);
            values.put(TattooSQLiteHelper.VenueMaster.COLUMN_VENUE_PHONE_FROMATED, params[0].contactPhoneString);
            values.put(TattooSQLiteHelper.VenueMaster.COLUMN_VENUE_RATING, params[0].rating);
            values.put(TattooSQLiteHelper.VenueMaster.COLUMN_VENUE_STATUS, params[0].status);
            values.put(TattooSQLiteHelper.VenueMaster.COLUMN_VENUE_RATING_CNT, params[0].rating_count);
            values.put(TattooSQLiteHelper.VenueMaster.COLUMN_VENUE_STATUS_CNT, params[0].status_count);
            values.put(TattooSQLiteHelper.VenueMaster.COLUMN_VENUE_COMMENT_CNT, params[0].commment_count);
            values.put(TattooSQLiteHelper.VenueMaster.COLUMN_VENUE_CHECKINS, params[0].checkinCnt);
            values.put(TattooSQLiteHelper.VenueMaster.COLUMN_USER_FAVORITE, (params[0].userFavorite) ? TattooConstants.STATUS_OK : TattooConstants.STATUS_NG);
            values.put(TattooSQLiteHelper.VenueMaster.COLUMN_VENUE_CHECKINS, params[0].checkinCnt);
            values.put(TattooSQLiteHelper.VenueMaster.COLUMN_PHOTO_URL, params[0].getPhotoUrl());

            Date now = new Date();
            values.put(TattooSQLiteHelper.VenueMaster.COLUMN_CREATED_TIME, now.getTime());

            // Insert the new row, returning the primary key value of the new row
            long newRowId;
            newRowId = db.insertWithOnConflict(
                    TattooSQLiteHelper.VenueMaster.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);


            return null;
        }
    }

    static String getAddressFromArray(String[] formattedAddress) {
        //SET ADDRESS BUTTON
        StringBuilder addresBuilder = new StringBuilder();
        try {
            if (formattedAddress != null && formattedAddress.length > 0) {
                for (int k = 0; k < formattedAddress.length; k++) {
                    addresBuilder.append(formattedAddress[k]);
                    if (k < formattedAddress.length - 1)
                        addresBuilder.append(", ");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return addresBuilder.toString();
    }

    static VenueObject parseVenue(JSONObject statusJSON, VenueObject venue) {
        try {

            String status = statusJSON.has("status") ? statusJSON.getString("status") : null;
            if (status != null && !status.equalsIgnoreCase("null")&&status.length()>0)
                venue.status = Integer.valueOf(status).intValue();

            //venue.status = statusJSON.has("status")?statusJSON.getInt("status"):-1;
            //venue.rating = statusJSON.has("rating")?statusJSON.getInt("rating"):-1;
            String rating = statusJSON.has("rating") ? statusJSON.getString("rating") : "-1";
            if (rating != null && !rating.equalsIgnoreCase("null")&&rating.length()>0) {
                venue.rating = (int) Double.valueOf(rating).doubleValue();
            }else{
                rating = "-1";
            }

            //venue.status_count = statusJSON.has("status_count")?statusJSON.getInt("status_count"):-1;
            String status_count = statusJSON.has("status_count") ? statusJSON.getString("status_count") : null;
            if (status_count != null && !status_count.equalsIgnoreCase("null")&&status_count.length()>0)
                venue.status_count = Integer.valueOf(status_count).intValue();

            // venue.rating_count = statusJSON.has("rating_count")?statusJSON.getInt("rating_count"):-1;
            String rating_count = statusJSON.has("rating_count") ? statusJSON.getString("rating_count") : null;
            if (rating_count != null && !rating_count.equalsIgnoreCase("null")&&rating_count.length()>0)
                venue.rating_count = Integer.valueOf(rating_count).intValue();

            //venue.commment_count = statusJSON.has("commment_count")?statusJSON.getInt("commment_count"):-1;
            String commment_count = statusJSON.has("commment_count") ? statusJSON.getString("commment_count") : null;
            if (commment_count != null && !commment_count.equalsIgnoreCase("null")&&commment_count.length()>0)
                venue.commment_count = Integer.valueOf(commment_count).intValue();

            //venue.userRating = statusJSON.has("user_rating")?statusJSON.getInt("user_rating"):-1;
            String user_rating = statusJSON.has("user_rating") ? statusJSON.getString("user_rating") : null;
            if (user_rating != null && !user_rating.equalsIgnoreCase("null")&&user_rating.length()>0)
                venue.userRating = Integer.valueOf(user_rating).intValue();

            //venue.usersTodaysPostedStatus = statusJSON.has("todays_user_post")?statusJSON.getInt("todays_user_post"):-1;
            String todays_user_post = statusJSON.has("todays_user_post") ? statusJSON.getString("todays_user_post") : null;
            if (todays_user_post != null && !todays_user_post.equalsIgnoreCase("null")&&todays_user_post.length()>0)
                venue.usersTodaysPostedStatus = Integer.valueOf(todays_user_post).intValue();

            String name = statusJSON.has("name") ? statusJSON.getString("name") : null;
            if (name != null && !name.equalsIgnoreCase("null"))
                venue.name = URLDecoder.decode(name, "UTF-8");

            String address = statusJSON.has("address") ? statusJSON.getString("address") : null;
            if (address != null && !address.equalsIgnoreCase("null"))
                venue.formattedAddress = new String[]{URLDecoder.decode(address, "UTF-8")};

            String lat = statusJSON.has("lat") ? statusJSON.getString("lat") : null;
            if (lat != null && !lat.equalsIgnoreCase("null")&& lat.length()>0)
                venue.lat = Double.valueOf(lat).doubleValue();

            String lon = statusJSON.has("lon") ? statusJSON.getString("lon") : null;
            if (lon != null && !lon.equalsIgnoreCase("null")&&lon.length()>0)
                venue.lon = Double.valueOf(lon).doubleValue();

            String tel = statusJSON.has("tel") ? statusJSON.getString("tel") : null;
            if (tel != null && !tel.equalsIgnoreCase("null")) {
                tel = URLDecoder.decode(tel, "UTF-8");
                venue.contactPhoneNo = tel;
                venue.contactPhoneString = tel;
            }


            String web_url = statusJSON.has("web_url") ? statusJSON.getString("web_url") : null;
            if (web_url != null && !web_url.equalsIgnoreCase("null"))
                venue.webSiteUrl = URLDecoder.decode(web_url, "UTF-8");

            String facebook = statusJSON.has("facebook") ? statusJSON.getString("facebook") : null;
            if (facebook != null && !facebook.equalsIgnoreCase("null"))
                venue.facebookID = URLDecoder.decode(facebook, "UTF-8");

            String twitter = statusJSON.has("twitter") ? statusJSON.getString("twitter") : null;
            if (twitter != null && !twitter.equalsIgnoreCase("null"))
                venue.twitterID = URLDecoder.decode(twitter, "UTF-8");

            String pronounce = statusJSON.has("pronounce") ? statusJSON.getString("pronounce") : null;
            if (pronounce != null && !pronounce.equalsIgnoreCase("null"))
                venue.pronounce = URLDecoder.decode(pronounce, "UTF-8");

            String photo_url = statusJSON.has("photo_url") ? statusJSON.getString("photo_url") : null;
            if (photo_url != null && !photo_url.equalsIgnoreCase("null"))
                venue.photoUrl = URLDecoder.decode(photo_url, "UTF-8");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return venue;
    }

    static void showPostErrorToast(Context ctx) {
        Toast.makeText(ctx, R.string.post_failed, Toast.LENGTH_SHORT).show();
    }

    static UserDetailObject parseUserDetailResponse(final String response) {

        try {
            // make an jsonObject in order to parse the response
            JSONObject resJDON = new JSONObject(response);
            if (resJDON != null) {
                UserDetailObject detail = new UserDetailObject();
                detail.success = ((resJDON.has("success") ? resJDON.getInt("success") : 0) == TattooConstants.STATUS_OK);
                detail.userID = resJDON.has("user_id") ? resJDON.getString("user_id") : "";
                JSONArray suggestJSON = resJDON.has("suggestions") ? resJDON.getJSONArray("suggestions") : null;
                if (suggestJSON != null && suggestJSON.length() > 0) {
                    detail.suggestions = new String[suggestJSON.length()];
                    for (int i = 0; i < suggestJSON.length(); i++) {
                        detail.suggestions[i] = URLDecoder.decode(suggestJSON.getString(i), "UTF-8");
                    }
                }

                return detail;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    static void setRatingToImgView(int rating, ImageView oneImg, ImageView twoImg, ImageView threeImg, ImageView fourImg, ImageView fiveImg) {
        boolean[] rates = {false, false, false, false, false};
        if (rating >= 1)
            rates[0] = true;
        if (rating >= 2)
            rates[1] = true;
        if (rating >= 3)
            rates[2] = true;
        if (rating >= 4)
            rates[3] = true;
        if (rating >= 5)
            rates[4] = true;

        oneImg.setSelected(rates[0]);
        twoImg.setSelected(rates[1]);
        threeImg.setSelected(rates[2]);
        fourImg.setSelected(rates[3]);
        fiveImg.setSelected(rates[4]);
    }

    static int getStatusIconResNo(int status, boolean favoriteIcon) {
        int resID = 0;
        if (favoriteIcon) {
            switch (status) {
                case TattooConstants.STATUS_OK:
                    resID = R.drawable.tattoo_ok_favorite;
                    break;
                case TattooConstants.STATUS_NG:
                    resID = R.drawable.tattoo_ng_favorite;
                    break;
                case TattooConstants.STATUS_NODATA:
                    resID = R.drawable.tattoo_nodata_favorite;
                    break;
                default:
                    resID = R.drawable.tattoo_nodata_favorite;
            }
        } else {
            switch (status) {
                case TattooConstants.STATUS_OK:
                    resID = R.drawable.tattoo_ok_icon;
                    break;
                case TattooConstants.STATUS_NG:
                    resID = R.drawable.tattoo_ng;
                    break;
                case TattooConstants.STATUS_NODATA:
                    resID = R.drawable.tattoo_nodata;
                    break;
                default:
                    resID = R.drawable.tattoo_nodata;
            }
        }

        return resID;
    }

    /*
 * Calculate distance between two points in latitude and longitude taking
 * into account height difference. If you are not interested in height
 * difference pass 0.0. Uses Haversine method as its base.
 *
 * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
 * el2 End altitude in meters
 * @returns Distance in Meters
 */
    public static double distance(double lat1, double lat2, double lon1,
                                  double lon2, double el1, double el2) {

        final int R = 6371; // Radius of the earth

        Double latDistance = Math.toRadians(lat2 - lat1);
        Double lonDistance = Math.toRadians(lon2 - lon1);
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }


    static void deleteItemFromDB(Context ctx, String venueID) {
        new DeleteItemFromDBTask(ctx).execute(venueID);

    }

    static class DeleteItemFromDBTask extends AsyncTask<String, Void, Void> {
        Context ctx;

        public DeleteItemFromDBTask(Context mCtx) {
            ctx = mCtx;
        }

        @Override
        protected Void doInBackground(String... params) {
            if (params == null || params.length != 1)
                return null;

            TattooSQLiteHelper dbHelper = TattooSQLiteHelper.getInstance(ctx);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            db.delete(
                    TattooSQLiteHelper.VenueMaster.TABLE_NAME,
                    TattooSQLiteHelper.VenueMaster.COLUMN_VENUE_ID + "=?",
                    new String[]{params[0]});

            return null;
        }
    }


    static void updateFavoriteInDB(Context ctx, SimpleArrayMap<String, Boolean> favoriteMap) {
        new UpdateFavoriteItemInDBTask(ctx).execute(favoriteMap);
    }


    static class UpdateFavoriteItemInDBTask extends AsyncTask<SimpleArrayMap<String, Boolean>, Void, Void> {
        Context ctx;

        public UpdateFavoriteItemInDBTask(Context mCtx) {
            ctx = mCtx;
        }

        @Override
        protected Void doInBackground(SimpleArrayMap<String, Boolean>... params) {
            if (params == null || params.length != 1)
                return null;

            TattooSQLiteHelper dbHelper = TattooSQLiteHelper.getInstance(ctx);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            for (int i = 0; i < params[0].size(); i++) {
                String vID = params[0].keyAt(i);
                Boolean newValue = params[0].valueAt(i);
                ContentValues values = new ContentValues();
                values.put(TattooSQLiteHelper.VenueMaster.COLUMN_USER_FAVORITE, (newValue == Boolean.TRUE) ? TattooConstants.STATUS_OK : TattooConstants.STATUS_NG);
                db.update(
                        TattooSQLiteHelper.VenueMaster.TABLE_NAME, values,
                        TattooSQLiteHelper.VenueMaster.COLUMN_VENUE_ID + "=?",
                        new String[]{vID});
            }

            return null;
        }
    }

    static void setVenueParams(HashMap<String, String> postParams, VenueObject venue) {
        try {
            postParams.put("shop_id", URLEncoder.encode(venue.id, "UTF-8"));
            postParams.put("shop_name", URLEncoder.encode(venue.getName(), "UTF-8"));
            postParams.put("shop_address", URLEncoder.encode(TattooUtils.getAddressFromArray(venue.formattedAddress), "UTF-8"));
            postParams.put("shop_lat", URLEncoder.encode(String.valueOf(String.valueOf(venue.lat)), "UTF-8"));
            postParams.put("shop_lon", URLEncoder.encode(String.valueOf(venue.lon), "UTF-8"));
            postParams.put("shop_tel", URLEncoder.encode(venue.getContactPhoneNo(), "UTF-8"));
            postParams.put("shop_web", URLEncoder.encode(venue.getWebSiteUrl(), "UTF-8"));
            postParams.put("shop_fb", URLEncoder.encode(venue.getFacebookID(), "UTF-8"));
            postParams.put("shop_twitter", URLEncoder.encode(venue.getTwitterID(), "UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    static void openConfirmDialog(final Context ctx, View v, final String url, final String url2) {
        // 確認ダイアログの生成
        //if(act==null)
        //    act = (OnsenMapsActivity)SummaryDialog.this.getActivity();
        AlertDialog.Builder alertDlg = new AlertDialog.Builder(ctx);
        String DialogTitle = ctx.getString(R.string.app_name);
        alertDlg.setTitle(DialogTitle);
        String DialogMessage = ctx.getString(R.string.dialog_web);
        alertDlg.setMessage(DialogMessage);
        String DialogButtonOk = ctx.getString(R.string.dialog_ok);
        String DialogButtonNG = ctx.getString(R.string.dialog_no);
        alertDlg.setPositiveButton(DialogButtonOk, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                Intent chooser = Intent.createChooser(intent, "");
                // Verify the original intent will resolve to at least one activity
                if (chooser.resolveActivity(ctx.getPackageManager()) != null) {
                    ctx.startActivity(chooser);
                } else if (url2 != null && url2.length() > 0) {
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url2));
                    chooser = Intent.createChooser(intent, "");
                    ctx.startActivity(chooser);
                }

            }
        });

        alertDlg.setNegativeButton(DialogButtonNG, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        // 表示
        alertDlg.create().show();
    }



    public static class ImageDownloader extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public ImageDownloader(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String url = urls[0];
            Bitmap mIcon = null;
            try {
                InputStream in = new java.net.URL(url).openStream();
                mIcon = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
            }
            return mIcon;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    public static ProgressDialog showLoadingDialog(Context ctx) {
        try {
            ProgressDialog dialog = new ProgressDialog(ctx);
            String loadingMessage = ctx.getString(R.string.loading);
            dialog.setMessage(loadingMessage);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

            return dialog;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String formatDateTime(Context context, String timeToFormat) {

        String finalDateTime = "";

        SimpleDateFormat iso8601Format = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");

        Date date = null;
        if (timeToFormat != null) {
            try {
                date = iso8601Format.parse(timeToFormat);
            } catch (Exception e) {
                date = null;
            }

            if (date != null) {
                long when = date.getTime();
                int flags = 0;
                flags |= android.text.format.DateUtils.FORMAT_SHOW_TIME;
                flags |= android.text.format.DateUtils.FORMAT_SHOW_DATE;
                flags |= android.text.format.DateUtils.FORMAT_ABBREV_MONTH;
                flags |= android.text.format.DateUtils.FORMAT_SHOW_YEAR;

                finalDateTime = android.text.format.DateUtils.formatDateTime(context,
                        when + TimeZone.getDefault().getOffset(when), flags);
            }
        }
        return finalDateTime;
    }

    static String[] getUserInfo(Context ctx) {
        SharedPreferences settings = ctx.getSharedPreferences(TattooConstants.SHARED_PREF, 0);
        String uID = settings.getString(TattooConstants.user_id, "");
        String nickname = settings.getString(TattooConstants.PREF_NICKNAME, "");
        if(nickname.length()==0) {
            uID = null;
            nickname = null;
        }

        return new String[]{uID, nickname};
    }



}
