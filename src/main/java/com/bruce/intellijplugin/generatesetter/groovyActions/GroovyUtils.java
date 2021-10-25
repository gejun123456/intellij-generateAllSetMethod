package com.bruce.intellijplugin.generatesetter.groovyActions;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hcy
 * @since 2021/10/25 15:08
 */
public class GroovyUtils {

    /**
     * 提取类对应的set方法，要求set方法满足一定条件
     *
     * @param psiClass
     * @return
     */
    @NotNull
    public static List<PsiMethod> extractSetMethods(@Nullable PsiClass psiClass) {
        List<PsiMethod> methodList = new ArrayList<>();
        while (psiClass != null && !isSystemClass(psiClass)) {
            addSetMethodToList(psiClass, methodList);
            psiClass = psiClass.getSuperClass();
        }
        return methodList;
    }

    /**
     * 构造无默认参数的set方法文本
     *
     * @param fieldName  变量名
     * @param splitText  分隔符
     * @param psiMethods 方法
     * @return
     */
    public static StringBuilder generateNotParamSetFunctionString(String fieldName, String splitText, List<PsiMethod> psiMethods) {
        StringBuilder sb = new StringBuilder();
        for (PsiMethod psiMethod : psiMethods) {
            sb.append(splitText).append(fieldName).append(".").append(psiMethod.getName()).append("()").append('\n');
        }
        return sb;
    }

    /**
     * 获取当前文档的文件名
     *
     * @return 例如 Abc.groovy , Abc.java
     */
    public static String getDocumentFileName(@NotNull PsiElement document) {
        return document.getContainingFile().getName();
    }

    /**
     * 获取文档当前行，从第一个字符开始，有多少空格或\t
     */
    public static String getSplitText(Document document, int currentOffset) {
        StringBuilder sb = new StringBuilder(8);
        int lineStartOffset = document.getLineStartOffset(document.getLineNumber(currentOffset));
        while (true) {
            String text = document.getText(new TextRange(lineStartOffset, lineStartOffset + 1));
            if (text.equals(" ") || text.equals("\t")) {
                sb.append(text);
                lineStartOffset++;
                continue;
            } else {
                return sb.toString();
            }
        }
    }

    /**
     * 返回psciClass是否是系统类
     *
     * @param psiClass
     * @return psiClass !=null && 包名已java 或 groovy 开头
     */
    private static boolean isSystemClass(@NotNull PsiClass psiClass) {
        String qualifiedName = psiClass.getQualifiedName();
        return qualifiedName != null && (qualifiedName.startsWith("java.") || qualifiedName.startsWith("groovy."));
    }

    private static void addSetMethodToList(PsiClass psiClass, List<PsiMethod> methodList) {
        PsiMethod[] methods = psiClass.getMethods();
        for (PsiMethod method : methods) {
            if (isValidSetMethod(method)) {
                methodList.add(method);
            }
        }
    }

    private static boolean isValidSetMethod(PsiMethod m) {
        return !m.hasModifierProperty(PsiModifier.STATIC)
                && !m.getName().equals("setMetaClass")
                && !m.getName().equals("setProperty")
                && m.hasModifierProperty(PsiModifier.PUBLIC)
                && (m.getName().startsWith("set") || m.getName().startsWith("with"))
                ;
    }

}
