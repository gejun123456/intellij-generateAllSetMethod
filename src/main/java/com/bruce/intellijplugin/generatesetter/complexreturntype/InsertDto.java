package com.bruce.intellijplugin.generatesetter.complexreturntype;

import java.util.List;

/**
 * @Author bruce.ge
 * @Date 2017/1/28
 * @Description
 */
public class InsertDto {
    private String addedText;

    private String addMethods;

    private List<String> importList;


    public String getAddedText() {
        return addedText;
    }

    public void setAddedText(String addedText) {
        this.addedText = addedText;
    }

    public String getAddMethods() {
        return addMethods;
    }

    public void setAddMethods(String addMethods) {
        this.addMethods = addMethods;
    }

    public List<String> getImportList() {
        return importList;
    }

    public void setImportList(List<String> importList) {
        this.importList = importList;
    }
}
