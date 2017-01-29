package com.bruce.intellijplugin.generatesetter.complexreturntype;

import com.bruce.intellijplugin.generatesetter.ParamInfo;
import com.bruce.intellijplugin.generatesetter.utils.PsiToolUtils;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiType;
import org.jetbrains.annotations.NotNull;

/**
 * @Author bruce.ge
 * @Date 2017/1/28
 * @Description
 */
public class ListReturnTypeHandler implements ComplexReturnTypeHandler {


    @NotNull
    @Override
    public InsertDto handle(ParamInfo returnParamInfo, String splitText, PsiParameter[] parameters, boolean hasGuava) {
        InsertDto insertDto = new InsertDto();
        String returnVariableName = "";
        StringBuilder insertText = new StringBuilder();
        insertText.append(splitText);
        if (returnParamInfo.getParams().size() > 0) {
            String realName = returnParamInfo.getParams().get(0).getRealName();
            returnVariableName = PsiToolUtils.lowerStart(realName) + "list";
            insertText.append("List<").append(realName)
                    .append("> " + returnVariableName).append("=");
        } else {
            returnVariableName = "list";
            insertText.append("List" + returnVariableName + "=");
        }

        if (hasGuava) {
            insertText.append("Lists.newArrayList();");
        } else {
            insertText.append("new ArrayList<>();");
        }
        //check if parameter containList ect, to build with it.
        PsiParameter findedListParameter = null;
        for (PsiParameter parameter : parameters) {
            PsiType type = parameter.getType();
            String canonicalText = parameter.getType().getCanonicalText();
//            todo for array class how to fix it.
            if (canonicalText.startsWith("java.util.List") ||
                    canonicalText.startsWith("java.util.Set") ||
                    canonicalText.startsWith("java.util.Map")) {
                findedListParameter = parameter;
            }
        }

        if (findedListParameter != null) {
            ParamInfo paraInfo = PsiToolUtils.extractParamInfo(findedListParameter.getType());
            insertText.append(splitText + "for(int i=0;i<" + findedListParameter.getName() + ".size();i++){");
            insertText.append(splitText + "\t" + returnVariableName + ".add(convertTo"+"(" + findedListParameter.getName() + ".get(i)));");
            insertText.append(splitText + "}");
        }

        insertText.append(splitText + "return " + returnVariableName + ";");

        insertDto.setAddedText(insertText.toString());
        return insertDto;
    }
}
