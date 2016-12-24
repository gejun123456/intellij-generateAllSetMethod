package com.bruce.intellijplugin.generatesetter;

import com.bruce.intellijplugin.generatesetter.utils.PsiDocumentUtils;
import com.bruce.intellijplugin.generatesetter.utils.PsiElementUtil;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTypesUtil;
import com.intellij.psi.util.PsiUtilCore;
import com.intellij.util.IncorrectOperationException;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bruce.ge on 2016/12/23.
 */
public class GenerateAllSetterAction extends PsiElementBaseIntentionAction {

    public static final String GENERATE_SETTER_METHOD = "generate all setter";

    private static Map<String, String> typeGeneratedMap = new HashMap<String, String>() {{
        put("boolean", "false");
        put("java.lang.Boolean", "false");
        put("int", "0");
        put("java.lang.Integer", "0");
        put("java.lang.String", "\"\"");
        put("java.math.BigDecimal", "new BigDecimal(\"0\")");
        put("java.lang.Long", "0");
        put("long", "0");
        put("short", "0");
        put("java.lang.Short", "0");
        put("java.util.Date", "new Date()");
        put("float", "0");
        put("java.lang.Float", "0");
        put("double", "0");
        put("java.lang.Double", "0");
        put("java.lang.Character", "\'\'");
        put("char", "\'\'");
    }};

    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {
        PsiElement parent = element.getParent();
        if (parent == null) {
            return;
        }
        PsiElement javaParent = parent.getParent();
        if (javaParent == null) {
            return;
        }

        if (!(javaParent instanceof PsiTypeElement)) {
            return;
        }

        PsiElement psiLocal = javaParent.getParent();
        if (!(psiLocal instanceof PsiLocalVariable)) {
            return;
        }


        PsiLocalVariable localVariable = (PsiLocalVariable) psiLocal;
        PsiElement parent1 = localVariable.getParent();
        if (!(parent1 instanceof PsiDeclarationStatement)) {
            return;
        }
        PsiClass psiClass = PsiTypesUtil.getPsiClass(localVariable.getType());
        String generateName = localVariable.getName();
        List<PsiMethod> methodList = new ArrayList<>();
        while (!psiClass.getQualifiedName().equals("java.lang.Object")) {
            addSetMethodToList(psiClass, methodList);
            psiClass = psiClass.getSuperClass();
            if (psiClass == null) {
                break;
            }
        }

        if (methodList.size() == 0) {
            return;
        }

        StringBuilder builder = new StringBuilder();
        PsiDocumentManager psiDocumentManager = PsiDocumentManager.getInstance(project);
        Document document = psiDocumentManager.getDocument(element.getContainingFile());
        int statementOffset = parent1.getTextOffset();
        String splitText = "";
        int cur = statementOffset;
        String text = document.getText(new TextRange(cur - 1, cur));
        while (text.equals(" ") || text.equals("\t")) {
            splitText = text + splitText;
            cur--;
            if (cur < 1) {
                break;
            }
            text = document.getText(new TextRange(cur - 1, cur));
        }
        splitText = "\n" + splitText;
        builder.append(splitText);
        for (PsiMethod method : methodList) {
            PsiParameter[] parameters = method.getParameterList().getParameters();
            builder.append(generateName + "." + method.getName() + "(");
            int u = parameters.length;
            int h = 0;
            for (PsiParameter parameter : parameters) {
                h++;
                String classType = parameter.getType().getCanonicalText();
                String ss = typeGeneratedMap.get(classType);
                if (ss != null) {
                    builder.append(ss);
                } else {
                    builder.append("new " + classType + "()");
                }
                if (h != u) {
                    builder.append(",");
                }
            }
            builder.append(");").append(splitText);
        }

        document.insertString(statementOffset + parent1.getText().length(), builder.toString());
        PsiDocumentUtils.commitAndSaveDocument(psiDocumentManager, document);
        return;
    }

    private void addSetMethodToList(PsiClass psiClass, List<PsiMethod> methodList) {
        PsiMethod[] methods = psiClass.getMethods();
        for (PsiMethod method : methods) {
            if (isValidSetMethod(method)) {
                methodList.add(method);
            }
        }
    }

    private String findNextNotNull(PsiTypeElement psiType, String defaultName) {
        PsiElement nextSibling = psiType.getNextSibling();
        while (nextSibling != null && StringUtils.isBlank(nextSibling.getText())) {
            nextSibling = nextSibling.getNextSibling();
        }
        if (nextSibling == null) {
            return defaultName;
        }
        return nextSibling.getText().trim();
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
        if (editor.isViewer()) {
            return false;
        }
        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
        if (psiFile == null) {
            return false;
        }
        PsiUtilCore.ensureValid(psiFile);
        if (!(psiFile instanceof PsiJavaFile)) {
            return false;
        }
        PsiClass containingClass = PsiElementUtil.getContainingClass(element);
        if (containingClass == null) {
            return false;
        }

        if (containingClass.isInterface()) {
            return false;
        }

        if (!(element instanceof PsiIdentifier)) {
            return false;
        }
        PsiElement parent = element.getParent();
        if (!(parent instanceof PsiJavaCodeReferenceElement)) {
            return false;
        }

        PsiJavaCodeReferenceElement javacode = (PsiJavaCodeReferenceElement) parent;
        if (javacode instanceof PsiReferenceExpression) {
            return false;
        }
        PsiElement javaCodeParent = javacode.getParent();
        if (!(javaCodeParent instanceof PsiTypeElement)) {
            return false;
        }

        PsiElement psiLocal = javaCodeParent.getParent();
        if (!(psiLocal instanceof PsiLocalVariable)) {
            return false;
        }

        if (!(psiLocal.getParent() instanceof PsiDeclarationStatement)) {
            return false;
        }
        PsiTypeElement psiType = (PsiTypeElement) javaCodeParent;
        PsiType type = psiType.getType();
        PsiClass psiClass = PsiTypesUtil.getPsiClass(type);
        if (psiClass == null) {
            return false;
        }
        while (!psiClass.getQualifiedName().equals("java.lang.Object")) {
            for (PsiMethod m : psiClass.getMethods()) {
                if (isValidSetMethod(m)) {
                    return true;
                }
            }
            psiClass = psiClass.getSuperClass();
            if (psiClass == null) {
                break;
            }
        }
        return false;
    }

    private boolean isValidSetMethod(PsiMethod m) {
        return m.hasModifierProperty("public") && !m.hasModifierProperty("static") && m.getName().startsWith("set");
    }

    @Nls
    @NotNull
    @Override
    public String getFamilyName() {
        return GENERATE_SETTER_METHOD;
    }

    @NotNull
    @Override
    public String getText() {
        return GENERATE_SETTER_METHOD;
    }
}
