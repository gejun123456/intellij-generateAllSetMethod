package com.bruce.intellijplugin.generatesetter;

import java.util.List;

/**
 * Created by bruce.ge on 2016/12/24.
 */
public class ParamInfo {

    private String collectPackege;

    private String collectName;

    private List<RealParam> params;


    public String getCollectPackege() {
        return collectPackege;
    }

    public void setCollectPackege(String collectPackege) {
        this.collectPackege = collectPackege;
    }

    public String getCollectName() {
        return collectName;
    }

    public void setCollectName(String collectName) {
        this.collectName = collectName;
    }

    public List<RealParam> getParams() {
        return params;
    }

    public void setParams(List<RealParam> params) {
        this.params = params;
    }
}
