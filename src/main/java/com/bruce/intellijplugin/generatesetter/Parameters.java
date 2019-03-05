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

package com.bruce.intellijplugin.generatesetter;

import com.intellij.psi.PsiClass;

import java.util.List;

/**
 * Created by bruce.ge on 2016/12/24.
 */
public class Parameters {

    private String collectPackege;

    private String collectName;

    private List<RealParam> params;

    private PsiClass returnType;


    public PsiClass getReturnType() {
        return returnType;
    }

    public void setReturnType(PsiClass returnType) {
        this.returnType = returnType;
    }

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
