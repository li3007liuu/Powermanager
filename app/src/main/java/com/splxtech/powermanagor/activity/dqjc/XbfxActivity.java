package com.splxtech.powermanagor.activity.dqjc;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.AxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.splxtech.powermanagor.Base.MqttBaseActivity;
import com.splxtech.powermanagor.R;
import com.splxtech.powermanagor.engine.MqttService;
import com.splxtech.powermanagor.entity.ElectricityMeter;
import com.splxtech.powermanagor.utils.AppProtocol;

import java.util.ArrayList;


/**
 * Created by li300 on 2016/10/15 0015.
 */

public class XbfxActivity extends MqttBaseActivity {

    private Button backButton;
    private BarChart dlChart;
    private BarChart dyChart;
    private ElectricityMeter device;
    private boolean flag = false;//查询定时器的开启与关闭标志位
    private boolean sflag = false;//消息是否发送标志位
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(flag==true) {
                if(sflag==true) {
                    sendMessage();
                }
                try {
                    handler.postDelayed(this, 500);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };


    @Override
    protected void initVariables()
    {
        Intent intent = this.getIntent();
        Bundle bundle = (Bundle)intent.getBundleExtra("dvbundle");
        device =  (ElectricityMeter)bundle.getSerializable("device");
        //给全局消息接收器赋值，并进行消息处理
        mReciver = new MqttBaseActivity.MessageMqttReciver(){
            @Override
            public void onReceive(Context context, Intent intent)
            {
                String action = intent.getAction();

                if(action.equals(MqttService.MQTT_RECE_MESSAGE_ACTION))
                {
                    String topic = intent.getStringExtra("Topic");
                    String message = "";
                    byte[] data = intent.getByteArrayExtra("Message");
                    if(topic.equals(DqActivity.hardrtopic+device.getProductId())) {
                        for (int i = 0; i < data.length; i++) {
                            message = message + data[i] + "\t";
                        }
                        int[] receData = AppProtocol.getReceiverData(AppProtocol.DLXBPORTNUM, data);
                        upDataBarChart(receData);
                    }
                }
                else
                {

                }
            }
        };
    }

    @Override
    protected void initViews(Bundle savedInstanceState)
    {
        setContentView(R.layout.activity_xbfx);
        backButton = (Button)findViewById(R.id.xbfx_button_return);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }
        });
        dlChart = (BarChart) findViewById(R.id.xbfx_linechart_dl);
        setBarChart(dlChart);
        dyChart = (BarChart) findViewById(R.id.xbfx_linechart_dy);
        setBarChart(dyChart);
    }

    @Override
    public void loadData()
    {
        handler.postDelayed(runnable,500);
        flag = true;
    }
    @Override
    public void onStart(){
        super.onStart();
        //第一个参数的意思是初始化之后多久执行timertask,第二个参数是间隔多久执行一次timertask
        //timer.schedule(timerTask,250,500);

        sflag = true;
    }
    @Override
    public void onStop()
    {
        sflag = false;
        super.onStop();
    }

    @Override
    public void onDestroy()
    {
        flag = false;
        super.onDestroy();
    }
    private void setBarChart(BarChart barChart)
    {
        barChart.setTouchEnabled(false);
        barChart.setDrawBorders(false);
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(false);
        barChart.setDescription("");
        barChart.setDescriptionColor(Color.WHITE);
        barChart.setMaxVisibleValueCount(42);
        barChart.setPinchZoom(false);
        barChart.setScaleXEnabled(false);
        barChart.setScaleYEnabled(false);
        barChart.setDrawGridBackground(false);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(8);

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setLabelCount(6,false);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinValue(0f);
        leftAxis.setValueFormatter(new AxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                if(value==100)
                {
                    return 100+"";
                }
                else if(value==80)
                {
                    return 10+"";
                }
                else if(value==60)
                {
                    return 1+"";
                }
                else if(value==40)
                {
                    return 0.1+"";
                }
                else if(value==20)
                {
                    return 0.01+"";
                }
                else if(value==0)
                {
                    return 0.001+"";
                }
                else
                {
                    return "";
                }
            }

            @Override
            public int getDecimalDigits() {
                return 0;
            }
        });

        barChart.getAxisRight().setEnabled(false);
        Legend l = barChart.getLegend();
        l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
        l.setTextSize(14f);
        BarData data = new BarData();
        data.setValueTextSize(10f);
        data.setBarWidth(0.9f);
        barChart.setData(data);
    }
    private void setData(BarChart barChart,int count,float[] data,boolean flag)
    {
        barChart.clear();
        float start = 0f;
        barChart.getXAxis().setAxisMinValue(start);
        barChart.getXAxis().setAxisMaxValue(start+count+1);
        ArrayList<BarEntry>yVals1 = new ArrayList<>();
        for(int i=(int)start;i<(start+count);i++)
        {
            yVals1.add(new BarEntry(i+1f,data[i]));
        }
        BarDataSet set1;
        if(barChart.getData()!=null&&
                barChart.getData().getDataSetCount()>0)
        {
            set1 = (BarDataSet)barChart.getData().getDataSetByIndex(0);
            set1.setValues(yVals1);
            barChart.getData().notifyDataChanged();
            barChart.notifyDataSetChanged();
        }
        else
        {
            if(flag==false){
                set1 = new BarDataSet(yVals1,"电压谐波");
                set1.setColors(ColorTemplate.DLXB_COLORS);
            }
            else
            {
                set1 = new BarDataSet(yVals1,"电流谐波");
                set1.setColors(ColorTemplate.DYXB_COLORS);
            }
            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            BarData data1 = new BarData(dataSets);
            data1.setDrawValues(false);
            data1.setBarWidth(0.9f);
            barChart.setData(data1);
        }

    }

    private void sendMessage()
    {
        try {
            String Message = new String(AppProtocol.getSendData(AppProtocol.DLXBPORTNUM, 0),"ISO-8859-1");
            try {
                iMqttService.mqttPubMessage(DqActivity.hardstopic+device.getProductId(), Message, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    private void upDataBarChart(int[] receData)
    {
        if(receData.length>79){
            float[] fdata1 = new float[40];
            float[] fdata2 = new float[40];
            double temp1 = Math.log10((float)(receData[0]));
            double temp2 = Math.log10((float)(receData[40]));
            for (int i = 0; i < 80; i++) {
                if (i == 40 || i == 0)
                {
                    fdata1[0] = 100;
                    fdata2[0] = 100;
                }
                else if (i < 40)
                {
                    fdata2[i] = (float) (Math.log10((float) (receData[i])) / temp1 * 100);
                }
                else
                {
                    fdata1[i-40] = (float) (Math.log10((float) (receData[i])) / temp2 * 100);
                }
            }
            setData(dlChart,40,fdata1,true);
            setData(dyChart,40,fdata2,false);
        }
    }
}
