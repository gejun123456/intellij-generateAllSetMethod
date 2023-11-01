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

package generateAllSetter;

import com.bruce.intellijplugin.generatesetter.CommonConstants;
import com.bruce.intellijplugin.generatesetter.TestEngine;
import com.bruce.intellijplugin.generatesetter.template.PreferredTestEngineProvider;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.pom.java.LanguageLevel;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.PsiTestUtil;
import com.intellij.testFramework.fixtures.*;
import com.intellij.util.PathUtil;
import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.model.module.JpsModuleSourceRootType;

import java.io.File;

import static org.jetbrains.jps.model.java.JavaSourceRootType.TEST_SOURCE;

/**
 * @author bruce ge
 */
public class AssertAllGetterWithDefaultValueTest extends LightCodeInsightFixtureTestCase {
    @NotNull
    @Override
    protected LightProjectDescriptor getProjectDescriptor() {

        class MyDescriptor extends ProjectDescriptor {

            public MyDescriptor(@NotNull LanguageLevel languageLevel) {
                super(languageLevel);
            }

            public MyDescriptor(@NotNull LanguageLevel languageLevel, boolean withAnnotations) {
                super(languageLevel, withAnnotations);
            }

            @NotNull
            @Override
            protected JpsModuleSourceRootType<?> getSourceRootType() {
                return TEST_SOURCE;
            }

            @Override
            public boolean equals(Object obj) {
                // make every project descriptor unique to prevent dependency interference
                // https://plugins.jetbrains.com/docs/intellij/light-and-heavy-tests.html#lightprojectdescriptor
                return false;
            }
        }

        return new MyDescriptor(LanguageLevel.HIGHEST);
    }


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        addLibrary(org.assertj.core.api.Assertions.class);
        addLibrary(org.junit.jupiter.api.Assertions.class);
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Override
    protected String getTestDataPath() {
        return "src/test/resources/testData";
    }

    public void testGenerateAllSetterDefaultValue_AssertJ() {

        PreferredTestEngineProvider.setTestEngine(TestEngine.ASSERTJ);

        myFixture.configureByFile(getName() + "/before.java");

        IntentionAction singleIntention = myFixture.findSingleIntention(CommonConstants.ASSERT_ALL_PROPS);

        Assertions.assertThat(singleIntention).isNotNull();
        myFixture.launchAction(singleIntention);

        myFixture.checkResultByFile(getName() + "/after.java",true);
    }

    public void testGenerateAllSetterDefaultValue_Jupiter() {

        PreferredTestEngineProvider.setTestEngine(TestEngine.JUNIT5);

        myFixture.configureByFile(getName() + "/before.java");

        IntentionAction singleIntention = myFixture.findSingleIntention(CommonConstants.ASSERT_ALL_PROPS);

        Assertions.assertThat(singleIntention).isNotNull();
        myFixture.launchAction(singleIntention);

        myFixture.checkResultByFile(getName() + "/after.java",true);
    }

    public void testGenerateAllSetterDefaultValue_UsesExistingImportIfPreferredNotOnClasspath() {

        PreferredTestEngineProvider.setTestEngine(TestEngine.HAMCREST);

        myFixture.configureByFile(getName() + "/before.java");

        IntentionAction singleIntention = myFixture.findSingleIntention(CommonConstants.ASSERT_ALL_PROPS);

        Assertions.assertThat(singleIntention).isNotNull();
        myFixture.launchAction(singleIntention);

        myFixture.checkResultByFile(getName() + "/after.java",true);
    }

    public void testGenerateAllSetterDefaultValue_SwitchesBetweenEnginesCorrectly() {

        PreferredTestEngineProvider.setTestEngine(TestEngine.JUNIT5);

        myFixture.configureByFile(getName() + "/before.java");

        IntentionAction singleIntention = myFixture.findSingleIntention(CommonConstants.ASSERT_ALL_PROPS);

        Assertions.assertThat(singleIntention).isNotNull();
        myFixture.launchAction(singleIntention);


        PreferredTestEngineProvider.setTestEngine(TestEngine.ASSERTJ);

        singleIntention = myFixture.findSingleIntention(CommonConstants.ASSERT_ALL_PROPS);

        Assertions.assertThat(singleIntention).isNotNull();
        myFixture.launchAction(singleIntention);

        myFixture.checkResultByFile(getName() + "/after.java",true);


    }

    public void testGenerateAllSetterDefaultValue_IgnoresExistingImportWithPreferredEngine() {

        PreferredTestEngineProvider.setTestEngine(TestEngine.JUNIT5);

        myFixture.configureByFile(getName() + "/before.java");

        IntentionAction singleIntention = myFixture.findSingleIntention(CommonConstants.ASSERT_ALL_PROPS);
        Assertions.assertThat(singleIntention).isNotNull();
        myFixture.launchAction(singleIntention);

        myFixture.checkResultByFile(getName() + "/after.java",true);

    }

    private void addLibrary(Class<?> aClass) {
        String jarPathForClass = PathUtil.getJarPathForClass(aClass);
        File file = new File(jarPathForClass);
        String libName = file.getName();
        PsiTestUtil.addLibrary(myFixture.getProjectDisposable(), myModule, libName, file.getParent(), libName);
    }


}
