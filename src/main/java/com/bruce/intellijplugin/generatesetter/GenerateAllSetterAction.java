package com.bruce.intellijplugin.generatesetter;

import com.bruce.intellijplugin.generatesetter.utils.PsiDocumentUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiTypesUtil;
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
    public static final String IS = "is";
    public static final String GET = "get";

    private static Map<String, String> typeGeneratedMap = new HashMap<String, String>() {{
        put("boolean", "false");
        put("java.lang.Boolean", "false");
        put("int", "0");
        put("java.lang.Integer", "0");
        put("java.lang.String", "\"\"");
        put("java.math.BigDecimal", "new BigDecimal(\"0\")");
        put("java.lang.Long", "0L");
        put("long", "0L");
        put("short", "0");
        put("java.lang.Short", "0");
        put("java.util.Date", "new Date()");
        put("float", "0.0F");
        put("java.lang.Float", "0.0F");
        put("double", "0.0D");
        put("java.lang.Double", "0.0D");
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
        PsiElement psiParent = PsiTreeUtil.getParentOfType(element, PsiLocalVariable.class, PsiMethod.class);
        if (psiParent == null) {
            return;
        }
        if (psiParent instanceof PsiLocalVariable) {
            PsiLocalVariable psiLocal = (PsiLocalVariable) psiParent;
            if (!(psiLocal.getParent() instanceof PsiDeclarationStatement)) {
                return;
            }
            handleWithLocalVariable(psiLocal, project, element);

        } else if (psiParent instanceof PsiMethod) {
            PsiMethod method = (PsiMethod) psiParent;
            handleWithMethod(method, project, element);
        }
    }

    private void handleWithMethod(PsiMethod method, Project project, PsiElement element) {
        PsiType returnType = method.getReturnType();
        PsiClass psiClass = PsiTypesUtil.getPsiClass(returnType);
        PsiParameter[] parameters = method.getParameterList().getParameters();
        List<PsiMethod> methods = extractSetMethods(psiClass);
        List<String> importList = Lists.newArrayList();
        String generateName = "vo";
        String insertText = "";
        GetInfo info = null;
        if (parameters.length > 0) {
            for (PsiParameter parameter : parameters) {
                PsiType type = parameter.getType();
                PsiClass parameterClass = PsiTypesUtil.getPsiClass(type);
                if (parameterClass == null || parameterClass.getQualifiedName().startsWith("java.")) {
                    continue;
                } else {
                    List<PsiMethod> getMethods = extractGetMethod(parameterClass);
                    if (getMethods.size() > 0) {
                        info = buildInfo(parameter, getMethods);
                        break;
                    }
                }
            }
        }
        PsiDocumentManager psiDocumentManager = PsiDocumentManager.getInstance(project);
        Document document = psiDocumentManager.getDocument(element.getContainingFile());
        String splitText = extractSplitText(method, document);
        insertText += splitText + psiClass.getName() + " " + generateName + "= new " + psiClass.getName() + "();";
        boolean hasGuava = checkGuavaExist(project, element);
        if (info == null) {
            insertText += generateStringForNoParam(generateName, methods, splitText, importList, hasGuava);
        } else {
            insertText += generateStringForParam(generateName, methods, splitText, importList, hasGuava, info);
        }
        insertText += "return " + generateName + ";";
        document.insertString(method.getBody().getTextOffset() + 1, insertText);
        PsiDocumentUtils.commitAndSaveDocument(psiDocumentManager, document);
        addImportToFile(psiDocumentManager, (PsiJavaFile) element.getContainingFile(), document, importList);
    }

    private String generateStringForParam(String generateName, List<PsiMethod> methodList, String splitText, List<String> newImportList, boolean hasGuava, GetInfo info) {
        StringBuilder builder = new StringBuilder();
        builder.append(splitText);
        for (PsiMethod method : methodList) {
            if (method.getName().startsWith("set")) {
                String fieldToLower = method.getName().substring(3).toLowerCase();
                String s = info.getNameToMethodMap().get(fieldToLower);
                if (s != null) {
                    builder.append(generateName + "." + method.getName() + "(" + info.getParamName() + "." + s + "());");
                } else {
                    generateDefaultForOneMethod(generateName, newImportList, hasGuava, builder, method);
                }
            }
            builder.append(splitText);
        }
        return builder.toString();
    }

    @NotNull
    private GetInfo buildInfo(PsiParameter parameter, List<PsiMethod> getMethods) {
        GetInfo info;
        info = new GetInfo();
        info.setParamName(parameter.getName());
        info.setGetMethods(getMethods);
        Map<String, String> nameToMethodMaps = Maps.newHashMap();
        for (PsiMethod getMethod : getMethods) {
            if (getMethod.getName().startsWith(IS)) {
                nameToMethodMaps.put(getMethod.getName().substring(2).toLowerCase(), getMethod.getName());
            } else if (getMethod.getName().startsWith(GET)) {
                nameToMethodMaps.put(getMethod.getName().substring(3).toLowerCase(), getMethod.getName());
            }
        }
        info.setNameToMethodMap(nameToMethodMaps);
        return info;
    }

    @NotNull
    private String extractSplitText(PsiMethod method, Document document) {
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
        String methodStartToLastLineText = document.getText(new TextRange(lastLine, startOffset));
        String splitText = null;
        if (isTable) {
            splitText += methodStartToLastLineText + "\t";
        } else {
            splitText = methodStartToLastLineText + "    ";
        }
        return splitText;
    }

    private List<PsiMethod> extractGetMethod(PsiClass psiClass) {
        List<PsiMethod> methodList = new ArrayList<>();
        while (isValid(psiClass)) {
            addGettMethodToList(psiClass, methodList);
            psiClass = psiClass.getSuperClass();
        }
        return methodList;
    }


    private void handleWithLocalVariable(PsiLocalVariable localVariable, Project project, PsiElement element) {
        PsiElement parent1 = localVariable.getParent();
        if (!(parent1 instanceof PsiDeclarationStatement)) {
            return;
        }
        PsiClass psiClass = PsiTypesUtil.getPsiClass(localVariable.getType());
        String generateName = localVariable.getName();
        List<PsiMethod> methodList = extractSetMethods(psiClass);
        if (methodList.size() == 0) {
            return;
        }
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
        boolean hasGuava = checkGuavaExist(project, element);

        String buildString = generateStringForNoParam(generateName, methodList, splitText, newImportList, hasGuava);
        document.insertString(statementOffset + parent1.getText().length(), buildString);
        PsiDocumentUtils.commitAndSaveDocument(psiDocumentManager, document);
        addImportToFile(psiDocumentManager, (PsiJavaFile) containingFile, document, newImportList);
        return;
    }

    private void addImportToFile(PsiDocumentManager psiDocumentManager, PsiJavaFile containingFile, Document document, List<String> newImportList) {
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
    private List<PsiMethod> extractSetMethods(PsiClass psiClass) {
        List<PsiMethod> methodList = new ArrayList<>();
        while (isValid(psiClass)) {
            addSetMethodToList(psiClass, methodList);
            psiClass = psiClass.getSuperClass();
        }
        return methodList;
    }

    @NotNull
    private String generateStringForNoParam(String generateName, List<PsiMethod> methodList, String splitText, List<String> newImportList, boolean hasGuava) {
        StringBuilder builder = new StringBuilder();
        builder.append(splitText);
        for (PsiMethod method : methodList) {
            generateDefaultForOneMethod(generateName, newImportList, hasGuava, builder, method);
            builder.append(splitText);
        }

        return builder.toString();
    }

    private void generateDefaultForOneMethod(String generateName, List<String> newImportList, boolean hasGuava, StringBuilder builder, PsiMethod method) {
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
        builder.append(");");
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


    private void addGettMethodToList(PsiClass psiClass, List<PsiMethod> methodList) {
        PsiMethod[] methods = psiClass.getMethods();
        for (PsiMethod method : methods) {
            if (isValidGetMethod(method)) {
                methodList.add(method);
            }
        }
    }

    private boolean isValidGetMethod(PsiMethod m) {
        return m.hasModifierProperty("public") && !m.hasModifierProperty("static") &&
                (m.getName().startsWith(GET) || m.getName().startsWith(IS));
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
        PsiElement psiParent = PsiTreeUtil.getParentOfType(element, PsiLocalVariable.class, PsiMethod.class);
        if (psiParent == null) {
            return false;
        }

        PsiClass psiClass = null;
        if (psiParent instanceof PsiLocalVariable) {
            PsiLocalVariable psiLocal = (PsiLocalVariable) psiParent;
            if (!(psiLocal.getParent() instanceof PsiDeclarationStatement)) {
                return false;
            }
            psiClass = PsiTypesUtil.getPsiClass(psiLocal.getType());


        } else if (psiParent instanceof PsiMethod) {
            PsiMethod method = (PsiMethod) psiParent;
            psiClass = PsiTypesUtil.getPsiClass(method.getReturnType());
        }
        //todo when psiClass is null, it can be list ect. can generate it as well could use method.getReturnType.getCanolicText to check for type ect.
        //todo may check with the cursor if it on the method definition area instead of everywhere in method.
        while (isValid(psiClass)) {
            for (PsiMethod m : psiClass.getMethods()) {
                if (isValidSetMethod(m)) {
                    return true;
                }
            }
            psiClass = psiClass.getSuperClass();
        }
        return false;
    }

    private boolean isValid(PsiClass psiClass) {
        if (psiClass == null) {
            return false;
        }
        String qualifiedName = psiClass.getQualifiedName();
        if (qualifiedName.startsWith("java.")) {
            return false;
        }
        return true;
    }

    private boolean notObjectClass(PsiClass psiClass) {
        if (psiClass.getQualifiedName().equals("java.lang.Object")) {
            return false;
        }
        return true;
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
