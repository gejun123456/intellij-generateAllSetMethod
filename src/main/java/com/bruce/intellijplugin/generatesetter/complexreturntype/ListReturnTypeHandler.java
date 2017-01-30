package com.bruce.intellijplugin.generatesetter.complexreturntype;

import com.bruce.intellijplugin.generatesetter.ParamInfo;
import com.bruce.intellijplugin.generatesetter.utils.PsiToolUtils;
import com.intellij.psi.PsiParameter;
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
        NewMethodInfo deepInfo = null;
        for (PsiParameter parameter : parameters) {
//            todo for array class how to fix it.
            //the list object shall have set method so it can work.
            ParamInfo wrapInfo = PsiToolUtils.extractParamInfo(parameter.getType());
            if (wrapInfo.getCollectPackege() == null) {
                continue;
            } else {
                //check the collectType.
                String qualifyTypeName = wrapInfo.getCollectPackege();
                //first check with parameter collect type.
                //then check if class contains any get method.
                if (qualifyTypeName.equals("java.util.List")
                        || qualifyTypeName.equals("java.util.Set")
                        || qualifyTypeName.equals("java.util.Map")) {

                } else {
                    continue;
                }

            }
        }

        if (deepInfo != null) {

            //todo could use with other style rather than for i ect
            //todo maybe we can check with null.

            String addText = generateAddTextForCollectParam(deepInfo, returnParamInfo, returnVariableName, splitText);

            //todo must have param size >0 for check or i have to check everywhere
            if (returnParamInfo.getParams().size() > 0) {
                String addMethods = splitText + "public static " + returnParamInfo.getParams().get(0).getRealName() + "";
                insertDto.setAddMethods(addMethods);

            }
        }

        insertText.append(splitText + "return " + returnVariableName + ";");

        insertDto.setAddedText(insertText.toString());
        return insertDto;
    }

    private String generateAddTextForCollectParam(NewMethodInfo deepInfo, ParamInfo returnParamInfo, String returnVariableName, String splitText) {
        String methodName = "convertTo";
        if (returnParamInfo.getParams().size() > 0) {
            methodName = methodName + returnParamInfo.getParams().get(0).getRealName();
        }
        StringBuilder insertText = new StringBuilder();
        insertText.append(splitText + "for(int i=0;i<" + deepInfo.getParamName() + ".size();i++){");
        insertText.append(splitText + "\t" + returnVariableName + ".add(" + methodName + "(" + deepInfo.getParamName() + ".get(i)));");
        insertText.append(splitText + "}");
        return insertText.toString();
    }
}
