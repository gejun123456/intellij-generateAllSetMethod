package com.bruce.intellijplugin.generatesetter.dialog;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.psi.PsiVariable;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class VariableDialog extends DialogWrapper {

    private JComboBox<String> variableComboBox;
    private List<PsiVariable> variables;

    public VariableDialog(List<PsiVariable> variables) {
        super(true); // use current window as parent
        this.variables = variables;
        init();
        setTitle("Choose a Variable");
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel dialogPanel = new JPanel(new GridLayout(0, 2));

        variableComboBox = new JComboBox<>();
        for (PsiVariable variable : variables) {
            variableComboBox.addItem(variable.getName());
        }

        dialogPanel.add(new JLabel("Variable:"));
        dialogPanel.add(variableComboBox);

        return dialogPanel;
    }

    public String getSelectedVariable() {
        return (String) variableComboBox.getSelectedItem();
    }
}