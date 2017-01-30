package com.bruce.intellijplugin.generatesetter.complexreturntype;

/**
 * @Author bruce.ge
 * @Date 2017/1/29
 * @Description
 */
@Deprecated
public class WrapInfo {
    private String qualifyTypeName;

    private String shortTypeName;


    public String getQualifyTypeName() {
        return qualifyTypeName;
    }

    public void setQualifyTypeName(String qualifyTypeName) {
        this.qualifyTypeName = qualifyTypeName;
    }

    public String getShortTypeName() {
        return shortTypeName;
    }

    public void setShortTypeName(String shortTypeName) {
        this.shortTypeName = shortTypeName;
    }
}
