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

import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.psi.PsiClass;
import com.intellij.ui.components.JBList;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * @author bruce ge 2022/9/13
 */
public class TemplateDialog extends DialogWrapper {
    private JTextArea textArea;
    private JPanel panel1;
    private JBList<Template> jbList;
    private JButton editTemplateInSettingButton;
    private PsiClass psiClass;
    private String name;
    private String generatedText;


    public TemplateDialog(@Nullable Project project, boolean canBeParent, List<Template> templateList, PsiClass psiClass,String name) {
        super(project, canBeParent);
        this.psiClass = psiClass;
        this.name = name;
        DefaultListModel<Template> templateDefaultListModel = new DefaultListModel<>();
        for (Template template : templateList) {
            templateDefaultListModel.addElement(template);
        }
        jbList.setModel(templateDefaultListModel);
        jbList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                Template selectedValue = jbList.getSelectedValue();
                String generate = VelocityUtils.generate(selectedValue.getTemplateText(), psiClass, name);
                textArea.setText(generate);
            }
        });
        editTemplateInSettingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ShowSettingsUtil.getInstance().showSettingsDialog(null,MySettings.class);
            }
        });
        setSize(1200,100);
        setTitle("Generate by template");
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return panel1;
    }

    public String getGeneratedText() {
        return generatedText;
    }

    public void setGeneratedText(String generatedText) {
        this.generatedText = generatedText;
    }

    @Override
    protected void doOKAction() {
        generatedText = textArea.getText();
        super.doOKAction();
    }
}
