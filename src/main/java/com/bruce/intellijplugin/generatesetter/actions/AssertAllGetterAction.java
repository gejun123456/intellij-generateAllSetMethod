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

package com.bruce.intellijplugin.generatesetter.actions;

import com.bruce.intellijplugin.generatesetter.CommonConstants;
import com.bruce.intellijplugin.generatesetter.GenerateAllHandlerAdapter;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiImportList;
import com.intellij.psi.PsiImportStatement;
import com.intellij.psi.PsiJavaFile;
import org.jetbrains.annotations.NotNull;

/**
 * @author bruce ge
 */
public class AssertAllGetterAction extends GenerateAllSetterBase {
    enum TestEngine {ASSERT, JUNIT4, JUNIT5, TESTNG, ASSERTJ}

    private TestEngine currentFileTestEngine = TestEngine.ASSERT;

    public AssertAllGetterAction() {
        setGenerateAllHandler(new GenerateAllHandlerAdapter() {
            @Override
            public boolean isSetter() {
                return false;
            }

            @Override
            public String formatLine(String line) {
                switch (currentFileTestEngine) {
                    case JUNIT4:
                    case JUNIT5:
                        return "assertEquals( , " + line.substring(0, line.length() - 1) + ");";
                    case TESTNG:
                        return "assertEquals(" + line.substring(0, line.length() - 1) + ", );";
                    case ASSERTJ:
                        return "assertThat(" + line.substring(0, line.length() - 1) + ").isEqualTo();";
                    case ASSERT:
                        return "assert Objects.equals(, " + line.substring(0, line.length() - 1) + ");";
                    default:
                        throw new Error("Unknown case: " + currentFileTestEngine);
                }
            }
        });
    }


    @NotNull
    @Override
    public String getText() {
        return CommonConstants.ASSERT_ALL_PROPS;
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
        PsiFile containingFile = element.getContainingFile();
        if (containingFile == null) {
            return false;
        }
        VirtualFile virtualFile = containingFile.getVirtualFile();
        if (virtualFile == null) {
            return false;
        }

        ProjectRootManager instance = ProjectRootManager.getInstance(element.getProject());
        boolean inTestSourceContent = instance.getFileIndex().isInTestSourceContent(virtualFile);

        if (inTestSourceContent) {
            currentFileTestEngine = detectCurrentTestEngine(containingFile);
            return super.isAvailable(project, editor, element);
        }
        return false;
    }

    private static TestEngine detectCurrentTestEngine(PsiFile containingFile) {
        if (containingFile instanceof PsiJavaFile) {
            PsiJavaFile javaFile = (PsiJavaFile) containingFile;
            PsiImportList importList = javaFile.getImportList();

            if (importList != null) {
                PsiImportStatement[] importStatements = importList.getImportStatements();

                for (PsiImportStatement importStatement : importStatements) {
                    String qualifiedName = importStatement.getQualifiedName();
                    if (qualifiedName == null) {
                        continue;
                    }

                    if (qualifiedName.startsWith("org.junit.jupiter.api.")) {
                        return TestEngine.JUNIT5;
                    }
                    if (qualifiedName.startsWith("org.junit.")) {
                        return TestEngine.JUNIT4;
                    }
                    if (qualifiedName.startsWith("org.assertj.")) {
                        return TestEngine.ASSERTJ;
                    }
                    if (qualifiedName.startsWith("org.testng.")) {
                        return TestEngine.TESTNG;
                    }
                }
            }
        }

        return TestEngine.ASSERT;
    }
}
