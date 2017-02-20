package com.splxtech.powermanagor.entity;

import java.io.Serializable;

/**
 * Created by li300 on 2016/9/28 0028.
 */

public class SsidItem implements Serializable {
    private String name;
    // 信号强度
    private int dbm;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDbm() {
        return dbm;
    }

    public void setDbm(int dbm) {
        this.dbm = dbm;
    }

}
