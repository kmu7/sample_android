package com.makewithus.tattooonsenmap;

import android.content.DialogInterface;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by KEN on 10/10/15.
 */
public interface VenueInfoInterface {

    void showNicknameDialog(String apiTAG, String venueID);
    //void showNicknameDialog();
    void sendPost(String urlString, final Map postParams, final String postTag, String venueID);

    String getUserID();
    String getPostURLString();
    void setUserID(String _userID);
    void setNickName(String _nickname);
    void setPostURLString(String _url);
    HashMap<String, String> getPostParams();
    void setPostParams(HashMap<String, String> _param);
    void infoViewHideButtonClick(View v);
    void nicknameDialogDismiss(DialogInterface dialog);
    void successDeviceLogin(boolean success);

}
