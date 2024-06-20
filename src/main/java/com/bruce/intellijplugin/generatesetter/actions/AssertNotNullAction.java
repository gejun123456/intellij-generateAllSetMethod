package com.bruce.intellijplugin.generatesetter.actions;

import com.bruce.intellijplugin.generatesetter.CommonConstants;
import com.bruce.intellijplugin.generatesetter.TestEngine;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class AssertNotNullAction extends AssertAllGetterAction {
    // imports to add when generating asserts.
    private static final Map<TestEngine, List<String>> engineImports = ImmutableMap.<TestEngine, List<String>>builder()
            .put(TestEngine.JUNIT4, ImmutableList.of("static org.junit.Assert.assertNotNull"))
            .put(TestEngine.JUNIT5, ImmutableList.of("static org.junit.jupiter.api.Assertions.assertNotNull"))
            .put(TestEngine.TESTNG, ImmutableList.of("static org.testng.Assert.assertNotNull"))
            .put(TestEngine.ASSERTJ, ImmutableList.of("static org.assertj.core.api.Assertions.assertThat"))
            .put(TestEngine.ASSERT, ImmutableList.of("java.util.Objects"))
            .put(TestEngine.HAMCREST, ImmutableList.of("static org.hamcrest.MatcherAssert.assertThat", "org.hamcrest.Matchers.*"))
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
            newImportList.addAll(engineImports.get(currentFileTestEngine));
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
            case HAMCREST:
                return splitText + "assertThat( " + generateName + ", notNullValue());";
            default:
                throw new Error("Unknown case: " + currentFileTestEngine);
        }
    }
}