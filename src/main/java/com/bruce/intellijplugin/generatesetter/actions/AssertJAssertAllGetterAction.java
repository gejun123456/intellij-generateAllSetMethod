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
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

/**
 * @author bruce ge
 */
public class AssertJAssertAllGetterAction extends GenerateAllSetterBase {
    public AssertJAssertAllGetterAction() {
        super(new GenerateAllHandlerAdapter() {
            @Override
            public boolean isSetter() {
                return false;
            }

            @Override
            public String formatLine(String line) {
                return "assertThat(" + line.substring(0, line.length() - 1) + ").isEqualTo();";
            }
        });
    }


    @NotNull
    @Override
    public String getText() {
        return CommonConstants.ASSERTALLPROPS;
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
        boolean inTestSourceContent = ProjectRootManager.getInstance(element.getProject()).getFileIndex().isInTestSourceContent(element.getContainingFile().getVirtualFile());
        if (inTestSourceContent) {
            return super.isAvailable(project, editor, element);
        }
        return false;
    }
}
