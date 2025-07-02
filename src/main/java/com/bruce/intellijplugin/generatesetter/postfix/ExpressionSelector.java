package com.bruce.intellijplugin.generatesetter.postfix;

import com.bruce.intellijplugin.generatesetter.utils.PsiClassUtils;
import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateExpressionSelector;
import com.intellij.openapi.editor.Document;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiTypesUtil;
import com.intellij.util.Function;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

class ExpressionSelector implements PostfixTemplateExpressionSelector {

    private final Predicate<PsiClass> classFilter;

    ExpressionSelector(Predicate<PsiClass> classFilter) {
        this.classFilter = classFilter;
    }

    static ExpressionSelector forClassWithSetterLikeMethods() {
        return new ExpressionSelector(c -> PsiClassUtils.isNotSystemClass(c));
    }

    @Override
    public boolean hasExpression(@NotNull PsiElement context, @NotNull Document copyDocument, int newOffset) {
        return findExpression(context) != null;
    }

    @NotNull
    @Override
    public List<PsiElement> getExpressions(@NotNull PsiElement context, @NotNull Document document, int offset) {
        PsiExpression expr = findExpression(context);
        return expr == null ? Collections.emptyList() : Collections.singletonList(expr);
    }

    @NotNull
    @Override
    public Function<PsiElement, String> getRenderer() {
        return PsiElement::getText;
    }

    private PsiExpression findExpression(PsiElement context) {
        PsiExpression expr = PsiTreeUtil.getParentOfType(context, PsiExpression.class, false);
        while (expr != null) {
            PsiType type = expr.getType();
            if (type != null) {
                PsiClass psiClass = PsiTypesUtil.getPsiClass(type);
                if (psiClass != null && classFilter.test(psiClass)) {
                    return expr;
                }
            }
            expr = PsiTreeUtil.getParentOfType(expr, PsiExpression.class, true);
        }
        return null;
    }
}
