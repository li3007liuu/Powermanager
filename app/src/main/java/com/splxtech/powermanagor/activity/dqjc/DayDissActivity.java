package com.splxtech.powermanagor.activity.dqjc;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.AxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.splxtech.powermanagor.Base.AppBaseActivity;
import com.splxtech.powermanagor.PowerManagerApp;
import com.splxtech.powermanagor.R;
import com.splxtech.powermanagor.adapter.DaydissAdapter;
import com.splxtech.powermanagor.entity.AppWasteAll;
import com.splxtech.powermanagor.entity.Appliance;
import com.splxtech.powermanagor.entity.WasteMoneyDis;
import com.splxtech.powermanagor.utils.Utils;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by li300 on 2016/10/10 0010.
 */

public class DayDissActivity extends AppBaseActivity{
    private BarChart barChart;
    private Spinner spinner;
    private ListView listView;
    private List<WasteMoneyDis> wasteMoneyDisArrayList = new ArrayList<>();
    private DaydissAdapter daydissAdapter;
    private TextView titleText;
    private TextView wasteText;
    private TextView moneyText;
    private TextView stitleText;
    private Button backButton;
    private String title;
    private Appliance appliance;
    private AppWasteAll appWasteAll;
    private PowerManagerApp app;
    @Override
    protected void initVariables()
    {
        Intent intent = this.getIntent();
        Bundle bundle = intent.getBundleExtra("dqbundle");
        appliance = (Appliance)bundle.getSerializable("appliance");
        title = appliance.getName();
        app = (PowerManagerApp)getApplication();
        appWasteAll = app.getAppWasteAll(appliance.getId());
        initWasteMoneyDis();
    }

    @Override
    protected void initViews(Bundle savedInstanceState)
    {
        setContentView(R.layout.activity_daydiss);
        barChart = (BarChart)findViewById(R.id.daydiss_barchart);
        setBarChart(barChart);
        setBarData(barChart);
        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                int a = wasteMoneyDisArrayList.size()-1-((int)e.getX()-1);
                setText(a);
            }

            @Override
            public void onNothingSelected() {

            }
        });
        spinner = (Spinner)findViewById(R.id.daydiss_spinner_title);
        spinner.setDropDownVerticalOffset(Utils.dip2px(this,50));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) view).setTextColor(Color.WHITE);
                //在这里进行切换内容显示
                if(i==1)
                {
                    initWasteMoneyWeekDis();
                    setText(0);
                    daydissAdapter.notifyDataSetChanged();
                    barChart.clear();
                    setBarData(barChart);
                }
                else if(i==0)
                {
                    initWasteMoneyDis();
                    setText(0);
                    daydissAdapter.notifyDataSetChanged();
                    barChart.clear();
                    setBarData(barChart);
                }
                else if(i==2)
                {
                    initWasteMoneyMouthDis();
                    setText(0);
                    daydissAdapter.notifyDataSetChanged();
                    barChart.clear();
                    setBarData(barChart);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        daydissAdapter = new DaydissAdapter(this,R.layout.item_daydiss,wasteMoneyDisArrayList);
        listView = (ListView)findViewById(R.id.daydiss_listview_content);
        listView.setAdapter(daydissAdapter);
        titleText = (TextView)findViewById(R.id.daydiss_text_time);
        wasteText = (TextView)findViewById(R.id.daydiss_text_waste);
        moneyText = (TextView)findViewById(R.id.daydiss_text_money);
        stitleText = (TextView)findViewById(R.id.daydiss_text_string);
        stitleText.setText("时间");
        setText(0);
        backButton = (Button)findViewById(R.id.daydiss_button_return);
        backButton.setText("<  "+title);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    @Override
    protected void loadData()
    {

    }


    private void initWasteMoneyDis()
    {
        wasteMoneyDisArrayList.clear();
        if(wasteMoneyDisArrayList.size()==0)
        {
            for(int i=0;i<20;i++) {
                float waste = 0;//(int)(appliance.getAppWasteAll().getDayWasteList().get(19-i).floatValue());
                float money = (float)(waste*0.53);
                String table;
                String xAxis;
                if(i==0)
                {
                    table = "今天";
                    xAxis = "今天";

                }
                else if(i==1)
                {
                    table = "昨天";
                    xAxis = "昨天";
                }
                else
                {
                    table = Utils.getMouth(0-i)+"月"+Utils.getDay(0-i)+"日";
                    xAxis = Utils.getMouth(0-i)+"/"+Utils.getDay(0-i);
                }
                wasteMoneyDisArrayList.add(new WasteMoneyDis(waste,money,table,xAxis));
            }
        }
    }

    private void initWasteMoneyWeekDis()
    {
        wasteMoneyDisArrayList.clear();
            for(int i=0;i<16;i++) {
                float waste = (int)(appWasteAll.getWeeksWasteList().get(15-i).floatValue());
                float money = (float)(waste*0.53);
                String table;
                String xAxis;
                if(i==0)
                {
                    table = "本周";
                    xAxis = "本周";

                }
                else if(i==1)
                {
                    table = "上周";
                    xAxis = "上周";
                }
                else
                {
                    int a= Utils.getDayOfWeek(0);
                    a = a-1+i*7;
                    table = Utils.getMouth(0-a)+"/"+Utils.getDay(0-a)+"-"+Utils.getMouth(0-a+7)+"/"+Utils.getDay(0-a+6);
                    xAxis = Utils.getDay(0-a)+"-"+Utils.getDay(0-a+6);
                }
                wasteMoneyDisArrayList.add(new WasteMoneyDis(waste,money,table,xAxis));
            }
    }

    private void initWasteMoneyMouthDis()
    {
        wasteMoneyDisArrayList.clear();
        for(int i=0;i<12;i++) {
            float waste = (int)(appWasteAll.getMonthsWasteList().get(11-i).floatValue());
            float money = (float)(waste*0.53);
            String table;
            String xAxis;
            if(i==0)
            {
                table = "本月";
                xAxis = "本月";

            }
            else if(i==1)
            {
                table = "上月";
                xAxis = "上月";
            }
            else
            {
                table = Utils.getYear(0-i)+"年"+Utils.getMouth2(0-i)+"月";
                xAxis = Utils.getYear(0-i)+"/"+Utils.getMouth2(0-i);
            }
            wasteMoneyDisArrayList.add(new WasteMoneyDis(waste,money,table,xAxis));
        }
    }

    private void setText(int a)
    {
        titleText.setText(wasteMoneyDisArrayList.get(a).getTable());
        wasteText.setText(wasteMoneyDisArrayList.get(a).getWaste()+"");
        moneyText.setText(wasteMoneyDisArrayList.get(a).getMoney()+"");
    }

    private void setBarChart(BarChart mChart)
    {
        mChart.setDrawBarShadow(false);
        mChart.setDrawValueAboveBar(false);
        mChart.setDescription("");
        mChart.setDescriptionColor(Color.WHITE);

        mChart.setPinchZoom(false);
        //设置不缩放
        mChart.setScaleXEnabled(true);
        mChart.setScaleYEnabled(false);
        mChart.setDrawGridBackground(false);
        mChart.setTouchEnabled(true);
        XAxis xAxis = mChart.getXAxis();

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(8);
        xAxis.setTextSize(10f);

        //关闭右侧坐标轴显示
        mChart.getAxisLeft().setEnabled(false);
        Legend l = mChart.getLegend();
        l.setEnabled(false);
        mChart.getAxisRight().setEnabled(false);
        l = mChart.getLegend();
        l.setEnabled(false);
    }

    private void setBarData(BarChart mChart)
    {
        float start = 0f;
        final int listsize = wasteMoneyDisArrayList.size();
        //为了保证表显示的完整性x y的尺寸要更大一点
        mChart.getXAxis().setAxisMinValue(start);
        mChart.getXAxis().setAxisMaxValue(start+listsize+1f);
        mChart.getXAxis().setValueFormatter(new AxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                if((int)(value)==0)
                {
                    return "";
                }
                else if((int)(value)<=listsize){
                    return wasteMoneyDisArrayList.get(listsize - (int) (value)).getxAxis();
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
        ArrayList<BarEntry> yVals1 = new ArrayList<>();
        for(int i= (int)start;i<(start+listsize);i++)
        {
            //需要对list中的元素翻转复制
            yVals1.add(new BarEntry(i+1f,wasteMoneyDisArrayList.get(listsize-1-i).getWaste()));
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
            set1.setColors(Color.argb(0xff,0xdd,0xdd,0xdd));
            set1.setHighLightColor(Color.argb(255,0x5a,0xa9,0xf9));
            set1.setHighLightAlpha(255);
            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);
            BarData data1 = new BarData(dataSets);
            data1.setDrawValues(false);
            data1.setBarWidth(0.4f);
            mChart.setData(data1);
        }
    }
}
