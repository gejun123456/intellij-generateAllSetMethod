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

package com.bruce.intellijplugin.generatesetter.template;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.highlighter.EditorHighlighterFactory;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.refactoring.ui.ClassNameReferenceEditor;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.components.JBList;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

/**
 * @author bruce ge 2022/8/26
 */
public class GenerateAllSetterSettingForm {
    private JPanel panel1;
    private JCheckBox enableGenerateByTemplateCheckBox;
    private JBList myJBList;
    private EditorTextField myEditorTextField;
    private JPanel toolbarPanel;
    private ClassNameReferenceEditor classNameReferenceEditor1;
    private JButton debugButton;
    private GenerateSetterState myGenerateSetterState;
    private int currentSelectedIndex = -1;

    public GenerateAllSetterSettingForm() {
        DefaultActionGroup actionGroup = new DefaultActionGroup();
        // 复制操作
//        actionGroup.add(createCopyAction());
//        // 新增操作
        actionGroup.add(createAddAction());

//        actionGroup.add(createMoveDownAction());
//
//        // 删除动作
        actionGroup.add(createRemoveAction());
//        // 向上移动
//        actionGroup.add(createMoveUpAction());
        // 向下移动
        ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar("Item Toolbar", actionGroup, true);
        this.toolbarPanel.add(actionToolbar.getComponent(), BorderLayout.CENTER);

        debugButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = myEditorTextField.getText();
                String result = generate(text);
                Messages.showMessageDialog(result, "Generated info", null);
            }
        });

        myJBList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int selectedIndex = myJBList.getSelectedIndex();
                //means current is reset.
                if (selectedIndex == -1) {
                    return;
                }
                if (currentSelectedIndex == -1) {

                } else {
                    myGenerateSetterState.getTemplateList().get(currentSelectedIndex).setTemplateText(myEditorTextField.getText());
                }
                Template template = myGenerateSetterState.getTemplateList().get(selectedIndex);
                myEditorTextField.setText(template.getTemplateText());
                currentSelectedIndex = selectedIndex;
            }
        });

        myEditorTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void documentChanged(@NotNull DocumentEvent event) {
                int selectedIndex = myJBList.getSelectedIndex();
                if (selectedIndex != -1) {
                    myGenerateSetterState.getTemplateList().get(selectedIndex).setTemplateText(myEditorTextField.getText());
                }
            }
        });
    }

    private String generate(String text) {
        VelocityEngine velocityEngine = new VelocityEngine();
        VelocityContext velocityContext = new VelocityContext();
        String text1 = classNameReferenceEditor1.getText();
        Project project = ProjectUtil.guessCurrentProject(null);
        PsiClass aClass = JavaPsiFacade.getInstance(project).findClass(text1, GlobalSearchScope.allScope(project));
        velocityContext.put("theClass", aClass);
        velocityContext.put("variableName", "demo");
        StringWriter stringWriter = new StringWriter();
        try {
            velocityEngine.evaluate(velocityContext, stringWriter, "Velocity Code Generate", text);
        } catch (Exception e) {
            StringBuilder builder = new StringBuilder("catch exception when generate\n");
            StringWriter writer = new StringWriter();
            e.printStackTrace(new PrintWriter(writer));
            builder.append(writer.toString());
            return builder.toString().replace("\r", "");
        }
        String code = stringWriter.toString();
        return code.toString();
    }

    public JPanel getThePanel() {
        return panel1;
    }

    private AnAction createAddAction() {
        return new AnAction(AllIcons.General.Add) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                String value = Messages.showInputDialog("Input your templateName", "Input " + "templateName", null);
                if (StringUtils.isEmpty(value)) {
                    return;
                }
                List<Template> templateList = myGenerateSetterState.getTemplateList();
                Template e1 = new Template();
                e1.setTemplateName(value);
                templateList.add(e1);
                ListModel model = myJBList.getModel();
                DefaultListModel model1 = (DefaultListModel) model;
                model1.addElement(e1);
//
            }
        };
    }

    private AnAction createRemoveAction() {
        return new AnAction(AllIcons.General.Remove) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                List<Template> templateList = myGenerateSetterState.getTemplateList();
                int selectedIndex = myJBList.getSelectedIndex();
                if(selectedIndex!=-1){
                    templateList.remove(selectedIndex);
                }
                ListModel model = myJBList.getModel();
                DefaultListModel model1 = (DefaultListModel) model;
                model1.remove(selectedIndex);
                currentSelectedIndex = -1;
                myEditorTextField.setText("");
            }

            @Override
            public void update(@NotNull AnActionEvent e) {
                e.getPresentation().setEnabled(myJBList.getSelectedIndex()!=-1);
            }
        };
    }

    public void importFromSettings(GenerateSetterState state) {
        myGenerateSetterState = state;
        Boolean generateByTemplate = state.getGenerateByTemplate();
        enableGenerateByTemplateCheckBox.setSelected(generateByTemplate);
        currentSelectedIndex = -1;
        DefaultListModel model = new DefaultListModel();
        List<Template> templateList = state.getTemplateList();
        for (Template template : templateList) {
            String templateText = template.getTemplateText();
            model.addElement(template);
//            JCheckBox comp = new JCheckBox(templateText);
//            comp.setSelected(template.getEnabled());
//            myJBList.add(comp);
        }
        myJBList.setModel(model);
        myEditorTextField.setText("");
//        myJBList.setCellRenderer(new ListCellRenderer() {
//            @Override
//            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
//                Template theTempate = (Template) value;
//                JCheckBox comp = new JCheckBox(theTempate.getTemplateName());
//                comp.setSelected(theTempate.getEnabled());
//                return comp;
//            }
//        });

    }

    public GenerateSetterState getTheState() {
        myGenerateSetterState.setGenerateByTemplate(enableGenerateByTemplateCheckBox.isSelected());
        return myGenerateSetterState;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        Project project = ProjectUtil.guessCurrentProject(null);
        classNameReferenceEditor1 = new ClassNameReferenceEditor(project, null);
        String fileName = "hello.vm";
        FileType fileType = FileTypeManager.getInstance().getFileTypeByFileName(fileName);
        PsiFileFactory psiFileFactory = PsiFileFactory.getInstance(project);
        PsiFile psiFile = psiFileFactory.createFileFromText(fileName, fileType, "", 0, true);
        Document document = PsiDocumentManager.getInstance(project).getDocument(psiFile);
        if (document != null) {
            myEditorTextField = new EditorTextField(document, project, fileType) {
                @Override
                protected EditorEx createEditor() {
                    EditorEx editor = super.createEditor();
                    editor.setOneLineMode(false);
                    editor.setHorizontalScrollbarVisible(true);
                    editor.setVerticalScrollbarVisible(true);
                    EditorSettings editorSettings = editor.getSettings();
                    // 关闭虚拟空间
                    editorSettings.setVirtualSpace(false);
                    // 关闭标记位置（断点位置）
                    editorSettings.setLineMarkerAreaShown(false);
                    // 关闭缩减指南
                    editorSettings.setIndentGuidesShown(false);
                    // 显示行号
                    editorSettings.setLineNumbersShown(true);
                    // 支持代码折叠
                    editorSettings.setFoldingOutlineShown(true);
                    // 附加行，附加列（提高视野）
                    editorSettings.setAdditionalColumnsCount(3);
                    editorSettings.setAdditionalLinesCount(3);
                    // 不显示换行符号
                    editorSettings.setCaretRowShown(false);
                    ((EditorEx) editor).setHighlighter(EditorHighlighterFactory.getInstance().createEditorHighlighter(project, new LightVirtualFile("hello" + ".vm")));
                    return editor;
                }
            };
        } else {
            myEditorTextField = new EditorTextField("", project, fileType) {
                @Override
                protected EditorEx createEditor() {
                    EditorEx editor = super.createEditor();
                    editor.setOneLineMode(false);
                    editor.setHorizontalScrollbarVisible(true);
                    editor.setVerticalScrollbarVisible(true);
                    EditorSettings editorSettings = editor.getSettings();
                    // 关闭虚拟空间
                    editorSettings.setVirtualSpace(false);
                    // 关闭标记位置（断点位置）
                    editorSettings.setLineMarkerAreaShown(false);
                    // 关闭缩减指南
                    editorSettings.setIndentGuidesShown(false);
                    // 显示行号
                    editorSettings.setLineNumbersShown(true);
                    // 支持代码折叠
                    editorSettings.setFoldingOutlineShown(true);
                    // 附加行，附加列（提高视野）
                    editorSettings.setAdditionalColumnsCount(3);
                    editorSettings.setAdditionalLinesCount(3);
                    // 不显示换行符号
                    editorSettings.setCaretRowShown(false);
                    ((EditorEx) editor).setHighlighter(EditorHighlighterFactory.getInstance().createEditorHighlighter(project, new LightVirtualFile("hello" + ".vm")));
                    return editor;
                }
            };
        }
    }
}
