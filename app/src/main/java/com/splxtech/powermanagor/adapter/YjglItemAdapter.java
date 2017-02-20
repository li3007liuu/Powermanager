package com.splxtech.powermanagor.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.splxtech.powermanagor.R;
import com.splxtech.powermanagor.entity.ElectricityMeter;


import java.util.List;

/**
 * Created by li300 on 2016/9/24 0024.
 */

public class YjglItemAdapter extends ArrayAdapter<ElectricityMeter>
{
    private int resourceId;
    public YjglItemAdapter (Context context, int textViewResourceId, List<ElectricityMeter> objects)
    {
        super(context,textViewResourceId,objects);
        resourceId = textViewResourceId;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ElectricityMeter yjglItem = getItem(position);
        View view;
        ViewHolder viewHolder;
        if(convertView == null)
        {
            view = LayoutInflater.from(getContext()).inflate(resourceId,null);
            viewHolder = new ViewHolder();
            viewHolder.textName = (TextView)view.findViewById(R.id.iyjgl_text_name);
            viewHolder.imageIcon = (ImageView)view.findViewById(R.id.iyjgl_image_icon);
            viewHolder.checkBox = (ImageView) view.findViewById(R.id.iyjgl_check_select);
            view.setTag(viewHolder);
        }
        else
        {
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }
        viewHolder.textName.setText(yjglItem.getName());
        viewHolder.imageIcon.setImageResource(yjglItem.getProductImageId());
        if(yjglItem.getChecked()==true)
        {
            viewHolder.checkBox.setVisibility(View.VISIBLE);
        }
        else
        {
            viewHolder.checkBox.setVisibility(View.INVISIBLE);
        }
        return view;
    }

    class ViewHolder
    {
        ImageView imageIcon;
        ImageView checkBox;
        TextView  textName;
    }

}
