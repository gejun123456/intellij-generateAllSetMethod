package com.bruce.intellijplugin.generatesetter.utils;

import com.bruce.intellijplugin.generatesetter.ParamInfo;
import com.bruce.intellijplugin.generatesetter.RealParam;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.psi.util.PsiTypesUtil;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @Author bruce.ge
 * @Date 2017/1/28
 * @Description
 */
public class PsiToolUtils {

    public static boolean checkGuavaExist(Project project, PsiElement element) {
        PsiClass[] listss = PsiShortNamesCache.getInstance(project).getClassesByName("Lists", GlobalSearchScope.moduleRuntimeScope(ModuleUtilCore.findModuleForPsiElement(element), false));
        for (PsiClass psiClass : listss) {
            if (psiClass.getQualifiedName().equals("com.google.common.collect.Lists")) ;
            return true;
        }

        return false;
    }

    @NotNull
    public static ParamInfo extractParamInfo(PsiType psiType) {
        String typeFullName = psiType.getCanonicalText();
        ParamInfo info = new ParamInfo();
        info.setReturnType(PsiTypesUtil.getPsiClass(psiType));
        int u = typeFullName.indexOf("<");
        if (u == -1) {
            int i = typeFullName.lastIndexOf(".");
            List<RealParam> realParamList = new ArrayList<>();
            RealParam real = new RealParam();
            real.setRealName(typeFullName.substring(i + 1));
            real.setRealPackage(typeFullName);
            realParamList.add(real);
            info.setParams(realParamList);
        } else {
            String collectpart = typeFullName.substring(0, u);
            String realClassPart = typeFullName.substring(u + 1, typeFullName.length() - 1);
            int collectIndex = collectpart.lastIndexOf(".");
            info.setCollectName(collectpart.substring(collectIndex + 1));
            info.setCollectPackege(collectpart);
            String[] split = realClassPart.split(",");
            List<RealParam> params = new ArrayList<>();
            if (split.length > 0) {
                for (String m : split) {
                    RealParam param = new RealParam();
                    int realIndex = m.lastIndexOf(".");
                    param.setRealPackage(m);
                    param.setRealName(m.substring(realIndex + 1));
                    params.add(param);
                }
            }
            info.setParams(params);
        }
        return info;
    }

    public static void addImportToFile(PsiDocumentManager psiDocumentManager, PsiJavaFile containingFile, Document document, List<String> newImportList) {
        if (newImportList.size() > 0) {
            Iterator<String> iterator = newImportList.iterator();
            while (iterator.hasNext()) {
                String u = iterator.next();
                if (u.startsWith("java.lang")) {
                    iterator.remove();
                }
            }
        }

        if (newImportList.size() > 0) {
            PsiJavaFile javaFile = containingFile;
            PsiImportStatement[] importStatements = javaFile.getImportList().getImportStatements();
            Set<String> containedSet = new HashSet<>();
            for (PsiImportStatement s : importStatements) {
                containedSet.add(s.getQualifiedName());
            }
            StringBuilder newImportText = new StringBuilder();
            for (String newImport : newImportList) {
                if (!containedSet.contains(newImport)) {
                    newImportText.append("\nimport " + newImport + ";");
                }
            }
            PsiPackageStatement packageStatement = javaFile.getPackageStatement();
            int start = 0;
            if (packageStatement != null) {
                start = packageStatement.getTextLength() + packageStatement.getTextOffset();
            }
            String insertText = newImportText.toString();
            if (StringUtils.isNotBlank(insertText)) {
                document.insertString(start, insertText);
                PsiDocumentUtils.commitAndSaveDocument(psiDocumentManager, document);
            }
        }
    }

    @NotNull
    public static String lowerStart(String name) {
        return name.substring(0, 1).toLowerCase() + name.substring(1);
    }
}
