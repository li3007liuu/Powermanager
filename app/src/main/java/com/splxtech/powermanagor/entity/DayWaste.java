package com.splxtech.powermanagor.entity;

import java.io.Serializable;

/**
 * Created by li300 on 2016/10/26 0026.
 */

public class DayWaste implements Serializable {
    public float[] daywaste;
    public static final long serialVersionUID = 9531L;
    public DayWaste(){
        daywaste = new float[12];
        for(int i=0;i<12;i++)
        {
            daywaste[i] = 0;
        }
    }
}
