package com.bruce.intellijplugin.generatesetter.groovyActions;

import com.bruce.intellijplugin.generatesetter.CommonConstants;
import com.bruce.intellijplugin.generatesetter.utils.PsiDocumentUtils;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiVariable;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiTypesUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * @author hcy
 * @since 2021/10/25 13:40
 */
public class GenerateAllSetterAction extends PsiElementBaseIntentionAction {

    private static Predicate<String> isGroovyFile = s -> s != null && s.endsWith(".groovy");


    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {
        PsiVariable psiVariable = PsiTreeUtil.getParentOfType(element, PsiVariable.class);
        if (psiVariable == null) {
            return;
        }
        PsiClass psiClass = PsiTypesUtil.getPsiClass(psiVariable.getType());
        if (psiClass == null) {
            return;
        }

        Document document = editor.getDocument();
        String fieldName = psiVariable.getName();
        List<PsiMethod> psiMethods = GroovyUtils.extractSetMethods(psiClass);
        if (psiMethods.isEmpty()) {
            return;
        }

        String splitText = GroovyUtils.getSplitText(document, element.getTextOffset());
        StringBuilder sb = GroovyUtils.generateNotParamSetFunctionString(fieldName, splitText, psiMethods);
        int nextLineNumber = document.getLineNumber(element.getTextOffset()) + 1;
        document.insertString(document.getLineStartOffset(nextLineNumber), sb);
        PsiDocumentUtils.commitAndSaveDocument(PsiDocumentManager.getInstance(project), document);
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
        String fileName = GroovyUtils.getDocumentFileName(element);
        if (!isGroovyFile.test(fileName)) {
            return false;
        }
        List<PsiMethod> psiMethods = Optional.of(element)
                .map(GenerateAllSetterAction::getVariableContainingClass)
                .map(GroovyUtils::extractSetMethods)
                .orElse(Collections.emptyList());
        return !psiMethods.isEmpty();
    }

    @Nls
    @NotNull
    @Override
    public String getFamilyName() {
        return CommonConstants.GENERATE_SETTER_METHOD_NO_DEAULT_VALUE;
    }

    @Override
    public @NotNull String getText() {
        return CommonConstants.GENERATE_SETTER_METHOD_NO_DEAULT_VALUE;
    }

    //获取当前psiVariable对应的psiClass
    @Nullable
    private static PsiClass getVariableContainingClass(@NotNull PsiElement element) {
        PsiVariable psiVariable = PsiTreeUtil.getParentOfType(element, PsiVariable.class);
        if (psiVariable == null) {
            return null;
        }
        return PsiTypesUtil.getPsiClass(psiVariable.getType());
    }

}
