package com.bruce.intellijplugin.generatesetter.complexreturntype;

import com.intellij.psi.PsiClass;

/**
 * @Author bruce.ge
 * @Date 2017/1/29
 * @Description the info to create statement for list ect.
 */
public class NewMethodInfo {
    private String paramWrapType;

    private PsiClass paramInnerType;

    private String paramName;

    private PsiClass returnClass;

    private String returnVariableName;


    private String splitText;

    public String getParamWrapType() {
        return paramWrapType;
    }

    public void setParamWrapType(String paramWrapType) {
        this.paramWrapType = paramWrapType;
    }

    public PsiClass getParamInnerType() {
        return paramInnerType;
    }

    public void setParamInnerType(PsiClass paramInnerType) {
        this.paramInnerType = paramInnerType;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public PsiClass getReturnClass() {
        return returnClass;
    }

    public void setReturnClass(PsiClass returnClass) {
        this.returnClass = returnClass;
    }

    public String getReturnVariableName() {
        return returnVariableName;
    }

    public void setReturnVariableName(String returnVariableName) {
        this.returnVariableName = returnVariableName;
    }

    public String getSplitText() {
        return splitText;
    }

    public void setSplitText(String splitText) {
        this.splitText = splitText;
    }
}
