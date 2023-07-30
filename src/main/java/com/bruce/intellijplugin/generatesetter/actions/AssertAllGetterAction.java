/*
 *  Copyright (c) 2017-2023, bruce.ge.
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
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiImportList;
import com.intellij.psi.PsiImportStatement;
import com.intellij.psi.PsiImportStaticStatement;
import com.intellij.psi.PsiJavaFile;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.bruce.intellijplugin.generatesetter.actions.AssertAllGetterAction.TestEngine.*;

/**
 * @author bruce ge
 */
public class AssertAllGetterAction extends GenerateAllSetterBase {
    enum TestEngine {ASSERT, JUNIT4, JUNIT5, TESTNG, ASSERTJ}

    private TestEngine currentFileTestEngine = TestEngine.ASSERT;
    private Set<TestEngine> currentFileAssertsImported = new HashSet<>();

    private static final Map<TestEngine, String> engineImports = new HashMap<>();
    // className -> engine
    private static final Map<String, TestEngine> enginePlainImportsReversed = new HashMap<>();
    // static className without static and method -> engine
    private static final Map<String, TestEngine> engineStaticImportsReversed = new HashMap<>();
    // engine -> static method like assertEquals
    private static final Map<TestEngine, String> engineStaticImportsMethod = new HashMap<>();

    static {
        engineImports.put(JUNIT4, "static org.junit.Assert.assertEquals");
        engineImports.put(JUNIT5, "static org.junit.jupiter.api.Assertions.assertEquals");
        engineImports.put(TESTNG, "static org.testng.Assert.assertEquals");
        engineImports.put(ASSERTJ, "static org.assertj.core.api.Assertions.assertThat");
        engineImports.put(ASSERT, "java.util.Objects");

        engineImports.forEach((a, b) -> {
            if (!b.startsWith("static ")) {
                enginePlainImportsReversed.put(b, a);
            } else {
                engineStaticImportsReversed.put(b
                        .substring(0, b.lastIndexOf("."))
                        .replace("static ", ""), a);
                engineStaticImportsMethod.put(a, b.substring(b.lastIndexOf(".")));
            }
        });
    }

    public AssertAllGetterAction() {
        setGenerateAllHandler(new GenerateAllAssertsHandlerAdapter(true));
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
            currentFileAssertsImported = new HashSet<>();
            currentFileTestEngine = detectCurrentTestEngine(containingFile);
            return super.isAvailable(project, editor, element);
        }
        return false;
    }

    private TestEngine detectCurrentTestEngine(PsiFile containingFile) {
        if (containingFile instanceof PsiJavaFile) {
            PsiJavaFile javaFile = (PsiJavaFile) containingFile;
            PsiImportList importList = javaFile.getImportList();

            if (importList != null) {
                PsiImportStaticStatement[] importStaticStatements = importList.getImportStaticStatements();
                for (PsiImportStaticStatement importStaticStatement : importStaticStatements) {

                    PsiClass psiClass = importStaticStatement.resolveTargetClass();
                    if (psiClass == null) {
                        continue;
                    }

                    String qualifiedName = psiClass.getQualifiedName();
                    TestEngine testEngine = engineStaticImportsReversed.get(qualifiedName);
                    if (testEngine != null) {
                        String referenceName = importStaticStatement.getReferenceName();
                        if (referenceName == null || referenceName.equals(engineStaticImportsMethod.get(testEngine))) {
                            currentFileAssertsImported.add(testEngine);
                        }
                    }
                }

                PsiImportStatement[] importStatements = importList.getImportStatements();

                for (PsiImportStatement importStatement : importStatements) {
                    String qualifiedName = importStatement.getQualifiedName();
                    if (qualifiedName == null) {
                        continue;
                    }

                    TestEngine testEngine = enginePlainImportsReversed.get(qualifiedName);
                    if (testEngine != null) {
                        currentFileAssertsImported.add(testEngine);
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

    class GenerateAllAssertsHandlerAdapter extends GenerateAllHandlerAdapter {
        private final boolean generateWithDefaultValues;

        public GenerateAllAssertsHandlerAdapter(boolean generateWithDefaultValues) {
            this.generateWithDefaultValues = generateWithDefaultValues;
        }

        @Override
        public boolean isSetter() {
            return false;
        }

        @Override
        public boolean shouldAddDefaultValue() {
            return generateWithDefaultValues;
        }

        @Override
        public boolean forAssertWithDefaultValues() {
            return generateWithDefaultValues;
        }

        @Override
        public String formatLine(String line) {
            String getter, value;

            if (forAssertWithDefaultValues()) {
                int index = line.indexOf("(");
                getter = line.substring(0, index + 1) + ")";
                value = line.substring(index + 1, line.length() - 2);
            } else {
                getter = line.substring(0, line.length() - 1);
                value = "";
            }

            switch (currentFileTestEngine) {
                case JUNIT4:
                case JUNIT5:
                    return "assertEquals(" + value + ", " + getter + ");";
                case TESTNG:
                    return "assertEquals(" + getter + ", " + value + ");";
                case ASSERTJ:
                    return "assertThat(" + getter + ").isEqualTo(" + value + ");";
                case ASSERT:
                    return "assert Objects.equals(" + value + ", " + getter + ");";
                default:
                    throw new Error("Unknown case: " + currentFileTestEngine);
            }
        }

        @Override
        public void appendImportList(Set<String> newImportList) {
            if (!currentFileAssertsImported.contains(currentFileTestEngine)) {
                newImportList.add(engineImports.get(currentFileTestEngine));
            }
        }
    }
}
