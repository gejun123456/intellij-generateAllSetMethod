/*
 *  Copyright (c) 2017-2025, bruce.ge.
 *    This program is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU General Public License
 *    as published by the Free Software Foundation; version 2 of
 *    the License.
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *    GNU General Public License for more details.
 *    You should have received a copy of the GNU General Public License
 *    along with this program;
 */

package com.bruce.intellijplugin.generatesetter.actions;

import com.bruce.intellijplugin.generatesetter.CommonConstants;
import com.bruce.intellijplugin.generatesetter.utils.PsiClassUtils;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiTypesUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.List;

/**
 * Action to generate an AI prompt describing a class's fields for random value generation.
 * Copies the prompt to clipboard so users can paste it into ChatGPT/Claude, etc.
 *
 * @author bruce ge
 */
public class GenerateAIRandomValueAction extends PsiElementBaseIntentionAction {

    @NotNull
    @Override
    public String getText() {
        return CommonConstants.GENERATE_AI_RANDOM_VALUE;
    }

    @Nls
    @NotNull
    @Override
    public String getFamilyName() {
        return getText();
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
        PsiClass psiClass = getLocalVariableClass(element);
        if (psiClass == null) {
            return false;
        }
        List<PsiMethod> setMethods = PsiClassUtils.extractSetMethods(psiClass);
        return !setMethods.isEmpty();
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {
        PsiClass psiClass = getLocalVariableClass(element);
        if (psiClass == null) {
            return;
        }

        List<PsiMethod> setMethods = PsiClassUtils.extractSetMethods(psiClass);
        if (setMethods.isEmpty()) {
            return;
        }

        String prompt = buildPrompt(psiClass, setMethods);

        // Copy to system clipboard
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(prompt), null);

        // Notify the user
        Notification notification = new Notification(
                "GenerateAllSetter",
                CommonConstants.GENERATE_AI_RANDOM_VALUE,
                "AI prompt copied to clipboard! Paste it into ChatGPT, Claude, or any AI assistant to generate random test values.",
                NotificationType.INFORMATION
        );
        Notifications.Bus.notify(notification);
    }

    /**
     * Build the AI prompt describing the class fields.
     */
    @NotNull
    private static String buildPrompt(PsiClass psiClass, List<PsiMethod> setMethods) {
        StringBuilder sb = new StringBuilder();
        String className = psiClass.getName();
        sb.append("I have a Java class called \"").append(className).append("\" with the following fields:\n\n");

        for (PsiMethod method : setMethods) {
            String fieldName = extractFieldName(method);
            if (fieldName == null) {
                continue;
            }
            PsiParameter[] parameters = method.getParameterList().getParameters();
            if (parameters.length == 0) {
                continue;
            }
            PsiType paramType = parameters[0].getType();
            String typeDisplay = simplifyTypeName(paramType);
            sb.append("- ").append(fieldName).append(": ").append(typeDisplay).append("\n");
        }

        sb.append("\nPlease generate realistic random test values for each field.");
        sb.append(" For string fields, generate sample realistic data (e.g., names, emails, addresses).");
        sb.append(" For numeric fields, generate reasonable numbers.");
        sb.append(" For date fields, generate realistic date values.");
        sb.append(" For nested objects, generate sample sub-objects.");
        sb.append(" For enum fields, pick a valid enum constant.");
        sb.append(" Return the result in a structured format such as JSON.\n");

        return sb.toString();
    }

    /**
     * Extract the field name from a setter method name.
     * e.g., "setName" -> "name", "withAge" -> "age"
     */
    private static String extractFieldName(PsiMethod method) {
        String methodName = method.getName();
        String prefix = null;
        if (methodName.startsWith("set")) {
            prefix = "set";
        } else if (methodName.startsWith("with")) {
            prefix = "with";
        }
        if (prefix == null || methodName.length() <= prefix.length()) {
            return null;
        }
        String fieldPart = methodName.substring(prefix.length());
        // Lowercase the first letter
        return Character.toLowerCase(fieldPart.charAt(0)) + fieldPart.substring(1);
    }

    /**
     * Simplify a PsiType to a user-friendly display string.
     * e.g., "java.lang.String" -> "String", "java.util.List<java.lang.String>" -> "List<String>"
     */
    @NotNull
    private static String simplifyTypeName(PsiType type) {
        String canonicalText = type.getCanonicalText();
        // Use presentable text which handles generics nicely (e.g., "List<String>")
        String presentable = type.getPresentableText();

        // For common Java types, try to shorten
        if (canonicalText.startsWith("java.lang.")) {
            return presentable;
        }
        if (canonicalText.startsWith("java.util.")) {
            return presentable;
        }
        if (canonicalText.startsWith("java.time.")) {
            return presentable;
        }
        if (canonicalText.startsWith("java.math.")) {
            return presentable;
        }
        // For custom types, presentable text is usually the simple class name
        return presentable;
    }

    /**
     * Get the PsiClass from the local variable the cursor is on.
     */
    private static PsiClass getLocalVariableClass(@NotNull PsiElement element) {
        PsiElement psiParent = PsiTreeUtil.getParentOfType(element, PsiLocalVariable.class);
        if (psiParent == null) {
            return null;
        }
        PsiLocalVariable psiLocal = (PsiLocalVariable) psiParent;
        if (!(psiLocal.getParent() instanceof PsiDeclarationStatement)) {
            return null;
        }
        return PsiTypesUtil.getPsiClass(psiLocal.getType());
    }
}
