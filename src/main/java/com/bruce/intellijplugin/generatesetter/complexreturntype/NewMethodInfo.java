/*
 *  Copyright (c) 2017-2019, bruce.ge.
 *    This program is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU General Public License
 *    as published by the Free Software Foundation; version 2 of
 *    the License.
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *    GNU General Public License for more details.
 *    You should have received a copy of the GNU General Public License
 *    along with this program;
 */

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
