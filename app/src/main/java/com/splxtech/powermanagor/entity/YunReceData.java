package com.splxtech.powermanagor.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by li300 on 2016/11/14 0014.
 */

public class YunReceData {
    private int msg;
    private List<YunReData> data = new ArrayList<>();
    public void setMsg(int a){
        msg = a;
    }
    public int getMsg()
    {
        return msg;
    }
    public void setData(List<YunReData>cc)
    {
        data = cc;
    }
    public List<YunReData> getData()
    {
        return data;
    }
}

