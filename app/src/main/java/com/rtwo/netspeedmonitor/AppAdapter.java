package com.rtwo.netspeedmonitor;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;


import java.util.List;

/**
 * Created by ThinkPad on 2015/6/12.
 */
public class AppAdapter extends ArrayAdapter<AppInfo> {
    private int mResource;

    public AppAdapter(Context context, int resource, List<AppInfo> objects) {
        super(context, resource, objects);
        mResource = resource;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        AppInfo appInfo = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(mResource, null);
        TextView appLabel = (TextView) view.findViewById(R.id.applabel);
        TextView packageNanme = (TextView) view.findViewById(R.id.apppackagename);
        ImageView appIcon = (ImageView) view.findViewById(R.id.appicon);
        RadioButton appSelected = (RadioButton)view.findViewById(R.id.appselect);
        appLabel.setText(appInfo.getAppLabel().toString());
        appLabel.setTextColor(Color.RED);
        packageNanme.setText(appInfo.getPackageName().toString());
        packageNanme.setTextColor(Color.BLUE);
        if(position == MyOnItemClickListener.mPosition){
            appSelected.setChecked(true);
            MyOnItemClickListener.onlyRadioButtonSelected = appSelected;
        }
        appIcon.setImageDrawable(appInfo.getAppIcon());
        return view;
    }
}

class AppInfo {
    private Drawable appIcon;
    private String appLabel;
    private String packageName;
    public Drawable getAppIcon() {
        return appIcon;
    }
    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }
    public String getAppLabel() {
        return appLabel;
    }
    public void setAppLabel(String appLabel) {
        this.appLabel = appLabel;
    }
    public String getPackageName() {
        return packageName;
    }
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}