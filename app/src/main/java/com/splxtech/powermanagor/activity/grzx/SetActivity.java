package com.splxtech.powermanagor.activity.grzx;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.splxtech.powermanagor.Base.AppBaseActivity;
import com.splxtech.powermanagor.R;
import com.splxtech.powermanagor.entity.SoftMess;
import com.splxtech.powermanagor.utils.Utils;

import java.io.File;

/**
 * Created by li300 on 2016/11/4 0004.
 */

public class SetActivity extends AppBaseActivity
{
    private Button backButton;
    private SeekBar seekBar;
    private TextView processText;
    private Switch aSwitch;
    private Button usersetButton;
    private Button hardsetButton;
    private Button softsetButton;
    private SoftMess softMess = new SoftMess();
    private String softMessagePath = Environment.getExternalStorageDirectory().toString()
            + File.separator +"PowerManager"+File.separator + "objdata"+File.separator+"softmessage.dat";
    @Override
    protected void initVariables()
    {
        softMess = (SoftMess)Utils.restoreObject(softMessagePath);
        if(softMess==null)
        {
            softMess = new SoftMess();
            softMess.setMessageflag(false);
            softMess.setProcess(500);
            softMess.setHardversion("halv1.0.0");
            softMess.setSoftversion("ver1.0.2");
        }
    }

    @Override
    protected void initViews(Bundle savedInstanceState)
    {
        setContentView(R.layout.activity_set);
        backButton = (Button)findViewById(R.id.set_button_return);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        seekBar = (SeekBar)findViewById(R.id.set_seekbar_sendf);
        seekBar.setMax(900);
        seekBar.setProgress(softMess.getProcess());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                processText.setText((1000-i)+"ms");
                softMess.setProcess(1000-i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        processText = (TextView)findViewById(R.id.set_text_sendf);
        processText.setText(softMess.getProcess()+"ms");
        aSwitch = (Switch)findViewById(R.id.set_switch_message);
        aSwitch.setChecked(softMess.getMessageFlag());
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                softMess.setMessageflag(b);
            }
        });
        usersetButton = (Button)findViewById(R.id.set_button_userset);
        usersetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SetActivity.this,UserSetActivity.class);
                SetActivity.this.startActivity(intent);
            }
        });
        hardsetButton = (Button)findViewById(R.id.set_button_hardup);
        softsetButton = (Button)findViewById(R.id.set_button_softup);
    }
    @Override
    protected void loadData()
    {

    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Utils.saveObject(softMessagePath,softMess);
    }
}
