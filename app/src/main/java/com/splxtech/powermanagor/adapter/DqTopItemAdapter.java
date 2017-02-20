package com.splxtech.powermanagor.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.splxtech.powermanagor.R;
import com.splxtech.powermanagor.entity.MainButton;

import java.util.List;

/**
 * Created by li300 on 2016/10/12 0012.
 */

public class DqTopItemAdapter extends ArrayAdapter<MainButton>
{
    private int resourceId;
    public DqTopItemAdapter(Context context , int textViewResourceId, List<MainButton> objects)
    {
        super(context,textViewResourceId,objects);
        resourceId = textViewResourceId;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        MainButton selectButton = getItem(position);
        View view;
        ViewHolder viewHolder;
        if(convertView == null)
        {
            view = LayoutInflater.from(getContext()).inflate(resourceId,null);
            viewHolder = new ViewHolder();
            viewHolder.selectbtnImage = (ImageView)view.findViewById(R.id.itopdq_image_icon);
            viewHolder.selectbtnName = (TextView)view.findViewById(R.id.itopdq_image_name);
            viewHolder.wasteText = (TextView)view.findViewById(R.id.itopdq_text_waste);
            view.setTag(viewHolder);
        }
        else
        {
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }
        viewHolder.selectbtnImage.setImageResource(selectButton.getImageId());
        viewHolder.selectbtnName.setText(selectButton.getName());
        viewHolder.wasteText.setText(selectButton.getTable());

        return view;

    }

    class ViewHolder
    {
        ImageView selectbtnImage;
        TextView selectbtnName;
        TextView wasteText;
    }
}
