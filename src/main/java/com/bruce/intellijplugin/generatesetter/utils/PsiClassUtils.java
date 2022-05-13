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

package com.bruce.intellijplugin.generatesetter.utils;

import com.bruce.intellijplugin.generatesetter.actions.GenerateAllSetterAction;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Author bruce.ge
 * @Date 2017/1/30
 * @Description
 */
public class PsiClassUtils {
    public static boolean isNotSystemClass(PsiClass psiClass) {
        if (psiClass == null) {
            return false;
        }
        String qualifiedName = psiClass.getQualifiedName();
        Set<String> okJavaSet = new HashSet<>();
        okJavaSet.add("java.util.Map.Entry");
        if (okJavaSet.contains(qualifiedName)) {
            return true;
        }
        if (qualifiedName == null || qualifiedName.startsWith("java.")) {
            return false;
        }
        return true;
    }

    public static boolean isValidSetMethod(PsiMethod m) {
        return m.hasModifierProperty("public") &&
                !m.hasModifierProperty("static") &&
                (m.getName().startsWith("set") || m.getName().startsWith("with"));
    }

    public static boolean isValidGetMethod(PsiMethod m) {
        return m.hasModifierProperty("public") && !m.hasModifierProperty("static") &&
                (m.getName().startsWith(GenerateAllSetterAction.GET) || m.getName().startsWith(GenerateAllSetterAction.IS));
    }

    public static void addSetMethodToList(PsiClass psiClass, List<PsiMethod> methodList) {
        PsiMethod[] methods = psiClass.getMethods();
        for (PsiMethod method : methods) {
            if (isValidSetMethod(method)) {
                methodList.add(method);
            }
        }
    }

    public static void addGettMethodToList(PsiClass psiClass, List<PsiMethod> methodList) {
        PsiMethod[] methods = psiClass.getMethods();
        for (PsiMethod method : methods) {
            if (isValidGetMethod(method)) {
                methodList.add(method);
            }
        }
    }

    @NotNull
    public static List<PsiMethod> extractSetMethods(PsiClass psiClass) {
        List<PsiMethod> methodList = new ArrayList<>();
        while (isNotSystemClass(psiClass)) {
            addSetMethodToList(psiClass, methodList);
            psiClass = psiClass.getSuperClass();
        }
        return methodList;
    }

    public static List<PsiMethod> extractGetMethod(PsiClass psiClass) {
        List<PsiMethod> methodList = new ArrayList<>();
        while (isNotSystemClass(psiClass)) {
            addGettMethodToList(psiClass, methodList);
            psiClass = psiClass.getSuperClass();
        }
        return methodList;
    }

    public static boolean checkClassHasValidSetMethod(PsiClass psiClass) {
        if (psiClass == null) {
            return false;
        }
        while (isNotSystemClass(psiClass)) {
            for (PsiMethod m : psiClass.getMethods()) {
                if (isValidSetMethod(m)) {
                    return true;
                }
            }
            psiClass = psiClass.getSuperClass();
        }
        return false;
    }


    public static boolean checkClasHasValidGetMethod(PsiClass psiClass) {
        if (psiClass == null) {
            return false;
        }
        while (isNotSystemClass(psiClass)) {
            for (PsiMethod m : psiClass.getMethods()) {
                if (isValidGetMethod(m)) {
                    return true;
                }
            }
            psiClass = psiClass.getSuperClass();
        }
        return false;
    }
}
