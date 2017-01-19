package com.bruce.intellijplugin.generatesetter;

import com.intellij.psi.PsiMethod;

import java.util.List;
import java.util.Map;

/**
 * @Author bruce.ge
 * @Date 2017/1/19
 * @Description
 */
public class GetInfo {
    private String paramName;

    private List<PsiMethod> getMethods;


    private Map<String,String> nameToMethodMap;


    public Map<String, String> getNameToMethodMap() {
        return nameToMethodMap;
    }

    public void setNameToMethodMap(Map<String, String> nameToMethodMap) {
        this.nameToMethodMap = nameToMethodMap;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public List<PsiMethod> getGetMethods() {
        return getMethods;
    }

    public void setGetMethods(List<PsiMethod> getMethods) {
        this.getMethods = getMethods;
    }
}
