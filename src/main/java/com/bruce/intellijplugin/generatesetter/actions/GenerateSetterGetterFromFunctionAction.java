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

package com.bruce.intellijplugin.generatesetter.actions;

import com.bruce.intellijplugin.generatesetter.CommonConstants;
import com.bruce.intellijplugin.generatesetter.GenerateAllHandlerAdapter;
import org.jetbrains.annotations.NotNull;

/**
 * @author bruce ge
 */
public class GenerateSetterGetterFromFunctionAction extends GenerateAllSetterBase {
    public GenerateSetterGetterFromFunctionAction() {
        super(new GenerateAllHandlerAdapter(){
            @Override
            public boolean isFromMethod() {
                return true;
            }
        });
    }


    @NotNull
    @Override
    public String getText() {
        return CommonConstants.GENERATE_CONVERTER_FROM_METHOD;
    }
}
