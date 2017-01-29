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
            //the list object shall have set method so it can work.
            WrappInfo wrappInfo = PsiToolUtils.extractWrappInfo(canonicalText);
            if (wrappInfo == null) {
                continue;
            } else {
                //check the collectType.
                String qualifyTypeName = wrappInfo.getQualifyTypeName();
                if (qualifyTypeName.equals("java.util.List")
                        || qualifyTypeName.equals("java.util.Set")
                        || qualifyTypeName.equals("java.util.Map")) {

                } else {
                    continue;
                }

            }
        }

        if (findedListParameter != null) {
            ParamInfo paraInfo = PsiToolUtils.extractParamInfo(findedListParameter.getType());
            String methodName = "convertTo";
            if (returnParamInfo.getParams().size() > 0) {
                methodName = methodName + returnParamInfo.getParams().get(0).getRealName();
            }
            //todo could use with other style rather than for i ect
            //todo maybe we can check with null.

            insertText.append(splitText + "for(int i=0;i<" + findedListParameter.getName() + ".size();i++){");
            insertText.append(splitText + "\t" + returnVariableName + ".add(" + methodName + "(" + findedListParameter.getName() + ".get(i)));");
            insertText.append(splitText + "}");

            //todo must have param size >0 for check or i have to check everywhere
            if (returnParamInfo.getParams().size() > 0) {
                String addMethods = splitText + "public static " + returnParamInfo.getParams().get(0).getRealName() +"";
                insertDto.setAddMethods(addMethods);

            }
        }

        insertText.append(splitText + "return " + returnVariableName + ";");

        insertDto.setAddedText(insertText.toString());
        return insertDto;
    }
}
