package com.makewithus.tattooonsenmap;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by KEN on 10/4/15.
 */
public class CommentObject implements Parcelable{
    String commentId;
    String userID;
    String nickname;
    String date;
    String comment;
    String pofileImgURL;

    String facebookID;
    String twitterID;

    public CommentObject() {

    }

    public CommentObject(Parcel in) {
        // TODO Auto-generated constructor stub
        try {
            Bundle data = in.readParcelable(getClass().getClassLoader());

            commentId = data.containsKey("commentId")?data.getString("commentId"):null;
            userID = data.containsKey("userID")?data.getString("userID"):null;
            nickname = data.containsKey("nickname")?data.getString("nickname"):null;
            date = data.containsKey("date")?data.getString("date"):null;
            comment = data.containsKey("comment")?data.getString("comment"):null;
            pofileImgURL = data.containsKey("pofileImgURL")?data.getString("pofileImgURL"):null;
            facebookID = data.containsKey("facebookID")?data.getString("facebookID"):null;
            twitterID = data.containsKey("twitterID")?data.getString("twitterID"):null;

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
            if (commentId != null && commentId.length() > 0)
                data.putString("commentId", commentId);
            if (userID != null && userID.length() > 0)
                data.putString("userID", userID);
            if (nickname != null && nickname.length() > 0)
                data.putString("nickname", nickname);
            if (date != null && date.length() > 0)
                data.putString("date", date);
            if (comment != null && comment.length() > 0)
                data.putString("comment", comment);
            if (pofileImgURL != null && pofileImgURL.length() > 0)
                data.putString("pofileImgURL", pofileImgURL);
            if (facebookID != null && facebookID.length() > 0)
                data.putString("facebookID", facebookID);
            if (twitterID != null && twitterID.length() > 0)
                data.putString("twitterID", twitterID);

            dest.writeParcelable(data, 0);
        } catch (Exception e) {
            // TODO Auto-generated catch block
                e.printStackTrace();
        }
    }

    public static final Creator CREATOR = new Creator() {
        public CommentObject createFromParcel(Parcel in) {
            return new CommentObject(in);
        }

        public CommentObject[] newArray(int size) {
            return new CommentObject[size];
        }
    };
}
