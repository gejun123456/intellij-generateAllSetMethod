/*
 *  Copyright (c) 2017-2018, bruce.ge.
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
