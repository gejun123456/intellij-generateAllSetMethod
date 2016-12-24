package com.bruce.intellijplugin.generatesetter.utils;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassOwner;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;

/**
 * Created by bruce.ge on 2016/12/8.
 */
public class PsiElementUtil {
    public static PsiClass getContainingClass(PsiElement psiElement) {
        PsiClass psiClass = PsiTreeUtil.getParentOfType(psiElement, PsiClass.class, false);
        if (psiClass == null) {
            PsiFile containingFile = psiElement.getContainingFile();
            if (containingFile instanceof PsiClassOwner) {
                PsiClass[] classes = ((PsiClassOwner) containingFile).getClasses();
                if (classes.length == 1) {
                    return classes[0];
                }
            }
        }
        return psiClass;
    }
}
