package com.bruce.intellijplugin.generatesetter.template;

import com.bruce.intellijplugin.generatesetter.TestEngine;

public class PreferredTestEngineProvider {

    public static TestEngine testEngine = TestEngine.ASSERTJ;

    public static TestEngine getTestEngine() {
        return testEngine;
    }

    public static void setTestEngine(TestEngine testEngine) {
        PreferredTestEngineProvider.testEngine = testEngine;
    }
}
