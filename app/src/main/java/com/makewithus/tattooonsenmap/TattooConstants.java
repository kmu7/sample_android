package com.makewithus.tattooonsenmap;

/**
 * Created by KEN on 10/4/15.
 */
public class TattooConstants {
    static final String forsquareCID = "QNJV4URVJW2IDSRXXM5F4SXOOR2P3YGEROQEDTSF12LZWO3O";
    static final String forsquareSECRET = "XNEKFJLYTSCDXP1FMPHIWVYJPIKF2Y1IRE442OLQHQ4ZWWI2";

    static final String BASEURL= "http://makewithus.com/tattoomap/";
    static final String API_GET_SHOP_STATUS = "get_shop_status.php";
    static final String API_GET_SHOP_COMMENT = "get_shop_comment.php";
    static final String API_GET_LATEST_SHOP_COMMENT = "get_latest_shop_comment.php";
    static final String API_GET_SHOP_STATUS_DETAIL = "get_shop_status_detail.php";
    static final String API_GET_TOP_SUPPORTER = "get_top_supporter.php";
    static final String API_GET_SHOP_STATUS_BY_LATLON = "get_shop_status_by_bounds.php";
    static final String API_POST_SHOP_COMMENT = "post_shop_comment.php";
    static final String API_POST_SHOP_RATING = "post_shop_rating.php";
    static final String API_POST_SHOP_STATUS = "post_shop_status.php";
    static final String API_POST_USER_INFO = "post_user_info.php";

    static final int STATUS_OK = 1;
    static final int STATUS_NG = 0;
    static final int STATUS_NODATA = -1;

    static final String SHARED_PREF = "tatoo_map_app";
    static final String user_id = "user_id";
    static final String PREF_FACEBOOK_ID = "fb_id";
    static final String PREF_TWITTER_ID = "tw_id";
    static final String PREF_NICKNAME = "nickname";
    static final int DEVICE_TYPE = 0;

    static final int RECOMMEND_MAX_CNT = 3;
    static final int RECOMMEND_RATING_WEIGHT = 10;
    static final int RECOMMEND_RATING_CNT_WEIGHT = 8;
    static final int RECOMMEND_RATING_COMNT_CNT_WEIGHT = 3;
    static final int RECOMMEND_RATING_STATUS_CNT_WEIGHT = 2;
    static final int RECOMMEND_RATING_FORSQUARE_CHECKIN_WEIGHT = 1;

     static final String SUMMARY_DIALOG_TAG = "SUMMARY_DIALOG_TAG";
    static final String COMMENT_EDIT_DIALOG_TAG = "COMMENT_EDIT_DIALOG_TAG";
    static final String SEARCH_DIALOG_TAG = "SEARCH_DIALOG_TAG";
     static final String NICKNAME_DIALOG_TAG = "NICKNAME_DIALOG_TAG";
     static final String API_FORSQUARE_EXPLORE = "API_FORSQUARE_EXPLORE";
     static final String API_TATTOOMAP_GET_PLACEINFO = "API_TATTOOMAP_GET_PLACEINFO";
     static final String API_TATTOOMAP_POST_USERINFO = "API_TATTOOMAP_POST_USERINFO";
     static final String API_TATTOOMAP_POST_RATING = "API_TATTOOMAP_POST_RATING";
     static final String API_TATTOOMAP_POST_STATUS = "API_TATTOOMAP_POST_STATUS";

    static final String INTENT_VENUE_ID = "INTENT_VENUE_ID";
    static final String INTENT_VENUE_OBJECT = "INTENT_VENUE_OBJECT";
    static final String INTENT_STATUS_ICON_RES = "INTENT_STATUS_ICON_RES";
    static final String INTENT_USER_ID = "INTENT_USER_ID";
    static final String INTENT_POSTING = "INTENT_POSTING";
    static final String INTENT_DO_SEARCH = "INTENT_DO_SEARCH";

    static final int SORT_BY_NAME = 0;
    static final int SORT_BY_NEARBY = 1;
    static final int SORT_BY_RATING = 2;
    static final int SORT_BY_DATE = 3;

    static final String OFFICIAL_WEB = "http://makewithus.com/";
    static final String GOOGLE_PLAY_URL = "https://play.google.com/store/apps/details?id=com.makewithus.reallocker2";
    static final String APP_STORE_URL = "https://itunes.apple.com/app/ice-world-princess/id965167597?mt=8";
    static final String DEVELOPER_WEB = "http://makewithus.com/";
    static final String EMAIL_ADDRESS = "service_info@makewithus.com";

    static final int MAX_MAP_ITEM_SIZE = 5000;
}
