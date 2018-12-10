/*
 *  Copyright (c) 2017-2018, bruce.ge.
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

        PsiField[] allFields = currentElementContext.getCurrentClass().getAllFields();

        PsiDocumentManager instance = PsiDocumentManager.getInstance(project);
        Document document = instance.getDocument(element.getContainingFile());

        String splitText = PsiToolUtils.calculateSplitText(document, property.getTextRange().getStartOffset());

        StringBuilder builder = new StringBuilder();
        for (PsiField allField : allFields) {
            builder.append(splitText + propertyName + "." + allField.getName() + " = ");
        }

        document.insertString(property.getTextRange().getEndOffset(),
                builder.toString());

        PsiDocumentUtils.commitAndSaveDocument(instance, document);
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
