package com.bruce.intellijplugin.generatesetter.postfix;

import com.intellij.codeInsight.template.postfix.templates.PostfixTemplate;
import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateProvider;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class GenerateSetterPostfixTemplateProvider implements PostfixTemplateProvider {

    private final Set<PostfixTemplate> templates = new HashSet<>(Arrays.asList(
            new AllSetterPostfixTemplate(),
            new AllSetterNoDefaultPostfixTemplate(),
            new AllGetterPostfixTemplate(),
            new AllBuilderPostfixTemplate(),
            new AllAccessorPostfixTemplate()
    ));

    @NotNull
    @Override
    public Set<PostfixTemplate> getTemplates() {
        return templates;
    }

    @Override
    public boolean isTerminalSymbol(char currentChar) {
        return currentChar == '.';
    }

    @Override
    public void preExpand(@NotNull PsiFile file, @NotNull Editor editor) {}

    @Override
    public void afterExpand(@NotNull PsiFile file, @NotNull Editor editor) {}

    @NotNull
    @Override
    public PsiFile preCheck(@NotNull PsiFile copyFile, @NotNull Editor realEditor, int currentOffset) {
        return copyFile;
    }
}
