/*
 *  Copyright (c) 2017-2019, bruce.ge.
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

package com.bruce.intellijplugin.generatesetter.koltinActions;

import com.bruce.intellijplugin.generatesetter.CommonConstants;
import com.bruce.intellijplugin.generatesetter.context.KotlinContext;
import com.bruce.intellijplugin.generatesetter.utils.PsiClassUtils;
import com.bruce.intellijplugin.generatesetter.utils.PsiDocumentUtils;
import com.bruce.intellijplugin.generatesetter.utils.PsiToolUtils;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.psi.*;

import java.util.Locale;

/**
 * @author bruce ge
 */
public class GenerateAllSetterForPropertiesAction extends PsiElementBaseIntentionAction {
    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {
        KotlinContext currentElementContext =
                KotlinUtils.getCurrentElementContext(element);

        KtProperty property = currentElementContext.getProperty();
        String propertyName = property.getName();

        PsiMethod[] methods = currentElementContext.getCurrentClass().getAllMethods();

        PsiDocumentManager instance = PsiDocumentManager.getInstance(project);
        Document document = instance.getDocument(element.getContainingFile());

        String splitText = PsiToolUtils.calculateSplitText(document, property.getTextRange().getStartOffset());

        StringBuilder builder = new StringBuilder();
        for (PsiMethod m : methods) {
            if(PsiClassUtils.isValidSetMethod(m)){
                String methodPropName = methodToProperty(m.getName());
                builder.append(splitText + propertyName + "." + methodPropName + " = ");
            }
        }

        document.insertString(property.getTextRange().getEndOffset(),
                builder.toString());

        PsiDocumentUtils.commitAndSaveDocument(instance, document);
    }

    public static String methodToProperty(String name) {
        if (name.startsWith("set")) {
            name = name.substring(3);
        } else {
//      throw new ReflectionException("Error parsing property name '" + name + "'.  Didn't start with 'is', 'get' or 'set'.");
            return "";
        }

        if (name.length() == 1 || (name.length() > 1 && !Character.isUpperCase(name.charAt(1)))) {
            name = name.substring(0, 1).toLowerCase(Locale.ENGLISH) + name.substring(1);
        }
        return name;
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
        KotlinContext currentElementContext =
                KotlinUtils.getCurrentElementContext(element);
        if (currentElementContext == null) {
            return false;
        }
        return true;

    }


    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
        return CommonConstants.GENERATE_SETTER_METHOD;
    }


    @NotNull
    @Override
    public String getText() {
        return CommonConstants.GENERATE_SETTER_METHOD;
    }
}
