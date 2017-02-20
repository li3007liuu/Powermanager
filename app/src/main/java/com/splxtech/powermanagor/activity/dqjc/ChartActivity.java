package com.splxtech.powermanagor.activity.dqjc;

import android.graphics.Color;
import android.os.Bundle;

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
import com.splxtech.powermanagor.Base.AppBaseActivity;
import com.splxtech.powermanagor.R;
import com.splxtech.powermanagor.ui.CircularRingPercentageView;

import java.util.ArrayList;


/**
 * Created by li300 on 2016/10/8 0008.
 */

public class ChartActivity extends AppBaseActivity {
    private BarChart barChart;
    private CircularRingPercentageView progressCircle;
    @Override
    protected void initVariables()
    {

    }
    @Override
    protected void initViews(Bundle savedInstanceState)
    {
        setContentView(R.layout.activity_chart);
        barChart = (BarChart)findViewById(R.id.bar_chart);
        setBarChart(barChart);
        setBarData(barChart);
        progressCircle = (CircularRingPercentageView)findViewById(R.id.chart_progress);
        progressCircle.setMaxColorNumber(100);
        progressCircle.setProgress(20);

    }
    @Override
    protected void loadData()
    {

    }

    private void setBarChart(BarChart mChart)
    {
        mChart.setDrawBarShadow(true);
        mChart.setDrawValueAboveBar(false);
        mChart.setDescription("");
        mChart.setDescriptionColor(Color.WHITE);

        mChart.setMaxVisibleValueCount(12);
        mChart.setPinchZoom(false);
        //设置不缩放
        mChart.setScaleXEnabled(true);
        //mChart.setScaleX(2);
        mChart.setScaleYEnabled(false);
        mChart.setDrawGridBackground(false);
        mChart.setTouchEnabled(true);
        XAxis xAxis = mChart.getXAxis();
        xAxis.setValueFormatter(new AxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return (int)(value)+":00";
            }

            @Override
            public int getDecimalDigits() {
                return 0;
            }
        });
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(8);
        xAxis.setTextSize(10f);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setLabelCount(6,false);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setDrawGridLines(false);
        leftAxis.setTextSize(12f);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinValue(0f);
        //关闭右侧坐标轴显示
        mChart.getAxisRight().setEnabled(false);
        Legend l = mChart.getLegend();
        l.setEnabled(false);
        /*
        l.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);*/

        //BarData data1 = new BarData();
        //data1.setValueTextSize(10f);
        //data1.setBarWidth(0.9f);
        //mChart.setData(data1);
    }

    private void setBarData(BarChart mChart)
    {
        float start = 0f;
        mChart.getXAxis().setAxisMinValue(start);
        mChart.getXAxis().setAxisMaxValue(start+13);
        ArrayList<BarEntry> yVals1 = new ArrayList<>();
        for(int i= (int)start;i<(start+12);i++)
        {
            yVals1.add(new BarEntry(i+1f,i*20));
        }
        BarDataSet set1;
        if(mChart.getData()!=null&&mChart.getData().getDataSetCount()>0)
        {
            set1 = (BarDataSet) mChart.getData().getDataSetByIndex(0);
            set1.setValues(yVals1);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        }
        else
        {
            set1 = new BarDataSet(yVals1, "");
            set1.setColors(Color.argb(255,0x5a,0xa9,0xf9));
            set1.setBarShadowColor(Color.argb(0xff,0xdd,0xdd,0xdd));
            set1.setHighLightColor(Color.argb(255,0x5a,0xa9,0xf9));
            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);
            BarData data1 = new BarData(dataSets);
            data1.setDrawValues(true);
            data1.setBarWidth(0.23f);
            mChart.setData(data1);
        }
    }

}
