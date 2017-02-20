package com.splxtech.powermanagor.activity.grzx;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.splxtech.powermanagor.PowerManagerApp;
import com.splxtech.powermanagor.R;
import com.splxtech.powermanagor.adapter.MainButtonAdapter;
import com.splxtech.powermanagor.entity.MainButton;
import com.splxtech.powermanagor.entity.PMUserInfo;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by li300 on 2016/9/19 0019.
 */

public class GrzxFragment extends Fragment {
    private ListView listView;
    private List<MainButton> mainButtonList = new ArrayList<>();
    private MainButtonAdapter mainButtonAdapter;
    private TextView nameText;
    private TextView emailText;
    private PMUserInfo pmUserInfo;
    private Button usersetButton;
    private PowerManagerApp app;
    private RadioButton sexRadio;
    private ImageView faceImage;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        app = (PowerManagerApp)getActivity().getApplication();
        pmUserInfo = app.getPmUserInfo();
        View view = inflater.inflate(R.layout.fragment_grzx, container, false);
        listView = (ListView) view.findViewById(R.id.grzx_list_content);
        nameText = (TextView) view.findViewById(R.id.grzx_text_name);
        emailText = (TextView) view.findViewById(R.id.grzx_text_email);
        usersetButton = (Button)view.findViewById(R.id.grzx_button_userset);
        sexRadio = (RadioButton)view.findViewById(R.id.grzx_radio_sex);
        faceImage = (ImageView)view.findViewById(R.id.grzx_imageB_face);
        initMainButton();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        initViewData();
        mainButtonAdapter = new MainButtonAdapter(this.getActivity(),R.layout.item_main_button,mainButtonList);
        listView.setAdapter(mainButtonAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MainButton mainButton = mainButtonList.get(i);
                if(mainButton.getName()=="关于")
                {
                    Intent intent = new Intent(getActivity(),GyActivity.class);
                    getActivity().startActivity(intent);
                }
                else if(mainButton.getName()=="设置")
                {
                    Intent intent = new Intent(getActivity(),SetActivity.class);
                    getActivity().startActivity(intent);
                }
            }
        });

        usersetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),UserSetActivity.class);
                //Bundle bundle = new Bundle();
                //bundle.putSerializable("PMUserInfo",pmUserInfo);
                //intent.putExtra("bundle",bundle);
                getActivity().startActivity(intent);
            }
        });
    }

    private void initMainButton()
    {
        if(mainButtonList.size()==0)
        {
            mainButtonList.add(new MainButton("设置",R.drawable.sz));
            mainButtonList.add(new MainButton("关于",R.drawable.gy));
        }

    }

    private void initViewData()
    {
        nameText.setText(pmUserInfo.getName());
        emailText.setText(pmUserInfo.getEmail());
        sexRadio.setChecked(pmUserInfo.getSex());
        //String path = pmUserInfo.getFaceuri().getPath();
        if(pmUserInfo.getFaceuri()==null)
        {
            faceImage.setImageResource(R.drawable.face);
        }
        else
        {
            faceImage.setImageURI(pmUserInfo.getFaceuri());
        }
    }
}
