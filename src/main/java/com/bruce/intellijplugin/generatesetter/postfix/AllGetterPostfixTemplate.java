package com.bruce.intellijplugin.generatesetter.postfix;

import com.bruce.intellijplugin.generatesetter.GenerateAllHandlerAdapter;
import com.bruce.intellijplugin.generatesetter.actions.GenerateAllSetterBase;
import com.bruce.intellijplugin.generatesetter.utils.PsiClassUtils;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;

import java.util.List;

public class AllGetterPostfixTemplate extends GenerateSetterPostfixTemplateBase {
    public AllGetterPostfixTemplate() {
        super("allget", "user.allget → String name = user.getName(); ...",
                new GenerateAllSetterBase(new GenerateAllHandlerAdapter() {
                    @Override public boolean isSetter() { return false; }
                    @Override public boolean shouldAddDefaultValue() { return true; }
                }) {
                    @Override public String getText() { return ""; }
                });
    }

    @Override
    protected List<PsiMethod> getMethods(PsiClass psiClass) {
        return PsiClassUtils.extractGetMethod(psiClass);
    }
}
