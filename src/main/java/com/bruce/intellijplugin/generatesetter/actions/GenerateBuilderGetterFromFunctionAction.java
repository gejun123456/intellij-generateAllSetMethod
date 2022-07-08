package com.bruce.intellijplugin.generatesetter.actions;

import com.bruce.intellijplugin.generatesetter.CommonConstants;
import com.bruce.intellijplugin.generatesetter.GenerateAllHandlerAdapter;
import com.bruce.intellijplugin.generatesetter.GetInfo;
import com.bruce.intellijplugin.generatesetter.utils.PsiToolUtils;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiTypesUtil;
import com.intellij.util.IncorrectOperationException;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

public class GenerateBuilderGetterFromFunctionAction extends GenerateAllSetterBase {

    public GenerateBuilderGetterFromFunctionAction() {
        super(new GenerateAllHandlerAdapter(){
            @Override
            public boolean isFromMethod() {
                return true;
            }

            @Override
            public boolean isSetter() {
                return false;
            }

            @Override
            public boolean forBuilder() {
                return true;
            }
        });
    }


    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {
        PsiElement psiParent = PsiTreeUtil.getParentOfType(element, PsiLocalVariable.class, PsiMethod.class);
        if (psiParent instanceof PsiMethod) {
            PsiMethod method = (PsiMethod) psiParent;
            PsiDocumentManager psiDocumentManager = PsiDocumentManager.getInstance(project);
            Document document = psiDocumentManager.getDocument(element.getContainingFile());
            if (method.getReturnType() == null || method.getBody() == null || document == null) {
                return;
            }

            PsiClass psiClass = PsiTypesUtil.getPsiClass(method.getReturnType());
            if (psiClass == null || psiClass.getName() == null) {
                return;
            }

            String splitText = extractSplitText(method, document);
            GetInfo info = getGetInfo(method.getParameterList().getParameters());

            StringBuilder builder = new StringBuilder(splitText);
            String resultName = PsiToolUtils.lowerStart(psiClass.getName());
            builder.append(psiClass.getName()).append(" ").append(resultName)
                    .append(" = ").append(psiClass.getName()).append(".builder()");
            builder.append(generateBuilderChain(psiClass.getFields(), splitText, info));
            builder.append(splitText).append("\t").append("\t").append(".build();");
            builder.append(splitText).append("return ").append(resultName).append(";");

            WriteCommandAction.runWriteCommandAction(project, () -> {
                document.insertString(method.getBody().getTextOffset() + 1, builder.toString());
                PsiDocumentManager.getInstance(project).commitDocument(document);
            });
        }
    }

    private String generateBuilderChain(PsiField[] psiFields, String splitText, GetInfo info) {
        StringBuilder builder = new StringBuilder();
        for (PsiField psiField : psiFields) {
            builder.append(splitText).append("\t").append("\t");
            String fieldName = psiField.getName();
            PsiMethod s = info.getNameToMethodMap().get(StringUtils.lowerCase(fieldName));
            if (s != null) {
                String getMethodText = info.getParamName() + "." + s.getName() + "()";
                builder.append(".").append(fieldName).append("(").append(getMethodText).append(")");
            } else {
                builder.append(".").append(fieldName).append("()");
            }
        }
        return builder.toString();
    }

    @NotNull
    @Override
    public String getText() {
        return CommonConstants.BUILDER_CONVERTER_FROM_METHOD;
    }
}
