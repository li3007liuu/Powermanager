package com.splxtech.powermanagor.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.splxtech.powermanagor.R;
import com.splxtech.powermanagor.entity.SsidItem;

import java.util.List;

/**
 * Created by li300 on 2016/9/28 0028.
 */

public class SsidItemAdapter extends ArrayAdapter<SsidItem> {
    private int resourceId;
    public SsidItemAdapter(Context context, int textViewResourceId,List<SsidItem> objects)
    {
        super(context,textViewResourceId,objects);
        resourceId = textViewResourceId;
    }
    @Override
    public View getView(int position,View convertView,ViewGroup parent)
    {
        SsidItem item = getItem(position);
        View view;
        ViewHolder viewHolder;
        if(convertView == null)
        {
            view = LayoutInflater.from(getContext()).inflate(resourceId,null);
            viewHolder = new ViewHolder();
            viewHolder.itemName = (TextView)view.findViewById(R.id.issid_text_name);
            viewHolder.itemDbm = (TextView)view.findViewById(R.id.issid_text_dbm);
            view.setTag(viewHolder);
        }
        else
        {
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();

        }
        viewHolder.itemName.setText(item.getName());
        viewHolder.itemDbm.setText(item.getDbm()+"");
        return view;
    }

    class ViewHolder
    {
        TextView itemName;
        TextView itemDbm;
    }
}
