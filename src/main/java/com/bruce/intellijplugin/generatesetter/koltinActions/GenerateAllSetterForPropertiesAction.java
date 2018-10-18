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
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiTypesUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.psi.*;

import java.util.Objects;

/**
 * @author bruce ge
 */
public class GenerateAllSetterForPropertiesAction extends PsiElementBaseIntentionAction {
    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {
        KtProperty parentOfType = PsiTreeUtil.getParentOfType(element, KtProperty.class);


    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
        PsiFile containingFile =
                element.getContainingFile();
        if (containingFile == null) {
            return false;
        }

        if (!(containingFile instanceof KtFile)) {
            return false;
        }

        KtProperty parentOfType = PsiTreeUtil.getParentOfType(element, KtProperty.class);
        if (parentOfType == null) {
            return false;
        }

        KtExpression initializer = parentOfType.getInitializer();
        if (initializer == null) {
            return false;
        }

        if (initializer.getChildren().length == 0) {
            return false;
        }

        PsiElement child = initializer.getChildren()[0];
        if (!(child instanceof KtNameReferenceExpression)) {
            return false;
        }
        KtNameReferenceExpression referenceExpression = (KtNameReferenceExpression) child;

        String referenceName = referenceExpression.getText();


        KtFile file = (KtFile) containingFile;

        KtImportList importList = file.getImportList();

        String quatifiedName = referenceName;

        if (importList != null) {
            for (KtImportDirective anImport : importList.getImports()) {
                if (anImport.getImportedReference() != null && anImport.getText().endsWith("." + referenceName)) {
                    quatifiedName = anImport.getImportedReference().getText();
                    break;
                }
            }
        }

        PsiClass findedClass = findClassWithQuatifiedName(project, referenceName, quatifiedName);

        if (findedClass == null) {
            return false;
        }

        return true;

    }

    @Nullable
    private PsiClass findClassWithQuatifiedName(@NotNull Project project, String referenceName, String quatifiedName) {
        PsiClass[] classesByName = PsiShortNamesCache.getInstance(project).getClassesByName(referenceName, GlobalSearchScope.allScope(project));
        PsiClass findedClass = null;
        for (PsiClass psiClass : classesByName) {
            if (Objects.equals(psiClass.getQualifiedName(), quatifiedName)) {
                findedClass = psiClass;
                break;
            }
        }
        return findedClass;
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
