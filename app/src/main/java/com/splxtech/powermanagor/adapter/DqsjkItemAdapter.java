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

public class DqsjkItemAdapter extends ArrayAdapter<Appliance> {
    private int resourceId;
    public DqsjkItemAdapter(Context context, int textViewResourceId, List<Appliance> objects)
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
            viewHolder.dqIcon = (ImageView)view.findViewById(R.id.idqsjk_image_dqicon);
            viewHolder.dqName = (TextView)view.findViewById(R.id.idqsjk_text_name);
            viewHolder.dqId = (TextView)view.findViewById(R.id.idqsjk_text_id);
            viewHolder.dqNmode = (TextView)view.findViewById(R.id.idqsjk_text_nmode);
            view.setTag(viewHolder);
        }
        else
        {
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }
        viewHolder.dqIcon.setImageResource(appliance.getImageId());
        viewHolder.dqName.setText(appliance.getName());
        viewHolder.dqNmode.setText(appliance.getModeNum()+"");
        viewHolder.dqId.setText(appliance.getAppId()+"");

        return view;
    }
    class ViewHolder
    {
        ImageView dqIcon;
        TextView dqName;
        TextView dqId;
        TextView dqNmode;
    }
}
