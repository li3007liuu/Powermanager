package com.splxtech.powermanagor.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.splxtech.powermanagor.R;
import com.splxtech.splxapplib.activity.BaseActivity;

/**
 * Created by li300 on 2016/10/11 0011.
 */

public class SpinnerAdapter extends BaseAdapter {
    private String[] idInt;
    private BaseActivity baseActivity;
    public SpinnerAdapter(BaseActivity baseActivities,String[] idInt)
    {
        this.idInt = idInt;
        this.baseActivity = baseActivities;
    }
    @Override
    public int getCount()
    {
        return idInt.length;
    }
    @Override
    public Object getItem(int arg0)
    {
        return idInt[arg0];
    }
    @Override
    public long getItemId(int arg0)
    {
        return 0;
    }
    @Override
    public View getView(int arg0, View arg1, ViewGroup arg2)
    {
        if(arg1==null)
        {
            arg1 = baseActivity.getLayoutInflater().inflate(R.layout.spinner_item, null);
        }
        TextView textView = (TextView)arg1.findViewById(R.id.spinner_item_id);
        textView.setText(idInt[arg0]);
        return arg1;
    }
}
