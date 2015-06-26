package com.rtwo.netspeedmonitor;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

/**
 * Created by Administrator on 2015/6/16.
 */
public class DrawView {
    String dataTag = "NetSpeed";
    String xTitle = "Time(S)";
    String yTitle = "NetSpeed(K/S)";
    double[] mDataSet = null;
    Context mContext = null;

    public DrawView(Context context, double[] dataSet) {
        mContext = context;
        mDataSet = dataSet;
    }

    public View drawView() {
        View view = null;
        XYMultipleSeriesDataset seriesDataset = new XYMultipleSeriesDataset();
        XYSeries dataNetSpeed = new XYSeries(dataTag);
        for (int mDatasetIndex = 0; mDatasetIndex < mDataSet.length; mDatasetIndex++) {
            dataNetSpeed.add(mDatasetIndex, mDataSet[mDatasetIndex]);
        }
        seriesDataset.addSeries(dataNetSpeed);

        XYMultipleSeriesRenderer seriesRenderer = new XYMultipleSeriesRenderer();
        XYSeriesRenderer rendererNetSpeed = new XYSeriesRenderer();
        rendererNetSpeed.setColor(Color.BLUE);
        rendererNetSpeed.setDisplayChartValues(true);
        rendererNetSpeed.setChartValuesTextSize(30);

        seriesRenderer.setXTitle(xTitle);
        seriesRenderer.setYTitle(yTitle);
        seriesRenderer.setAxisTitleTextSize(40);
        seriesRenderer.setLabelsTextSize(45);
        seriesRenderer.setLabelsColor(Color.RED);
        seriesRenderer.setXLabelsColor(Color.GREEN);
        seriesRenderer.setYLabelsColor(0, Color.GREEN);//第一个参数必须是0
        seriesRenderer.setBackgroundColor(Color.WHITE);
        seriesRenderer.setMarginsColor(Color.WHITE);
        seriesRenderer.setXAxisMin(0);
        seriesRenderer.setYAxisMin(0);
        seriesRenderer.setShowGrid(true);
        seriesRenderer.setZoomButtonsVisible(true);
        seriesRenderer.setMargins(new int[]{10,60,0,0});
        seriesRenderer.addSeriesRenderer(rendererNetSpeed);
        Log.v("tag",seriesRenderer+"..."+seriesRenderer.getYAxisMax());
        switch (MainActivity.chartType) {
            case MainActivity.LINECHART:
                view = ChartFactory.getLineChartView(mContext, seriesDataset, seriesRenderer);
                break;
            case MainActivity.BARCHART:
                view = ChartFactory.getBarChartView(mContext, seriesDataset, seriesRenderer, BarChart.Type.STACKED);
                break;
            case MainActivity.SCATTERCHART:
                view = ChartFactory.getScatterChartView(mContext, seriesDataset, seriesRenderer);
                break;
            case MainActivity.CUBELINECHART:
                view = ChartFactory.getCubeLineChartView(mContext, seriesDataset, seriesRenderer, 2.0f);
                break;
            default:
                break;
        }
        return view;
    }
}