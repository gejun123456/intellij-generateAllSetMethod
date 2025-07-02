package com.bruce.intellijplugin.generatesetter.postfix;

import com.bruce.intellijplugin.generatesetter.GenerateAllHandlerAdapter;
import com.bruce.intellijplugin.generatesetter.actions.GenerateAllSetterBase;
import com.bruce.intellijplugin.generatesetter.utils.PsiClassUtils;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;

import java.util.List;

public class AllSetterPostfixTemplate extends GenerateSetterPostfixTemplateBase {
    public AllSetterPostfixTemplate() {
        super("allset", "user.allset → user.setName(\"\"); ...",
                new GenerateAllSetterBase(new GenerateAllHandlerAdapter() {
                    @Override public boolean shouldAddDefaultValue() { return true; }
                }) {
                    @Override public String getText() { return ""; }
                });
    }

    @Override
    protected List<PsiMethod> getMethods(PsiClass psiClass) {
        return PsiClassUtils.extractSetMethods(psiClass);
    }
}
