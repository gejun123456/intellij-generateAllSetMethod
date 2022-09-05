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
import com.bruce.intellijplugin.generatesetter.utils.PsiClassUtils;
import com.bruce.intellijplugin.generatesetter.utils.PsiDocumentUtils;
import com.bruce.intellijplugin.generatesetter.utils.PsiToolUtils;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiTypesUtil;
import com.intellij.util.IncorrectOperationException;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @author bruce ge
 */
public class GenerateAllGetterAction extends PsiElementBaseIntentionAction {

    public Map<String, PsiType[]> genericListMap = new HashMap<>();

    private static String extractSplitText(PsiMethod method, Document document) {
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
        String splitText = "";
        if (isTable) {
            splitText += methodStartToLastLineText + "\t";
        } else {
            splitText = methodStartToLastLineText + "    ";
        }
        return splitText;
    }

    @NotNull
    public static String calculateSplitText(Document document, int statementOffset, String addition) {
        // 取得要计算的行有代码地方的初始 offset, 即 statementOffset
        // 根据这个offset 往前遍历取得其缩进, 可能为 空格或 \t
        // 若需要在此基础上再缩进一次, 可对参数 addition 赋值 4个空格
        String splitText = "";
        int cur = statementOffset;
        String text = document.getText(new TextRange(cur - 1, cur));
        while (text.equals(" ") || text.equals("\t")) {
            splitText = text + splitText;
            cur--;
            if (cur < 1) {
                break;
            }
            text = document.getText(new TextRange(cur - 1, cur));
        }
        splitText = "\n" + addition + splitText;
        return splitText;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {
        PsiLocalVariable psiLocal = PsiTreeUtil.getParentOfType(element, PsiLocalVariable.class);
        PsiDocumentManager psiDocumentManager = PsiDocumentManager.getInstance(project);
        PsiFile containingFile = element.getContainingFile();
        Document document = psiDocumentManager.getDocument(containingFile);
        if (psiLocal != null) {
            PsiElement parent = psiLocal.getParent();
            String splitText = calculateSplitText(document, parent.getTextOffset(), "");
            int textOffset = parent.getTextOffset() + parent.getText().length();
            handleWithPsiType(document, psiDocumentManager, containingFile, psiLocal.getType(), psiLocal.getName(), splitText, textOffset + 1);
        }
        PsiParameter psiParameter = PsiTreeUtil.getParentOfType(element, PsiParameter.class);
        if (psiParameter != null) {
            PsiElement parent = psiParameter.getParent();
            PsiElement parent0 = parent.getParent();
            if (parent0 instanceof PsiMethod) {
                PsiMethod psiMethod = (PsiMethod) parent0;
                String splitText = calculateSplitText(document, psiMethod.getTextOffset(), "    ");
                int insertOffset = psiMethod.getBody().getTextOffset() + 1;
                handleWithPsiType(document, psiDocumentManager, containingFile, psiParameter.getType(), psiParameter.getName(), splitText, insertOffset);
            }
            if (parent instanceof PsiForeachStatement) {
                PsiForeachStatement psiForeachStatement = (PsiForeachStatement) parent;
                String splitText = calculateSplitText(document, psiForeachStatement.getTextOffset(), "    ");
                int insertOffset = psiForeachStatement.getBody().getTextOffset() + 1;
                handleWithPsiType(document, psiDocumentManager, containingFile, psiParameter.getType(), psiParameter.getName(), splitText, insertOffset);
            }
        }
    }

    private void handleWithPsiType(Document document, PsiDocumentManager psiDocumentManager, PsiFile containingFile, PsiType psiType, String generateName, String splitText, int insertOffset) {
        HashSet<String> newImportList = new HashSet<>();
        PsiClass psiClass = PsiTypesUtil.getPsiClass(psiType);
        if (psiType instanceof PsiClassReferenceType) {
            PsiClassReferenceType referenceType = (PsiClassReferenceType) psiType;
            resolvePsiClassParameter(referenceType);
        }
        List<PsiMethod> methodList = PsiClassUtils.extractGetMethod(psiClass);
        if (methodList.size() == 0) {
            return;
        }

        String buildString = generateStringForGetter(generateName, methodList, splitText, newImportList);
        document.insertString(insertOffset, buildString);
        PsiDocumentUtils.commitAndSaveDocument(psiDocumentManager, document);
        PsiToolUtils.addImportToFile(psiDocumentManager, (PsiJavaFile) containingFile, document, newImportList);
    }

    private String generateStringForGetter(String generateName, List<PsiMethod> methodList, String splitText, Set<String> newImportList) {
        StringBuilder builder = new StringBuilder();
        builder.append(splitText);
        for (PsiMethod method : methodList) {
            generateDefaultForOneMethod(generateName, newImportList,
                    builder, method);
            builder.append(splitText);
        }
        return builder.toString();
    }

    private void generateDefaultForOneMethod(String generateName, Set<String> newImportList, StringBuilder builder, PsiMethod method) {
        Project project = method.getProject();
        PsiType returnType = method.getReturnType();
        String typeName = returnType.getPresentableText();
        String fieldType = handlerByPsiType(newImportList, project, returnType, "%s", typeName);

        String methodName = method.getName();
        String varName = methodName.replaceFirst("get", "");
        varName = varName.substring(0, 1).toLowerCase() + varName.substring(1);
        String str = fieldType + " " + varName + " = " + generateName + "." + methodName + "();";
        builder.append(str);
    }

    protected void resolvePsiClassParameter(PsiClassType psiClassReferenceType) {
        PsiClass psiClass = psiClassReferenceType.resolve();
        String qualifiedName = psiClass.getQualifiedName();
        PsiClassType[] extendsListTypes = psiClass.getExtendsListTypes();
        PsiType[] parameters = psiClassReferenceType.getParameters();
        if (parameters.length > 0) {
            genericListMap.put(qualifiedName, parameters);
        }
        if (extendsListTypes != null && extendsListTypes.length > 0) {
            for (PsiClassType extendsListType : extendsListTypes) {
                resolvePsiClassParameter(extendsListType);
            }
        }
    }

    public boolean isJavaBaseType(String typeName) {
        switch (typeName) {
            case "byte":
            case "char":
            case "character":
            case "boolean":
            case "int":
            case "integer":
            case "double":
            case "float":
            case "long":
            case "short":
            case "number":
            case "bigdecimal":
            case "string":
            case "date":
                return true;
            default:
                return false;
        }
    }

    protected void handleSyntaxError(String code) throws RuntimeException {
        throw new RuntimeException("您的代码可能存在语法错误, 无法为您生成代码, 参考信息: " + code);
    }

    protected String getRealPsiTypeName(PsiType psiType, Project project, String typeNameFormat) {
        if (isPsiTypeFromParameter(psiType)) {
            PsiType realPsiType = getRealPsiType(psiType, project, psiType);
            if (psiType == realPsiType) {
                String typeName = "Object";
                typeName = String.format(typeNameFormat, typeName);
                return typeName;
            } else {
                psiType = realPsiType;
            }
        }
        boolean isArrayType = psiType instanceof PsiArrayType;
        if (isArrayType) {
            PsiArrayType psiArrayType = (PsiArrayType) psiType;
            PsiType componentType = psiArrayType.getComponentType();
            String typeFormat = String.format(typeNameFormat, "%s[]");
            return getRealPsiTypeName(componentType, project, typeFormat);
        }

        if (psiType instanceof PsiClassReferenceType) {
            PsiClassReferenceType psiClassReferenceType = (PsiClassReferenceType) psiType;
            PsiClass resolveClass = psiClassReferenceType.resolve();
            PsiType[] parameters = psiClassReferenceType.getParameters();
            if (isPsiTypeFromList(psiType, project)) {
                if (parameters.length > 0) {
                    PsiType elementType = parameters[0];
                    String typeFormat = String.format(typeNameFormat, "List<%s>");
                    return getRealPsiTypeName(elementType, project, typeFormat);
                }
            }
            if (isPsiTypeFromMap(psiType, project)) {
                if (parameters.length > 1) {
                    PsiType keyType = parameters[0];
                    PsiType valueType = parameters[1];
                    String keyTypeName;
                    if (isPsiTypeFromParameter(keyType)) {
                        keyType = getRealPsiType(keyType, project, keyType);
                    }
                    keyTypeName = keyType.getPresentableText();
                    String typeFormat = String.format(typeNameFormat, "Map<" + keyTypeName + ", %s>");
                    return getRealPsiTypeName(valueType, project, typeFormat);
                }
            }
        }
        String typeName = psiType.getPresentableText();
        typeName = String.format(typeNameFormat, typeName);
        return typeName;
    }

    private PsiType getRealPsiType0(String ownerQname, int index, Project project, PsiType defaultVal) {
        PsiType[] psiTypes = genericListMap.get(ownerQname);
        if (psiTypes != null && psiTypes.length > 0) {
            if (index >= 0 && index <= psiTypes.length - 1) {
                PsiType psiType = psiTypes[index];
                if (isPsiTypeFromParameter(psiType)) {
                    return getRealPsiType(psiType, project, defaultVal);
                } else {
                    return psiType;
                }
            } else {
                return PsiType.getTypeByName("java.lang.Object", project, GlobalSearchScope.allScope(project));
            }
        }
        return defaultVal;
    }

    protected PsiType getRealPsiType(PsiType psiFieldType, Project project, PsiType defaultVal) {
        PsiClassReferenceType psiClassReferenceType = (PsiClassReferenceType) psiFieldType;
        PsiClass resolveClass = psiClassReferenceType.resolve();
        if (resolveClass instanceof PsiTypeParameter) {
            PsiTypeParameter typeParameter = (PsiTypeParameter) resolveClass;
            int index = typeParameter.getIndex();
            PsiTypeParameterListOwner owner = typeParameter.getOwner();
            if (owner instanceof PsiClass) {
                PsiClass psiClass = (PsiClass) owner;
                String qualifiedName = psiClass.getQualifiedName();
                return getRealPsiType0(qualifiedName, index, project, defaultVal);
            }
        }
        return defaultVal;
    }

    protected boolean isPsiTypeFromParameter(PsiType psiFieldType) {
        if (psiFieldType instanceof PsiClassReferenceType) {
            PsiClassReferenceType psiClassReferenceType = (PsiClassReferenceType) psiFieldType;
            PsiClass resolveClass = psiClassReferenceType.resolve();
            if (resolveClass == null) {
                handleSyntaxError(psiClassReferenceType.getCanonicalText());
            }
            if (resolveClass instanceof PsiTypeParameter) {
                return true;
            }
        }
        return false;
    }

    protected boolean isPsiTypeFromList(PsiType psiFieldType, Project project) {
        boolean isReferenceType = psiFieldType instanceof PsiClassReferenceType;
        if (isReferenceType) {
            PsiClassReferenceType psiClassReferenceType = (PsiClassReferenceType) psiFieldType;
            PsiClass resolveClass = psiClassReferenceType.resolve();
            if (resolveClass == null) {
                handleSyntaxError(psiClassReferenceType.getCanonicalText());
            }
            String qNameOfList = "java.util.List";
            return isPsiClassFromXxx(resolveClass, project, qNameOfList);
        }
        return false;
    }

    protected boolean isPsiTypeFromMap(PsiType psiFieldType, Project project) {
        boolean isReferenceType = psiFieldType instanceof PsiClassReferenceType;
        if (isReferenceType) {
            PsiClassReferenceType psiClassReferenceType = (PsiClassReferenceType) psiFieldType;
            PsiClass resolveClass = psiClassReferenceType.resolve();
            if (resolveClass == null) {
                handleSyntaxError(psiClassReferenceType.getCanonicalText());
            }
            String qNameOfMap = "java.util.Map";
            return isPsiClassFromXxx(resolveClass, project, qNameOfMap);
        }
        return false;
    }

    public boolean isPsiClassFromXxx(PsiClass psiClass, Project project, String qNameOfXxx) {
        String qNameOfClass = psiClass.getQualifiedName();
        if (StringUtils.isBlank(qNameOfClass)) {
            return false;
        }
        PsiClassType psiType = PsiType.getTypeByName(qNameOfClass, project, GlobalSearchScope.allScope(project));
        PsiClassType xxxType = PsiType.getTypeByName(qNameOfXxx, project, GlobalSearchScope.allScope(project));
        boolean assignableFromXxx = xxxType.isAssignableFrom(psiType);
        PsiClass xxxClass = findOnePsiClassByClassName(qNameOfXxx, project);
        boolean isXxxType = psiClass.isInheritor(xxxClass, true);
        if (assignableFromXxx || isXxxType) {
            return true;
        }
        return false;
    }

    public PsiClass findOnePsiClassByClassName(String qualifiedClassName, Project project) {
        return JavaPsiFacade.getInstance(project).findClass(qualifiedClassName, GlobalSearchScope.allScope(project));
    }

    private String handlerByPsiType(Set<String> newImportList, Project project, PsiType psiFieldType, String typeNameFormat, String typeName) {
        String fieldTypeName = String.format(typeNameFormat, typeName);

        String typeSimpleName = psiFieldType.getPresentableText();
        if (isJavaBaseType(typeSimpleName) || "Object".equals(typeSimpleName)) {
            return fieldTypeName;
        }

        boolean isArrayType = psiFieldType instanceof PsiArrayType;
        if (isArrayType) {
            PsiArrayType psiArrayType = (PsiArrayType) psiFieldType;
            PsiType componentType = psiArrayType.getComponentType();
            String typeFormat = String.format(typeNameFormat, "%s[]");
            String realTypeName = componentType.getPresentableText();
            return handlerByPsiType(newImportList, project, componentType, typeFormat, realTypeName);
        }

        boolean isReferenceType = psiFieldType instanceof PsiClassReferenceType;
        // 引用(枚举/对象/List/Map)
        if (isReferenceType) {
            PsiClassReferenceType psiClassReferenceType = (PsiClassReferenceType) psiFieldType;
            PsiClass resolveClass = psiClassReferenceType.resolve();
            if (resolveClass == null) {
                handleSyntaxError(psiClassReferenceType.getCanonicalText());
            }

            PsiType[] parameters = psiClassReferenceType.getParameters();
            if (parameters.length > 0) {
                StringBuilder name = new StringBuilder();
                for (PsiType parameter : parameters) {
                    String realTypeName = getRealPsiTypeName(parameter, project, "%s");
                    name.append(realTypeName).append(", ");
                }
                if (name.length() != 0) {
                    String name0 = name.substring(0, name.length() - 2);
                    typeSimpleName = typeSimpleName.substring(0, typeSimpleName.indexOf("<")) + "<" + name0 + ">";
                }
                // 此处 put 只影响普通 class, 不影响 list, map 等
                fieldTypeName = typeSimpleName;
            }


            PsiType realPsiType = getRealPsiType(psiFieldType, project, null);
            if (realPsiType != null) {
                String realTypeName = realPsiType.getPresentableText();
                String typeFormat = String.format(typeNameFormat, "%s");
                return handlerByPsiType(newImportList, project, realPsiType, typeFormat, realTypeName);
            }

            // 枚举
            if (resolveClass.isEnum()) {
                return fieldTypeName;
            }

            // List
            if (isPsiTypeFromList(psiFieldType, project)) {
                if (parameters != null && parameters.length > 0) {
                    newImportList.add("java.util.List");
                    PsiType elementType = parameters[0];
                    String typeFormat = String.format(typeNameFormat, "List<%s>");
                    String realTypeName = elementType.getPresentableText();
                    return handlerByPsiType(newImportList, project, elementType, typeFormat, realTypeName);
                } else {
                    return fieldTypeName;
                }
            }

            // Map
            if (isPsiTypeFromMap(psiFieldType, project)) {
                if (parameters != null && parameters.length > 1) {
                    newImportList.add("java.util.Map");
                    PsiType keyType = parameters[0];
                    PsiType valueType = parameters[1];
                    String keyTypeName;
                    if (isPsiTypeFromParameter(keyType)) {
                        keyType = getRealPsiType(keyType, project, keyType);
                    }
                    keyTypeName = keyType.getPresentableText();
                    String typeFormat = String.format(typeNameFormat, "Map<" + keyTypeName + ", %s>");
                    String realTypeName = valueType.getPresentableText();
                    return handlerByPsiType(newImportList, project, valueType, typeFormat, realTypeName);
                } else {
                    return fieldTypeName;
                }
            }
            newImportList.add(resolveClass.getQualifiedName());
        } else {
            System.out.println(psiFieldType.getPresentableText() + " ==> not basic type, not ReferenceType");
        }
        return fieldTypeName;
    }

    @Nls
    @NotNull
    @Override
    public String getFamilyName() {
        return CommonConstants.GENERATE_GETTER_METHOD;
    }

    @NotNull
    @Override
    public String getText() {
        return CommonConstants.GENERATE_GETTER_METHOD;
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
        // 只需确保该变量含有get方法即可
        PsiClass localVariableContainingClass = getLocalVariableContainingClass(element);
        if (localVariableContainingClass != null) {

            return PsiClassUtils.checkClasHasValidGetMethod(localVariableContainingClass);
        }
        PsiClass methodParameterContainingClass = getMethodParameterContainingClass(element);
        if (methodParameterContainingClass != null) {
            return PsiClassUtils.checkClasHasValidGetMethod(methodParameterContainingClass);
        }
        return false;
    }

    private PsiClass getMethodParameterContainingClass(PsiElement element) {
        PsiParameter psiParent = PsiTreeUtil.getParentOfType(element, PsiParameter.class);
        if (psiParent == null) {
            return null;
        }
        return PsiTypesUtil.getPsiClass(psiParent.getType());
    }

    private PsiClass getLocalVariableContainingClass(@NotNull PsiElement element) {
        PsiLocalVariable psiLocal = PsiTreeUtil.getParentOfType(element, PsiLocalVariable.class);
        if (psiLocal == null) {
            return null;
        }
        if (!(psiLocal.getParent() instanceof PsiDeclarationStatement)) {
            return null;
        }
        return PsiTypesUtil.getPsiClass(psiLocal.getType());
    }
}
