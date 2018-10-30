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

package com.bruce.intellijplugin.generatesetter.actions;

import com.bruce.intellijplugin.generatesetter.CommonConstants;
import com.bruce.intellijplugin.generatesetter.GetInfo;
import com.bruce.intellijplugin.generatesetter.Parameters;
import com.bruce.intellijplugin.generatesetter.complexreturntype.*;
import com.bruce.intellijplugin.generatesetter.utils.PsiClassUtils;
import com.bruce.intellijplugin.generatesetter.utils.PsiDocumentUtils;
import com.bruce.intellijplugin.generatesetter.utils.PsiToolUtils;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiTypesUtil;
import com.intellij.util.IncorrectOperationException;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Created by bruce.ge on 2016/12/23.
 */
public class GenerateAllSetterAction extends PsiElementBaseIntentionAction {

    public static final String IS = "is";
    public static final String GET = "get";

    private static Map<String, String> typeGeneratedMap = new HashMap<String, String>() {
        {
            put("boolean", "false");
            put("java.lang.Boolean", "false");
            put("int", "0");
            put("byte", "(byte)0");
            put("java.lang.Byte", "(byte)0");
            put("java.lang.Integer", "0");
            put("java.lang.String", "\"\"");
            put("java.math.BigDecimal", "new BigDecimal(\"0\")");
            put("java.lang.Long", "0L");
            put("long", "0L");
            put("short", "(short)0");
            put("java.lang.Short", "(short)0");
            put("java.util.Date", "new Date()");
            put("float", "0.0F");
            put("java.lang.Float", "0.0F");
            put("double", "0.0D");
            put("java.lang.Double", "0.0D");
            put("java.lang.Character", "\'\'");
            put("char", "\'\'");
            put("java.time.LocalDateTime", "LocalDateTime.now()");
            put("java.time.LocalDate", "LocalDate.now()");

        }
    };

    private static Map<String, ComplexReturnTypeHandler> handlerMap = new HashMap<String, ComplexReturnTypeHandler>() {
        {
            put("java.util.List", new ListReturnTypeHandler());
            put("java.util.Set", new SetReturnTypeHandler());
            put("java.util.Map", new MapReturnTypeHandler());
        }
    };

    private static Map<String, String> typeGeneratedImport = new HashMap<String, String>() {
        {
            put("java.math.BigDecimal", "java.math.BigDecimal");
            put("java.util.Date", "java.util.Date");
            put("java.time.LocalDateTime", "java.time.LocalDateTime");
            put("java.time.LocalDate", "java.time.LocalDate");
        }
    };

    private static Map<String, String> guavaTypeMaps = new HashMap<String, String>() {
        {
            put("List", "Lists.newArrayList()");
            put("Map", "Maps.newHashMap()");
            put("Set", "Sets.newHashSet()");
        }
    };

    private static Map<String, String> guavaImportMap = new HashMap<String, String>() {
        {
            put("List", "com.google.common.collect.Lists");
            put("Map", "com.google.common.collect.Maps");
            put("Set", "com.google.common.collect.Sets");
        }
    };

    private static Map<String, String> defaultCollections = new HashMap<String, String>() {
        {
            put("List", "ArrayList");
            put("Map", "HashMap");
            put("Set", "HashSet");
        }
    };

    private static Map<String, String> defaultImportMap = new HashMap<String, String>() {
        {
            put("List", "java.util.ArrayList");
            put("Map", "java.util.HashMap");
            put("Set", "java.util.HashSet");
        }
    };

    private static Map<String, String> defaultPacakgeValues = new HashMap<String, String>() {
        {
            put("java.sql.Date", "new Date(new java.util.Date().getTime())");
            put("java.sql.Timestamp",
                    "new Timestamp(new java.util.Date().getTime())");
        }
    };

    @Override
    public void invoke(@NotNull Project project, Editor editor,
                       @NotNull PsiElement element) throws IncorrectOperationException {
        PsiElement psiParent = PsiTreeUtil.getParentOfType(element,
                PsiLocalVariable.class, PsiMethod.class);
        if (psiParent == null) {
            return;
        }
        if (psiParent instanceof PsiLocalVariable) {
            PsiLocalVariable psiLocal = (PsiLocalVariable) psiParent;
            if (!(psiLocal.getParent() instanceof PsiDeclarationStatement)) {
                return;
            }
            handleWithLocalVariable(psiLocal, project, psiLocal);

        } else if (psiParent instanceof PsiMethod) {
            PsiMethod method = (PsiMethod) psiParent;
            if (method.getReturnType() == null) {
                return;
            }

            handleWithMethod(method, project, method);
        }
    }

    private void handleWithMethod(PsiMethod method, Project project,
                                  PsiElement element) {
        PsiDocumentManager psiDocumentManager = PsiDocumentManager
                .getInstance(project);
        Document document = psiDocumentManager
                .getDocument(element.getContainingFile());
        String splitText = extractSplitText(method, document);
        Parameters returnTypeInfo = PsiToolUtils
                .extractParamInfo(method.getReturnType());
        InsertDto dto = null;
        boolean hasGuava = PsiToolUtils.checkGuavaExist(project, element);
        if (returnTypeInfo.getCollectPackege() != null
                && handlerMap.containsKey(returnTypeInfo.getCollectPackege())) {
            //
            dto = handlerMap.get(returnTypeInfo.getCollectPackege()).handle(
                    returnTypeInfo, splitText,
                    method.getParameterList().getParameters(), hasGuava);
        } else {
            PsiClass returnTypeClass = PsiTypesUtil
                    .getPsiClass(method.getReturnType());
            dto = getBaseInsertDto(splitText, hasGuava,
                    method.getParameterList().getParameters(), returnTypeClass);
        }
        if (dto.getAddedText() != null) {
            document.insertString(method.getBody().getTextOffset() + 1,
                    dto.getAddedText());
        }
        PsiDocumentUtils.commitAndSaveDocument(psiDocumentManager, document);

        if (dto.getImportList() != null) {
            PsiToolUtils.addImportToFile(psiDocumentManager,
                    (PsiJavaFile) element.getContainingFile(), document,
                    dto.getImportList());
        }
    }

    @NotNull
    private static InsertDto getBaseInsertDto(String splitText,
                                              boolean hasGuava, PsiParameter[] parameters1, PsiClass psiClass) {
        InsertDto dto = new InsertDto();
        PsiParameter[] parameters = parameters1;
        List<PsiMethod> methods = PsiClassUtils.extractSetMethods(psiClass);
        Set<String> importList = Sets.newHashSet();
        String generateName = PsiToolUtils.lowerStart(psiClass.getName());
        GetInfo info = null;
        if (parameters.length > 0) {
            for (PsiParameter parameter : parameters) {
                PsiType type = parameter.getType();
                PsiClass parameterClass = PsiTypesUtil.getPsiClass(type);
                if (parameterClass == null || parameterClass.getQualifiedName()
                        .startsWith("java.")) {
                    continue;
                } else {
                    List<PsiMethod> getMethods = PsiClassUtils
                            .extractGetMethod(parameterClass);
                    // TODO: 2017/1/20 may be i can extract get memthod from all
                    // parameter
                    if (getMethods.size() > 0) {
                        info = buildInfo(parameter, getMethods);
                        break;
                    }
                }
            }
        }
        // TODO: 2017/8/2 what if two class has the same name
        String insertText = splitText + psiClass.getName() + " " + generateName
                + "= new " + psiClass.getName() + "();";
        if (info == null) {
            insertText += generateStringForNoParam(generateName, methods,
                    splitText, importList, hasGuava);
        } else {
            insertText += generateStringForParam(generateName, methods,
                    splitText, importList, hasGuava, info);
        }
        insertText += "return " + generateName + ";";
        dto.setAddedText(insertText);
        dto.setImportList(importList);
        return dto;
    }

    private static String generateStringForParam(String generateName,
                                                 List<PsiMethod> methodList, String splitText,
                                                 Set<String> newImportList, boolean hasGuava, GetInfo info) {
        StringBuilder builder = new StringBuilder();
        builder.append(splitText);
        for (PsiMethod method : methodList) {
            if (method.getName().startsWith("set")) {
                String fieldToLower = method.getName().substring(3)
                        .toLowerCase();
                PsiMethod s = info.getNameToMethodMap().get(fieldToLower);
                if (s != null) {
                    // TODO: 2017/8/2 check the get method return type and set
                    // method param type.
                    if (method.getParameterList().getParameters().length == 1) {
                        PsiParameter psiParameter = method.getParameterList()
                                .getParameters()[0];
                        PsiType type = psiParameter.getType();
                        PsiType returnType = s.getReturnType();
                        String setTypeText = type.getCanonicalText();
                        String getTypeText = returnType.getCanonicalText();
                        String getMethodText = info.getParamName() + "."
                                + s.getName() + "()";
                        String startText = generateName + "." + method.getName()
                                + "(";
                        builder.append(generateSetterString(setTypeText,
                                getTypeText, getMethodText, startText));
                    }
                } else {
                    generateDefaultForOneMethod(generateName, newImportList,
                            hasGuava, builder, method);
                }
            }
            builder.append(splitText);
        }
        return builder.toString();
    }

    private static String generateSetterString(String setTypeText,
                                               String getTypeText, String getMethodText, String startText) {
        if (setTypeText.equals(getTypeText)) {
            return startText + getMethodText + ");";
        } else {
            if (setTypeText.equals("java.lang.String")) {
                return startText + "String.valueOf(" + getMethodText + "));";
            } else if (setTypeText.equals("java.util.Date")
                    && checkMethodIsLong(getTypeText)) {
                return startText + "new Date(" + getMethodText + "));";
            } else if (checkMethodIsLong(setTypeText)
                    && getTypeText.equals("java.util.Date")) {
                return startText + getMethodText + ".getTime());";
            } else if (setTypeText.equals("java.sql.Timestamp")
                    && checkMethodIsLong(getTypeText)) {
                return startText + "new Timestamp(" + getMethodText + "));";
            } else if (checkMethodIsLong(setTypeText)
                    && getTypeText.equals("java.sql.Timestamp")) {
                return startText + getMethodText + ".getTime());";
            }
        }
        return startText + getMethodText + ");";
    }

    private static boolean checkMethodIsLong(String getMethodText) {
        return getMethodText.equals("java.lang.Long")
                || getMethodText.equals("long");
    }

    @NotNull
    private static GetInfo buildInfo(PsiParameter parameter,
                                     List<PsiMethod> getMethods) {
        GetInfo info;
        info = new GetInfo();
        info.setParamName(parameter.getName());
        info.setGetMethods(getMethods);
        Map<String, PsiMethod> nameToMethodMaps = Maps.newHashMap();
        for (PsiMethod getMethod : getMethods) {
            if (getMethod.getName().startsWith(IS)) {
                nameToMethodMaps.put(
                        getMethod.getName().substring(2).toLowerCase(),
                        getMethod);
            } else if (getMethod.getName().startsWith(GET)) {
                nameToMethodMaps.put(
                        getMethod.getName().substring(3).toLowerCase(),
                        getMethod);
            }
        }
        info.setNameToMethodMap(nameToMethodMaps);
        return info;
    }

    @NotNull
    private static String extractSplitText(PsiMethod method,
                                           Document document) {
        int startOffset = method.getTextRange().getStartOffset();
        int lastLine = startOffset - 1;
        String text = document.getText(new TextRange(lastLine, lastLine + 1));
        boolean isTable = false;
        while (!text.equals("\n")) {
            if (text.equals('\t')) {
                isTable = true;
            }
            lastLine--;
            text = document.getText(new TextRange(lastLine, lastLine + 1));
        }
        String methodStartToLastLineText = document
                .getText(new TextRange(lastLine, startOffset));
        String splitText = null;
        if (isTable) {
            splitText += methodStartToLastLineText + "\t";
        } else {
            splitText = methodStartToLastLineText + "    ";
        }
        return splitText;
    }

    private void handleWithLocalVariable(PsiLocalVariable localVariable,
                                         Project project, PsiElement element) {
        PsiElement parent1 = localVariable.getParent();
        if (!(parent1 instanceof PsiDeclarationStatement)) {
            return;
        }
        PsiClass psiClass = PsiTypesUtil.getPsiClass(localVariable.getType());
        String generateName = localVariable.getName();
        List<PsiMethod> methodList = PsiClassUtils.extractSetMethods(psiClass);
        if (methodList.size() == 0) {
            return;
        }
        PsiDocumentManager psiDocumentManager = PsiDocumentManager
                .getInstance(project);
        PsiFile containingFile = element.getContainingFile();
        Document document = psiDocumentManager.getDocument(containingFile);
        String splitText = PsiToolUtils.calculateSplitText(document, parent1.getTextOffset());

        Set<String> newImportList = new HashSet<>();
        boolean hasGuava = PsiToolUtils.checkGuavaExist(project, element);

        String buildString = generateStringForNoParam(generateName, methodList,
                splitText, newImportList, hasGuava);
        document.insertString(parent1.getTextOffset() + parent1.getText().length(),
                buildString);
        PsiDocumentUtils.commitAndSaveDocument(psiDocumentManager, document);
        PsiToolUtils.addImportToFile(psiDocumentManager,
                (PsiJavaFile) containingFile, document, newImportList);
        return;
    }

    @NotNull
    private static String generateStringForNoParam(String generateName,
                                                   List<PsiMethod> methodList, String splitText,
                                                   Set<String> newImportList, boolean hasGuava) {
        StringBuilder builder = new StringBuilder();
        builder.append(splitText);
        for (PsiMethod method : methodList) {
            generateDefaultForOneMethod(generateName, newImportList, hasGuava,
                    builder, method);
            builder.append(splitText);
        }

        return builder.toString();
    }

    private static void generateDefaultForOneMethod(String generateName,
                                                    Set<String> newImportList, boolean hasGuava, StringBuilder builder,
                                                    PsiMethod method) {
        PsiParameter[] parameters = method.getParameterList().getParameters();
        builder.append(generateName + "." + method.getName() + "(");
        int u = parameters.length;
        int h = 0;
        for (PsiParameter parameter : parameters) {
            h++;
            String classType = parameter.getType().getCanonicalText();
            String ss = typeGeneratedMap.get(classType);
            if (ss != null) {
                builder.append(ss);
                String importType = typeGeneratedImport.get(classType);
                if (importType != null) {
                    newImportList.add(importType);
                }
            } else {
                // shall check with import list to use.
                Parameters paramInfo = PsiToolUtils
                        .extractParamInfo(parameter.getType());
                if (paramInfo.getCollectName() != null && guavaTypeMaps
                        .containsKey(paramInfo.getCollectName())) {
                    if (hasGuava) {
                        builder.append(
                                guavaTypeMaps.get(paramInfo.getCollectName()));
                        newImportList.add(
                                guavaImportMap.get(paramInfo.getCollectName()));
                    } else {
                        // handleWithoutGuava.
                        String defaultImpl = defaultCollections
                                .get(paramInfo.getCollectName());
                        appendCollectNotEmpty(builder, paramInfo, defaultImpl,
                                newImportList);
                        newImportList.add(defaultImportMap
                                .get(paramInfo.getCollectName()));
                    }
                    // using default to do it.
                } else {
                    if (paramInfo.getCollectName() != null) {
                        String defaultImpl = defaultCollections
                                .get(paramInfo.getCollectName());
                        if (defaultImpl != null) {
                            appendCollectNotEmpty(builder, paramInfo,
                                    defaultImpl, newImportList);
                            newImportList.add(defaultImportMap
                                    .get(paramInfo.getCollectName()));
                        } else {
                            appendCollectNotEmpty(builder, paramInfo,
                                    paramInfo.getCollectName(), newImportList);
                            newImportList.add(paramInfo.getCollectPackege());
                        }
                    } else {
                        // may be could get the construct of the class. get the
                        // constructor of the class.
                        String realPackage = paramInfo.getParams().get(0)
                                .getRealPackage();
                        // todo could add more to the default package values.
                        String s = defaultPacakgeValues.get(realPackage);
                        if (s != null) {
                            builder.append(s);
                        } else {
                            builder.append("new "
                                    + paramInfo.getParams().get(0).getRealName()
                                    + "()");
                        }
                        newImportList.add(realPackage);
                    }
                }

            }
            if (h != u) {
                builder.append(",");
            }
        }
        builder.append(");");
    }

    private static void appendCollectNotEmpty(StringBuilder builder,
                                              Parameters paramInfo, String defaultImpl,
                                              Set<String> newImportList) {
        builder.append("new " + defaultImpl + "<");
        for (int i = 0; i < paramInfo.getParams().size(); i++) {
            builder.append(paramInfo.getParams().get(i).getRealName());
            newImportList.add(paramInfo.getParams().get(i).getRealPackage());
            if (i != paramInfo.getParams().size() - 1) {
                builder.append(",");
            }
        }
        builder.append(">()");
    }

    private String findNextNotNull(PsiTypeElement psiType, String defaultName) {
        PsiElement nextSibling = psiType.getNextSibling();
        while (nextSibling != null
                && StringUtils.isBlank(nextSibling.getText())) {
            nextSibling = nextSibling.getNextSibling();
        }
        if (nextSibling == null) {
            return defaultName;
        }
        return nextSibling.getText().trim();
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor,
                               @NotNull PsiElement element) {
        return (isValidAsLocalVariableWithSetterMethod(element) || isValidAsMethodWithSetterMethod(element));
    }

    @NotNull
    private Boolean isValidAsMethodWithSetterMethod(@NotNull PsiElement element) {
        PsiClass psiClass;
        PsiElement parentMethod = PsiTreeUtil.getParentOfType(element,
                PsiMethod.class);
        if (parentMethod == null) {
            return false;
        }
        PsiMethod method = (PsiMethod) parentMethod;
        // if is constructor the return type will be null.
        if (method.getReturnType() == null) {
            return false;
        }
        psiClass = PsiTypesUtil.getPsiClass(method.getReturnType());
        Parameters returnTypeInfo = PsiToolUtils
                .extractParamInfo(method.getReturnType());
        if (returnTypeInfo.getCollectPackege() != null && handlerMap
                .containsKey(returnTypeInfo.getCollectPackege())) {
            return true;
        }
        return PsiClassUtils.checkClassHasValidSetMethod(psiClass);
    }

    @NotNull
    private Boolean isValidAsLocalVariableWithSetterMethod(@NotNull PsiElement element) {
        PsiElement psiParent = PsiTreeUtil.getParentOfType(element,
                PsiLocalVariable.class);
        if (psiParent == null) {
            return false;
        }

        PsiLocalVariable psiLocal = (PsiLocalVariable) psiParent;
        if (!(psiLocal.getParent() instanceof PsiDeclarationStatement)) {
            return false;
        }
        PsiClass psiClass = PsiTypesUtil.getPsiClass(psiLocal.getType());
        return PsiClassUtils.checkClassHasValidSetMethod(psiClass);
    }

    private static boolean notObjectClass(PsiClass psiClass) {
        if (psiClass.getQualifiedName().equals("java.lang.Object")) {
            return false;
        }
        return true;
    }

    @Nls
    @NotNull
    @Override
    public String getFamilyName() {
        return CommonConstants.GENERATE_SETTER_METHOD;
    }

    @NotNull
    @Override
    public String getText() {
        return CommonConstants.GENERATE_SETTER_METHOD;
    }
}
