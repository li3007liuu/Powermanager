package com.splxtech.powermanagor.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.splxtech.powermanagor.R;
import com.splxtech.powermanagor.entity.WasteMoneyDis;

import java.util.List;

/**
 * Created by li300 on 2016/10/11 0011.
 */

public class DaydissAdapter extends ArrayAdapter<WasteMoneyDis> {
    private int resourceId;
    public DaydissAdapter(Context context, int textViewResourceId, List<WasteMoneyDis> objects)
    {
        super(context,textViewResourceId,objects);
        resourceId = textViewResourceId;
    }
    @Override
    public View getView(int position,View convertView,ViewGroup parent)
    {
        WasteMoneyDis wasteMoneyDis = getItem(position);
        View view;
        ViewHolder viewHolder;
        if(convertView == null)
        {
            view = LayoutInflater.from(getContext()).inflate(resourceId,null);
            viewHolder = new ViewHolder();
            viewHolder.wasteText = (TextView)view.findViewById(R.id.idaydiss_text_waste);
            viewHolder.moneyText = (TextView)view.findViewById(R.id.idaydiss_text_money);
            viewHolder.tableText = (TextView)view.findViewById(R.id.idaydiss_text_time);
            view.setTag(viewHolder);
        }
        else
        {
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }
        viewHolder.wasteText.setText(wasteMoneyDis.getWaste()+"");
        viewHolder.moneyText.setText(wasteMoneyDis.getMoney()+"");
        viewHolder.tableText.setText(wasteMoneyDis.getTable());

        return view;
    }

    class ViewHolder
    {
        TextView wasteText;
        TextView moneyText;
        TextView tableText;
    }
}
