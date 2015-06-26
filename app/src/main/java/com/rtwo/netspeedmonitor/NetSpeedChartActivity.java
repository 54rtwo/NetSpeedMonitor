package com.rtwo.netspeedmonitor;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;

/**
 * Created by Administrator on 2015/6/16.
 */
public class NetSpeedChartActivity extends Activity{
    private TextView netSpeedlabel;
    private LinearLayout netSpeedView;
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.netspeed);
        netSpeedlabel = (TextView)findViewById(R.id.netspeedviewlabel);
        netSpeedView = (LinearLayout)findViewById(R.id.netspeedviewlayout);

        netSpeedlabel.setText(MainActivity.dataListSelected.subSequence(MainActivity.dataListSelected.indexOf("<"),MainActivity.dataListSelected.indexOf(">"))+">"
                +"网速监控图表(总流量"+String.valueOf(new DecimalFormat("0.00").format(MyOnClickListener.totalFlow/1024))+"MB)");
        view = new DrawView(this,MainActivity.drawDataContentDouble).drawView();
        netSpeedView.addView(view);
    }
}
