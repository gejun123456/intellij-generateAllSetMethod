/*
 *  Copyright (c) 2017-2018, bruce.ge.
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

package com.bruce.intellijplugin.generatesetter.context;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import org.jetbrains.kotlin.psi.KtProperty;

/**
 * @author bruce ge
 */
public class KotlinContext {
    private KtProperty property;

    private PsiClass currentClass;

    public KtProperty getProperty() {
        return property;
    }

    public void setProperty(KtProperty property) {
        this.property = property;
    }

    public PsiClass getCurrentClass() {
        return currentClass;
    }

    public void setCurrentClass(PsiClass currentClass) {
        this.currentClass = currentClass;
    }
}
