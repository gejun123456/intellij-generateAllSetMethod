package com.bruce.intellijplugin.generatesetter.postfix;

import com.bruce.intellijplugin.generatesetter.CommonConstants;
import com.bruce.intellijplugin.generatesetter.utils.PsiClassUtils;
import com.bruce.intellijplugin.generatesetter.utils.PsiDocumentUtils;
import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateWithExpressionSelector;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiTypesUtil;
import org.jetbrains.annotations.NotNull;

import static com.bruce.intellijplugin.generatesetter.actions.GenerateAllSetterBase.STATIC;

public class AllBuilderPostfixTemplate extends PostfixTemplateWithExpressionSelector {
    public AllBuilderPostfixTemplate() {
        super(null, "allbuilder", "User.builder().name().build();",
                new ExpressionSelector(psiClass -> {
                    for (PsiMethod m : psiClass.getMethods()) {
                        if (m.getName().equals(CommonConstants.BUILDER_METHOD_NAME) && m.hasModifierProperty(STATIC))
                            return true;
                    }
                    return false;
                }), null);
    }

    @Override
    public void expandForChooseExpression(@NotNull PsiElement expression, @NotNull Editor editor) {
        Project project = expression.getProject();
        PsiDocumentManager psiDocumentManager = PsiDocumentManager.getInstance(project);
        Document document = psiDocumentManager.getDocument(expression.getContainingFile());
        if (document == null) return;

        PsiStatement statement = PsiTreeUtil.getParentOfType(expression, PsiStatement.class);
        if (statement == null) return;

        PsiClass psiClass = PsiTypesUtil.getPsiClass(((PsiExpression) expression).getType());
        if (psiClass == null) return;

        for (PsiMethod method : psiClass.getMethods()) {
            if (!method.getName().equals(CommonConstants.BUILDER_METHOD_NAME)) continue;
            PsiClass builderClass = PsiTypesUtil.getPsiClass(method.getReturnType());
            if (builderClass == null) continue;
            StringBuilder sb = new StringBuilder(psiClass.getQualifiedName() + ".builder()");
            for (PsiMethod m : builderClass.getMethods()) {
                if (!m.isConstructor() && !m.getName().equals("toString") && !m.getName().equals("build")) {
                    sb.append(".").append(m.getName()).append("()");
                }
            }
            sb.append(".build();");
            int insertOffset = statement.getTextOffset() + statement.getText().length();
            document.insertString(insertOffset, "\n" + sb);
            PsiDocumentUtils.commitAndSaveDocument(psiDocumentManager, document);
            break;
        }
    }
}
