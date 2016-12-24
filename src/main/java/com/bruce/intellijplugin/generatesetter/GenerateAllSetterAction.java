package com.bruce.intellijplugin.generatesetter;

import com.bruce.intellijplugin.generatesetter.utils.PsiDocumentUtils;
import com.bruce.intellijplugin.generatesetter.utils.PsiElementUtil;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.psi.util.PsiTypesUtil;
import com.intellij.psi.util.PsiUtilCore;
import com.intellij.util.IncorrectOperationException;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Created by bruce.ge on 2016/12/23.
 */
public class GenerateAllSetterAction extends PsiElementBaseIntentionAction {

    public static final String GENERATE_SETTER_METHOD = "generate all setter";

    private static Map<String, String> typeGeneratedMap = new HashMap<String, String>() {{
        put("boolean", "false");
        put("java.lang.Boolean", "false");
        put("int", "0");
        put("java.lang.Integer", "0");
        put("java.lang.String", "\"\"");
        put("java.math.BigDecimal", "new BigDecimal(\"0\")");
        put("java.lang.Long", "0");
        put("long", "0");
        put("short", "0");
        put("java.lang.Short", "0");
        put("java.util.Date", "new Date()");
        put("float", "0");
        put("java.lang.Float", "0");
        put("double", "0");
        put("java.lang.Double", "0");
        put("java.lang.Character", "\'\'");
        put("char", "\'\'");
    }};


    private static Map<String, String> typeGeneratedImport = new HashMap<String, String>() {{
        put("java.math.BigDecimal", "java.math.BigDecimal");
        put("java.util.Date", "java.util.Date");
    }};


    private static Map<String, String> guavaTypeMaps = new HashMap<String, String>() {{
        put("List", "Lists.newArrayList()");
        put("Map", "Maps.newHashMap()");
        put("Set", "Sets.newHashSet()");
    }};

    private static Map<String, String> guavaImportMap = new HashMap<String, String>() {{
        put("List", "com.google.common.collect.Lists");
        put("Map", "com.google.common.collect.Maps");
        put("Set", "com.google.common.collect.Sets");
    }};


    private static Map<String, String> defaultCollections = new HashMap<String, String>() {{
        put("List", "ArrayList");
        put("Map", "HashMap");
        put("Set", "HashSet");
    }};


    private static Map<String, String> defaultImportMap = new HashMap<String, String>() {{
        put("List", "java.util.ArrayList");
        put("Map", "java.util.HashMap");
        put("Set", "java.util.HashSet");
    }};

    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {
        PsiElement parent = element.getParent();
        if (parent == null) {
            return;
        }
        PsiElement javaParent = parent.getParent();
        if (javaParent == null) {
            return;
        }

        if (!(javaParent instanceof PsiTypeElement)) {
            return;
        }

        PsiElement psiLocal = javaParent.getParent();
        if (!(psiLocal instanceof PsiLocalVariable)) {
            return;
        }

        PsiLocalVariable localVariable = (PsiLocalVariable) psiLocal;
        PsiElement parent1 = localVariable.getParent();
        if (!(parent1 instanceof PsiDeclarationStatement)) {
            return;
        }
        PsiClass psiClass = PsiTypesUtil.getPsiClass(localVariable.getType());
        String generateName = localVariable.getName();
        List<PsiMethod> methodList = new ArrayList<>();
        while (!psiClass.getQualifiedName().equals("java.lang.Object")) {
            addSetMethodToList(psiClass, methodList);
            psiClass = psiClass.getSuperClass();
            if (psiClass == null) {
                break;
            }
        }

        if (methodList.size() == 0) {
            return;
        }

        StringBuilder builder = new StringBuilder();
        PsiDocumentManager psiDocumentManager = PsiDocumentManager.getInstance(project);
        PsiFile containingFile = element.getContainingFile();
        Document document = psiDocumentManager.getDocument(containingFile);
        int statementOffset = parent1.getTextOffset();
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
        splitText = "\n" + splitText;


        List<String> newImportList = new ArrayList<>();

        boolean checkedGuava = false;
        boolean hasGuava = false;
        builder.append(splitText);
        for (PsiMethod method : methodList) {
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
                    //shall check with import list to use.
                    ParamInfo paramInfo = extractParamInfo(classType);
                    if (paramInfo.getCollectName() != null && guavaTypeMaps.containsKey(paramInfo.getCollectName())) {
                        if (!checkedGuava) {
                            hasGuava = checkGuavaExist(project, element);
                            checkedGuava = true;
                        }
                        if (hasGuava) {
                            builder.append(guavaTypeMaps.get(paramInfo.getCollectName()));
                            newImportList.add(guavaImportMap.get(paramInfo.getCollectName()));
                        } else {
                            //handleWithoutGuava.
                            String defaultImpl = defaultCollections.get(paramInfo.getCollectName());
                            appendCollectNotEmpty(builder, paramInfo, defaultImpl, newImportList);
                            newImportList.add(defaultImportMap.get(paramInfo.getCollectName()));
                        }
                        //使用自带的来搞起
                    } else {
                        if (paramInfo.getCollectName() != null) {
                            String defaultImpl = defaultCollections.get(paramInfo.getCollectName());
                            if (defaultImpl != null) {
                                appendCollectNotEmpty(builder, paramInfo, defaultImpl, newImportList);
                                newImportList.add(defaultImportMap.get(paramInfo.getCollectName()));
                            } else {
                                appendCollectNotEmpty(builder, paramInfo, paramInfo.getCollectName(), newImportList);
                                newImportList.add(paramInfo.getCollectPackege());
                            }
                        } else {
                            //may be could get the construct of the class.
                            builder.append("new " + paramInfo.getParams().get(0).getRealName() + "()");
                            newImportList.add(paramInfo.getParams().get(0).getRealPackage());
                        }
                    }


                }
                if (h != u) {
                    builder.append(",");
                }
            }
            builder.append(");").append(splitText);
        }


        document.insertString(statementOffset + parent1.getText().length(), builder.toString());
        PsiDocumentUtils.commitAndSaveDocument(psiDocumentManager, document);
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
            PsiJavaFile javaFile = (PsiJavaFile) containingFile;
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

        return;
    }

    private void appendCollectNotEmpty(StringBuilder builder, ParamInfo paramInfo, String defaultImpl, List<String> newImportList) {
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


    private ParamInfo extractParamInfo(String classType) {
        ParamInfo info = new ParamInfo();
        int u = classType.indexOf("<");
        if (u == -1) {
            int i = classType.lastIndexOf(".");
            List<RealParam> realParamList = new ArrayList<>();
            RealParam real = new RealParam();
            real.setRealName(classType.substring(i + 1));
            real.setRealPackage(classType);
            realParamList.add(real);
            info.setParams(realParamList);
        } else {
            String collectpart = classType.substring(0, u);
            String realClassPart = classType.substring(u + 1, classType.length() - 1);
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


    private boolean checkGuavaExist(Project project, PsiElement element) {
        PsiClass[] listss = PsiShortNamesCache.getInstance(project).getClassesByName("Lists", GlobalSearchScope.moduleRuntimeScope(ModuleUtilCore.findModuleForPsiElement(element), false));
        for (PsiClass psiClass : listss) {
            if (psiClass.getQualifiedName().equals("com.google.common.collect.Lists")) ;
            return true;
        }

        return false;
    }

    private void addSetMethodToList(PsiClass psiClass, List<PsiMethod> methodList) {
        PsiMethod[] methods = psiClass.getMethods();
        for (PsiMethod method : methods) {
            if (isValidSetMethod(method)) {
                methodList.add(method);
            }
        }
    }

    private String findNextNotNull(PsiTypeElement psiType, String defaultName) {
        PsiElement nextSibling = psiType.getNextSibling();
        while (nextSibling != null && StringUtils.isBlank(nextSibling.getText())) {
            nextSibling = nextSibling.getNextSibling();
        }
        if (nextSibling == null) {
            return defaultName;
        }
        return nextSibling.getText().trim();
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
        if (editor.isViewer()) {
            return false;
        }
        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
        if (psiFile == null) {
            return false;
        }
        PsiUtilCore.ensureValid(psiFile);
        if (!(psiFile instanceof PsiJavaFile)) {
            return false;
        }
        PsiClass containingClass = PsiElementUtil.getContainingClass(element);
        if (containingClass == null) {
            return false;
        }

        if (containingClass.isInterface()) {
            return false;
        }

        if (!(element instanceof PsiIdentifier)) {
            return false;
        }
        PsiElement parent = element.getParent();
        if (!(parent instanceof PsiJavaCodeReferenceElement)) {
            return false;
        }

        PsiJavaCodeReferenceElement javacode = (PsiJavaCodeReferenceElement) parent;
        if (javacode instanceof PsiReferenceExpression) {
            return false;
        }
        PsiElement javaCodeParent = javacode.getParent();
        if (!(javaCodeParent instanceof PsiTypeElement)) {
            return false;
        }

        PsiElement psiLocal = javaCodeParent.getParent();
        if (!(psiLocal instanceof PsiLocalVariable)) {
            return false;
        }

        if (!(psiLocal.getParent() instanceof PsiDeclarationStatement)) {
            return false;
        }
        PsiTypeElement psiType = (PsiTypeElement) javaCodeParent;
        PsiType type = psiType.getType();
        PsiClass psiClass = PsiTypesUtil.getPsiClass(type);
        if (psiClass == null) {
            return false;
        }
        while (!psiClass.getQualifiedName().equals("java.lang.Object")) {
            for (PsiMethod m : psiClass.getMethods()) {
                if (isValidSetMethod(m)) {
                    return true;
                }
            }
            psiClass = psiClass.getSuperClass();
            if (psiClass == null) {
                break;
            }
        }
        return false;
    }

    private boolean isValidSetMethod(PsiMethod m) {
        return m.hasModifierProperty("public") && !m.hasModifierProperty("static") && m.getName().startsWith("set");
    }

    @Nls
    @NotNull
    @Override
    public String getFamilyName() {
        return GENERATE_SETTER_METHOD;
    }

    @NotNull
    @Override
    public String getText() {
        return GENERATE_SETTER_METHOD;
    }
}
