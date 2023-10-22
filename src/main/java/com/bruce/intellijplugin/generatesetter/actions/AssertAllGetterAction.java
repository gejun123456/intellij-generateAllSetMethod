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
import com.bruce.intellijplugin.generatesetter.TestEngine;
import com.bruce.intellijplugin.generatesetter.template.GenerateSetterService;
import com.bruce.intellijplugin.generatesetter.template.GenerateSetterState;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiImportList;
import com.intellij.psi.PsiImportStatement;
import com.intellij.psi.PsiImportStaticStatement;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.impl.compiled.ClsClassImpl;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.bruce.intellijplugin.generatesetter.TestEngine.*;

/**
 * @author bruce ge
 */
public class AssertAllGetterAction extends GenerateAllSetterBase {

    // imports to add when generating asserts.
    private static final Map<TestEngine, List<String>> engineImports = ImmutableMap.<TestEngine, List<String>>builder()
            .put(JUNIT4, ImmutableList.of("static org.junit.Assert.assertEquals"))
            .put(JUNIT5, ImmutableList.of("static org.junit.jupiter.api.Assertions.assertEquals"))
            .put(TESTNG, ImmutableList.of("static org.testng.Assert.assertEquals"))
            .put(ASSERTJ, ImmutableList.of("static org.assertj.core.api.Assertions.assertThat"))
            .put(ASSERT, ImmutableList.of("java.util.Objects"))
            .put(HAMCREST, ImmutableList.of("static org.hamcrest.MatcherAssert.assertThat", "org.hamcrest.Matchers.*"))
            .build();

    // className like 'java.util.Objects' -> engine (only java.util.Objects)
    private static final Map<String, TestEngine> enginePlainImportsReversed = ImmutableMap.<String, TestEngine>builder()
            .put("java.util.Objects", ASSERT)
            .build();

    // static className -> engine
    private static final Map<String, TestEngine> engineStaticImportsReversed = ImmutableMap.<String, TestEngine>builder()
            .put("org.junit.Assert", JUNIT4)
            .put("org.junit.jupiter.api.Assertions", JUNIT5)
            .put("org.testng.Assert", TESTNG)
            .put("org.assertj.core.api.Assertions", ASSERTJ)
            .put("org.hamcrest.MatcherAssert", HAMCREST)
            .build();

    // engine -> assert static method
    private static final Map<TestEngine, String> engineStaticImportsMethod = ImmutableMap.<TestEngine, String>builder()
            .put(JUNIT4, "assertEquals")
            .put(JUNIT5, "assertEquals")
            .put(TESTNG, "assertEquals")
            .put(ASSERTJ, "assertThat")
            .put(HAMCREST, "assertThat")
            .build();

    protected Project project;
    protected PsiFile containingFile;

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
            this.project = project;
            this.containingFile = containingFile;
            return super.isAvailable(project, editor, element);
        }
        return false;
    }


    class GenerateAllAssertsHandlerAdapter extends GenerateAllHandlerAdapter {
        private final boolean generateWithDefaultValues;

        protected final Set<TestEngine> currentFileImportedEngines = new HashSet<>();
        protected TestEngine currentFileTestEngine = TestEngine.ASSERT;

        public GenerateAllAssertsHandlerAdapter(boolean generateWithDefaultValues) {
            this.generateWithDefaultValues = generateWithDefaultValues;
        }

        public void detectCurrentTestEngine(Project project, PsiFile containingFile) {
            currentFileTestEngine = detectCurrentTestEngineInternal(project, containingFile);
        }

        private TestEngine detectCurrentTestEngineInternal(Project project, PsiFile containingFile) {

            if (containingFile instanceof PsiJavaFile) {
                PsiJavaFile javaFile = (PsiJavaFile) containingFile;
                PsiImportList importList = javaFile.getImportList();


                // prefer TestEngine from settings if it is in classpath
                ProjectFileIndex index = ProjectFileIndex.getInstance(project);
                Module module = index.getModuleForFile(containingFile.getVirtualFile());
                if (module != null) {
                    GenerateSetterState state = GenerateSetterService.getInstance().getState();
                    TestEngine preferredTestEngine = state.getPreferredTestEngine();

                    String assertionsClassName = preferredTestEngine.getAssertionsClassName();

                    GlobalSearchScope searchScope = GlobalSearchScope.moduleRuntimeScope(module, true);
                    PsiClass[] projectAssertionClasses = PsiShortNamesCache.getInstance(project)
                            .getClassesByName(assertionsClassName, searchScope);

                    for (PsiClass psiClass : projectAssertionClasses) {
                        String psiClassQualifiedName = psiClass.getQualifiedName();
                        if (String.format("%s.%s", preferredTestEngine.getAssertionsPackage(), preferredTestEngine.getAssertionsClassName()).equals(psiClassQualifiedName)) {
                            detectImportedEngines(importList);
                            return preferredTestEngine;
                        }
                    }
                }


                if (importList != null) {
                    detectImportedEngines(importList);

                    PsiImportStatement[] importStatements = importList.getImportStatements();

                    for (PsiImportStatement importStatement : importStatements) {
                        String qualifiedName = importStatement.getQualifiedName();
                        if (qualifiedName == null) {
                            continue;
                        }

                        if (qualifiedName.startsWith("org.junit.jupiter.api.")) {
                            return JUNIT5;
                        }
                        if (qualifiedName.startsWith("org.junit.")) {
                            return JUNIT4;
                        }
                        if (qualifiedName.startsWith("org.assertj.")) {
                            return ASSERTJ;
                        }
                        if (qualifiedName.startsWith("org.testng.")) {
                            return TESTNG;
                        }
                        if (qualifiedName.startsWith("org.hamcrest.")) {
                            return HAMCREST;
                        }
                    }
                }
            }

            return ASSERT;
        }

        private void detectImportedEngines(PsiImportList importList) {
            currentFileImportedEngines.clear();

            if (importList == null) {
                return;
            }

            PsiImportStaticStatement[] importStaticStatements = importList.getImportStaticStatements();

            for (PsiImportStaticStatement importStaticStatement : importStaticStatements) {
                PsiClass psiClass = importStaticStatement.resolveTargetClass();
                if (psiClass == null) {
                    continue;
                }

                String qualifiedName = psiClass.getQualifiedName();
                TestEngine testEngine = engineStaticImportsReversed.get(qualifiedName);
                if (testEngine != null) {
                    String referenceName = importStaticStatement.getReferenceName(); // like assertEquals
                    if (referenceName == null || referenceName.equals(engineStaticImportsMethod.get(testEngine))) {
                        currentFileImportedEngines.add(testEngine);
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
                    currentFileImportedEngines.add(testEngine);
                }
            }
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

            detectCurrentTestEngine(project, containingFile);

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
                case HAMCREST:
                    return "assertThat(" + value + ", equalTo(" + getter + ");";
                default:
                    throw new Error("Unknown case: " + currentFileTestEngine);
            }
        }

        @Override
        public void appendImportList(Set<String> newImportList) {
            if (!currentFileImportedEngines.contains(currentFileTestEngine)) {
                newImportList.addAll(engineImports.get(currentFileTestEngine));
            }
        }
    }
}
