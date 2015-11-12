package com.makewithus.tattooonsenmap;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by KEN on 10/5/15.
 */
public class NetworkUtils {
        private static NetworkUtils mInstance;
        private RequestQueue mRequestQueue;
        private static Context mCtx;

        private NetworkUtils(Context context) {
            mCtx = context;
            mRequestQueue = getRequestQueue();

        }

        public static synchronized NetworkUtils getInstance(Context context) {
            if (mInstance == null) {
                mInstance = new NetworkUtils(context);
            }
            return mInstance;
        }

        public RequestQueue getRequestQueue() {
            if (mRequestQueue == null) {
                // getApplicationContext() is key, it keeps you from leaking the
                // Activity or BroadcastReceiver if someone passes one in.
                mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
            }
            return mRequestQueue;
        }

        public <T> void addToRequestQueue(Request<T> req) {
            getRequestQueue().add(req);
        }


}
