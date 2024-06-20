package com.bruce.intellijplugin.generatesetter;

public enum TestEngine {
    ASSERT("Plain java assert", "java.util", "Objects"),
    JUNIT4("JUnit 4", "org.junit", "Assert"),
    JUNIT5("JUnit 5", "org.junit.jupiter.api", "Assertions"),
    TESTNG("TestNG", "org.testng.asserts", "Assertion"),
    ASSERTJ("AssertJ", "org.assertj.core.api", "Assertions"),
    HAMCREST("Hamcrest", "org.hamcrest.core", "MatcherAssert");

    private final String formattedName;
    private final String assertionsPackage;
    private final String assertionsClassName;

    TestEngine(String formattedName, String assertionsPackage, String assertionsClassName) {
        this.formattedName = formattedName;
        this.assertionsPackage = assertionsPackage;
        this.assertionsClassName = assertionsClassName;
    }

    public String getFormattedName() {
        return formattedName;
    }

    public String getAssertionsPackage() {
        return assertionsPackage;
    }

    public String getAssertionsClassName() {
        return assertionsClassName;
    }
}
