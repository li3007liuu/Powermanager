package com.splxtech.powermanagor.activity.dqjc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.splxtech.powermanagor.R;
import com.splxtech.powermanagor.activity.grzx.YjglActivity;
import com.splxtech.powermanagor.activity.main.MainActivity;
import com.splxtech.powermanagor.adapter.DqjcDeviceAdapter;
import com.splxtech.powermanagor.adapter.MainButtonAdapter;
import com.splxtech.powermanagor.entity.ElectricityMeter;
import com.splxtech.powermanagor.entity.MainButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by li300 on 2016/9/19 0019.
 */

public class DqjcFragment extends Fragment
{
    private ListView listView;
    private ListView listtop;
    private List<MainButton>mainButtonList = new ArrayList<>();
    private List<MainButton>topButtonlist = new ArrayList<>();
    private DqjcDeviceAdapter mainButtonAdapter;
    private MainButtonAdapter topButtonAdapter;
    private List<ElectricityMeter> deviceList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        MainActivity mainActivity = (MainActivity)getActivity();
        deviceList = mainActivity.app.getElectricityMeters(mainActivity.app.getPmUserInfo().getId());
        View view =inflater.inflate(R.layout.fragment_dqjc,container,false);
        listView = (ListView)view.findViewById(R.id.dqjc_list_content);
        listtop = (ListView)view.findViewById(R.id.dqjc_list_top);
        initMainButton();
        return view;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        mainButtonAdapter = new DqjcDeviceAdapter(this.getActivity(),R.layout.item_main_button,deviceList);
        listView.setAdapter(mainButtonAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    Intent intent = new Intent(getActivity(),DqActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("device",deviceList.get(i));
                    intent.putExtra("devicebundle",bundle);
                    getActivity().startActivity(intent);

            }
        });
        topButtonAdapter = new MainButtonAdapter(this.getActivity(),R.layout.item_main_button,topButtonlist);
        listtop.setAdapter(topButtonAdapter);
        listtop.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MainButton mainButton = topButtonlist.get(i);
                if(mainButton.getName()=="设备管理")
                {
                    Intent intent = new Intent(getActivity(),YjglActivity.class);
                    getActivity().startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onResume()
    {
        super.onResume();
        mainButtonAdapter.notifyDataSetChanged();
    }

    private void initMainButton()
    {


        if(topButtonlist.size()==0)
        {
            topButtonlist.add(new MainButton("设备管理",R.drawable.sbgl));
        }
        /*
        if(mainButtonList.size()==0)
        {
            //mainButtonList.add(new MainButton("电器",R.drawable.dq));
            if(deviceList!=null) {
                for (int i = 0; i < deviceList.size(); i++) {
                    MainButton mainButton = new MainButton("", 0);
                    mainButton.setName(deviceList.get(i).getName());
                    if (deviceList.get(i).getProductType() == 1) {
                        mainButton.setImageId(R.drawable.db);
                    } else if (deviceList.get(i).getProductType() == 2) {
                        mainButton.setImageId(R.drawable.znkg);
                    } else {
                        mainButton.setImageId(R.drawable.dq);
                    }
                    mainButtonList.add(mainButton);
                }
            }
        }*/
    }
}