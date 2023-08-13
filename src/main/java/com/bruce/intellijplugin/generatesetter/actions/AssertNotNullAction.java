package com.bruce.intellijplugin.generatesetter.actions;

import com.bruce.intellijplugin.generatesetter.CommonConstants;
import com.google.common.collect.ImmutableMap;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.bruce.intellijplugin.generatesetter.actions.AssertAllGetterAction.TestEngine.ASSERT;
import static com.bruce.intellijplugin.generatesetter.actions.AssertAllGetterAction.TestEngine.ASSERTJ;
import static com.bruce.intellijplugin.generatesetter.actions.AssertAllGetterAction.TestEngine.JUNIT4;
import static com.bruce.intellijplugin.generatesetter.actions.AssertAllGetterAction.TestEngine.JUNIT5;
import static com.bruce.intellijplugin.generatesetter.actions.AssertAllGetterAction.TestEngine.TESTNG;

public class AssertNotNullAction extends AssertAllGetterAction {
    // imports to add when generating asserts.
    private static final Map<TestEngine, String> engineImports = ImmutableMap.<TestEngine, String>builder()
            .put(JUNIT4, "static org.junit.Assert.assertNotNull")
            .put(JUNIT5, "static org.junit.jupiter.api.Assertions.assertNotNull")
            .put(TESTNG, "static org.testng.Assert.assertNotNull")
            .put(ASSERTJ, "static org.assertj.core.api.Assertions.assertThat")
            .put(ASSERT, "java.util.Objects")
            .build();

    private final GenerateAllAssertsHandlerAdapter generateAllHandler;

    public AssertNotNullAction() {
        generateAllHandler = new GenerateAllAssertsHandlerAdapter(true);
        setGenerateAllHandler(generateAllHandler);
    }

    @NotNull
    @Override
    public String getText() {
        return CommonConstants.ASSERT_NOT_NULL;
    }

    @NotNull
    protected String generateStringForNoParam(String generateName,
                                              List<PsiMethod> methodList, String splitText,
                                              Set<String> newImportList, boolean hasGuava) {
        generateAllHandler.detectCurrentTestEngine(project, containingFile);

        TestEngine currentFileTestEngine = generateAllHandler.currentFileTestEngine;
        Set<TestEngine> currentFileImportedEngines = generateAllHandler.currentFileImportedEngines;

        if (!currentFileImportedEngines.contains(currentFileTestEngine)) {
            newImportList.add(engineImports.get(currentFileTestEngine));
        }

        switch (currentFileTestEngine) {
            case JUNIT4:
            case JUNIT5:
            case TESTNG:
                return splitText + "assertNotNull(" + generateName + ");";
            case ASSERTJ:
                return splitText + "assertThat(" + generateName + ").isNotNull();";
            case ASSERT:
                return splitText + "assert " + generateName + " != null";
            default:
                throw new Error("Unknown case: " + currentFileTestEngine);
        }
    }
}