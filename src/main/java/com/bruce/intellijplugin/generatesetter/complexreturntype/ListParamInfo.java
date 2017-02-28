package com.bruce.intellijplugin.generatesetter.complexreturntype;

/**
 * @Author bruce.ge
 * @Date 2017/2/28
 * @Description
 */
public class ListParamInfo {
    private String paramName;

    private String collectType;

    private String realType;


    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getCollectType() {
        return collectType;
    }

    public void setCollectType(String collectType) {
        this.collectType = collectType;
    }

    public String getRealType() {
        return realType;
    }

    public void setRealType(String realType) {
        this.realType = realType;
    }
}
