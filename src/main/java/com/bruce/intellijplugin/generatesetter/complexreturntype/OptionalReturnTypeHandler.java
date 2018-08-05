package com.bruce.intellijplugin.generatesetter.complexreturntype;

import com.bruce.intellijplugin.generatesetter.Parameters;
import com.intellij.psi.PsiParameter;
import org.jetbrains.annotations.NotNull;

/**
 * @Author bruce.ge
 * @Date 2017/1/28
 * @Description
 */
public class OptionalReturnTypeHandler implements ComplexReturnTypeHandler {


    @NotNull
    @Override
    public InsertDto handle(Parameters returnParamInfo, String splitText, PsiParameter[] parameters, boolean hasGuava) {
        return null;
    }
}
