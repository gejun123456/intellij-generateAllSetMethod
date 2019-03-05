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

import com.bruce.intellijplugin.generatesetter.Parameters;
import com.bruce.intellijplugin.generatesetter.utils.PsiToolUtils;
import com.google.common.collect.Sets;
import com.intellij.psi.PsiParameter;
import org.jetbrains.annotations.NotNull;

/**
 * @Author bruce.ge
 * @Date 2017/1/28
 * @Description
 */
public class SetReturnTypeHandler implements ComplexReturnTypeHandler {

    @NotNull
    @Override
    public InsertDto handle(Parameters returnParamInfo, String splitText, PsiParameter[] parameters, boolean hasGuava) {
        InsertDto insertDto = new InsertDto();
        String returnVariableName = "";
        StringBuilder insertText = new StringBuilder();
        insertText.append(splitText);
        if (returnParamInfo.getParams().size() > 0) {
            String realName = returnParamInfo.getParams().get(0).getRealName();
            returnVariableName = PsiToolUtils.lowerStart(realName) + "Set";
            insertText.append("Set<").append(realName)
                    .append("> " + returnVariableName).append("=");
        } else {
            returnVariableName = "set";
            insertText.append("Set " + returnVariableName + "=");
        }

        if (hasGuava) {
            insertText.append("Sets.newHashSet();");
            insertDto.setImportList(Sets.newHashSet("com.google.common.collect.Sets"));

        } else {
            insertText.append("new HashSet<>();");
        }

        insertText.append(splitText + "return " + returnVariableName + ";");

        insertDto.setAddedText(insertText.toString());
        return insertDto;
    }
}
