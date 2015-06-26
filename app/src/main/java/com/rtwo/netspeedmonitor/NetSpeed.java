package com.rtwo.netspeedmonitor;

import android.content.Context;
import android.net.TrafficStats;

public class NetSpeed {
    private Context mContext;
    private static NetSpeed mNetSpeed;

    private NetSpeed(Context mContext) {
        this.mContext = mContext;
    }

    public static NetSpeed getInstance(Context mContext) {
        if (mNetSpeed == null) {
            mNetSpeed = new NetSpeed(mContext);
        }
        return mNetSpeed;
    }

    public long getNetworkRxBytes(int appUid) {
        if (appUid < 0) {
            return 0;
        }
        long rxBytes = TrafficStats.getUidRxBytes(appUid);
        return rxBytes;
    }
}
