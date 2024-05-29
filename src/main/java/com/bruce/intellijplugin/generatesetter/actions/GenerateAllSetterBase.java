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
import com.bruce.intellijplugin.generatesetter.GetInfo;
import com.bruce.intellijplugin.generatesetter.Parameters;
import com.bruce.intellijplugin.generatesetter.complexreturntype.*;
import com.bruce.intellijplugin.generatesetter.dialog.VariableDialog;
import com.bruce.intellijplugin.generatesetter.utils.PsiClassUtils;
import com.bruce.intellijplugin.generatesetter.utils.PsiDocumentUtils;
import com.bruce.intellijplugin.generatesetter.utils.PsiToolUtils;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiTypesUtil;
import com.intellij.util.IncorrectOperationException;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author bruce ge
 */
public abstract class GenerateAllSetterBase extends PsiElementBaseIntentionAction {
    public static final String IS = "is";
    public static final String GET = "get";
    private static final String SET_SETTER_PREFIX = "set";
    private static final String WITH_SETTER_PREFIX = "with";
    public static final String STATIC = "static";
    private GenerateAllHandler generateAllHandler;

    public GenerateAllSetterBase() {
    }

    public GenerateAllSetterBase(GenerateAllHandler generateAllHandler) {
        this.generateAllHandler = generateAllHandler;
    }

    public void setGenerateAllHandler(GenerateAllHandler generateAllHandler) {
        this.generateAllHandler = generateAllHandler;
    }

    private static final Set<String> javaSimpleTypes = new HashSet<>(Arrays.asList(
            "char",
            "boolean",
            "byte", "short", "int", "long",
            "float", "double"
    ));

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
            if (generateAllHandler.isSetterFromVariable()) {
                handleWithLocalVariableFromVariable(psiLocal, project, psiLocal);
            } else {
                handleWithLocalVariable(psiLocal, project, psiLocal);
            }
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
        if (document == null) {
            return;
        }
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


    private void handleWithLocalVariableFromVariable(PsiLocalVariable localVariable,
                                                     Project project, PsiElement element) {
        int currentOffset = localVariable.getTextOffset();

        PsiElement parent = localVariable.getParent();
        if (!(parent instanceof PsiDeclarationStatement)) {
            return;
        }
        PsiMethod method = PsiTreeUtil.getParentOfType(localVariable, PsiMethod.class);
        if (method == null) {
            return;
        }

        // 创建一个列表来存储局部变量
        PsiParameter[] parameters = method.getParameterList().getParameters();
        List<PsiVariable> localVariables = new ArrayList<>(Arrays.asList(parameters));
        // 遍历方法的所有元素
        List<PsiVariable> finalLocalVariables = localVariables;
        method.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitLocalVariable(PsiLocalVariable variable) {
                // 如果变量的位置在当前变量之前，就添加到列表中
                if (variable.getTextOffset() < currentOffset) {
                    finalLocalVariables.add(variable);
                }
            }
        });
        localVariables = localVariables.stream().filter(psiVariable -> PsiClassUtils.isNotSystemClass(PsiTypesUtil.getPsiClass(psiVariable.getType()))).collect(Collectors.toList());

        // 创建并显示对话框
        VariableDialog dialog = new VariableDialog(localVariables);
        dialog.show();

        if (dialog.getExitCode() == DialogWrapper.OK_EXIT_CODE) {
            // 用户点击了 OK，获取选中的变量
            String selectedVariable = dialog.getSelectedVariable();
            if (selectedVariable == null) {
                return;
            }
            PsiVariable selectedVariableObject = null;
            for (PsiVariable variable : localVariables) {
                if (variable.getName().equals(selectedVariable)) {
                    selectedVariableObject = variable;
                    break;
                }
            }
            if (selectedVariableObject == null) {
                return;
            }
            PsiDocumentManager psiDocumentManager = PsiDocumentManager.getInstance(project);
            Document document = psiDocumentManager.getDocument(element.getContainingFile());
            if (document == null) {
                return;
            }
            String splitText = extractSplitText(method, document);

            boolean hasGuava = PsiToolUtils.checkGuavaExist(project, element);
            PsiClass returnTypeClass = PsiTypesUtil
                    .getPsiClass(localVariable.getType());

            InsertDto dto = getBaseInsertNoReturnDto(localVariable, selectedVariableObject, splitText, hasGuava
                    , returnTypeClass);
            if (dto.getAddedText() != null) {
                document.insertString(parent.getTextOffset() + parent.getText().length(),
                        dto.getAddedText());
            }
            PsiDocumentUtils.commitAndSaveDocument(psiDocumentManager, document);

            if (dto.getImportList() != null) {
                PsiToolUtils.addImportToFile(psiDocumentManager,
                        (PsiJavaFile) element.getContainingFile(), document,
                        dto.getImportList());
            }
        }
    }

    @NotNull
    private InsertDto getBaseInsertDto(String splitText,
                                       boolean hasGuava, PsiParameter[] parameters1, PsiClass psiClass) {
        InsertDto dto = new InsertDto();
        PsiParameter[] parameters = parameters1;
        List<PsiMethod> methods = PsiClassUtils.extractSetMethods(psiClass);
        Set<String> importList = Sets.newHashSet();
        String generateName = PsiToolUtils.lowerStart(psiClass.getName());
        GetInfo info = getGetInfo(parameters);
        // TODO: 2017/8/2 what if two class has the same name
        String insertText = splitText + psiClass.getName() + " " + generateName
                + " = new " + psiClass.getName() + "();";
        if (info == null) {
            insertText += generateStringForNoParam(generateName, methods,
                    splitText, importList, hasGuava);
        } else {
            insertText = splitText + "if (" + info.getParamName() + " == null) {"
                    + indentText(splitText) + "return null;"
                    + splitText + "}" + insertText;
            insertText += generateStringForParam(generateName, methods,
                    splitText, importList, hasGuava, info);
        }
        insertText += "return " + generateName + ";";
        dto.setAddedText(insertText);
        dto.setImportList(importList);
        return dto;
    }

    @NotNull
    private InsertDto getBaseInsertNoReturnDto(PsiLocalVariable from, PsiVariable to, String splitText,
                                               boolean hasGuava, PsiClass psiClass) {

        String generateName = from.getName();
        InsertDto dto = new InsertDto();
        List<PsiMethod> methods = PsiClassUtils.extractSetMethods(psiClass);
        Set<String> importList = Sets.newHashSet();
        GetInfo info = getGetInfo1(to);
        // TODO: 2017/8/2 what if two class has the same name
        String insertText = "";
        if (info == null) {
            insertText += generateStringForNoParam(generateName, methods,
                    splitText, importList, hasGuava);
        } else {
            insertText += generateStringForParam(generateName, methods,
                    splitText, importList, hasGuava, info);
        }
        dto.setAddedText(insertText);
        dto.setImportList(importList);
        return dto;
    }

    @Nullable
    protected GetInfo getGetInfo(PsiParameter[] parameters) {
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
        return info;
    }

    @Nullable
    protected GetInfo getGetInfo1(PsiVariable parameter) {
        GetInfo info = null;

        PsiType type = parameter.getType();
        PsiClass parameterClass = PsiTypesUtil.getPsiClass(type);
        if (parameterClass == null || parameterClass.getQualifiedName()
                .startsWith("java.")) {
            return null;
        } else {
            List<PsiMethod> getMethods = PsiClassUtils
                    .extractGetMethod(parameterClass);
            // TODO: 2017/1/20 may be i can extract get memthod from all
            // parameter
            if (getMethods.size() > 0) {
                info = buildInfo(parameter, getMethods);
            }
        }
        return info;
    }

    private String generateStringForParam(String generateName,
                                          List<PsiMethod> methodList, String splitText,
                                          Set<String> newImportList, boolean hasGuava, GetInfo info) {
        StringBuilder builder = new StringBuilder();
        builder.append(splitText);
        for (PsiMethod method : methodList) {
            String setterMethodNamePrefix =
                    method.getName().startsWith(SET_SETTER_PREFIX)
                            ? SET_SETTER_PREFIX
                            : method.getName().startsWith(WITH_SETTER_PREFIX)
                            ? WITH_SETTER_PREFIX
                            : null;
            if (setterMethodNamePrefix != null) {
                String fieldToLower = method.getName().substring(setterMethodNamePrefix.length())
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
    private static GetInfo buildInfo(PsiVariable parameter,
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

    /**
     * 分析当前方法的缩进符, 并追加缩进
     * 为保持原方法功能, 返回值的开头会增加一个"\n"
     * -
     * Analyze the current method's indentation character and append indentation.
     * To maintain the original function of the method, a "\n" will be added to the beginning of the return value.
     *
     * @param method   curr method
     * @param document curr doc
     * @return the indented text
     */
    @NotNull
    protected static String extractSplitText(PsiMethod method, Document document) {
        int methodStartOffset = method.getTextRange().getStartOffset();
        int lineNumber = document.getLineNumber(methodStartOffset);
        int lineStartOffset = document.getLineStartOffset(lineNumber);
        String currIndentedText = document.getText(new TextRange(lineStartOffset, methodStartOffset));
        return "\n" + indentText(currIndentedText);
    }

    /**
     * 对文本进行缩进
     * 本方法内部会判断原文本的缩进符是\t还是空格
     * 增加的缩进如果是空格的话，默认使用4个空格
     * -
     * indent the text
     * this method internally determines whether the original text's indentation character is a tab (\t) or a space.
     * if the added indentation is spaces, the default is to use 4 spaces.
     *
     * @param text raw text
     * @return the indented text
     */
    @NotNull
    protected static String indentText(String text) {
        if (text == null) {
            return CommonConstants.INDENTATION_CHARACTER_4_SPACE;
        }
        return text +
                (text.contains(CommonConstants.INDENTATION_CHARACTER_TAB)
                        ? CommonConstants.INDENTATION_CHARACTER_TAB
                        : CommonConstants.INDENTATION_CHARACTER_4_SPACE);
    }

    private void handleWithLocalVariable(PsiLocalVariable localVariable,
                                         Project project, PsiElement element) {
        PsiElement parent1 = localVariable.getParent();
        if (!(parent1 instanceof PsiDeclarationStatement)) {
            return;
        }
        PsiClass psiClass = PsiTypesUtil.getPsiClass(localVariable.getType());
        String generateName = localVariable.getName();
        List<PsiMethod> methodList;
        if (generateAllHandler.isSetter()) {
            methodList = PsiClassUtils.extractSetMethods(psiClass);
        } else {
            methodList = PsiClassUtils.extractGetMethod(psiClass);
        }
        if (methodList.size() == 0) {
            return;
        }
        PsiDocumentManager psiDocumentManager = PsiDocumentManager
                .getInstance(project);
        PsiFile containingFile = element.getContainingFile();
        Document document = psiDocumentManager.getDocument(containingFile);
        if (document == null) {
            return;
        }
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
    protected String generateStringForNoParam(String generateName,
                                              List<PsiMethod> methodList, String splitText,
                                              Set<String> newImportList, boolean hasGuava) {
        StringBuilder builder = new StringBuilder();
        builder.append(splitText);

        if (generateAllHandler.forAccessor()) {
            builder.append(generateName);
            for (PsiMethod method : methodList) {
                builder.append(generateAllHandler.formatLine("." + method.getName() + "()"));
                builder.append(splitText);
                builder.append("\t");
            }
            builder.append(";");
            return builder.toString();
        }

        for (PsiMethod method : methodList) {
            generateDefaultForOneMethod(generateName, newImportList, hasGuava, builder, method);
            builder.append(splitText);
        }
        return builder.toString();
    }

    private void generateDefaultForOneMethod(String generateName,
                                             Set<String> newImportList, boolean hasGuava, StringBuilder mainBuilder,
                                             PsiMethod method) {
        if (!generateAllHandler.shouldAddDefaultValue()) {
            mainBuilder.append(generateAllHandler.formatLine(generateName + "." + method.getName() + "();"));
            generateAllHandler.appendImportList(newImportList);
            return;
        }

        PsiType[] types;
        if (generateAllHandler.forAssertWithDefaultValues()) {
            types = method.getReturnType() != null
                    ? new PsiType[]{method.getReturnType()}
                    : new PsiType[0];
        } else {
            PsiParameter[] parameters = method.getParameterList().getParameters();
            types = new PsiType[parameters.length];
            for (int i = 0; i < types.length; i++) {
                types[i] = parameters[i].getType();
            }
        }

        StringBuilder builder = new StringBuilder();
        builder.append(generateName + "." + method.getName() + "(");

        int u = types.length;
        int h = 0;
        for (PsiType type : types) {
            h++;
            String classType = type.getCanonicalText();
            String ss = typeGeneratedMap.get(classType);
            if (ss != null) {
                builder.append(ss);
                String importType = typeGeneratedImport.get(classType);
                if (importType != null) {
                    newImportList.add(importType);
                }
            } else {
                // shall check which import list to use.
                Parameters paramInfo = PsiToolUtils.extractParamInfo(type);
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
                        final PsiClass psiClassOfParameter = PsiTypesUtil.getPsiClass(type);
                        if (s != null) {
                            builder.append(s);
                        } else if (psiClassOfParameter != null && psiClassOfParameter.isEnum()) {
                            final PsiField[] allFields = psiClassOfParameter.getAllFields();
                            Arrays.stream(allFields).findFirst().ifPresent(field -> builder.append(psiClassOfParameter.getName()).append(".").append(field.getName()));
                        } else {
                            String realName = paramInfo.getParams().get(0).getRealName();
                            builder.append("new " + realName);

                            if (paramInfo.isArray()) {
                                for (int i = 0; i < paramInfo.getArrayDimensions(); i++) {
                                    builder.append("[0]");
                                }
                            } else {
                                builder.append("()");
                            }
                        }
                        if (!javaSimpleTypes.contains(realPackage)) {
                            newImportList.add(realPackage);
                        }
                    }
                }

            }
            if (h != u) {
                builder.append(",");
            }
        }
        builder.append(");");

        generateAllHandler.appendImportList(newImportList);

        if (generateAllHandler.forAssertWithDefaultValues()) {
            mainBuilder.append(generateAllHandler.formatLine(builder.toString()));
        } else {
            mainBuilder.append(builder);
        }
    }

    private static void appendCollectNotEmpty(StringBuilder builder,
                                              Parameters paramInfo, String defaultImpl,
                                              Set<String> newImportList) {
        builder.append("new ").append(defaultImpl);
        if (paramInfo.isArray()) {
            for (int i = 0; i < paramInfo.getArrayDimensions(); i++) {
                builder.append("[0]");
            }
        } else {
            builder.append("<");
            for (int i = 0; i < paramInfo.getParams().size(); i++) {
                builder.append(paramInfo.getParams().get(i).getRealName());
                newImportList.add(paramInfo.getParams().get(i).getRealPackage());
                if (i != paramInfo.getParams().size() - 1) {
                    builder.append(",");
                }
            }
            builder.append(">()");
        }
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
        boolean setter = generateAllHandler.isSetter();
        if (generateAllHandler.forBuilder()) {
            if (generateAllHandler.isFromMethod()) {
                return isExistBuilder(element);
            }
            PsiClass localVarialbeContainingClass = getLocalVarialbeContainingClass(element);
            if (localVarialbeContainingClass == null) {
                return false;
            }
            PsiMethod[] methods = localVarialbeContainingClass.getMethods();
            for (PsiMethod method : methods) {
                if (method.getName().equals(CommonConstants.BUILDER_METHOD_NAME) && method.hasModifierProperty(STATIC)) {
                    return true;
                }
            }
            return false;
        }
        Boolean validAsLocalVariableWithSetterOrGetterMethod = isValidAsLocalVariableWithSetterOrGetterMethod(element, setter);
        if (validAsLocalVariableWithSetterOrGetterMethod) {
            return validAsLocalVariableWithSetterOrGetterMethod;
        }
        if (generateAllHandler.isSetter() && generateAllHandler.isFromMethod()) {
            return isValidAsMethodWithSetterMethod(element);
        }

        return false;
    }

    @NotNull
    private Boolean isExistBuilder(@NotNull PsiElement element) {
        PsiMethod parentMethod = PsiTreeUtil.getParentOfType(element, PsiMethod.class);
        if (parentMethod != null && parentMethod.getReturnType() != null) {
            PsiClass psiClass = PsiTypesUtil.getPsiClass(parentMethod.getReturnType());
            if (psiClass != null) {
                for (PsiMethod method : psiClass.getMethods()) {
                    if (method.getName().equals(CommonConstants.BUILDER_METHOD_NAME) && method.hasModifierProperty(STATIC)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @NotNull
    private Boolean isValidAsMethodWithSetterMethod(@NotNull PsiElement element) {
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
        PsiClass psiClass = PsiTypesUtil.getPsiClass(method.getReturnType());
        Parameters returnTypeInfo = PsiToolUtils
                .extractParamInfo(method.getReturnType());
        if (returnTypeInfo.getCollectPackege() != null && handlerMap
                .containsKey(returnTypeInfo.getCollectPackege())) {
            return true;
        }
        return PsiClassUtils.checkClassHasValidSetMethod(psiClass);
    }

    @NotNull
    private Boolean isValidAsLocalVariableWithSetterOrGetterMethod(@NotNull PsiElement element, boolean setter) {
        PsiClass psiClass = getLocalVarialbeContainingClass(element);
        if (psiClass == null) {
            return false;
        }
        if (setter) {
            return PsiClassUtils.checkClassHasValidSetMethod(psiClass);
        } else {
            return PsiClassUtils.checkClasHasValidGetMethod(psiClass);
        }
    }

    public static PsiClass getLocalVarialbeContainingClass(@NotNull PsiElement element) {
        PsiElement psiParent = PsiTreeUtil.getParentOfType(element,
                PsiLocalVariable.class);
        if (psiParent == null) {
            return null;
        }
        PsiLocalVariable psiLocal = (PsiLocalVariable) psiParent;
        if (!(psiLocal.getParent() instanceof PsiDeclarationStatement)) {
            return null;
        }
        PsiClass psiClass = PsiTypesUtil.getPsiClass(psiLocal.getType());
        return psiClass;
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
        return getText();
    }

}
