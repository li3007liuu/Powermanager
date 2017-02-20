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
 * Created by li300 on 2016/11/21 0021.
 */

public class DqjcDeviceAdapter extends ArrayAdapter<ElectricityMeter> {
    private int resourceId;

    public DqjcDeviceAdapter(Context context, int textViewResourceId, List<ElectricityMeter> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ElectricityMeter selectButton = getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.selectbtnImage = (ImageView) view.findViewById(R.id.imain_image_btlogo);
            viewHolder.selectbtnName = (TextView) view.findViewById(R.id.imain_text_content);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.selectbtnImage.setImageResource(selectButton.getProductImageId());
        viewHolder.selectbtnName.setText(selectButton.getName());

        return view;

    }

    class ViewHolder {
        ImageView selectbtnImage;
        TextView selectbtnName;
    }
}