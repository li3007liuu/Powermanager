package com.splxtech.powermanagor.activity.dlcx;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.splxtech.powermanagor.R;
import com.splxtech.powermanagor.adapter.MainButtonAdapter;
import com.splxtech.powermanagor.entity.MainButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by li300 on 2016/9/19 0019.
 */

public class DlcxFragment extends Fragment
{
    private ListView listView;
    private List<MainButton> mainButtonList = new ArrayList<>();
    private MainButtonAdapter mainButtonAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view=inflater.inflate(R.layout.fragment_dlcx,container,false);
        listView = (ListView) view.findViewById(R.id.dlcx_list_content);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mainButtonAdapter = new MainButtonAdapter(this.getActivity(),R.layout.item_main_button,mainButtonList);
        listView.setAdapter(mainButtonAdapter);

    }
}
