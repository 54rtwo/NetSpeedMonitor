package com.rtwo.netspeedmonitor;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.text.format.Formatter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2015/6/15.
 */
public class NetSpeedService extends Service {
    boolean quitService = false;
    long netSpeedData = 0;
    long oNetSpeedData = 0;
    String trueSpeedStr = null;
    String trueSpeed = null;
    String fileName = null;
    FileOutputStream dataLog = null;
    OutputStreamWriter dataLogWriter = null;
    public static File speedData = new File(Environment.getExternalStorageDirectory()+"/speeddata");
    public static String dataLogPath = null;
    private File dataLogFile = null;
    public static boolean onServering = false;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        onServering = true;
        super.onCreate();
        SimpleDateFormat time=new SimpleDateFormat("MM-dd HH:mm:ss");
        fileName = time.format(new Date());
        try {
            dataLogFile = new File(Environment.getExternalStorageDirectory()+"/speeddata/"+"<"+MyOnItemClickListener.appLabel+">"+fileName+".txt");
            dataLog = new FileOutputStream(dataLogFile);
            dataLogPath = dataLogFile.getName();
            dataLogWriter = new OutputStreamWriter(dataLog);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(NetSpeedService.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        final PendingIntent oIntent = PendingIntent.getActivity(NetSpeedService.this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        final Notification appNotice = new Notification(R.drawable.car, "后台监控" + MyOnItemClickListener.appLabel + "的网速", System.currentTimeMillis());
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!quitService) {
                    netSpeedData = NetSpeed.getInstance(NetSpeedService.this).getNetworkRxBytes(MyOnItemClickListener.appUid);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    oNetSpeedData = NetSpeed.getInstance(NetSpeedService.this).getNetworkRxBytes(MyOnItemClickListener.appUid);
                    trueSpeed = Formatter.formatFileSize(NetSpeedService.this, oNetSpeedData - netSpeedData);
                    appNotice.setLatestEventInfo(NetSpeedService.this, "后台监控网速", "应用<" + MyOnItemClickListener.appLabel + ">" + "的网速:   " +trueSpeed+"/S", oIntent);
                    trueSpeedStr = ""+Math.floor((oNetSpeedData - netSpeedData)/1024+0.5);//单位为KB,小于KB取0
                    try {
                        dataLogWriter.write(trueSpeedStr+",");
                        dataLogWriter.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    startForeground(1, appNotice);//第一个参数必须大于0
                }

            }
        }).start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        quitService = true;
        try {
            dataLogWriter.write("0");//因为格式是"XX,",最后一个数据在","就没有数据了,所以使用0来填充
            dataLogWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
