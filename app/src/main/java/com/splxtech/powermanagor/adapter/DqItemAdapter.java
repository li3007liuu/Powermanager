package com.splxtech.powermanagor.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.splxtech.powermanagor.R;
import com.splxtech.powermanagor.entity.Appliance;


import java.util.List;

/**
 * Created by li300 on 2016/10/6 0006.
 */

public class DqItemAdapter extends ArrayAdapter<Appliance> {
    private int resourceId;
    public DqItemAdapter(Context context, int textViewResourceId, List<Appliance> objects)
    {
        super(context,textViewResourceId,objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Appliance appliance = getItem(position);
        View view;
        ViewHolder viewHolder;
        if(convertView == null)
        {
            view = LayoutInflater.from(getContext()).inflate(resourceId,null);
            viewHolder = new ViewHolder();
            viewHolder.dqIcon = (ImageView)view.findViewById(R.id.idq_image_dqicon);
            viewHolder.dqName = (TextView)view.findViewById(R.id.idq_text_name);
            viewHolder.dqWaste = (TextView)view.findViewById(R.id.idq_text_daywaste);
            viewHolder.dqMoney = (TextView)view.findViewById(R.id.idq_text_daymoney);
            viewHolder.dqMode = (TextView)view.findViewById(R.id.idq_text_mode);
            view.setTag(viewHolder);
        }
        else
        {
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }
        viewHolder.dqIcon.setImageResource(appliance.getOnlineImage());
        viewHolder.dqName.setText(appliance.getName());
        viewHolder.dqMode.setText(appliance.getMode()+"");
        int w = appliance.getWaste();
        float m = (float) (w*0.53);
        viewHolder.dqWaste.setText(w+"");
        viewHolder.dqMoney.setText(m+"");
        return view;
    }
    class ViewHolder
    {
        ImageView dqIcon;
        TextView dqName;
        TextView dqWaste;
        TextView dqMoney;
        TextView dqMode;
    }

}
