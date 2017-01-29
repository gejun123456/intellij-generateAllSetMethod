package com.bruce.intellijplugin.generatesetter.complexreturntype;

import com.bruce.intellijplugin.generatesetter.ParamInfo;
import com.intellij.psi.PsiParameter;
import org.jetbrains.annotations.NotNull;

/**
 * @Author bruce.ge
 * @Date 2017/1/28
 * @Description
 */
public interface ComplexReturnTypeHandler {
    //the generate class for the usage.
    @NotNull
    InsertDto handle(ParamInfo returnParamInfo, String splitText, PsiParameter[] parameters);
}
