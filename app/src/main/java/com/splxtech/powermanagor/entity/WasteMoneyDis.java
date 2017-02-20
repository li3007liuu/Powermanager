package com.splxtech.powermanagor.entity;

import com.splxtech.powermanagor.utils.Utils;

/**
 * Created by li300 on 2016/10/11 0011.
 */

public class WasteMoneyDis {
    private float waste;
    private float money;
    private String table;
    private String xAxis;
    public WasteMoneyDis (float w,float m,String t,String x)
    {
        waste = w;
        money = m;
        table = t;
        xAxis = x;
    }
    public WasteMoneyDis(int a,int t,float w,float m)
    {
        switch (t){
            case 1:
                if(a==0)
                {
                    table = "今天";
                    xAxis = "今天";
                }
                else if(a==1)
                {
                    table = "昨天";
                    xAxis = "昨天";
                }
                else if(a>1)
                {
                    table = Utils.getMouth(0-a)+"月"+Utils.getDay(0-a)+"日";
                    xAxis = Utils.getMouth(0-a)+"/"+Utils.getDay(0-a);
                }
                else
                {
                    table = "";
                    xAxis = "";
                }
                break;
            case 2:
                if(a==0)
                {
                    table = "本周";
                    xAxis = "本周";
                }
                else if(a==1)
                {
                    table = "上周";
                    xAxis = "上周";
                }
                else if(a>1)
                {
                    int b= Utils.getDayOfWeek(0);
                    b = b-1+a*7;
                    table = Utils.getMouth(0-b)+"/"+Utils.getDay(0-b)+"-"+Utils.getMouth(0-b+7)+"/"+Utils.getDay(0-b+6);
                    xAxis = Utils.getDay(0-b)+"-"+Utils.getDay(0-b+6);
                }
                else
                {
                    table = "";
                    xAxis = "";
                }
                break;
            case 3:
                if(a==0)
                {
                    table = "本月";
                    xAxis = "本月";
                }
                else if(a==1)
                {
                    table = "上月";
                    xAxis = "上月";
                }
                if(a>1)
                {
                    table = Utils.getYear(0-a)+"年"+Utils.getMouth2(0-a)+"月";
                    xAxis = Utils.getYear(0-a)+"/"+Utils.getMouth2(0-a);
                }
                else
                {
                    table = "";
                    xAxis = "";
                }
                break;
        }
        waste = w;
        money = m;
    }
    public void setWaste(float a)
    {
        waste = a;
    }
    public float getWaste()
    {
        return waste;
    }
    public void setMoney(float a)
    {
        money = a;
    }
    public float getMoney()
    {
        return money;
    }
    public void setTable(String a)
    {
        table = a;
    }
    public String getTable()
    {
        return table;
    }
    public void setxAxis(String a)
    {
        xAxis = a;
    }
    public String getxAxis()
    {
        return xAxis;
    }
}
