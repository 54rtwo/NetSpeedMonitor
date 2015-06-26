package com.rtwo.netspeedmonitor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;


public class MainActivity extends Activity {
    private ListView appList;
    private AppAdapter appAdapter;
    private Button start,stop,drawview;
    public static Spinner dataList;
    public static ArrayAdapter<String> dataAdapter;
    public static String dataListSelected = null;
    public static double[] drawDataContentDouble = null;
    public static ArrayList<String> dataFile = null;
    public static final int LINECHART = 1;
    public static final int BARCHART = 2;
    public static final int SCATTERCHART = 3;
    public static final int CUBELINECHART = 4;
    public static int chartType = LINECHART;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start = (Button)findViewById(R.id.start);
        stop = (Button)findViewById(R.id.stop);
        drawview = (Button)findViewById(R.id.drawview);
        appList = (ListView) findViewById(R.id.applist);
        dataList = (Spinner)findViewById(R.id.datalist);
        dataFile = getDataList();
        dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,dataFile);
        dataList.setAdapter(dataAdapter);
        dataListSelected = dataList.getSelectedItem().toString();
        dataList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                dataListSelected = parent.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        try {
            appAdapter = new AppAdapter(this, R.layout.applist, new Packages(this).getPackageName());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        appList.setAdapter(appAdapter);
        appList.setOnItemClickListener(new MyOnItemClickListener(this));
        start.setOnClickListener(new MyOnClickListener(this));
        stop.setOnClickListener(new MyOnClickListener(this));
        drawview.setOnClickListener(new MyOnClickListener(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.settings,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.delete_data:
                AlertDialog.Builder delete = new AlertDialog.Builder(MainActivity.this);
                delete.setTitle("删除网速监控数据");
                delete.setMessage("是否删除全部的数据?");
                delete.setCancelable(false);
                delete.setNegativeButton("毫不犹豫", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dataAdapter.clear();
                        dataAdapter.notifyDataSetChanged();
                        dataListSelected = null;
                        File[] files = NetSpeedService.speedData.listFiles();
                        for (File file : files) {
                            file.delete();
                        }
                        NetSpeedService.speedData.mkdir();
                        dialog.dismiss();
                    }
                });
                delete.setPositiveButton("手贱,请无视", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                delete.show();
                break;
            case R.id.linechart:
                chartType = LINECHART;
                Toast.makeText(MainActivity.this,"你选择了线型图表模式",Toast.LENGTH_SHORT).show();
                break;
            case R.id.cubechart:
                chartType = CUBELINECHART;
                Toast.makeText(MainActivity.this,"你选择了锯齿图表模式",Toast.LENGTH_SHORT).show();
                break;
            case R.id.barchart:
                chartType = BARCHART;
                Toast.makeText(MainActivity.this,"你选择了条型图表模式",Toast.LENGTH_SHORT).show();
                break;
            case R.id.scatterchart:
                chartType = SCATTERCHART;
                Toast.makeText(MainActivity.this,"你选择了散射图表模式",Toast.LENGTH_SHORT).show();
                break;
            case R.id.help:
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle("工具使用说明");
                dialog.setMessage("1. 选择需要进行监控的应用,然后点击开始,就会打开该工具;\n" +
                        "2. 在应用中进行相关操作,产生流量或者完成期望的操作;\n" +
                        "3. 点击返回切回监控工具,或者在下拉通知中点击工具的后台监控服务切回监控工具;\n" +
                        "4. 点击停止,完成网速的监控,同时自动生成监控文件且默认选中;\n" +
                        "5. 点击绘图,就会完成网速的图谱和流量的统计;\n" +
                        "6. 在点击开始后可以随时在下拉通知中查看实时的流量监控数据;\n" +
                        "7. 可以选择过往的统计数据进行绘图;\n" +
                        "8. 可以选择图谱的样式和删除已有的统计数据;\n");
                dialog.show();
                break;
            default:
                break;
        }
        return true;
    }

    ArrayList<String> getDataList(){
        ArrayList<String> arrayList = new ArrayList<String>();
        if(!NetSpeedService.speedData.exists()){
            NetSpeedService.speedData.mkdir();
        }
        String[] allFiles = NetSpeedService.speedData.list(new FileFilter());
        if(allFiles.length==0){
            arrayList.add("");//为了防止没有数据文件的时候,无数据加载,app启动报错
        }
        for(int index=0;index<allFiles.length;index++){
            arrayList.add(allFiles[index]);
        }
        return arrayList;
    }
}

class FileFilter implements FilenameFilter{
    @Override
    public boolean accept(File dir, String filename){
        return filename.endsWith(".txt");
    }
}

class MyOnClickListener implements View.OnClickListener{
    private Context mContext;
    public static double totalFlow;
    public MyOnClickListener(Context context){
        mContext = context;
    }

    @Override
    public void onClick(View view){

        switch (view.getId()){
            case R.id.start:
                PackageManager pm = mContext.getPackageManager();
                if(MyOnItemClickListener.packageNameSelected!=null){
                    Intent appIntent = pm.getLaunchIntentForPackage(MyOnItemClickListener.packageNameSelected);
                    mContext.startActivity(appIntent);
                    Intent appService = new Intent(mContext,NetSpeedService.class);
                    mContext.startService(appService);
                }
                else{
                    Toast.makeText(mContext,"请选择一个应用",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.stop:
                if(NetSpeedService.onServering==true){
                    Intent mAppService = new Intent(mContext,NetSpeedService.class);
                    mContext.stopService(mAppService);
                    NetSpeedService.onServering = false;
                    if(MyOnItemClickListener.appLabel!=null){
                        Toast.makeText(mContext,"你停止了<"+MyOnItemClickListener.appLabel+">"+"的网速监控",Toast.LENGTH_SHORT).show();
                        MainActivity.dataFile.add(NetSpeedService.dataLogPath);
                        MainActivity.dataAdapter.notifyDataSetChanged();
                        MainActivity.dataList.setSelection(MainActivity.dataList.getBottom());
                    }
                    else{
                        Toast.makeText(mContext,"没有应用启动网速监控",Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(mContext, "没有应用启动网速监控", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.drawview:
                try {
                    if(MainActivity.dataListSelected==""||MainActivity.dataListSelected==null){
                        Toast.makeText(mContext,"没有可以加载的数据文件",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        totalFlow = 0;
                        FileReader dataFile = new FileReader(NetSpeedService.speedData.getAbsolutePath()+"/"+MainActivity.dataListSelected);
                        char[] dataContent = new char[32];
                        int dataContentLen = 0;
                        StringBuilder dataBuilder = new StringBuilder("");
                        while((dataContentLen=dataFile.read(dataContent))>0){
                            dataBuilder.append(dataContent);
                        }
                        String[] drawDataContentString = dataBuilder.toString().split(",");
                        MainActivity.drawDataContentDouble = new double[drawDataContentString.length];
                        for(int index=0;index<drawDataContentString.length;index++){
                            MainActivity.drawDataContentDouble[index] = Double.valueOf(drawDataContentString[index]);
                            totalFlow = totalFlow + MainActivity.drawDataContentDouble[index];
                        }
                        dataFile.close();
                        Intent intent = new Intent(mContext,NetSpeedChartActivity.class);
                        mContext.startActivity(intent);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }
}

class MyOnItemClickListener implements AdapterView.OnItemClickListener {
    public static RadioButton onlyRadioButtonSelected = null;
    public static int mPosition = -1;
    public static String packageNameSelected = null;
    public static String appLabel = null;
    public static int appUid = -1;
    private PackageManager pm = null;
    private Context mContext;

    public MyOnItemClickListener(Context context){
        mContext = context;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.applist:
                if (onlyRadioButtonSelected != null) {
                    onlyRadioButtonSelected.setChecked(false);
                }
                appLabel  = ((TextView) view.findViewById(R.id.applabel)).getText().toString();
                RadioButton radioButtonSelected = (RadioButton) view.findViewById(R.id.appselect);
                radioButtonSelected.setChecked(true);
                onlyRadioButtonSelected = radioButtonSelected;
                mPosition = position;
                packageNameSelected = ((TextView)view.findViewById(R.id.apppackagename)).getText().toString();
                pm = mContext.getPackageManager();
                try {
                    appUid = pm.getApplicationInfo(packageNameSelected,PackageManager.GET_ACTIVITIES).uid;
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }
}
