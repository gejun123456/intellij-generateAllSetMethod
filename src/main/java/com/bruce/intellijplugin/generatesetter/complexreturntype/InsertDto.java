package com.bruce.intellijplugin.generatesetter.complexreturntype;

import java.util.Set;

/**
 * @Author bruce.ge
 * @Date 2017/1/28
 * @Description
 */
public class InsertDto {
    private String addedText;

    private String addMethods;

    private Set<String> importList;


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


    public Set<String> getImportList() {
        return importList;
    }

    public void setImportList(Set<String> importList) {
        this.importList = importList;
    }
}
