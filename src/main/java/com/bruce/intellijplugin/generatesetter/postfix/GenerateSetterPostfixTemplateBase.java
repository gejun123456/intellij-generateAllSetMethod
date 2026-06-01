package com.bruce.intellijplugin.generatesetter.postfix;

import com.bruce.intellijplugin.generatesetter.actions.GenerateAllSetterBase;
import com.bruce.intellijplugin.generatesetter.utils.PsiClassUtils;
import com.bruce.intellijplugin.generatesetter.utils.PsiDocumentUtils;
import com.bruce.intellijplugin.generatesetter.utils.PsiToolUtils;
import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateWithExpressionSelector;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiTypesUtil;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

abstract class GenerateSetterPostfixTemplateBase extends PostfixTemplateWithExpressionSelector {

    private final GenerateAllSetterBase delegate;

    GenerateSetterPostfixTemplateBase(String name, String example, GenerateAllSetterBase delegate) {
        super(null, name, example, ExpressionSelector.forClassWithSetterLikeMethods(), null);
        this.delegate = delegate;
    }

    @Override
    public void expandForChooseExpression(@NotNull PsiElement expression, @NotNull Editor editor) {
        Project project = expression.getProject();
        PsiDocumentManager psiDocumentManager = PsiDocumentManager.getInstance(project);
        PsiFile containingFile = expression.getContainingFile();
        if (!(containingFile instanceof PsiJavaFile)) return;
        PsiJavaFile javaFile = (PsiJavaFile) containingFile;
        Document document = psiDocumentManager.getDocument(javaFile);
        if (document == null) return;

        PsiStatement statement = PsiTreeUtil.getParentOfType(expression, PsiStatement.class);
        if (statement == null) return;

        PsiType type = ((PsiExpression) expression).getType();
        PsiClass psiClass = PsiTypesUtil.getPsiClass(type);
        if (psiClass == null) return;

        List<PsiMethod> methodList = getMethods(psiClass);
        if (methodList.isEmpty()) return;

        String varName = expression.getText();
        String splitText = PsiToolUtils.calculateSplitText(document, statement.getTextOffset());
        Set<String> importList = new HashSet<>();
        boolean hasGuava = PsiToolUtils.checkGuavaExist(project, expression);

        String insertText = delegate.generateStringForNoParam(varName, methodList, splitText, importList, hasGuava);
        if (insertText.startsWith(splitText)) {
            insertText = insertText.substring(splitText.length());
        }

        int statementStart = statement.getTextOffset();
        int statementEnd = statementStart + statement.getText().length();
        document.replaceString(statementStart, statementEnd, insertText);
        PsiDocumentUtils.commitAndSaveDocument(psiDocumentManager, document);
        PsiToolUtils.addImportToFile(psiDocumentManager, javaFile, document, importList);
    }

    protected abstract List<PsiMethod> getMethods(PsiClass psiClass);
}
